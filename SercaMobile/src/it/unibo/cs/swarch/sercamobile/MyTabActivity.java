package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.sercamobile.R;
import android.app.TabActivity;
import android.content.Intent;
//import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class MyTabActivity extends TabActivity{
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_layout);

	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    intent = new Intent().setClass(this, TableListActivity.class);
	    spec = tabHost.newTabSpec("table").setIndicator("Table List").setContent(intent);
	    tabHost.addTab(spec);
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, CreatesTableActivity.class);
	    spec = tabHost.newTabSpec("creates_table").setIndicator("Create table").setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
	    case R.id.user_list: {
	    	Intent intent = new Intent(this, UserListActivity.class);
			startActivity(intent);
	        break;
	    }    
	    case R.id.global_chat:{
	    	Intent intent = new Intent(this, ChatActivity.class);
		    intent.setAction(Intent.ACTION_SEND);
		    intent.putExtra("chattype", "global");
			startActivity(intent);
	    	break;
	    }	
	    default:
	        break;
	    }
	 
	    return true;
	}

}
