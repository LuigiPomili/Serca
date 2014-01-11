/*
 * SercaSwingView.java
 */
package sercaswing;

import it.unibo.cs.swarch.serca.clientsideconnectionlibrary.SercaConnectionManager.SercaConnectionManager;
import it.unibo.cs.swarch.serca.protocol.jaxb.IncomingChatMessage;
import it.unibo.cs.swarch.serca.protocol.jaxb.Login;
import it.unibo.cs.swarch.serca.protocol.jaxb.OutgoingChatMessage;
import it.unibo.cs.swarch.serca.protocol.jaxb.Problem;
import it.unibo.cs.swarch.serca.protocol.jaxb.Registration;
import it.unibo.cs.swarch.serca.protocol.jaxb.TablesList;
import it.unibo.cs.swarch.serca.protocol.jaxb.UsersList;
import java.awt.TrayIcon;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import sercaswing.overridedswingclasses.NonEditableJTable;
import sercaswing.overridedswingclasses.OurDefaultTableModel;
import sercaswing.scmutils.ConnectionsUtils;
import sercaswing.table.SercaGameTable;

/**
 * The application's main frame.
 */
public class SercaSwingView extends FrameView implements PropertyChangeListener {

    public SercaSwingView(SingleFrameApplication app) {
        super(app);

        initMyComponent();

        initComponents();

        /**
         * INIT MY PROPERTIES
         * 
         */
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SercaSwingApp.getApplication().getMainFrame();
            aboutBox = new SercaSwingAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SercaSwingApp.getApplication().show(aboutBox);
    }

    public void initMyComponent() {
        //tables list model initialization
        String[] tablesListHeaders = {"Owner", "Players Allowed", "Registered Watchers"};
        String[] usersListHeaders = {"Name", "Score", "Status"};
        this.tablesList = new OurDefaultTableModel(tablesListHeaders, 0);
        this.usersList = new OurDefaultTableModel(usersListHeaders, 0);
        try {
            BufferedReader configReader = new BufferedReader(new FileReader("config/serverurl.conf"));
            this.serverUrl = configReader.readLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        pnlAuth = new javax.swing.JPanel();
        txtUid = new javax.swing.JTextField();
        lblUid = new javax.swing.JLabel();
        lblPwd = new javax.swing.JLabel();
        cmdLogin = new javax.swing.JButton();
        cmdRegister = new javax.swing.JToggleButton();
        txtPwd = new javax.swing.JPasswordField();
        cmdCreateTable = new javax.swing.JButton();
        SpinnerModel sm = new SpinnerNumberModel(0, 0, 3, 1);
        spnBotNumber = new javax.swing.JSpinner(sm);
        jLabel2 = new javax.swing.JLabel();
        cmdLogout = new javax.swing.JButton();
        pnlLists = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTables = new NonEditableJTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUsers = new NonEditableJTable();
        optWathcer = new javax.swing.JRadioButton();
        optPlayer = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        pnlChats = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtGlobalChatReader = new javax.swing.JTextArea();
        txtGlobalChatWriter = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtLocalChatReader = new javax.swing.JTextArea();
        txtLocalChatWriter = new javax.swing.JTextField();
        lblGlobalChat = new javax.swing.JLabel();
        lblLocalChat = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        chcRole = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        pnlAuth.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlAuth.setName("pnlAuth"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(sercaswing.SercaSwingApp.class).getContext().getResourceMap(SercaSwingView.class);
        txtUid.setText(resourceMap.getString("txtUid.text")); // NOI18N
        txtUid.setName("txtUid"); // NOI18N

        lblUid.setText(resourceMap.getString("lblUid.text")); // NOI18N
        lblUid.setName("lblUid"); // NOI18N

        lblPwd.setText(resourceMap.getString("lblPwd.text")); // NOI18N
        lblPwd.setName("lblPwd"); // NOI18N

        cmdLogin.setText(resourceMap.getString("cmdLogin.text")); // NOI18N
        cmdLogin.setName("cmdLogin"); // NOI18N
        cmdLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdLoginMouseClicked(evt);
            }
        });

