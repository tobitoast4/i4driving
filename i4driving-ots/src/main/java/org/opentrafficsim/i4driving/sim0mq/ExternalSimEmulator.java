package org.opentrafficsim.i4driving.sim0mq;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Direction;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.logger.CategoryLogger;
import org.djutils.serialization.SerializationException;
import org.pmw.tinylog.Level;
import org.sim0mq.Sim0MQException;
import org.sim0mq.message.Sim0MQMessage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * Emulates an external simulation for testing purposes.
 * @author wjschakel
 */
public final class ExternalSimEmulator
{

    /** Federation id to receive/sent messages. */
    private static final String FEDERATION = "Ots_ExternalSim";

    /** OTS id to receive/sent messages. */
    private static final String OTS = "Ots";

    /** Fosim id to receive/sent messages. */
    private static final String EXTERNAL_SIM = "ExternalSim";

    /** Endianness. */
    private static final boolean BIG_ENDIAN = false;

    /** Port number. */
    private static final int PORT = 5556;

    /** Trajectory update frequecy. */
    private static final Frequency EXTERNAL_FREQUENCY = Frequency.instantiateSI(30);

    /** Whether to emulate demo network (or OpenDRIVE network). */
    private static final boolean DEMO_NETWORK = true;
    
    /** Run real-time or as fast as possible. */
    private static final boolean REAL_TIME = true;

    /**
     * Constructor.
     */
    private ExternalSimEmulator()
    {
        //
    }

    /**
     * Main method.
     * @param args command line arguments.
     */
    public static void main(final String... args)
    {
        new Worker().start();
    }

    /**
     * Worker thread to listen to messages and respond.
     * <p>
     * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved.
     * <br>
     * BSD-style license. See <a href="https://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
     * </p>
     * @author <a href="https://github.com/wjschakel">Wouter Schakel</a>
     */
    protected static class Worker extends Thread
    {

        /** */
        private ZContext context;

        /** the socket. */
        private ZMQ.Socket responder;

        /** Message id. */
        private int messageId = 0;

        /** Ego vehicle control. */
        private Map<String, TrajectorySender> trajectorySenders = new LinkedHashMap<>();

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            this.context = new ZContext(1);
            this.responder = this.context.createSocket(SocketType.PAIR);
            this.responder.connect("tcp://*:" + PORT);
            CategoryLogger.setAllLogLevel(Level.DEBUG);
            CategoryLogger.setAllLogMessageFormat("[{date: YYYY-MM-dd HH:mm:ss.SSS}] {level}: {message}");
            CategoryLogger.always().debug("ExternalSim is running");

            try
            {
                byte[] encodedMessage;

                Set<Integer> awaiting = new LinkedHashSet<>();
                int msgId = this.messageId++;
                awaiting.add(msgId);
                String odFile = DEMO_NETWORK ? "/od/OdMatrix.json" : "/od/OdMatrixOpenDrive.json";
                String jsonOd = Files.readString(Paths.get(getClass().getResource(odFile).toURI()));
                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "ODMATRIX",
                        msgId, new Object[] {jsonOd});
                this.responder.send(encodedMessage, ZMQ.DONTWAIT);
                CategoryLogger.always().debug("ExternalSim sent ODMATRIX message");

                msgId = this.messageId++;
                awaiting.add(msgId);
                String routesFile = DEMO_NETWORK ? "/route/Routes.json" : "/route/RoutesOpenDrive.json";
                String jsonRoutes = Files.readString(Paths.get(getClass().getResource(routesFile).toURI()));
                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "ROUTES", msgId,
                        new Object[] {jsonRoutes});
                this.responder.send(encodedMessage, ZMQ.DONTWAIT);
                CategoryLogger.always().debug("ExternalSim sent ROUTES message");

