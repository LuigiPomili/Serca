package it.unibo.cs.swarch.serca.clientsideconnectionlibrary.SercaConnectionManager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import android.util.Base64;
import it.unibo.cs.swarch.protocol.simplexml.classes.Gameover;
import it.unibo.cs.swarch.protocol.simplexml.classes.Move;
import it.unibo.cs.swarch.protocol.simplexml.classes.OutgoingChatMessage;
import it.unibo.cs.swarch.protocol.simplexml.classes.Ping;
import it.unibo.cs.swarch.protocol.simplexml.classes.Unsubscription;
import it.unibo.cs.swarch.protocol.simplexml.translator.*;
//import sun.misc.BASE64Encoder;
import it.unibo.cs.swarch.sercamobile.SingletonUser;

/**
 *
 * @author tappof
 */
public class SercaConnectionManager {

    private static String uid = null;
    private static String pwd = null;
    
    private static PropertyChangeSupport[] pcss;
    private static ServerStreamingHandler[] streamingConnections;
    
    public static enum ConnectionsName {
        GLOBAL, GAME
    }
    
    
    public SercaConnectionManager(String _uid, String _pwd) {
        
            if(uid == null && pwd == null) {
                uid = _uid;
                pwd = _pwd;
            }
            
            if(pcss == null)
                pcss = new PropertyChangeSupport[2];
            
            if(streamingConnections == null)
                streamingConnections = new ServerStreamingHandler[2];
            
    }
    
    public boolean addYourListenerToStreamingConnection(PropertyChangeListener listener, ConnectionsName context) {
        if(pcss[context.ordinal()] != null)
                pcss[context.ordinal()].addPropertyChangeListener(listener);
        else    return false;
        
        return true;
    }
    
    public boolean removeYourListenerFromStreamingConnection(PropertyChangeListener listener, ConnectionsName context) {
        if(pcss[context.ordinal()] != null)
                pcss[context.ordinal()].removePropertyChangeListener(listener);
        else    return false;
        
        return true;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String _pwd) {
        pwd = _pwd;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String _uid) {
        uid = _uid;
    }

    public Integer requestServerStreamingService(String _url, Object _request, ConnectionsName context, PropertyChangeListener listener) throws IOException {

        if(pcss[context.ordinal()] != null)
            return -1;
        
        String xmlrequest = SimpleXmlProtocolTranslator.fromObjToXml(_request);

        if (!SimpleXmlProtocolTranslator.validateXml(xmlrequest)) {
            return null;
        }

        URL url;
        HttpURLConnection con;

        url = new URL(_url);
        con = (HttpURLConnection) url.openConnection();

        con.setConnectTimeout(4 * 1000);
        con.setUseCaches(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");

        String clearAuth = uid + ":" + pwd;
        String encodedAuth = Base64.encodeToString(clearAuth.getBytes(), Base64.DEFAULT);
        encodedAuth = encodedAuth.substring(0, encodedAuth.length() - 1);
        con.setRequestProperty("Authorization", "Basic " + encodedAuth);

        String data = URLEncoder.encode("xml", "UTF-8") + "=" + URLEncoder.encode(xmlrequest, "UTF-8");

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();

        if (con.getResponseCode() == 200) {
            ServerStreamingHandler t = new ServerStreamingHandler(con, context, listener);
            
            System.out.println(context.toString() + " " + context.ordinal() + " " + t);
            
            streamingConnections[context.ordinal()] = t;
            
            new Thread(t).start();
        }

        return con.getResponseCode();
    }

    //return null se c'è un errore di connessione e lo stato è diverso da 200
    public SingleRequestReplyResponse singleRequestReplyService(String _url, Object _request, PropertyChangeListener listener) throws ProtocolException, IOException {

        String xmlrequest = SimpleXmlProtocolTranslator.fromObjToXml(_request);

        if (SimpleXmlProtocolTranslator.validateXml(xmlrequest) == false) {
            return null;
        }

        URL url = new URL(_url);

        // Construct data
        String data = URLEncoder.encode("xml", "UTF-8") + "=" + URLEncoder.encode(xmlrequest, "UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String clearAuth = uid + ":" + pwd;
        String encodedAuth = Base64.encodeToString(clearAuth.getBytes(), Base64.DEFAULT);
        encodedAuth = encodedAuth.substring(0, encodedAuth.length() - 1);
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

        //send
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();

        // Get the http response, prepare application response obj
        SingleRequestReplyResponse response = new SingleRequestReplyResponse(conn.getResponseCode(), null);

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String reply = rd.readLine();
        rd.close();

        if (response.getHttpStatusCode() != 200) {
            return response;
        }

        if (reply != null && SimpleXmlProtocolTranslator.validateXml(reply)) {
            response.setReturnedData(SimpleXmlProtocolTranslator.fromXmlToObj(reply));
            
            PropertyChangeSupport pcs = new PropertyChangeSupport(this);
            pcs.addPropertyChangeListener(listener);
            pcs.firePropertyChange("SINGLEREQUEST", null, SimpleXmlProtocolTranslator.fromXmlToObj(reply));
            
        }

        return response;
    }
    
    public void closeStreamingConnections(ConnectionsName context, String serverurl) {
        if(streamingConnections[context.ordinal()] != null)
            streamingConnections[context.ordinal()].exit(serverurl);
        
        if(streamingConnections[ConnectionsName.GAME.ordinal()] == null && streamingConnections[ConnectionsName.GLOBAL.ordinal()] == null) {
            pwd = uid = null;
        }
    }

    public class SingleRequestReplyResponse {

        private int httpStatusCode;
        private Object returnedData;

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

        public void setHttpStatusCode(int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
        }

        public Object getReturnedData() {
            return returnedData;
        }

        public void setReturnedData(Object returnedData) {
            this.returnedData = returnedData;
        }

        public SingleRequestReplyResponse(int httpStatusCode, Object returnedData) {
            this.httpStatusCode = httpStatusCode;
            this.returnedData = returnedData;
        }
    }

    private class ServerStreamingHandler implements Runnable{

        HttpURLConnection con;
        PropertyChangeSupport pcs;
        boolean run = true;
        ConnectionsName cn;
        BufferedReader rd;
        
        public ServerStreamingHandler(HttpURLConnection conn, ConnectionsName context, PropertyChangeListener listener) {
            this.pcs = new PropertyChangeSupport(this);
            this.con = conn;
            this.pcs.addPropertyChangeListener(listener);
            this.cn = context;
            pcss[context.ordinal()] = this.pcs;
        }
        
        public void exit(String serverurl) {
            run = false;
            Ping p = new Ping();
            p.setUid(uid);
            try {
				singleRequestReplyService(serverurl + "admintables", p, null);
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }

        //qui c'era un overide
        public void run(){
            String line = "";
            rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(con.getInputStream()));

                do {
                    
                    try {
                    	line = rd.readLine();
                    } catch(Exception ex) {
                        System.err.println(ex.getMessage());
                        ex.printStackTrace();
                        line = null;
                    }
                    
                    if (line == null) {
                        break;
                    }
                    
                    pcs.firePropertyChange(cn.toString(), null, SimpleXmlProtocolTranslator.fromXmlToObj(line));
                	if (SimpleXmlProtocolTranslator.fromXmlToObj(line) instanceof Gameover)
                		break;
                } while (run);

                
                con.disconnect();
                pcss[cn.ordinal()] = null;
                streamingConnections[cn.ordinal()] = null;
            } catch (Exception ex) {
                Logger.getLogger(SercaConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
