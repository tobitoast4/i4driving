package org.opentrafficsim.i4driving.opendrive;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.opentrafficsim.i4driving.opendrive.generated.OpenDRIVE;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * OpenDRIVE parser, which can parse .xodr files, strings or other input stream that represents the same byte information.
 * @author wjschakel
 */
public final class OpenDriveParser
{

    /** Empty private constructor. */
    private OpenDriveParser()
    {
        //
    }

    /**
     * Parse OpenDrive XML (.xodr) input file and build OpenDRIVE object.
     * @param filename file name, including path.
     * @return OpenDRIVE object
     * @throws MalformedURLException if the file cannot be made in to a URL
     * @throws JAXBException when the parsing fails
     * @throws SAXException on error creating SAX parser
     * @throws ParserConfigurationException on error with parser configuration
     * @throws IOException if the file does no exist or is not accessible
     */
    public static OpenDRIVE parseXodr(final String filename)
            throws MalformedURLException, JAXBException, SAXException, ParserConfigurationException, IOException
    {
        return parseStream(new File(filename).toURI().toURL().openStream());
    }

    /**
     * Parse OpenDrive XML (.xodr) string and build OpenDRIVE object using UTF-8 character encoding.
     * @param string the xml string
     * @return OpenDRIVE object
     * @throws JAXBException when the parsing fails
     * @throws ParserConfigurationException on error with parser configuration
     * @throws SAXException on error creating SAX parser
     */
    public static OpenDRIVE parseString(final String string) throws JAXBException, SAXException, ParserConfigurationException
    {
        return parseString(string, StandardCharsets.UTF_8);
    }

    /**
     * Parse OpenDrive XML (.xodr) string and build OpenDRIVE object.
     * @param string the xml string
     * @param charset character set
     * @return OpenDRIVE object
     * @throws JAXBException when the parsing fails
     * @throws ParserConfigurationException on error with parser configuration
     * @throws SAXException on error creating SAX parser
     */
    public static OpenDRIVE parseString(final String string, final Charset charset)
            throws JAXBException, SAXException, ParserConfigurationException
    {
        return parseStream(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Parse OpenDrive XML (.xodr) input stream and build OpenDRIVE object.
     * @param xmlStream the xml stream
     * @return OpenDRIVE object
     * @throws JAXBException when the parsing fails
     * @throws ParserConfigurationException on error with parser configuration
     * @throws SAXException on error creating SAX parser
     */
    public static OpenDRIVE parseStream(final InputStream xmlStream)
            throws JAXBException, SAXException, ParserConfigurationException
    {
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        JAXBContext jc = JAXBContext.newInstance(OpenDRIVE.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setXIncludeAware(false);
        spf.setNamespaceAware(true);
        spf.setValidating(true);
        XMLReader xmlReader = spf.newSAXParser().getXMLReader();
        XMLFilterImpl xmlFilter = new XmlNamespaceFilter(xmlReader);
        xmlReader.setContentHandler(unmarshaller.getUnmarshallerHandler());
        SAXSource saxSource = new SAXSource(xmlFilter, new InputSource(xmlStream));
        OpenDRIVE result = (OpenDRIVE) unmarshaller.unmarshal(saxSource);
        Locale.setDefault(locale);
        return result;
    }

    /**
     * This class adds name space to elements, so .xodr that do not include the name space can still be parsed.
     */
    private static class XmlNamespaceFilter extends XMLFilterImpl
    {
        /**
         * Constructor.
         * @param xmlReader XML reader
         */
        XmlNamespaceFilter(final XMLReader xmlReader)
        {
            super(xmlReader);
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName,
                final Attributes attributes) throws SAXException
        {
            // Compensate for missing xmlns="http://code.asam.net/simulation/standard/opendrive_schema" in OpenDRIVE tag
            super.startElement("http://code.asam.net/simulation/standard/opendrive_schema", localName, qName, attributes);
        }
    }

}
