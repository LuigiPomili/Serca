/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.servlet.chat.comethandlers;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import com.sun.grizzly.comet.CometEvent;
import com.sun.grizzly.comet.CometHandler;

import it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.Gameover;
import it.unibo.cs.swarch.serca.protocol.jaxb.IncomingChatMessage;
import it.unibo.cs.swarch.serca.protocol.jaxb.Ping;
import it.unibo.cs.swarch.serca.protocol.jaxb.TablesList;
import it.unibo.cs.swarch.serca.protocol.jaxb.UsersList;
import it.unibo.cs.swarch.serca.protocol.translator.ProtocolTranslator;
import it.unibo.cs.swarch.serca.servlet.game.admin.comethandlers.GameCometHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author tappof
 */
public class ChatCometHandler implements CometHandler<HttpServletResponse> {

    TablesManagerLocal tablesManager = lookupTablesManagerLocal();
    UserManagementLocal userManagement = lookupUserManagementLocal();
    
    private HttpServletResponse response = null;
    private List<String> contexts = new ArrayList<String>();
    private String uid = "";
    Logger logger;
    
    public ChatCometHandler(String _context, HttpServletResponse _response, String _userId) {
        super();
        this.contexts.add(_context);
        this.response = _response;
        this.uid = _userId;
        this.logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.finest("New ChatCometHandler created for user " + uid);
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getContext(int i) {
        return contexts.get(i);
    }

    public void setContext(String context, int i) {
        this.contexts.set(i, context);
    }
    
    public void removeContext(String context) {
        this.contexts.remove(context);
    }

    public void addContext(String context) {
        this.contexts.add(context);
    }

    public int getContextsNo() {
        return this.contexts.size();
    }

    public String getUserId() {
        return uid;
    }

    public void setUserId(String userId) {
        this.uid = userId;
    }

    @Override
    public void attach(HttpServletResponse _response) {
        this.response = _response;
    }

    @Override
    public void onEvent(CometEvent event) throws IOException {
        if (CometEvent.NOTIFY == event.getType()) {

            logger.finest("Chat handler cause onEvent " + event.getType() + " " + CometEvent.NOTIFY + " " + event.attachment());
            
            boolean send = false;

            Object attachment = ProtocolTranslator.fromXmlToObj((String) event.attachment());

            if (attachment instanceof Gameover) {
                logger.finest("Game over: destroy local cha");
                Gameover go = (Gameover) attachment;
                this.contexts.remove(go.getTableId() + "Chat");
            }

            if (attachment instanceof IncomingChatMessage) {
                IncomingChatMessage incMsg = (IncomingChatMessage) attachment;

                if (this.contexts.contains(incMsg.getScope())) {
                    send = true;
                }
            }

            if (attachment instanceof TablesList || attachment instanceof UsersList) {
                send = true;
            }
            
            if(attachment instanceof Ping) {
                if(((Ping)attachment).getUid().equals(this.uid))
                    send = true;
            }
            
            if (send) {
                PrintWriter writer = response.getWriter();
                writer.write((String) event.attachment());
                writer.flush();
            }
        }
    }

    @Override
    public void onInitialize(CometEvent ce) throws IOException {
        
    }

    @Override
    public void onTerminate(CometEvent ce) throws IOException {
        logger.info("Global Connection with " + this.uid + " closed by comet engine.");
        removeThisFromContext(ce);
    }

    @Override
    public void onInterrupt(CometEvent ce) throws IOException {
        
        String tableId = tablesManager.getTableIdByPlayerOrWatcher(uid);
        if(tableId != null)
            tablesManager.removeUserFromTable(uid, tableId);
        
        logger.info("Global Connection with " + this.uid + " closed by client.");
        removeThisFromContext(ce);
    }

    private void removeThisFromContext(CometEvent event) throws IOException {

        this.response.getWriter().close();

        CometContext cc = CometEngine.getEngine().getCometContext("global");

        cc.removeCometHandler(this, false);

        userManagement.logout(uid);

    }

    private TablesManagerLocal lookupTablesListLocal() {
        try {
            Context c = new InitialContext();
            return (TablesManagerLocal) c.lookup("java:global/Serca/Serca-ejb/TablesManager!it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
    private UserManagementLocal lookupUserManagementLocal() {
        try {
            Context c = new InitialContext();
            return (UserManagementLocal) c.lookup("java:global/Serca/Serca-ejb/UserManagement!it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private TablesManagerLocal lookupTablesManagerLocal() {
        try {
            Context c = new InitialContext();
            return (TablesManagerLocal) c.lookup("java:global/Serca/Serca-ejb/TablesManager!it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
