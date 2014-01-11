    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.servlet.chat;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;


import it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.IncomingChatMessage;
import it.unibo.cs.swarch.serca.protocol.jaxb.OutgoingChatMessage;
import it.unibo.cs.swarch.serca.protocol.jaxb.Problem;
import it.unibo.cs.swarch.serca.protocol.translator.ProtocolTranslator;
import it.unibo.cs.swarch.serca.servlet.chat.comethandlers.ChatCometHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author tappof
 */
public class ChatServletImpl extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @EJB
    UserManagementLocal userManagement;
    @EJB
    TablesManagerLocal tablesManagement;

    Logger logger;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.logger = Logger.getLogger(this.getClass().getSimpleName());
        
        CometEngine ce = CometEngine.getEngine();
        CometContext cc = null;

        if ((cc = ce.getCometContext("global")) == null) {
            this.logger.info("Global context created.");
            cc = ce.register("global");
            cc.setExpirationDelay(30000 * 1000);
        }
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/xml;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String reply = "";

        String requestor = request.getRemoteUser();

        try {

            String xml = request.getParameter("xml");

            if (xml == null) {
                throw new Exception("Missing xml parameter.");
            }

            if (ProtocolTranslator.validateXml(xml) == false) {
                throw new Exception("Malformed xml request.");
            }

            Object req = ProtocolTranslator.fromXmlToObj(xml);
            
            if (!(req instanceof OutgoingChatMessage)) {
                throw new Exception("This servlet cannot accept this request.");
            }
            
            OutgoingChatMessage outMsg = (OutgoingChatMessage) ProtocolTranslator.fromXmlToObj(xml);

            IncomingChatMessage incMsg = new IncomingChatMessage();

            //find the context
            if (outMsg.getScope().equals("global")) {
                incMsg.setScope("global");
            } else {
                String chatConnectionId = tablesManagement.getTableIdByPlayerOrWatcher(requestor) + "Chat";
                incMsg.setScope(chatConnectionId);
            }

            incMsg.setSender(requestor);
            incMsg.setMessage(outMsg.getMessage());

            reply = ProtocolTranslator.fromObjToXml(incMsg);

            CometContext cc = CometEngine.getEngine().getCometContext("global");

            cc.notify(reply);

        } catch (Exception ex) {

            this.logger.severe(ex.getMessage());

            //unknown error
            Problem problem = new Problem();

            if (ex instanceof javax.ejb.EJBAccessException) {
                problem.setDescription("Client not authorized for this invocation.");
            } else {
                problem.setDescription(ex.getMessage());
            }

            reply = ProtocolTranslator.fromObjToXml(problem);
            
            out.write(reply);
            out.flush();
            out.close();

        }

    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);

        /*PrintWriter out = response.getWriter();
        
        //unknown service requested
        Problem problem = new Problem();
        problem.setDescription("service not implemented");
        String reply = ProtocolTranslator.fromObjToXml(problem);
        
        //send the reply to the client
        out.print(reply);
        out.flush();
        out.close();
         */
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public synchronized static void prepareUserToChat(String room, HttpServletResponse response, String uid) {
        //check if the user is logged in and if it has an handler

        Logger logger = Logger.getLogger(ChatServletImpl.class.getSimpleName());
        
        logger.info("Prepare user to chat in room: " + room);

        CometEngine ce = CometEngine.getEngine();
        CometContext cc = null;

        //creiamo il contesto se ci serve
        if ((cc = ce.getCometContext("global")) == null) {
            logger.info("Created context global");
            cc = ce.register("global");
            cc.setExpirationDelay(30000 * 1000);
        }

        Set<ChatCometHandler> handlers = ce.getCometContext("global").getCometHandlers();

        //for-each handler in handlers
        ChatCometHandler handlerToCopy = null;
        for (ChatCometHandler handler : handlers) {
            if (handler.getUserId().equals(uid)) {
                handlerToCopy = handler;
                break;
            }
        }

        if (handlerToCopy == null) {
            logger.info("No handler for " + uid + " in the global context");
            handlerToCopy = new ChatCometHandler("global", response, uid);
            ce.getCometContext("global").addCometHandler(handlerToCopy);
        }

        if (room.equals("global") == false) {
            logger.info("context attached");
            handlerToCopy.addContext(room);
        }
    }
}
