package it.unibo.cs.swarch.sercamobile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ProtocolException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import it.unibo.cs.swarch.serca.clientsideconnectionlibrary.SercaConnectionManager.SercaConnectionManager;
import it.unibo.cs.swarch.serca.clientsideconnectionlibrary.SercaConnectionManager.SercaConnectionManager.SingleRequestReplyResponse;
import it.unibo.cs.swarch.protocol.simplexml.classes.CreateTable;
import it.unibo.cs.swarch.protocol.simplexml.classes.CreateTableReply;
import it.unibo.cs.swarch.protocol.simplexml.classes.Gameover;
import it.unibo.cs.swarch.protocol.simplexml.classes.HandFinished;
import it.unibo.cs.swarch.protocol.simplexml.classes.IncomingChatMessage;
import it.unibo.cs.swarch.protocol.simplexml.classes.Login;
import it.unibo.cs.swarch.protocol.simplexml.classes.LoginReply;
import it.unibo.cs.swarch.protocol.simplexml.classes.Move;
import it.unibo.cs.swarch.protocol.simplexml.classes.MoveReply;
import it.unibo.cs.swarch.protocol.simplexml.classes.OutgoingChatMessage;
import it.unibo.cs.swarch.protocol.simplexml.classes.Registration;
import it.unibo.cs.swarch.protocol.simplexml.classes.Subscription;
import it.unibo.cs.swarch.protocol.simplexml.classes.SubscriptionReply;
import it.unibo.cs.swarch.protocol.simplexml.classes.TablesList;
import it.unibo.cs.swarch.protocol.simplexml.classes.UserHasJoinedTheTable;
import it.unibo.cs.swarch.protocol.simplexml.classes.UserHasLeftTheTable;
import it.unibo.cs.swarch.protocol.simplexml.classes.UsersList;

public class SingletonUser extends Activity implements PropertyChangeListener {
	private static SingletonUser instance;
	private String username;
	private String password;
	private int score;
	private SercaConnectionManager scm;
	private Handler loginhandler;
	private Object tablelisthandler;
	private Object chatglobalhandler;
	private Object chatlocalhandler;
	private Object userlisthandler;
	private Handler createtablehandler;
	private CreateTableReply ctrresults = null;
	private Object tablecreatedhandler;
	private int botno;
	private boolean issubscription = false;
	private SubscriptionReply srresults = null;
	private String serverurl;
	 
    // Private constructor prevents instantiation from other classes
    private SingletonUser() {

    }
 
    public static SingletonUser getInstance() {
    	if (instance == null)
    		instance = new SingletonUser();
        return instance;
    }
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public String getServerURL() {
		return serverurl;
	}

	public void setServerURL(String serverurl) {
		this.serverurl = serverurl;
	}
	
	public void login(Handler loginhandler){
		scm = null;
		scm = new SercaConnectionManager(username, password);
		scm.setUid(username);
		scm.setPwd(password);
		Login login = new Login();
        login.setUid(username);
        login.setPwd(password);
        this.loginhandler = loginhandler;
        
        try {
        	Integer response = scm.requestServerStreamingService(serverurl + "reglog", login,
            		SercaConnectionManager.ConnectionsName.GLOBAL, this);
        	Message msg = new Message();
        	if (response == null){
        		msg.obj = "Malformed Login Request";
            	loginhandler.sendMessage(msg);
            	scm = null;
        	}
        	if (response != 200){
        		msg.obj = "HTTP ERROR: " + response;
            	loginhandler.sendMessage(msg);
            	scm = null;
        	}
        } catch (IOException ex) {
            Message msg = new Message();
            msg.obj = ex.getMessage();
            loginhandler.sendMessage(msg);
        }
	}
	
