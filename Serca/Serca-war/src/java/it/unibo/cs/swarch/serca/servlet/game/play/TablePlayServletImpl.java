/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.servlet.game.play;

import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful.TableLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.Move;
import it.unibo.cs.swarch.serca.protocol.jaxb.MoveReply;
import it.unibo.cs.swarch.serca.protocol.jaxb.Problem;
import it.unibo.cs.swarch.serca.protocol.translator.ProtocolTranslator;
import java.io.IOException;
import java.io.PrintWriter;
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
public class TablePlayServletImpl extends HttpServlet {

    @EJB
    private TablesManagerLocal tablesManager;
    
    Logger logger;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        this.logger = Logger.getLogger(this.getClass().getSimpleName());
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

        response.setContentType("text/html;charset=UTF-8");
        
        PrintWriter out = response.getWriter();

        String reply = "";

        String requestor = request.getUserPrincipal().getName();

        this.logger.info("Request by " + requestor + " on the current servlet.");

        try {

            String xml = request.getParameter("xml");

            if (xml == null) {
                throw new Exception("Missing xml parameter.");
            }

            if (ProtocolTranslator.validateXml(xml) == false) {
                throw new Exception("Malformed xml request.");
            }

            Object req = ProtocolTranslator.fromXmlToObj(xml);

            if (!(req instanceof Move)) {
                //TODO aggiungere il messaggio all'xsd
                throw new Exception("This servlet cannot accept this request.");
            }

            Move move = (Move) req;

            this.logger.info("Move of " + requestor + ", card: " + move.getCard());
            
            String tableId = tablesManager.getTableIdByPlayerOrWatcher(requestor);
            TableLocal tl = tablesManager.getTableReferenceByTableId(tableId);
            
            if(tl == null) {
                //TODO aggiungere il messaggio all'xsd
                throw new Exception("The servlet cannot find the table needed to complete the action.");
            }

            if (tl.moveOnTheTable(move.getCard()) == false) {
                MoveReply mr = new MoveReply();
                mr.setCard("NOT A VALID CARD");
                mr.setMoveOf(requestor);
                mr.setNextTurnOf(requestor);
                
                reply = ProtocolTranslator.fromObjToXml(mr);
            }


        } catch (Exception ex) {
            this.logger.severe(ex.getMessage());
            
            Problem problem = new Problem();
            
            if (ex instanceof javax.ejb.EJBAccessException) {
                problem.setDescription("Client not authorized for this invocation.");
            } else {
                problem.setDescription(ex.getMessage());
            }
            reply = ProtocolTranslator.fromObjToXml(problem);
            
        } finally {
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
}
