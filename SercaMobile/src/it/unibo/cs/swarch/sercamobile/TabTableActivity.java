package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.sercamobile.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TabTableActivity extends TabActivity{
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_layout_1);

	    //Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    intent = new Intent().setClass(this, TableCreatedActivity.class);
	    spec = tabHost.newTabSpec("table").setIndicator("Table game").setContent(intent);
	    tabHost.addTab(spec);
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, ChatActivity.class);
	    intent.setAction(Intent.ACTION_SEND);
	    intent.putExtra("chattype", "local");
	    spec = tabHost.newTabSpec("local_chat").setIndicator("Local chat").setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, ChatActivity.class);
	    intent.setAction(Intent.ACTION_SEND);
	    intent.putExtra("chattype", "globalgame");
	    spec = tabHost.newTabSpec("global_chat").setIndicator("Global chat").setContent(intent);
	    tabHost.addTab(spec);	    

	    tabHost.setCurrentTab(0);
	}

}