                msgId = this.messageId++;
                awaiting.add(msgId);
                Object[] networkPayload = DEMO_NETWORK ? new Object[] {} : new Object[] {Files.readString(Paths
                        .get(getClass().getResource("/opendrive/examples/i4Driving_scenario01_urban-straight.xodr").toURI()))};
                encodedMessage =
                        Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "NETWORK", msgId, networkPayload);
                this.responder.send(encodedMessage, ZMQ.DONTWAIT);
                CategoryLogger.always().debug("ExternalSim sent NETWORK message");

                Speed gtuSpeed = new Speed(50, SpeedUnit.KM_PER_HOUR);
                Length startX1 = Length.instantiateSI(40.0);
                Length startY1 = Length.instantiateSI(5.25);
                Length startX2 = Length.instantiateSI(30.0);
                Length startY2 = Length.instantiateSI(5.25);
                setupVehicles(awaiting, gtuSpeed, startX1, startY1, startX2, startY2);

                Long start = null;
                // reset, stop, terminate, command (lane change), vehicle (during sim), active mode
                long[] events = new long[] {30000, 40000, 45000, 10000, 500, 5000};
                while (!Thread.currentThread().isInterrupted())
                {
                    // Wait for next message from the server
                    byte[] request = this.responder.recv(ZMQ.DONTWAIT);
                    while (request == null)
                    {
                        // Hard-coded reset, stop, terminate
                        if (externalEvents(awaiting, start, events))
                        {
                            break;
                        }
                        try
                        {
                            // In order to allow resources to go to other processes, we sleep before checking again
                            Thread.sleep(3);
                        }
                        catch (InterruptedException e)
                        {

                        }
                        request = this.responder.recv(ZMQ.DONTWAIT);
                    }
                    if (request == null)
                    {
                        break; // terminate performed break in message receiving while loop
                    }

                    Sim0MQMessage message = Sim0MQMessage.decode(request);
                    if ("PLAN".equals(message.getMessageTypeId()))
                    {
                        // String id
                        Object[] payload = message.createObjectArray();
                        String id = (String) payload[8];
                        CategoryLogger.always().debug("ExternalSim received PLAN message for GTU " + id);
                    }
                    else if ("DELETE".equals(message.getMessageTypeId()))
                    {
                        // String id
                        Object[] payload = message.createObjectArray();
                        String id = (String) payload[8];
                        TrajectorySender ts = this.trajectorySenders.remove(id);
                        if (ts != null)
                        {
                            ts.terminate();
                        }
                        CategoryLogger.always().debug("ExternalSim received DELETE message for GTU " + id);
                    }
                    else if ("READY".equals(message.getMessageTypeId()))
                    {
                        msgId = (int) message.createObjectArray()[8];
                        awaiting.remove(msgId);
                        CategoryLogger.always().debug("ExternalSim received READY message");
                        if (awaiting.isEmpty())
                        {
                            // Trajectory senders
                            TrajectorySender ts1 = new TrajectorySender("Ego 1", EXTERNAL_FREQUENCY, startX1, startY1, gtuSpeed,
                                    Acceleration.instantiateSI(0.5), Duration.instantiateSI(5.0));
                            TrajectorySender ts2 = new TrajectorySender("Ego 2", EXTERNAL_FREQUENCY, startX2, startY2, gtuSpeed,
                                    Acceleration.instantiateSI(-0.5), Duration.instantiateSI(3.0));
                            this.trajectorySenders.put("Ego 1", ts1);
                            this.trajectorySenders.put("Ego 2", ts2);

                            // Start simulation
                            String messageType;
                            if (REAL_TIME)
                            {
                                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "START",
                                        this.messageId++, new Object[] {});
                                messageType = "START";
                            }
                            else
                            {
                                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "PROGRESS",
                                        this.messageId++, new Object[] {Duration.instantiateSI(60.0)});
                                messageType = "PROGRESS";
                            }
                            this.responder.send(encodedMessage, 0);
                            this.trajectorySenders.values().forEach((ts) -> ts.start());
                            CategoryLogger.always().debug("ExternalSim sent {} message", messageType);
                            if (start == null)
                            {
                                start = System.currentTimeMillis(); // only first time, this is for reset/stop/terminate
                            }
                        }
                    }
                }
            }
            catch (Sim0MQException | SerializationException | URISyntaxException | IOException e)
            {
                e.printStackTrace();
            }

            terminateTrajectories();
            this.responder.close();
            this.context.destroy();
            this.context.close();
            CategoryLogger.always().debug("ExternalSim terminated");
            System.exit(0);
        }

        /**
         * Execute external events such as reset, stop and terminate.
         * @param awaiting set of message ids that need to be processed before we start
         * @param start system time when first simulation started
         * @param events event times after start
         * @return whether to terminate
         * @throws Sim0MQException
         * @throws SerializationException
         */
        private boolean externalEvents(final Set<Integer> awaiting, final Long start, final long[] events)
                throws Sim0MQException, SerializationException
        {
            int msgId;
            byte[] encodedMessage;
            boolean terminate = false;
            long now = System.currentTimeMillis();
            if (start != null && now - start > events[0])
            {
                // Reset
                terminateTrajectories();
                msgId = this.messageId++;
                awaiting.add(msgId);
                encodedMessage =
                        Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "RESET", msgId, new Object[] {});
                this.responder.send(encodedMessage, 0);
                events[0] = Long.MAX_VALUE;
                CategoryLogger.always().debug("ExternalSim sent RESET message");
            }
            if (start != null && now - start > events[1])
            {
                // Stop
                terminateTrajectories();
                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "STOP", this.messageId++,
                        new Object[] {});
                this.responder.send(encodedMessage, 0);
                events[1] = Long.MAX_VALUE;
                CategoryLogger.always().debug("ExternalSim sent STOP message");
            }
            if (start != null && now - start > events[2])
            {
                // Terminate
                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "TERMINATE",
                        this.messageId++, new Object[] {});
                this.responder.send(encodedMessage, 0);
                CategoryLogger.always().debug("ExternalSim sent TERMINATE message");
                terminate = true;
            }
            if (start != null && now - start > events[3])
            {
                // Command (indicator)
                String json = "{ \"time\": \"0.0 s\", \"type\": \"setIndicator\", "
                        + "\"data\": {\"direction\": \"RIGHT\", \"duration\": \"5.0 s\"} }";
                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "COMMAND",
                        this.messageId++, new Object[] {"Ego 2", json});
                this.responder.send(encodedMessage, 0);
                events[3] = Long.MAX_VALUE;
                CategoryLogger.always().debug("ExternalSim sent COMMAND message");
            }
            if (DEMO_NETWORK && start != null && now - start > events[4])
            {
                // Vehicle after Start
                Object[] payload = new Object[] {"Florian", "Hybrid", Length.instantiateSI(0.152921),
                        Length.instantiateSI(1.75338), Direction.instantiateSI(-0.0399514), Speed.instantiateSI(5.0), "CAR",
                        Length.instantiateSI(4.74), Length.instantiateSI(1.75), Length.instantiateSI(0.89), 0, "A-B"};
                this.responder.send(Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM, "VEHICLE",
                        this.messageId++, payload), ZMQ.DONTWAIT);
                events[4] = Long.MAX_VALUE;
                CategoryLogger.always().debug("ExternalSim sent VEHICLE message TEST FLORIAN");
            }
            if (start != null && now - start > events[5])
            {
                // Pedestrian
                Length x = Length.instantiateSI(20.0);
                Length y = Length.instantiateSI(-1.0);
                Speed v = Speed.instantiateSI(1.0);
                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "VEHICLE",
                        this.messageId++, new Object[] {"Pedestrian", "Active", x, y, Direction.instantiateSI(Math.PI / 2.0), v,
                                "", Length.ZERO, Length.ZERO, Length.ZERO, 0, ""});
                this.responder.send(encodedMessage, 0);
                events[5] = Long.MAX_VALUE;
                CategoryLogger.always().debug("ExternalSim sent ACTIVE vehicle message");
                TrajectorySender ts =
                        new TrajectorySender("Pedestrian", EXTERNAL_FREQUENCY, x, y, v, Acceleration.ZERO, Duration.ZERO);
                ts.start();
                this.trajectorySenders.put("Pedestrian", ts);
            }
            return terminate;
        }

        /**
         * Terminates all trajectories.
         */
        private void terminateTrajectories()
        {
            this.trajectorySenders.values().forEach((ts) -> ts.terminate());
            this.trajectorySenders.clear();
        }

        /**
         * Setup initial vehicles.
         * @param awaiting set of message ids that need to be processed before we start
         * @param gtuSpeed speed of GTUs
         * @param startX1 start x coordinate of ego GTU 1
         * @param startY1 start y coordinate of ego GTU 1
         * @param startX2 start x coordinate of ego GTU 2
         * @param startY2 start y coordinate of ego GTU 2
         */
        private void setupVehicles(final Set<Integer> awaiting, final Speed gtuSpeed, final Length startX1,
                final Length startY1, final Length startX2, final Length startY2)
        {
            try
            {
                int msgId;
                // Fellow
                // TODO set custom parameters
                Length length = Length.instantiateSI(4.0);
                Length width = Length.instantiateSI(1.9);
                Length refToNose = Length.instantiateSI(3.0);
                Object[] payload = new Object[] {"Fellow 1", "Ots", Length.instantiateSI(20.0), Length.instantiateSI(1.75),
                        Direction.ZERO, gtuSpeed, "CAR", length, width, refToNose, 0, "A-B"};

                msgId = this.messageId++;
                awaiting.add(msgId);
                Worker.this.responder.send(
                        Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM, "VEHICLE", msgId, payload),
                        ZMQ.DONTWAIT);
                CategoryLogger.always().debug("ExternalSim sent VEHICLE message for Fellow 1");
                payload = new Object[] {"Fellow 2", "Ots", Length.instantiateSI(10.0), Length.instantiateSI(1.75),
                        Direction.ZERO, gtuSpeed, "CAR", length, width, refToNose, 0, "A-B"};
                msgId = this.messageId++;
                awaiting.add(msgId);
                Worker.this.responder.send(
                        Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM, "VEHICLE", msgId, payload),
                        ZMQ.DONTWAIT);
                CategoryLogger.always().debug("ExternalSim sent VEHICLE message for Fellow 2");

                // Ego
                payload = new Object[] {"Ego 1", "Hybrid", startX1, startY1, Direction.ZERO, gtuSpeed, "CAR", length, width,
                        refToNose, 0, "A-B"};
                msgId = this.messageId++;
                awaiting.add(msgId);
                Worker.this.responder.send(
                        Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM, "VEHICLE", msgId, payload),
                        ZMQ.DONTWAIT);
                CategoryLogger.always().debug("ExternalSim sent VEHICLE message for Ego 1");
                payload = new Object[] {"Ego 2", "Hybrid", startX2, startY2, Direction.ZERO, gtuSpeed, "CAR", length, width,
                        refToNose, 0, "A-B"};
                msgId = this.messageId++;
                awaiting.add(msgId);
                Worker.this.responder.send(
                        Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM, "VEHICLE", msgId, payload),
                        ZMQ.DONTWAIT);
                CategoryLogger.always().debug("ExternalSim sent VEHICLE message for Ego 2");
            }
            catch (Sim0MQException | SerializationException ex)
            {
                ex.printStackTrace();
            }
        }

        /**
         * Class that the Worker thread uses to sent dummy frequent EGO vehicle updates with lateral noise.
         */
        protected class TrajectorySender extends Thread
        {
            /** GTU id. */
            private final String gtuId;

            /** Start speed. */
            private final Speed startSpeed;

            /** Start x position. */
            private final Length startPositionX;

            /** Start y position. */
            private final Length startPositionY;

            /** Acceleration. */
            private final Acceleration acceleration;

            /** Acceleration time. */
            private final Duration accelerationTime;

            /** Disables the trajectory from sending more messages. */
            private boolean active;

            /** Last update execution. */
            private long executionTime;

            /** Interval between updates. */
            private final long interval;

            /** Start time of simulation. */
            private long startTime;

            /** Lateral shift for lane change. */
            private double dy = 0.0;

            /** Random number generator. */
            private Random random = new Random();

            /** Auto-correlated Wiener value. */
            private double wiener;

            /** Auto-correlation time. */
            private double wienerTime;

            /**
             * Constructor defining an initial constant acceleration phase, followed by a constant speed phase.
             * @param gtuId GTU id to sent updates for
             * @param frequency frequency of updates
             * @param startPositionX start x position
             * @param startPositionY start y position
             * @param startSpeed start speed
             * @param acceleration acceleration in first phase
             * @param accelerationTime duration of first phase
             */
            TrajectorySender(final String gtuId, final Frequency frequency, final Length startPositionX,
                    final Length startPositionY, final Speed startSpeed, final Acceleration acceleration,
                    final Duration accelerationTime)
            {
                this.gtuId = gtuId;
                this.startSpeed = startSpeed;
                this.startPositionX = startPositionX;
                this.startPositionY = startPositionY;
                this.acceleration = acceleration;
                this.accelerationTime = accelerationTime;
                this.interval = (long) (1000.0 / frequency.si);
            }

            /** {@inheritDoc} */
            @Override
            public void run()
            {
                this.startTime = System.currentTimeMillis();
                this.active = true;
                while (this.active)
                {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime >= this.executionTime + this.interval)
                    {
                        // Kinematics
                        this.executionTime = currentTime;
                        double dt = (currentTime - this.startTime) / 1000.0;
                        Speed speed;
                        Acceleration accel;
                        Length positionX;
                        Length positionY;
                        Direction dir;
                        if (this.gtuId.equals("Pedestrian"))
                        {
                            speed = this.startSpeed;
                            accel = Acceleration.ZERO;
                            positionX = this.startPositionX;
                            positionY = Length.instantiateSI(this.startPositionY.si + speed.si * dt);
                            dir = Direction.instantiateSI(Math.PI / 2.0);
                        }
                        else
                        {
                            if (dt < this.accelerationTime.si)
                            {
                                // phase one: constant acceleration
                                accel = this.acceleration;
                                speed = Speed.instantiateSI(this.startSpeed.si + dt * this.acceleration.si);
                                positionX = Length.instantiateSI(
                                        this.startPositionX.si + this.startSpeed.si * dt + .5 * this.acceleration.si * dt * dt);
                            }
                            else
                            {
                                // phase two: constant speed
                                accel = Acceleration.ZERO;
                                speed = Speed
                                        .instantiateSI(this.startSpeed.si + this.accelerationTime.si * this.acceleration.si);
                                positionX = Length.instantiateSI(this.startPositionX.si
                                        + this.startSpeed.si * this.accelerationTime.si
                                        + .5 * this.acceleration.si * this.accelerationTime.si * this.accelerationTime.si
                                        + (dt - this.accelerationTime.si) * speed.si);
                            }

                            // Add some noise to the Y-position
                            if (this.dy > -3.5 && positionX.si > 200.0)
                            {
                                this.dy -= 3.5 * (this.interval / 3000.0); // 3.5m in 3000ms
                            }
                            double delta = dt - this.wienerTime;
                            double tau = 30.0;
                            this.wiener = Math.exp(-delta / tau) * this.wiener
                                    + Math.sqrt((2 * delta) / tau) * this.random.nextGaussian();
                            this.wienerTime = dt;
                            positionY = Length.instantiateSI(this.startPositionY.si + this.wiener * 0.5 + this.dy);
                            dir = Direction.ZERO;
                        }

                        // Sent update
                        try
                        {
                            Object[] payload = new Object[] {this.gtuId, positionX, positionY, dir, speed, accel};
                            Worker.this.responder.send(Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM,
                                    "EXTERNAL", Worker.this.messageId++, payload), ZMQ.DONTWAIT);
                        }
                        catch (Sim0MQException | SerializationException e)
                        {
                            System.err.println("Stopping trajectory sender for GTU " + this.gtuId + " due to exception:");
                            e.printStackTrace();
                            this.active = false;
                        }
                    }

                    // Wait for next execution
                    long wait = this.executionTime - System.currentTimeMillis() + this.interval;
                    if (wait > 0)
                    {
                        try
                        {
                            Thread.sleep(wait);
                        }
                        catch (InterruptedException e)
                        {
                            System.err.println("Stopping trajectory sender for GTU " + this.gtuId + " due to exception:");
                            e.printStackTrace();
                            this.active = false;
                        }
                    }
                }
            }

            /**
             * Terminates sending trajectory updates.
             */
            public void terminate()
            {
                this.active = false;
            }
        }

    }

}
