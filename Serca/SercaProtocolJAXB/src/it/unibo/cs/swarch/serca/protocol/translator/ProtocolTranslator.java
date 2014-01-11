/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.protocol.translator;

import it.unibo.cs.swarch.serca.protocol.jaxb.*;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author tappof
 */
public class ProtocolTranslator {

    public static enum CreateObj {

        LOGINREPLY, REGISTRATIONREPLY, CREATETABLEREPLY
    }
    //JAXB context variable
    private static final ObjectFactory of = new ObjectFactory();
    private static JAXBContext ctx = null;
    private static Unmarshaller um = null;
    private static Marshaller m = null;
    private static SAXBuilder builder;

    private ProtocolTranslator() {
    }

    static synchronized void initProtocolTranslator() {
        try {
            if (ctx == null) {
                //JAXB Init
                ctx = JAXBContext.newInstance(ObjectFactory.class);
                um = ctx.createUnmarshaller();
                m = ctx.createMarshaller();

                //VALIDATION TOOLS
                String ns2 = "http://www.serca.org/protocol";
                String xsd2 = ProtocolTranslator.class.getResource("/it/unibo/cs/swarch/serca/protocol/xsd/sercaProtocol.xsd").toURI().toString();
                
                builder = new SAXBuilder(true);
                builder.setFeature("http://apache.org/xml/features/validation/schema", true);
                builder.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", ns2 + " " + xsd2);
                builder.setValidation(true);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(ProtocolTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ProtocolTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public synchronized static Object fromXmlToObj(String xml) {

        initProtocolTranslator();

        Object obj = null;
        try {
            obj = um.unmarshal(new StreamSource(new StringReader(xml)));
            if (obj instanceof JAXBElement) {
                return (Object) (((JAXBElement<String>) obj).getValue());
            } else {
                return obj;
            }
        } catch (JAXBException ex) {
            Logger.getLogger(ProtocolTranslator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public synchronized static String fromObjToXml(Object obj) {

        initProtocolTranslator();

        StringWriter sw = new StringWriter();
        try {
            m.marshal(obj, sw);
            String result = sw.toString();
            return result + "\n";
        } catch (JAXBException ex) {
            Logger.getLogger(ProtocolTranslator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public synchronized static String fromObjToXml(Object obj, CreateObj obtain) {

        initProtocolTranslator();

        switch (obtain) {
            case LOGINREPLY:
                return fromObjToXml(of.createLoginReply((String) obj));
            case REGISTRATIONREPLY:
                return fromObjToXml(of.createRegistrationReply((String) obj));
            default:
                return fromObjToXml(obj);
        }
    }

    public synchronized static boolean validateXml(String xmlString) {

        initProtocolTranslator();

        try {
            builder.build(new InputSource(new StringReader(xmlString)));
            return true;
        } catch (JDOMException ex) {
            Logger.getLogger(ProtocolTranslator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(ProtocolTranslator.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