        cmdRegister.setText(resourceMap.getString("cmdRegister.text")); // NOI18N
        cmdRegister.setName("cmdRegister"); // NOI18N
        cmdRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdRegisterMouseClicked(evt);
            }
        });

        txtPwd.setText(resourceMap.getString("txtPwd.text")); // NOI18N
        txtPwd.setName("txtPwd"); // NOI18N

        cmdCreateTable.setText(resourceMap.getString("cmdCreateTable.text")); // NOI18N
        cmdCreateTable.setEnabled(false);
        cmdCreateTable.setName("cmdCreateTable"); // NOI18N
        cmdCreateTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdCreateTableMouseClicked(evt);
            }
        });

        spnBotNumber.setName("spnBotNumber"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        cmdLogout.setText(resourceMap.getString("cmdLogout.text")); // NOI18N
        cmdLogout.setName("cmdLogout"); // NOI18N
        cmdLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdLogoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlAuthLayout = new javax.swing.GroupLayout(pnlAuth);
        pnlAuth.setLayout(pnlAuthLayout);
        pnlAuthLayout.setHorizontalGroup(
            pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAuthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUid)
                    .addComponent(txtUid, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPwd)
                    .addGroup(pnlAuthLayout.createSequentialGroup()
                        .addGroup(pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmdLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPwd, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdCreateTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmdRegister, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addGroup(pnlAuthLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                                .addComponent(spnBotNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(42, 42, 42))
        );
        pnlAuthLayout.setVerticalGroup(
            pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAuthLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUid)
                    .addComponent(lblPwd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUid, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPwd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAuthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdCreateTable)
                    .addComponent(spnBotNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cmdLogin)
                    .addComponent(cmdLogout))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pnlLists.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlLists.setName("pnlLists"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblTables.setModel(this.tablesList);
        tblTables.setName("tblTables"); // NOI18N
        tblTables.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTablesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTables);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblUsers.setModel(this.usersList);
        tblUsers.setName("tblUsers"); // NOI18N
        jScrollPane2.setViewportView(tblUsers);

        chcRole.add(optWathcer);
        optWathcer.setText(resourceMap.getString("optWathcer.text")); // NOI18N
        optWathcer.setName("optWathcer"); // NOI18N

        chcRole.add(optPlayer);
        optPlayer.setSelected(true);
        optPlayer.setText(resourceMap.getString("optPlayer.text")); // NOI18N
        optPlayer.setName("optPlayer"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout pnlListsLayout = new javax.swing.GroupLayout(pnlLists);
        pnlLists.setLayout(pnlListsLayout);
        pnlListsLayout.setHorizontalGroup(
            pnlListsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlListsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
                    .addGroup(pnlListsLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optPlayer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(optWathcer)))
                .addContainerGap())
        );
        pnlListsLayout.setVerticalGroup(
            pnlListsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlListsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optWathcer)
                    .addComponent(optPlayer)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlChats.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pnlChats.setName("pnlChats"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        txtGlobalChatReader.setColumns(20);
        txtGlobalChatReader.setEditable(false);
        txtGlobalChatReader.setRows(5);
        txtGlobalChatReader.setName("txtGlobalChatReader"); // NOI18N
        jScrollPane3.setViewportView(txtGlobalChatReader);

        txtGlobalChatWriter.setEditable(false);
        txtGlobalChatWriter.setText(resourceMap.getString("txtGlobalChatWriter.text")); // NOI18N
        txtGlobalChatWriter.setName("txtGlobalChatWriter"); // NOI18N
        txtGlobalChatWriter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtGlobalChatWriterKeyPressed(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        txtLocalChatReader.setColumns(20);
        txtLocalChatReader.setEditable(false);
        txtLocalChatReader.setRows(5);
        txtLocalChatReader.setName("txtLocalChatReader"); // NOI18N
        jScrollPane4.setViewportView(txtLocalChatReader);

        txtLocalChatWriter.setEditable(false);
        txtLocalChatWriter.setText(resourceMap.getString("txtLocalChatWriter.text")); // NOI18N
        txtLocalChatWriter.setName("txtLocalChatWriter"); // NOI18N
        txtLocalChatWriter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLocalChatWriterKeyPressed(evt);
            }
        });

        lblGlobalChat.setText(resourceMap.getString("lblGlobalChat.text")); // NOI18N
        lblGlobalChat.setName("lblGlobalChat"); // NOI18N

        lblLocalChat.setText(resourceMap.getString("lblLocalChat.text")); // NOI18N
        lblLocalChat.setName("lblLocalChat"); // NOI18N

        javax.swing.GroupLayout pnlChatsLayout = new javax.swing.GroupLayout(pnlChats);
        pnlChats.setLayout(pnlChatsLayout);
        pnlChatsLayout.setHorizontalGroup(
            pnlChatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChatsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlChatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(txtGlobalChatWriter, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(txtLocalChatWriter, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(lblGlobalChat)
                    .addComponent(lblLocalChat))
                .addContainerGap())
        );
        pnlChatsLayout.setVerticalGroup(
            pnlChatsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChatsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblGlobalChat)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtGlobalChatWriter, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(lblLocalChat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLocalChatWriter, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlLists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAuth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlChats, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlChats, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(pnlAuth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlLists, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(sercaswing.SercaSwingApp.class).getContext().getActionMap(SercaSwingView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1003, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 819, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

private void cmdLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdLoginMouseClicked
    if (this.txtUid.getText().length() >= 6 && this.txtUid.getText().length() <= 15
            && this.txtPwd.getPassword().length >= 6 && this.txtPwd.getPassword().length <= 32) {

        this.scm = new SercaConnectionManager(this.txtUid.getText(), String.copyValueOf(this.txtPwd.getPassword()), this.serverUrl);

        this.scm.setUid(this.txtUid.getText());
        this.scm.setPwd(String.copyValueOf(this.txtPwd.getPassword()));

        Login login = new Login();
        login.setUid(this.txtUid.getText());
        login.setPwd(String.copyValueOf(this.txtPwd.getPassword()));
        try {
            this.scm.requestServerStreamingService(this.serverUrl + "reglog", login, SercaConnectionManager.ConnectionsName.GLOBAL, this);
        } catch (IOException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        }
    } else {
        JOptionPane.showMessageDialog(this.getFrame(), "Login result", "Incomplete data.", TrayIcon.MessageType.ERROR.ordinal());
    }
}//GEN-LAST:event_cmdLoginMouseClicked

private void txtGlobalChatWriterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGlobalChatWriterKeyPressed
    if (evt.getKeyChar() == '\n' && this.txtGlobalChatWriter.getText().length() > 0) {
        String msg = this.txtGlobalChatWriter.getText().trim();
        OutgoingChatMessage ocm = new OutgoingChatMessage();
        ocm.setMessage(msg);
        ocm.setScope("global");
        SercaConnectionManager.SingleRequestReplyResponse response = null;
        try {
            response = scm.singleRequestReplyService(this.serverUrl + "chat", ocm, null);
        } catch (ProtocolException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.txtGlobalChatWriter.setText("");
    }
}//GEN-LAST:event_txtGlobalChatWriterKeyPressed

private void cmdCreateTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdCreateTableMouseClicked
    SercaGameTable sgt = new SercaGameTable(this.scm, true, this.scm.getUid(), true, (Integer) this.spnBotNumber.getValue(), this.serverUrl, this.txtLocalChatWriter);
}//GEN-LAST:event_cmdCreateTableMouseClicked

private void tblTablesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTablesMouseClicked
    if (evt.getClickCount() == 2) {
        JTable row = (JTable) evt.getSource();
        int rowIndex = row.getSelectedRow();

        String tableId = (String) this.tablesList.getValueAt(rowIndex, 0);

        SercaGameTable sgt = new SercaGameTable(this.scm, false, tableId, (optPlayer.isSelected()) ? true : false, 0, this.serverUrl, this.txtLocalChatWriter);

    }
}//GEN-LAST:event_tblTablesMouseClicked

