/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.servlet.game.admin.comethandlers;

import com.sun.grizzly.comet.CometEngine;
import com.sun.grizzly.comet.CometEvent;
import com.sun.grizzly.comet.CometHandler;
import it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.Ping;
import it.unibo.cs.swarch.serca.protocol.jaxb.UserHasLeftTheTable;
import it.unibo.cs.swarch.serca.protocol.translator.ProtocolTranslator;
import java.io.IOException;
import java.io.PrintWriter;
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
public class GameCometHandler implements CometHandler<HttpServletResponse> {

    UserManagementLocal userManagement = lookupUserManagementLocal();
    private TablesManagerLocal tablesManager = null;
    private UserManagementLocal userManager = null;
    private String gameContext;
    private HttpServletResponse response;
    private String uid;
    Logger logger;

    public GameCometHandler(String context, HttpServletResponse response, String user) {
        this.gameContext = context;
        this.response = response;
        this.uid = user;
        logger = Logger.getLogger(this.getClass().getSimpleName());
    }

    public String getUser() {
        return uid;
    }

    public void setUser(String user) {
        this.uid = user;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getGameContext() {
        return gameContext;
    }

    public void setGameContext(String context) {
        this.gameContext = context;
    }

    @Override
    public void attach(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void onEvent(CometEvent event) throws IOException {

        if (CometEvent.NOTIFY == event.getType()) {
            Object attachment = ProtocolTranslator.fromXmlToObj((String)event.attachment());

            boolean send = false;

            //NEW CODE
            if (attachment instanceof UserHasLeftTheTable) {
                if (((UserHasLeftTheTable) attachment).getUserThatHasLeft().equals(this.uid)) {
                    logger.info("unregistering game context for " + this.uid);
                    CometEngine.getEngine().getCometContext(this.gameContext).removeCometHandler(this);
                    return;
                }
            }

            if (attachment instanceof Ping) {
                if (((Ping) attachment).getUid().equals(this.uid)) {
                    send = true;
                }
            } else {
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

        logger.info("Game connection with " + this.uid + " closed by comet engine");

        if (this.response != null) {
            this.response.getWriter().close();
            this.response = null;
        }
    }

    @Override
    public void onInterrupt(CometEvent ce) throws IOException {

        logger.info("Game connection with " + this.uid + " closed by client");

        tablesManager = lookupTablesListLocal();
        tablesManager.removeUserFromTable(uid, this.gameContext.replace("Game", ""));

        removeThisFromContext();
    }

    private void removeThisFromContext() throws IOException {
        if (this.response != null) {
            this.response.getWriter().close();
            this.response = null;
        }

        if (CometEngine.getEngine().getCometContext(this.gameContext) != null) {

            CometEngine.getEngine().getCometContext(this.gameContext).removeCometHandler(this);

            if (CometEngine.getEngine().getCometContext(this.gameContext).getCometHandlers().isEmpty()) {
                CometEngine.getEngine().unregister(this.gameContext);
            }
        }
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
}