	public void registration(Object registrationhandler, String mail, String name, String password, String surname, String userid, String serverurl){
		Registration reg = new Registration();
		reg.setMail(mail);
		reg.setName(name);
		reg.setPwd(password);
		reg.setSurname(surname);
		reg.setUid(userid);
		try {
			SingleRequestReplyResponse srrr = (new SercaConnectionManager(userid,password)).singleRequestReplyService(serverurl + "reglog", reg, null);
			Message msg = new Message();
			msg.obj = srrr.getReturnedData();
			((Handler)registrationhandler).sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendChatMessage(String message, String kind){
		OutgoingChatMessage ocm = new OutgoingChatMessage();
		ocm.setMessage(message);
		ocm.setScope(kind);
		try {
			scm.singleRequestReplyService(serverurl + "chat", ocm, null);
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createTable(int botno){
		CreateTable ct = new CreateTable();
		ct.setBotsno(botno);
		this.botno = botno;
		try {
			Integer response = scm.requestServerStreamingService(serverurl + "admintables", ct, SercaConnectionManager.ConnectionsName.GAME, this);
			Message msg = new Message();
        	if (response == null){
        		msg.obj = "Malformed Create Table Request";
            	createtablehandler.sendMessage(msg);
        	}
        	if (response != 200){
        		msg.obj = "HTTP ERROR: " + response;
            	createtablehandler.sendMessage(msg);
        	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void move(String card){
		Move m = new Move();
		m.setCard(card);
		try {
			SingleRequestReplyResponse response = scm.singleRequestReplyService(serverurl + "move", m, null);
			if ((response != null) && (response.getHttpStatusCode() != 200)){
				Message msg = new Message();
				msg.arg1 = 1;
				msg.obj = response.getReturnedData();
				((Handler)tablecreatedhandler).sendMessage(msg);
			}
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTableListhandler(Object tablelisthandler){
		this.tablelisthandler = tablelisthandler;
	}
	
	public void setChathandler(Object chathandler, String chattype){
		if (chattype.equals("global"))
			this.chatglobalhandler = chathandler;
		else
			this.chatlocalhandler = chathandler;
	}
	
	public void setUserListhandler(Object userlisthandler){
		this.userlisthandler = userlisthandler;
	}
	
	public void setCreateTableHandler(Handler createtablehandler){
		this.createtablehandler = createtablehandler;
	}
	
	public CreateTableReply getCreateTableReplyResults(){
		return this.ctrresults;
	}
	
	public void setTableCreatedhandler(Object tablecreatedhandler){
		this.tablecreatedhandler = tablecreatedhandler;
	}
	
	public int getBotNo(){
		return this.botno;
	}
	
	public void subscribeToATable(String tableid, String kind){
		Subscription s = new Subscription();
		s.setKind(kind);
		s.setTableId(tableid);
		try {
			Integer response = scm.requestServerStreamingService(serverurl + "admintables", s, SercaConnectionManager.ConnectionsName.GAME, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSubscription(){
		return this.issubscription;
	}
	
	public SubscriptionReply getSubscriptionReplyResults(){
		return this.srresults;
	}
	
	public void killMe(String whatconnection){
		if (whatconnection.equals("game"))
			scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GAME, serverurl);
		if (whatconnection.equals("global"))
			scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GLOBAL, serverurl);
		if (whatconnection.equals("all")){
			scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GAME, serverurl);
			scm.closeStreamingConnections(SercaConnectionManager.ConnectionsName.GLOBAL, serverurl);
		}
	}

	public void propertyChange(PropertyChangeEvent pce) {
		Object newValue = pce.getNewValue();

		if (newValue != null)
        //login reply
			if (newValue instanceof LoginReply) {
				Message msg = new Message();
				msg.obj = ((LoginReply)newValue).getValue();
				loginhandler.sendMessage(msg);
			} else if (newValue instanceof TablesList) {
            TablesList tsl = (TablesList) newValue;
            List<it.unibo.cs.swarch.protocol.simplexml.classes.TablesList.Table> tslwrapper = tsl.getTable();
            
            Table[] newtableslist = new Table[tslwrapper.size()];
            try{
            for(int i = 0; i < tslwrapper.size(); i++){
            	if (!tslwrapper.get(i).getMembers().equals("full"))
            		newtableslist[i] = new Table(tslwrapper.get(i).getId(),
            				Integer.parseInt(tslwrapper.get(i).getMembers().replace(" player/s allowed", "")));
            	else
            		newtableslist[i] = new Table(tslwrapper.get(i).getId(), 0);
            }
            }catch(Exception e){
            	System.out.println("Exception");
            }
            if (tablelisthandler != null){
            	Message msg = new Message();
            	if (newtableslist.length == 0)
            		msg.obj = null;
            	else
            		msg.obj = newtableslist;
            	((Handler)tablelisthandler).sendMessage(msg);
            }

        } else if (newValue instanceof UsersList) {
            UsersList usl = (UsersList) newValue;
            List<it.unibo.cs.swarch.protocol.simplexml.classes.UsersList.User> uslwrapper = usl.getUser();

            User[] newuserslist = new User[uslwrapper.size()];
            try{
            for(int i = 0; i < uslwrapper.size(); i++){
            	newuserslist[i] = new User(uslwrapper.get(i).getUid(),
            			uslwrapper.get(i).getScore(), uslwrapper.get(i).getStatus());
            }
            }catch(Exception e){
            	System.out.println("Exception");
            }
            if (userlisthandler != null){
            	Message msg = new Message();
            	if (newuserslist.length == 0)
            		msg.obj = null;
            	else
            		msg.obj = newuserslist;
            	((Handler)userlisthandler).sendMessage(msg);
            }
            
        } else if (newValue instanceof IncomingChatMessage) {
            IncomingChatMessage icm = (IncomingChatMessage) newValue;
            if (icm.getScope().equals("global")){
                if (chatglobalhandler != null){
                	Message msg = new Message();
                	msg.obj = icm;
                	((Handler)chatglobalhandler).sendMessage(msg);
                }
            }else{
                if (chatlocalhandler != null){
                	Message msg = new Message();
                   	msg.obj = icm;
                   	((Handler)chatlocalhandler).sendMessage(msg);
                }
            }
            
        } else if (newValue instanceof CreateTableReply) {
            CreateTableReply ctr = (CreateTableReply) newValue;
            
            issubscription = false;
            
            ctrresults = ctr;
            
            Message msg = new Message();
            msg.obj = ctr;
            
            createtablehandler.sendMessage(msg);

        } else if (newValue instanceof SubscriptionReply) {

            SubscriptionReply sr = (SubscriptionReply) newValue;
            
            issubscription = true;
            
            srresults = sr;

            if ((tablelisthandler != null) && sr.getResult().equals("subscribed")){
            	Message msg = new Message();
            	msg.obj = sr;
            	((Handler)tablelisthandler).sendMessage(msg);
            }

        } else if (newValue instanceof UserHasJoinedTheTable) {
            UserHasJoinedTheTable uhjtt = (UserHasJoinedTheTable) newValue;
            
            if (tablecreatedhandler != null){
            	Message msg = new Message();
            	msg.obj = uhjtt;
            	((Handler)tablecreatedhandler).sendMessage(msg);
            }

        } else if(newValue instanceof UserHasLeftTheTable){
            UserHasLeftTheTable uhltt = (UserHasLeftTheTable)newValue;
            
            if (tablecreatedhandler != null){
            	Message msg = new Message();
            	msg.obj = uhltt;
            	((Handler)tablecreatedhandler).sendMessage(msg);
            }

        }else if (newValue instanceof MoveReply) {

            MoveReply mr = (MoveReply) newValue;
            
            if (tablecreatedhandler != null){
            	Message msg = new Message();
            	msg.obj = mr;
            	((Handler)tablecreatedhandler).sendMessage(msg);
            }
            
        } else if (newValue instanceof HandFinished) {

            HandFinished hf = (HandFinished) newValue;
            
            if (tablecreatedhandler != null){
            	Message msg = new Message();
            	msg.obj = hf;
            	((Handler)tablecreatedhandler).sendMessage(msg);
            }
            
        } else if (newValue instanceof Gameover) {
            Gameover go = (Gameover) newValue;
            
            if (tablecreatedhandler != null){
            	Message msg = new Message();
            	msg.obj = go;
            	((Handler)tablecreatedhandler).sendMessage(msg);
            }
            
        }
	}
 
}