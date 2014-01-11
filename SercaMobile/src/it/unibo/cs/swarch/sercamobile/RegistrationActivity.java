package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.sercamobile.R;
import it.unibo.cs.swarch.protocol.simplexml.classes.Problem;
import it.unibo.cs.swarch.protocol.simplexml.classes.RegistrationReply;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RegistrationActivity extends Activity implements OnTouchListener,OnClickListener {
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.registration);
	        Button buttRegister = (Button) findViewById(R.id.buttRegistration);
	        buttRegister.setOnClickListener(this);
	        LinearLayout myLayout = (LinearLayout) findViewById(R.id.RegistrationLayout);
	        myLayout.isInTouchMode();
	        setVisible(true);
	        

	 }
	 
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false; 
	}

	public void onClick(View v) {
		EditText txtUsername = (EditText) findViewById(R.id.editUsername);
		EditText txtName = (EditText) findViewById(R.id.editName);
		EditText txtSurname = (EditText) findViewById(R.id.editSurname);
		EditText txtMail = (EditText) findViewById(R.id.editMail);
		EditText txtPass = (EditText) findViewById(R.id.editPassword);
		EditText txtConfPass = (EditText) findViewById(R.id.editConfPass);
		if ((txtUsername.getText().toString().equals("")) ||
			(txtName.getText().toString().equals("")) ||
			(txtSurname.getText().toString().equals("")) ||
			(txtMail.getText().toString().equals("")) ||
			(txtPass.getText().toString().equals("")) ||
			(txtConfPass.getText().toString().equals(""))) {
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				CharSequence text = "Please, fill in all fields";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
		} else {
			String password = txtPass.getText().toString();
			String confpass = txtConfPass.getText().toString();
			if (! (password.equals(confpass))) {
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				CharSequence text = "Password mismatch";
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			} else {
				SingletonUser.getInstance().registration(handler, txtMail.getText().toString(),
						txtName.getText().toString(), txtPass.getText().toString(), txtSurname.getText().toString(),
						txtUsername.getText().toString(), getIntent().getSerializableExtra("serverurl").toString());
			}
		}
			
		
	}
	
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;
			CharSequence text = null;
			if (msg.obj instanceof RegistrationReply)
				text = ((RegistrationReply)msg.obj).getValue();
			else
				if (msg.obj instanceof Problem)
					text = ((Problem)msg.obj).getDescription();
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			onBackPressed();
		}
	};

	  
}
