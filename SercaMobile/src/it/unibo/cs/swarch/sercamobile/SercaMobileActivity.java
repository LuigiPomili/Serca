package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.sercamobile.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SercaMobileActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	private Button butLogin;
	private Button butRegister;
	private EditText txtUsername;
	private EditText txtPassword;
	private EditText txtURL;
	private SingletonUser user;
	private SercaMobileActivity login;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login = this;
        setContentView(R.layout.main);
        butLogin = (Button) findViewById(R.id.button1);
        butRegister = (Button) findViewById(R.id.button2);
        txtUsername = (EditText) findViewById(R.id.editText1);
        txtPassword = (EditText) findViewById(R.id.editText2);
        txtURL = (EditText) findViewById(R.id.txtURL);
        butLogin.setOnClickListener(this);
        butRegister.setOnClickListener(this);
    }


	public void onClick(View v) {
		int IdButton = v.getId();
		
		if (IdButton == butLogin.getId()){
			if ((txtUsername.getText().toString().equals("")) || (txtPassword.getText().toString().equals(""))){
				Context context = getApplicationContext();
				CharSequence text = "You must insert Username and Password. If you are not register, you must register before";
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}else{
				user = SingletonUser.getInstance();
				user.setScore(0);
				user.setUsername(txtUsername.getText().toString());
				user.setPassword(txtPassword.getText().toString());
				user.setServerURL(txtURL.getText().toString());
				user.login(handler);
			}				
		}
		else if (IdButton == butRegister.getId()){
			Intent intent = new Intent().setClass(this, RegistrationActivity.class);
			intent.setAction(Intent.ACTION_SEND);
		    intent.putExtra("serverurl", txtURL.getText().toString());
			startActivity(intent);
		}
	}
	
	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
    		if (((String)msg.obj).equals("LOGGED_IN")){
    			try{
    				Intent intent = new Intent(login, MyTabActivity.class);
    				startActivity(intent);
    			}catch(Exception e){
    				System.out.println("Exception");
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
	
}