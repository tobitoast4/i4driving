package org.opentrafficsim.i4driving.sim0mq;

import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import org.djunits.unit.AccelerationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.network.*;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.strategical.RouteGenerator;
import org.opentrafficsim.road.network.lane.LanePosition;

public class ArrivalSynchronizer {

    private RouteGenerator routeGenerator;
    private Network network;
    private Node targetNode;

    public ArrivalSynchronizer(Network network, Node targetNode) {
        this.routeGenerator = RouteGenerator.getDefaultRouteSupplier(new MersenneTwister(12345), LinkWeight.LENGTH_NO_CONNECTORS);
        this.network = network;
        this.targetNode = targetNode;
    }

    public Acceleration getRecommendedAVAcceleration(LaneBasedGtu avGtu,LaneBasedGtu userGtu, Acceleration userA, Speed userV)
            throws GtuException {
        // This should be used if AV is controlled by OTS
        RouteGenerator routeGenerator = RouteGenerator.getDefaultRouteSupplier(new MersenneTwister(12345), LinkWeight.LENGTH_NO_CONNECTORS);
        double v0 = userV.getSI();
        double a = userA.getSI();
        if (v0 <= 0) {  // Stop AV if user is also not moving
            return new Acceleration(-6, AccelerationUnit.METER_PER_SECOND_2);
        }

        // Calculate user's distance to target
        Length userDistToTarget = this.getDistToTarget(userGtu);
        if (userDistToTarget == null) {
            return new Acceleration(0, AccelerationUnit.METER_PER_SECOND_2);
        }

        // Calculate AV's distance to target
        Length avDistToTarget = this.getDistToTarget(avGtu);
        if (avDistToTarget == null) {
            return new Acceleration(0, AccelerationUnit.METER_PER_SECOND_2);
        }

        double s = userDistToTarget.getSI();
        Speed avSpeed = avGtu.getSpeed();
//        double userTimeToArrival = s / v0;  // simple approach (does not consider acceleration of user GTU
        double userTimeToArrival = getTimeToArrival(a, v0, s);

        double avAcceleration = 2 * (avDistToTarget.getSI() - (avSpeed.getSI()*userTimeToArrival)) / (userTimeToArrival*userTimeToArrival);
        if (Double.isNaN(avAcceleration)) {
            return new Acceleration(0, AccelerationUnit.METER_PER_SECOND_2);
        }
        return new Acceleration(avAcceleration, AccelerationUnit.METER_PER_SECOND_2);
    }

    public Speed getRecommendedAVSpeed(LaneBasedGtu avGtu,LaneBasedGtu userGtu, Acceleration userA, Speed userV)
            throws GtuException {
        // This should be used if AV is controlled by OTS
        double v0 = userV.getSI();
        double a = userA.getSI();
        if (v0 <= 0) { // AV should not move if user is also not moving
            return new Speed(0, SpeedUnit.METER_PER_SECOND);
        }

        // Calculate user's distance to target
        Length userDistToTarget = getDistToTarget(userGtu);
        if (userDistToTarget == null) {
            return new Speed(0, SpeedUnit.METER_PER_SECOND);
        }

        // Calculate AV's distance to target
        Length avDistToTarget = getDistToTarget(avGtu);
        if (avDistToTarget == null) {
            return new Speed(0, SpeedUnit.METER_PER_SECOND);
        }

        double s = userDistToTarget.getSI();
//        double userTimeToArrival = s / v0;  // simple approach (does not consider acceleration of user GTU
        double userTimeToArrival = getTimeToArrival(a, v0, s);

        double recommendedSpeed = avDistToTarget.getSI() / userTimeToArrival;
        return new Speed(recommendedSpeed, SpeedUnit.METER_PER_SECOND);
    }

    private Length getDistToTarget(LaneBasedGtu gtu) {
        // Calculate AV's distance to target
        LanePosition lanePosition;
        try {
            lanePosition = gtu.getReferencePosition();
        } catch (GtuException e) {
            return null;
        }
        Node routeStart = lanePosition.lane().getLink().getStartNode();
        Route route = this.routeGenerator.getRoute(routeStart, this.targetNode, DefaultsNl.CAR);
        return this.calculateRouteLength(route).minus(lanePosition.position());
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
