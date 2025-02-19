package org.opentrafficsim.i4driving.opendrive;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.animation.DefaultAnimationFactory;
import org.opentrafficsim.animation.gtu.colorer.DefaultSwitchableGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.core.dsol.AbstractOtsModel;
import org.opentrafficsim.core.dsol.OtsAnimator;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.network.Network;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.draw.OtsDrawingException;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.swing.gui.OtsAnimationPanel;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;
import org.xml.sax.SAXException;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.language.DsolException;

/**
 * Test class for OpenDRIVE parser.
 * @author wjschakel
 */
public final class OpenDriveParserTest
{

    private static final GtuColorer COLORER = new DefaultSwitchableGtuColorer();

    /** Empty private constructor. */
    private OpenDriveParserTest()
    {
        //
    }

    /**
     * Tester.
     * @param args arguments
     * @throws NamingException
     * @throws SimRuntimeException
     * @throws DsolException
     * @throws RemoteException
     * @throws OtsDrawingException
     */
    public static void main(final String[] args)
            throws SimRuntimeException, NamingException, RemoteException, DsolException, OtsDrawingException
    {
        OtsAnimator sim = new OtsAnimator("animator");
        OpenDriveModel model = new OpenDriveModel(sim);
        sim.initialize(Time.ZERO, Duration.ZERO, Duration.instantiateSI(3600.0), model);

        OtsAnimationPanel animationPanel = new OtsAnimationPanel(model.getNetwork().getExtent(), new Dimension(800, 600), sim,
                model, COLORER, model.getNetwork());
        OtsSimulationApplication<OpenDriveModel> app = new OtsSimulationApplication<>(model, animationPanel);
        app.setExitOnClose(true);

        animationPanel.enableSimulationControlButtons();
    }

    private static class OpenDriveModel extends AbstractOtsModel
    {

        private RoadNetwork network;

        public OpenDriveModel(OtsSimulatorInterface simulator)
        {
            super(simulator);
        }

        @Override
        public Network getNetwork()
        {
            return this.network;
        }

        @Override
        public void constructModel() throws SimRuntimeException
        {
            try
            {
                InputStream stream = OpenDriveParserTest.class
                        .getResource("/opendrive/examples/UC_Motorway-Exit-Entry.xodr").openStream();
                OpenDriveParser parser = OpenDriveParser.parseStream(stream);
                this.network = new RoadNetwork("roadNetwork", getSimulator());
                parser.build(this.network);
                DefaultAnimationFactory.animateXmlNetwork(this.network, COLORER);
            }
            catch (IOException | JAXBException | SAXException | ParserConfigurationException | NetworkException
                    | OtsGeometryException | OtsDrawingException ex)
            {
                throw new SimRuntimeException(ex);
            }
        }

    }

}
