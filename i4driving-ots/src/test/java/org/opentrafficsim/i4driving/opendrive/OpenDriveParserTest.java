package org.opentrafficsim.i4driving.opendrive;

import java.awt.Dimension;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.core.dsol.OtsAnimator;
import org.opentrafficsim.draw.OtsDrawingException;
import org.opentrafficsim.swing.gui.OtsAnimationPanel;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.language.DsolException;

/**
 * Test class for OpenDRIVE parser.
 * @author wjschakel
 */
public final class OpenDriveParserTest
{

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
        OpenDriveModel model = new OpenDriveModel(sim, "/opendrive/examples/UC_Motorway-Exit-Entry.xodr");
        sim.initialize(Time.ZERO, Duration.ZERO, Duration.instantiateSI(3600.0), model);

        OtsAnimationPanel animationPanel = new OtsAnimationPanel(model.getNetwork().getExtent(), new Dimension(800, 600), sim,
                model, OpenDriveModel.COLORER, model.getNetwork());
        OtsSimulationApplication<OpenDriveModel> app = new OtsSimulationApplication<>(model, animationPanel);
        app.setExitOnClose(true);

        animationPanel.enableSimulationControlButtons();
    }

}
