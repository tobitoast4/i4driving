package org.opentrafficsim.i4driving.sim0mq;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Direction;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.serialization.SerializationException;
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

    /** Whether to spawn test GTUs. */
    private static final boolean SPAWN_GTUS = false;

    /** Port number. */
    private static final int PORT = 5556;

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

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            this.context = new ZContext(1);
            this.responder = this.context.createSocket(SocketType.CHANNEL);
            this.responder.connect("tcp://*:" + PORT);
            System.out.println("ExternalSim is running");

            try
            {
                byte[] encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "NETWORK",
                        this.messageId++, new Object[] {});
                this.responder.send(encodedMessage, 0);
                System.out.println("ExternalSim sent NETWORK message");

                if (SPAWN_GTUS)
                {
                    Speed gtuSpeed = new Speed(50, SpeedUnit.KM_PER_HOUR);
                    try
                    {
                        Object[] payload = new Object[] {"Fellow 1", false, Length.instantiateSI(20.0),
                                Length.instantiateSI(1.75), Direction.ZERO, gtuSpeed, "CAR", Length.instantiateSI(4.0),
                                Length.instantiateSI(3.0), 0, "Route..."};
                        Worker.this.responder.send(Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM,
                                "VEHICLE", Worker.this.messageId++, payload), 0);
                        payload = new Object[] {"Fellow 2", false, Length.instantiateSI(10.0), Length.instantiateSI(1.75),
                                Direction.ZERO, gtuSpeed, "CAR", Length.instantiateSI(4.0), Length.instantiateSI(3.0), 0,
                                "Route..."};
                        Worker.this.responder.send(Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, OTS, EXTERNAL_SIM,
                                "VEHICLE", Worker.this.messageId++, payload), 0);
                    }
                    catch (Sim0MQException | SerializationException ex)
                    {
                        ex.printStackTrace();
                    }
                }

                // TODO we need reply messages sometimes, so ExternalSim knows OTS is ready
                Thread.sleep(5000);

                encodedMessage = Sim0MQMessage.encodeUTF8(BIG_ENDIAN, FEDERATION, EXTERNAL_SIM, OTS, "START", this.messageId++,
                        new Object[] {});
                this.responder.send(encodedMessage, 0);
                System.out.println("ExternalSim sent START message");

                while (!Thread.currentThread().isInterrupted())
                {
                    // Wait for next request from the client
                    byte[] request = this.responder.recv(ZMQ.DONTWAIT);
                    while (request == null)
                    {
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
                    Sim0MQMessage message = Sim0MQMessage.decode(request);
                    if ("PLAN".equals(message.getMessageTypeId()))
                    {
                        // String id
                        Object[] payload = message.createObjectArray();
                        String id = (String) payload[8];
                        System.out.println("ExternalSim received PLAN message for GTU " + id);
                    }
                    else if ("DELETE".equals(message.getMessageTypeId()))
                    {
                        // String id
                        Object[] payload = message.createObjectArray();
                        String id = (String) payload[8];
                        System.out.println("ExternalSim received DELETE message for GTU " + id);
                    }
                }
            }
            catch (Sim0MQException | SerializationException | InterruptedException e)
            {
                e.printStackTrace();
            }

            this.responder.close();
            this.context.destroy();
            this.context.close();
            System.out.println("ExternalSim terminated");
            System.exit(0);
        }

    }
}
