package org.opentrafficsim.i4driving.sim0mq;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XIncludeTest {
    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setXIncludeAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(new File("C:\\GitHub\\i4driving\\i4driving-ots\\src\\main\\resources\\main.xml"));
        System.out.println("Parsed with XInclude!");
    }
}
