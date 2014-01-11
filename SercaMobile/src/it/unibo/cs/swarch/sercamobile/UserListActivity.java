package it.unibo.cs.swarch.sercamobile;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class UserListActivity extends ListActivity{
	
	private User[] userList = new User[] {};
	private String[] username;
	private String[] score;
	private String[] status;
	private SingletonUser user;
	private UserArrayAdapter adapter;
	private Object ulathis;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		user = SingletonUser.getInstance();
		user.setUserListhandler(handler);
		ulathis = this;
		// Create an array of Strings, that will be put to our ListActivity
		int numEle = userList.length;
	    username = new String[numEle];
	    score = new String[numEle];
	    status = new String[numEle];
	      for (int i=0; i<numEle; i++){
	    	  username[i] = userList[i].getUsername();
	    	  score[i] = Integer.toString(userList[i].getScore());
	    	  status[i] = userList[i].getStatus();
	      }
	      adapter = new UserArrayAdapter(this, username, score, status);
	      setListAdapter(adapter);
	}
	
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	try{
        		User[] newuserslist = ((User[])msg.obj);
        		int numEle;
        		if ((msg.obj == null) || (newuserslist[0] == null))
        			numEle = 0;
        		else
        			numEle = newuserslist.length;
        		username = new String[numEle];
        	    score = new String[numEle];
        	    status = new String[numEle];
        		for (int i = 0; i < numEle; i++){
        			username[i] = newuserslist[i].getUsername();
        			score[i] = Integer.toString(newuserslist[i].getScore());
        			status[i] = newuserslist[i].getStatus();
        		}
        		adapter = new UserArrayAdapter((UserListActivity)ulathis, username, score, status);
        		setListAdapter(adapter);
        	}catch(Exception e){
        		System.out.println("Exception");
        	}
        }
    };

}
