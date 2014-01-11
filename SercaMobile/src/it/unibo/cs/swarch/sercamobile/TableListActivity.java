package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.protocol.simplexml.classes.SubscriptionReply;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class TableListActivity extends ListActivity{
	
	private Table[] tableList = new Table[] {};
	private String[] nameTable;
	private String[] userConn;
	private SingletonUser user;
	private TableArrayAdapter adapter;
	private Object tlathis = null;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		user = SingletonUser.getInstance();
		user.setTableListhandler(handler);
		tlathis = this;
		// Create an array of Strings, that will be put into our ListActivity
		int numEle = tableList.length;
	    nameTable = new String[numEle];
	    userConn = new String[numEle];
	      for (int i=0; i<numEle; i++){
	    	  nameTable[i]=tableList[i].getUsername();
	    	  userConn[i]=Integer.toString(tableList[i].getPlayersAllowed());
	     }
	     adapter = new TableArrayAdapter(this,nameTable,userConn, handler);
	     setListAdapter(adapter);
	}
	
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	if (msg.arg1 == 1){
        		final String clicked = (String)msg.obj;

        		AlertDialog.Builder builder = new AlertDialog.Builder(((TableListActivity)tlathis));
        		builder.setTitle("Subscribe as:");
        		if (msg.arg2 == 1){
        			final CharSequence[] items2 = {"Watcher"};
        			builder.setSingleChoiceItems(items2, -1, new DialogInterface.OnClickListener() {
            		    public void onClick(DialogInterface dialog, int item) {
            		        user.subscribeToATable(clicked, "watcher");
            		    }
            		});
        		}else{
            		final CharSequence[] items = {"Player", "Watcher"};
        			builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int item) {
        					if (item == 0)
        						user.subscribeToATable(clicked, "player");
        					else
        						user.subscribeToATable(clicked, "watcher");
        				}
        			});
        		}
        		AlertDialog alert = builder.create();
        		alert.show();
        	}else
        	if (msg.obj instanceof SubscriptionReply){
        		SubscriptionReply sr = (SubscriptionReply)msg.obj;
        		if (sr.getResult().equals("subscribed")){
        			Intent intent = new Intent((Context)tlathis, TabTableActivity.class);
        			startActivity(intent);
        		}else{
        			Context context = getApplicationContext();
        			CharSequence text = "Subscription Failed";
        			int duration = Toast.LENGTH_LONG;
        			Toast toast = Toast.makeText(context, text, duration);
        			toast.show();
        		}
        	}else
        	try{
        		Table[] newtableslist = ((Table[])msg.obj);
        		int numEle;
        		if ((msg.obj == null) || (newtableslist[0] == null))
        			numEle = 0;
        		else
        			numEle = newtableslist.length;
        		nameTable = new String[numEle];
        		userConn = new String[numEle];
        		for (int i = 0; i < numEle; i++){
        			nameTable[i] = newtableslist[i].getUsername();
        			userConn[i] = Integer.toString(newtableslist[i].getPlayersAllowed());
        		}
        		adapter = new TableArrayAdapter((TableListActivity)tlathis, nameTable, userConn, handler);
        		setListAdapter(adapter);
            }catch(Exception e){
            	System.out.println("Exception");
            }
        }
    };
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	user.killMe("global");
    };

}
