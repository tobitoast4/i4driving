package org.opentrafficsim.i4driving.opendrive;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.opentrafficsim.i4driving.opendrive.generated.OpenDRIVE;
import org.xml.sax.SAXException;

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
     * @throws MalformedURLException
     * @throws IOException
     * @throws JAXBException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static void main(final String[] args)
            throws MalformedURLException, IOException, JAXBException, SAXException, ParserConfigurationException
    {
        InputStream stream =
                OpenDriveParserTest.class.getResource("/opendrive/examples/i4Driving_scenario28_motorway.xodr").openStream();
        OpenDRIVE openDrive = OpenDriveParser.parseStream(stream);
    }

}
