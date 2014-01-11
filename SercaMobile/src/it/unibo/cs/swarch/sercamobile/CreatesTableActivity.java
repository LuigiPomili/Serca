package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.sercamobile.R;
import it.unibo.cs.swarch.protocol.simplexml.classes.CreateTableReply;
import it.unibo.cs.swarch.protocol.simplexml.translator.SimpleXmlProtocolTranslator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class CreatesTableActivity extends Activity implements OnClickListener{
	
	private SingletonUser user;
	private RadioGroup radiogroup;
	private CreatesTableActivity cta;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cta = this;
        user = SingletonUser.getInstance();
        user.setCreateTableHandler(handler);
        setContentView(R.layout.creates_table);
        TextView txtNameTable = (TextView) findViewById(R.id.txtCTNameMyTable);
        String user = SingletonUser.getInstance().getUsername();
        txtNameTable.setText(user);
        radiogroup = (RadioGroup) findViewById(R.id.rdgroup);
        Button butCreation = (Button)findViewById(R.id.cmdCreaMyTable);
        butCreation.setOnClickListener(this);
    }

	public void onClick(View v) {
		user.createTable(radiogroup.getCheckedRadioButtonId() - R.id.rdb0bot);
	}
	
	public void createTableResult(boolean result){
		if(result){
			Intent intent = new Intent(this, TabTableActivity.class);
			startActivity(intent);
		}else{
			Context context = getApplicationContext();
			CharSequence text = "Create Table Failed";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	if (msg.obj instanceof CreateTableReply){
        		if (((CreateTableReply)msg.obj).isCreated()){
        			Intent intent = new Intent(cta, TabTableActivity.class);
    				startActivity(intent);
        		}else{
        			Context context = getApplicationContext();
        			CharSequence text = "Create Table Failed";
        			int duration = Toast.LENGTH_LONG;
        			Toast toast = Toast.makeText(context, text, duration);
        			toast.show();
        		}
        	}else{
        		Context context = getApplicationContext();
    			CharSequence text = (String)msg.obj;
    			int duration = Toast.LENGTH_LONG;
    			Toast toast = Toast.makeText(context, text, duration);
    			toast.show();
        	}
        }
	};
	
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	user.killMe("global");
    };

}
