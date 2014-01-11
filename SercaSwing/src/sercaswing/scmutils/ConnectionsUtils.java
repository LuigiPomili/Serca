/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sercaswing.scmutils;

import it.unibo.cs.swarch.serca.protocol.jaxb.IncomingChatMessage;
import it.unibo.cs.swarch.serca.protocol.jaxb.Problem;
import it.unibo.cs.swarch.serca.protocol.jaxb.TablesList;
import it.unibo.cs.swarch.serca.protocol.jaxb.UsersList;
import java.awt.TrayIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sercaswing.overridedswingclasses.OurDefaultTableModel;

/**
 *
 * @author tappof
 */
public class ConnectionsUtils {

    public static synchronized void manageLogin(String reply, sercaswing.SercaSwingView view) {
        if (reply.equals("LOGGED_IN")) {
            view.cmdLogin.setEnabled(false);
            view.cmdRegister.setEnabled(false);
            view.txtUid.setEnabled(false);
            view.txtPwd.setEnabled(false);

            view.cmdCreateTable.setEnabled(true);
            view.txtGlobalChatWriter.setEditable(true);
            view.cmdCreateTable.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(view.getFrame(), "Login failed. Verify your credentials.", "Login result", TrayIcon.MessageType.ERROR.ordinal());
            view.cmdLogin.setEnabled(true);
            view.cmdRegister.setEnabled(true);
            view.txtUid.setEnabled(true);
            view.txtPwd.setEnabled(true);

            view.cmdCreateTable.setEnabled(false);
            view.txtGlobalChatWriter.setEditable(false);
            view.cmdCreateTable.setEnabled(false);
        }
    }

    public static synchronized void manageTableLists(TablesList reply, sercaswing.SercaSwingView view) {
        TablesList tsl = (TablesList) reply;

        DefaultTableModel tablesListModel = (OurDefaultTableModel) view.getTablesList();

        synchronized (tablesListModel) {

            int tablesListEntries = tablesListModel.getRowCount();

            for (int r = 0; r < tablesListEntries; r++) {
                tablesListModel.removeRow(0);
            }

            java.util.List<TablesList.Table> tables = tsl.getTable();

            for (TablesList.Table t : tables) {
                Object[] row = new Object[3];
                row[0] = t.getId();
                row[1] = t.getMembers();
                row[2] = t.getWatchers();
                tablesListModel.addRow(row);
            }
        }
    }

    public static synchronized void manageUsersList(UsersList reply, sercaswing.SercaSwingView view) {
        UsersList usl = (UsersList) reply;

        DefaultTableModel usersListModel = (OurDefaultTableModel) view.getUsersList();

        synchronized (usersListModel) {

            int usersListEntries = usersListModel.getRowCount();
            for (int r = 0; r < usersListEntries; r++) {
                usersListModel.removeRow(0);
            }

            java.util.List<UsersList.User> users = usl.getUser();

            for (UsersList.User u : users) {
                Object[] row = new Object[3];
                row[0] = u.getUid();
                row[1] = u.getScore();
                row[2] = u.getStatus();
                usersListModel.addRow(row);
            }
        }
    }

    public static synchronized void manageIncomingChatMessage(IncomingChatMessage reply, sercaswing.SercaSwingView view) {
        IncomingChatMessage icm = (IncomingChatMessage) reply;
        if (icm.getScope().equals("global")) {
            view.txtGlobalChatReader.append(icm.getSender() + ": " + icm.getMessage() + "\n");
            view.txtGlobalChatReader.setCaretPosition(view.txtGlobalChatReader.getText().length());
        } else {
            view.txtLocalChatReader.append(icm.getSender() + ": " + icm.getMessage() + "\n");
            view.txtLocalChatReader.setCaretPosition(view.txtLocalChatReader.getText().length());
        }
    }

    public static synchronized void manageProblem(Problem reply, sercaswing.SercaSwingView view) {
        JOptionPane.showMessageDialog(view.getFrame(), "Problem", reply.getDescription(), TrayIcon.MessageType.ERROR.ordinal());
    }
}
