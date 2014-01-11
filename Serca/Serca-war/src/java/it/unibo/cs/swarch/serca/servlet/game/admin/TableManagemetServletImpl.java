/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibo.cs.swarch.serca.servlet.game.admin;

import com.sun.grizzly.comet.CometContext;
import com.sun.grizzly.comet.CometEngine;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.singleton.TablesManagerLocal;
import it.unibo.cs.swarch.serca.ejb.tablemanagement.stateful.TableLocal;
import it.unibo.cs.swarch.serca.protocol.jaxb.*;
import it.unibo.cs.swarch.serca.protocol.translator.ProtocolTranslator;
import it.unibo.cs.swarch.serca.servlet.chat.ChatServletImpl;
import it.unibo.cs.swarch.serca.servlet.chat.comethandlers.ChatCometHandler;
import it.unibo.cs.swarch.serca.servlet.game.admin.comethandlers.GameCometHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
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
public class TableManagemetServletImpl extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        PrintWriter out = response.getWriter();

        String reply = "";

        String requestor = request.getUserPrincipal().getName();

        try {

            String xml = request.getParameter("xml");

            if (xml == null) {
                throw new Exception("Missing xml parameter.");
            }

            if (ProtocolTranslator.validateXml(xml) == false) {
                throw new Exception("Malformed xml request.");
            }

            Object req = ProtocolTranslator.fromXmlToObj(xml);

            if (req instanceof CreateTable) {
                CreateTable ct = (CreateTable) req;

                CreateTableReply ctr = new CreateTableReply();
                TableLocal tl = null;
                if ((tl = tablesManager.addTable(ct, requestor)) != null) {

                    String chatConnectionId = requestor + "Chat";
                    ChatServletImpl.prepareUserToChat(chatConnectionId, null, requestor);

                    String gameConnectionId = requestor + "Game";
                    createNewGameRoom(gameConnectionId);
                    addClientToGameRoom(gameConnectionId, response, requestor);
                    
                    ctr.setCreated(true);
                    CreateTableReply.CardsList cardsList = new CreateTableReply.CardsList();
                    cardsList.getCard().addAll(tl.getPlayerCards(requestor));
                    ctr.setCardsList(cardsList);
                    
                    ctr.setGameIsStarted(tl.isStarted());
                    
                    if(ctr.isGameIsStarted()) {
                        ctr.setFirstPlayer(tl.getFirstPlayer());
                    }

                } else {
                    ctr.setCreated(false);
                }

                reply = ProtocolTranslator.fromObjToXml(ctr);

                out.write(reply);
                out.flush();

            } else if (req instanceof Subscription) {
                Subscription s = (Subscription) req;

                SubscriptionReply sr = new SubscriptionReply();

                if (tablesManager.subscribe(requestor, s.getTableId(), s.getKind().equals("player"))) {
                    String chatConnectionId = s.getTableId() + "Chat";
                    ChatServletImpl.prepareUserToChat(chatConnectionId, null, requestor);

                    String gameConnectionId = s.getTableId() + "Game";
                    addClientToGameRoom(gameConnectionId, response, requestor);

                    sr.setResult("subscribed");

                    TableLocal tl = tablesManager.getTableReferenceByTableId(s.getTableId());

                    SubscriptionReply.UsersAlreadySubscribed usersList = new SubscriptionReply.UsersAlreadySubscribed();
                    Collection<String> uids = tablesManager.getUserSubscribedToTable(s.getTableId());
                    for (String uid : uids) {
                        SubscriptionReply.UsersAlreadySubscribed.SubscribedUser subUser = new SubscriptionReply.UsersAlreadySubscribed.SubscribedUser();

                        SubscriptionReply.UsersAlreadySubscribed.SubscribedUser.CardsList subUserCardsList = new SubscriptionReply.UsersAlreadySubscribed.SubscribedUser.CardsList();

                        subUser.setUid(uid);

                        if (s.getKind().equals("watcher") || (s.getKind().equals("player") && uid.equals(requestor))) {

                            subUserCardsList.getCard().addAll(tl.getPlayerCards(uid));

                            subUser.setCardsList(subUserCardsList);

                            String cardOnTable = tl.getXmlEncodedCardOnTableByUser(uid);
                            if (cardOnTable != null) {
                                subUser.setCardOnTable(cardOnTable);
                            }
                        }

                        usersList.getSubscribedUser().add(subUser);
                    }

                    sr.setUsersAlreadySubscribed(usersList);

                    reply = ProtocolTranslator.fromObjToXml(sr);

                    out.write(reply);
                    out.flush();

                    if (s.getKind().equals("player")) {
                        UserHasJoinedTheTable join = new UserHasJoinedTheTable();
                        join.setUserThatHasJoined(requestor);
                        join.setGameStarted(tl.isStarted());
                        join.setFirstPlayer(tl.getFirstPlayer());

                        reply = (String) ProtocolTranslator.fromObjToXml(join);
                        out.write(reply);
                        out.flush();

                        try {
                            CometEngine.getEngine().getCometContext(gameConnectionId).notify((String) ProtocolTranslator.fromObjToXml(join));
                        } catch (IOException ex) {
                            this.logger.severe(ex.getMessage());
                        }
                    }
                    
                } else {

                    sr.setResult("not subscribed");
                    
                    reply = (String) ProtocolTranslator.fromObjToXml(sr);
                    out.write(reply);
                    out.flush();
                    out.close();
                }
                
            } else if(req instanceof Ping) {
                Ping p = (Ping)req;
                
                String tableId = tablesManager.getTableIdByPlayerOrWatcher(p.getUid());
                
                if(tableId != null) {
                    String gameConnectionId = tableId + "Game";
                    CometEngine.getEngine().getCometContext(gameConnectionId).notify((String) ProtocolTranslator.fromObjToXml(p));
                }
                
                String ChatConnectionId = tableId + "Chat";
                CometEngine.getEngine().getCometContext("global").notify((String) ProtocolTranslator.fromObjToXml(p));
                
                
                
            } else {
                throw new Exception("This servlet cannot accept this request.");
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

    private void createNewGameRoom(String roomId) {
        CometEngine ce = CometEngine.getEngine();
        CometContext cc = ce.register(roomId);

        cc.setExpirationDelay(30000 * 1000);

    }

    private boolean addClientToGameRoom(String roomId, HttpServletResponse response, String uid) {
        CometEngine ce = CometEngine.getEngine();
        CometContext cc = ce.getCometContext(roomId);

        if (cc == null) {
            return false;
        }

        GameCometHandler handler = new GameCometHandler(roomId, response, uid);

        cc.addCometHandler(handler);

        return true;
    }

    private void deleteGameRoom(String roomId) {
        CometEngine.getEngine().unregister(roomId);
    }
}
