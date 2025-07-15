package org.opentrafficsim.i4driving.sim0mq;


import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import org.djunits.unit.AccelerationUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.Point2d;
import org.djutils.event.EventListener;
import org.djutils.io.URLResource;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.AbstractOtsModel;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlan;
import org.opentrafficsim.core.gtu.plan.operational.Segments;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlusFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.DefaultLmrsPerceptionFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.LmrsFactory;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalPlanner;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalPlannerFactory;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.factory.xml.parser.XmlParser;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LanePosition;

import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static org.djunits.unit.LengthUnit.METER;
import static org.djunits.unit.SpeedUnit.KM_PER_HOUR;

public class OtsScenarioModel extends AbstractOtsModel
{
    /** */
    private static final long serialVersionUID = 20170407L;
    /** The network. */
    private RoadNetwork network;

    /**
     * Constructor.
     * @param simulator the simulator
     */
    public OtsScenarioModel(final OtsSimulatorInterface simulator)
    {
        super(simulator);
    }

    /**
     * Set network.
     * @param network set network.
     */
    public void setNetwork(final RoadNetwork network)
    {
        this.network = network;
    }

    @Override
    public void constructModel() throws SimRuntimeException
    {
        try
        {
            URL xmlURL = URLResource.getResource("/resources/OtsScenario.xml");
            this.network = new RoadNetwork("OtsScenario", getSimulator());
            new XmlParser(this.network).setUrl(xmlURL).build();
            System.out.println("Network created");
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public RoadNetwork getNetwork()
    {
        return this.network;
    }

    private Lane getLane(final CrossSectionLink link, final String id)
    {
        return (Lane) link.getCrossSectionElement(id);
    }

}
