package org.opentrafficsim.i4driving.sim0mq;

import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import org.djunits.unit.AccelerationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.network.*;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.strategical.RouteGenerator;
import org.opentrafficsim.road.network.lane.LanePosition;

public class AccelerationRecommender {

    private Network network;
    private Node targetNode;

    public AccelerationRecommender(Network network, Node targetNode) {
        this.network = network;
        this.targetNode = targetNode;
    }

    public Acceleration getRecommendedAVAcceleration(LaneBasedGtu avGtu,LaneBasedGtu userGtu, Acceleration userA, Speed userV)
            throws GtuException {
        RouteGenerator routeGenerator = RouteGenerator.getDefaultRouteSupplier(new MersenneTwister(12345), LinkWeight.LENGTH_NO_CONNECTORS);
        double v0 = userV.getSI();
        double a = userA.getSI();
        if (v0 <= 0) {
            // Stop AV if user is also not moving
            return new Acceleration(-6, AccelerationUnit.METER_PER_SECOND_2);
        }

        // Calculate user's distance to target
        LanePosition userLanePosition;
        try {
            userLanePosition = userGtu.getReferencePosition();
        } catch (GtuException e) {
            return new Acceleration(0, AccelerationUnit.METER_PER_SECOND_2);
        }
        Node userRouteStart = userLanePosition.lane().getLink().getStartNode();
        Route userRoute = routeGenerator.getRoute(userRouteStart, this.targetNode, DefaultsNl.CAR);
        Length userDistToTarget = this.calculateRouteLength(userRoute).minus(userLanePosition.position());

        // Calculate AV's distance to target
        LanePosition avLanePosition;
        try {
            avLanePosition = avGtu.getReferencePosition();
        } catch (GtuException e) {
            return new Acceleration(0, AccelerationUnit.METER_PER_SECOND_2);
        }
        Node avRouteStart = avLanePosition.lane().getLink().getStartNode();
        Route avRoute = routeGenerator.getRoute(avRouteStart, this.targetNode, DefaultsNl.CAR);
        Length avDistToTarget = this.calculateRouteLength(avRoute).minus(avLanePosition.position());

        double s = userDistToTarget.getSI();
        Speed avSpeed = avGtu.getSpeed();
        double userTimeToArrival = s / v0;
//        double timeToArrival = getTimeToArrival(a, v0, s);

        double avAcceleration = 2 * (avDistToTarget.getSI() - (avSpeed.getSI()*userTimeToArrival)) / (userTimeToArrival*userTimeToArrival);
        if (Double.isNaN(avAcceleration)) {
            return new Acceleration(0, AccelerationUnit.METER_PER_SECOND_2);
        }
        return new Acceleration(avAcceleration, AccelerationUnit.METER_PER_SECOND_2);
    }

    private double getTimeToArrival(double a, double v0, double s) {
        double timeToArrival;
        if (Math.abs(a) < 1e-4) { // ~0 acceleration, fall back to basic
            timeToArrival = s / v0;
        } else {
            double discriminant = v0 * v0 + 2 * a * s;
            if (discriminant < 0) {
                timeToArrival = Double.POSITIVE_INFINITY; // can't reach
            } else {
                timeToArrival = (-v0 + Math.sqrt(discriminant)) / a;
                if (timeToArrival < 0) {
                    timeToArrival = (-v0 - Math.sqrt(discriminant)) / a; // fallback to other root
                }
            }
        }
        return timeToArrival;
    }

    private Length calculateRouteLength(Route route) {
        Length length = new Length(0, LengthUnit.METER);
        for (int i = 0; i < route.getNodes().size()-1; i++) {
            Node nodeA = null;
            Node nodeB = null;
            try {
                nodeA = route.getNode(i);
                nodeB = route.getNode(i+1);
            } catch (NetworkException e) {
                throw new RuntimeException(e);
            }
            Link link = this.network.getLink(nodeA, nodeB);
            length = length.plus(link.getLength());
        }
        return length;
    }

}
