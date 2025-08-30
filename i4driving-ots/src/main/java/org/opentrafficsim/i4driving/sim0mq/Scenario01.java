package org.opentrafficsim.i4driving.sim0mq;

import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.io.URLResource;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.gtu.GtuCharacteristics;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.LinkWeight;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristics;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.gtu.lane.VehicleModel;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.gtu.strategical.RouteGenerator;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.factory.xml.XmlParserException;
import org.opentrafficsim.road.network.factory.xml.parser.XmlParser;
import org.opentrafficsim.road.od.Category;
import org.opentrafficsim.trafficcontrol.TrafficControlException;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Defines a simple two-lane test network to develop OtsTransceiver.
 * @author wjschakel
 * @author Bramin Ramachandra Ravi Kiran
 */
public final class Scenario01 implements Sim0mqSimulation
{

    /** Network. */
    private final RoadNetwork network;

    /** GTU characteristics generator. */
    private final LaneBasedGtuCharacteristicsGeneratorOd charateristicsGeneratorOd;

    /** Parameter factory. */
    private ParameterFactorySim0mq parameterFactory;

    /**
     * Constructor.
     * @param simulator simulator
     * @param tacticalFactory tactical planner factory
     * @throws NetworkException
     * @throws OtsGeometryException
     * @throws GtuException
     */
    public Scenario01(final OtsSimulatorInterface simulator, final ScenarioTacticalPlannerFactory tacticalFactory)
            throws GtuException, OtsGeometryException, NetworkException
    {
        URL xmlURL = URLResource.getResource("/Scenario01.xml");
        this.network = new RoadNetwork("SilabMap", simulator);
        try {
            new XmlParser(this.network).setUrl(xmlURL).build();
        } catch (JAXBException | URISyntaxException | XmlParserException
                 | SAXException | ParserConfigurationException | IOException | TrafficControlException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Network created");

        // Model
        StreamInterface stream = simulator.getModel().getStream("generation");
        tacticalFactory.setStream(stream);
        // LaneBasedTacticalPlannerFactory<?> tacticalFactory = mixinModel.getTacticalPlanner(stream);
        this.parameterFactory = new ParameterFactorySim0mq();
        this.parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.VGAIN, new Speed(35.0, SpeedUnit.KM_PER_HOUR));

        Node nodeA = this.network.getNode("cp0-lane0-0");
        Node nodeB = this.network.getNode("cp9-lane0-1");
        RouteGenerator routeGenerator = RouteGenerator.getDefaultRouteSupplier(new MersenneTwister(12345), LinkWeight.LENGTH_NO_CONNECTORS);
        Route route = routeGenerator.getRoute(nodeA, nodeB, DefaultsNl.CAR);

        // GTU characteristics generator
        GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);
        GtuCharacteristics gtucharacteristics = GtuType.defaultCharacteristics(DefaultsNl.CAR, this.network, stream);
        LaneBasedStrategicalRoutePlannerFactory stratFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory, this.parameterFactory);
        this.charateristicsGeneratorOd = new LaneBasedGtuCharacteristicsGeneratorOd()
        {
            @Override
            public LaneBasedGtuCharacteristics draw(final Node origin, final Node destination, final Category category,
                    final StreamInterface randomStream) throws GtuException
            {
                return new LaneBasedGtuCharacteristics(gtucharacteristics, stratFactory, route, nodeA, nodeB,
                        VehicleModel.MINMAX);
            }
        };

    }

    @Override
    public RoadNetwork getNetwork()
    {
        return this.network;
    }

    @Override
    public LaneBasedGtuCharacteristicsGeneratorOd getGtuCharacteristicsGeneratorOd()
    {
        return this.charateristicsGeneratorOd;
    }

    @Override
    public ParameterFactorySim0mq getParameterFactory()
    {
        return this.parameterFactory;
    }

}