private void cmdRegisterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdRegisterMouseClicked
    RegistrationDialog rd = new RegistrationDialog(new javax.swing.JFrame(), true);
    rd.setVisible(true);

    //name, surname, mail, uid, passwird
    List<Object> userFormArray = rd.getReturnStatus();
    if (userFormArray != null) {
        Registration r = new Registration();
        r.setName((String) userFormArray.remove(0));
        r.setSurname((String) userFormArray.remove(0));
        r.setMail((String) userFormArray.remove(0));
        r.setUid((String) userFormArray.remove(0));
        r.setPwd((String) userFormArray.remove(0));
        try {
            this.scm = new SercaConnectionManager(null, null, null);
            SercaConnectionManager.SingleRequestReplyResponse response = this.scm.singleRequestReplyService(this.serverUrl + "/reglog", r, null);
            if (response != null && response.getHttpStatusCode() == 200) {
                System.out.println("registration result");
                if (((String) response.getReturnedData()).equals("user registered")) {
                    JOptionPane.showMessageDialog(this.getFrame(), "User " + r.getName() + " registered.", "Registration result", TrayIcon.MessageType.INFO.ordinal());
                } else {
                    JOptionPane.showMessageDialog(this.getFrame(), "User " + r.getName() + " failed.", "Registration result", TrayIcon.MessageType.ERROR.ordinal());
                }
            }
        } catch (ProtocolException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}//GEN-LAST:event_cmdRegisterMouseClicked

private void cmdLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdLogoutMouseClicked
    if (this.scm != null) {
        this.scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GAME);
        this.scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GLOBAL);
    }

    this.txtUid.setText("");
    this.txtPwd.setText("");
    this.txtUid.setEnabled(true);
    this.txtPwd.setEnabled(true);
    this.cmdLogin.setEnabled(true);
    this.cmdRegister.setEnabled(true);
    this.cmdCreateTable.setEnabled(false);
    this.txtGlobalChatWriter.setEnabled(false);
    this.txtGlobalChatReader.setText("");
    this.txtLocalChatWriter.setEnabled(false);
    this.txtLocalChatReader.setText("");

    int rowCounter = this.tablesList.getRowCount();
    for (int i = 0; i < rowCounter; i++) {
        ((OurDefaultTableModel) this.tablesList).removeRow(0);
    }

    rowCounter = this.usersList.getRowCount();
    for (int i = 0; i < rowCounter; i++) {
        ((OurDefaultTableModel) this.usersList).removeRow(0);
    }
}//GEN-LAST:event_cmdLogoutMouseClicked

