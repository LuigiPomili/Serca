/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.servlet.regauth;

import it.unibo.cs.swarch.serca.servlet.chat.ChatServletImpl;
import it.unibo.cs.swarch.serca.ejb.stateless.regauth.UserManagementLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.*;
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
public class RegistrationAuthenticationServletImpl extends HttpServlet {

    @EJB
    UserManagementLocal userManagement;
    @EJB
    TablesManagerLocal tablesManager;
    
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String reply = "";

        try {

            String xml = request.getParameter("xml");

            if (xml == null) {
                throw new Exception("Missing xml parameter.");
            }

            if (ProtocolTranslator.validateXml(xml) == false) {
                throw new Exception("Malformed xml request.");
            }

            Object req = ProtocolTranslator.fromXmlToObj(xml);

            if (req instanceof Registration) {

                //try to satisfy registration request
                Registration reg = (Registration) req;

                if (userManagement.registration(reg)) {
                    reply = ProtocolTranslator.fromObjToXml("user registered", ProtocolTranslator.CreateObj.REGISTRATIONREPLY);
                } else {
                    reply = ProtocolTranslator.fromObjToXml("error during user registration", ProtocolTranslator.CreateObj.REGISTRATIONREPLY);
                }
                out.print(reply);
                out.flush();
                out.close();
            } else if (req instanceof Login) {

                //try to satisfy login request
                Login login = (Login) req;

                if (userManagement.authentication(login) == UserManagementLocal.authenticationResult.LOGGED_IN || userManagement.authentication(login) == UserManagementLocal.authenticationResult.ALREADY_LOGGED_IN) {

                    reply = ProtocolTranslator.fromObjToXml("LOGGED_IN", ProtocolTranslator.CreateObj.LOGINREPLY);
                    out.print(reply);
                    out.flush();
                    
                    ChatServletImpl.prepareUserToChat("global", response, login.getUid());
                    
                    tablesManager.setNeedsToUpdate(true);

                } else {

                    reply = ProtocolTranslator.fromObjToXml("INVALID_USER_OR_PASSWORD", ProtocolTranslator.CreateObj.LOGINREPLY);
                    out.print(reply);
                    out.flush();
                    out.close();
                }

            } else {
                throw new Exception("This servlet cannot accept this request.");
            }

        } catch (Exception ex) {

            logger.severe(ex.getMessage());

            Problem problem = new Problem();
            problem.setDescription(ex.getMessage());

            reply = ProtocolTranslator.fromObjToXml(problem);

            //send the reply to the client
            out.print(reply);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        //unknown service requested
        Problem problem = new Problem();
        problem.setDescription("service not implemented");
        String reply = ProtocolTranslator.fromObjToXml(problem);

        //send the reply to the client
        out.print(reply);
        out.flush();
        out.close();

    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
