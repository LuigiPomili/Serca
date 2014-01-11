package it.unibo.cs.swarch.protocol.simplexml.translator;

import it.unibo.cs.swarch.protocol.simplexml.classes.ObjectFactory;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class SimpleXmlProtocolTranslator {

	public static enum CreateObj {
        LOGINREPLY, REGISTRATIONREPLY, CREATETABLEREPLY
    }
	
	//Simple XML variables
	private static Serializer s = null;
    private static final ObjectFactory of = new ObjectFactory();
    
    private SimpleXmlProtocolTranslator() {
    }

    static synchronized void initProtocolTranslator() {
        try {
        	
        	if (s == null) {
        		//Simple XML init
        		s = new Persister();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            Logger.getLogger(SimpleXmlProtocolTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public synchronized static Object fromXmlToObj(String xml) {
    	
    	try{
    		initProtocolTranslator();
    	}catch(Exception e){
    		System.out.println("exception");
    	}
    	Object obj = null;
        
        xml = xml.substring(xml.indexOf("ns2:") + 4, xml.length());
        xml = "<" + xml;
        String tmp = " xmlns:ns2=\"http://www.serca.org/protocol\"";
        xml = xml.replaceAll(tmp, "");
        String classname = xml.substring(xml.indexOf("<") + 1, xml.indexOf(">"));
        if (!(classname.contains("usersList") || classname.contains("tablesList")))
        	System.out.println("ok");
        	
        xml = xml.replaceAll("</ns2:" + classname + ">", "</" + classname + ">");
        xml = xml.replaceFirst(">", ">\n   ");
        
        //parso gli argomenti
        int lastindex = xml.indexOf(">\n   ") + 5;
        int lastargumentindex = xml.indexOf("</" + classname + ">");
        if (!xml.equals("<" + classname + ">\n   ")){
        	String xmllistofarguments = xml.substring(lastindex, lastargumentindex);
        	//non ci sono argomenti
        	if (xmllistofarguments.length() == 0){
        			xml = xml.substring(0, lastindex) + "</" + classname + ">";
        	}else
        	//c'e' un solo argomento ma non e' messo come parametro
        	if (xmllistofarguments.indexOf(">") == -1){
        		xml = xml.substring(0, lastindex) + "<value>" + xmllistofarguments + "</value>\n"
        				+ "</" + classname + ">";
        	
        	}else{
        		//c'e' almeno un elemento parametrizzato
        		xml = xml.replaceAll("><", ">\n   <");
        		xml = xml.replace("   </" + classname + ">", "</" + classname + ">");
        		//xml.replaceAll("</ns2:" + classname + ">", "</" + classname + ">");
                if(classname.equals("tablesList") || classname.equals("usersList")){
                	int firstlinebeginindex = xml.indexOf("   <") + 3;
                	int firstlineendindex = xml.indexOf(">\n", firstlinebeginindex);
                	xml = xml.substring(0, firstlinebeginindex) + xml.substring(firstlinebeginindex, firstlineendindex)
                			+ " class=\"java.util.ArrayList\">" + xml.substring(firstlinebeginindex - 4, xml.indexOf("</" + classname + ">"))
                			+ xml.substring(firstlinebeginindex - 1, firstlineendindex + 1).replace("<", "</") + "\n</" + classname + ">" ;
                }
                if(classname.equals("createTableReply")){
                	xml = xml.replaceAll("card>", "string>");
                	int index0 = xml.indexOf("<string>");
                	int index1 = xml.indexOf("</cardsList>");
                	xml = xml.substring(0, index0) + "<card class=\"java.util.ArrayList\">\n   " + xml.substring(index0, index1)
                			+ "</card>\n   " + xml.substring(index1, xml.length());
                }
                if(classname.equals("subscriptionReply")){
                	xml = xml.replaceFirst("<subscribedUser>", "<subscribedUser class=\"java.util.ArrayList\">\n   <subscribedUser>");
                	xml = xml.replaceAll("<cardsList>", "<cardsList>\n   <card class=\"java.util.ArrayList\">");
                	xml = xml.replaceAll("<card>", "<string>");
                	xml = xml.replaceAll("</card>", "</string>");
                	int index0 = xml.indexOf("</cardsList>", 0);
                	do{
                		xml = xml.substring(0, index0) + "</card>\n   " + xml.substring(index0, xml.length());
                		index0 = xml.indexOf("</cardsList>", index0 + 15);
                	}while(index0 != -1);
                	xml = xml.replace("</subscribedUser>\n   </usersAlreadySubscribed>",
                						"</subscribedUser>\n   </subscribedUser>" +
                						"\n   </usersAlreadySubscribed>");
                	
                }
        	}
        }else{
        	classname = classname.substring(0, classname.length() - 1);
        	xml = xml.replace(">\n   ", ">");
        }
        try {
        	classname = "it.unibo.cs.swarch.protocol.simplexml.classes." + Character.toString(classname.charAt(0)).toUpperCase() + classname.substring(1);
			obj = s.read(Class.forName(classname), xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return obj;
    }

    public synchronized static String fromObjToXml(Object obj) {
        
    	try{
    		initProtocolTranslator();
    	}catch(Exception e){
    		System.out.println("exception");
    	}
        
        StringWriter sw = new StringWriter();
        try {
			s.write(obj, sw);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String result = sw.toString();
        //From SimpleXML to JAXB
        String objclass = result.substring(result.indexOf("<") + 1, result.indexOf(">"));
        result = result.replaceAll("\n   ", "");
        result = result.replaceAll("\n", "");
        result = result.substring(1);
        result = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:" + result;
        int i = result.indexOf(objclass);
        result = result.substring(0, i + objclass.length())
        		+ " xmlns:ns2=\"http://www.serca.org/protocol\""
        		+ result.substring(i + objclass.length(), result.length());
        result = result.substring(0, result.indexOf("/" + objclass + ">"))
        		+ "/ns2:" + result.substring(result.indexOf("/" + objclass + ">") + 1, result.length());
        
        return result + "\n";
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
        return true;

    }
	
}