private void txtLocalChatWriterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLocalChatWriterKeyPressed
if (evt.getKeyChar() == '\n' && this.txtLocalChatWriter.getText().length() > 0) {
        String msg = this.txtLocalChatWriter.getText().trim();
        OutgoingChatMessage ocm = new OutgoingChatMessage();
        ocm.setMessage(msg);
        ocm.setScope("local");
        SercaConnectionManager.SingleRequestReplyResponse response = null;
        try {
            response = scm.singleRequestReplyService(this.serverUrl + "chat", ocm, null);
        } catch (ProtocolException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SercaSwingView.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.txtLocalChatWriter.setText("");
    }
}//GEN-LAST:event_txtLocalChatWriterKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.ButtonGroup chcRole;
    public javax.swing.JButton cmdCreateTable;
    public javax.swing.JButton cmdLogin;
    private javax.swing.JButton cmdLogout;
    public javax.swing.JToggleButton cmdRegister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblGlobalChat;
    private javax.swing.JLabel lblLocalChat;
    private javax.swing.JLabel lblPwd;
    private javax.swing.JLabel lblUid;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JRadioButton optPlayer;
    private javax.swing.JRadioButton optWathcer;
    public javax.swing.JPanel pnlAuth;
    public javax.swing.JPanel pnlChats;
    public javax.swing.JPanel pnlLists;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSpinner spnBotNumber;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable tblTables;
    private javax.swing.JTable tblUsers;
    public javax.swing.JTextArea txtGlobalChatReader;
    public javax.swing.JTextField txtGlobalChatWriter;
    public javax.swing.JTextArea txtLocalChatReader;
    public javax.swing.JTextField txtLocalChatWriter;
    public javax.swing.JPasswordField txtPwd;
    public javax.swing.JTextField txtUid;
    // End of variables declaration//GEN-END:variables
    /**
     * My properties
     */
    private TableModel tablesList;
    private TableModel usersList;
    private SercaConnectionManager scm;
    private String serverUrl;
    /**
     * 
     */
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    public synchronized void propertyChange(PropertyChangeEvent pce) {

        Object reply = pce.getNewValue();
        
        if (pce.getNewValue() instanceof String) {
            ConnectionsUtils.manageLogin((String) reply, this);
        } else if (pce.getNewValue() instanceof TablesList) {
            ConnectionsUtils.manageTableLists((TablesList) pce.getNewValue(), this);
        } else if (pce.getNewValue() instanceof UsersList) {
            ConnectionsUtils.manageUsersList((UsersList) pce.getNewValue(), this);
        } else if (pce.getNewValue() instanceof IncomingChatMessage) {
            ConnectionsUtils.manageIncomingChatMessage((IncomingChatMessage) pce.getNewValue(), this);
        } else if (pce.getNewValue() instanceof Problem) {
            ConnectionsUtils.manageProblem((Problem) reply, this);
        }
    }

    public TableModel getTablesList() {
        return tablesList;
    }

    public TableModel getUsersList() {
        return usersList;
    }
    
    
}
