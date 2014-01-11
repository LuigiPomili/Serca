package it.unibo.cs.swarch.sercamobile;


import it.unibo.cs.swarch.sercamobile.R;
import it.unibo.cs.swarch.protocol.simplexml.classes.IncomingChatMessage;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity implements OnClickListener{
	
	private Button butSend;
	private TextView txtChatRead;
	private TextView txtChatWrite;
	private SingletonUser user;
	private String kind = null;
	private boolean isgame = false;
	
	public void onCreate(Bundle savedInstanceState) {
		user = SingletonUser.getInstance();
		if (getIntent().getSerializableExtra("chattype").toString().contains("global")){
			user.setChathandler(handler, "global");
			kind = "global";
			if (getIntent().getSerializableExtra("chattype").toString().equals("globalgame")){
				setContentView(R.layout.chat_menu);
				isgame = true;
			}else
				setContentView(R.layout.chat);
		}else{
			user.setChathandler(handler, "local");
			kind = "local";
			setContentView(R.layout.chat_menu);
			isgame = true;
		}
        super.onCreate(savedInstanceState);
        butSend = (Button) findViewById(R.id.butSend);
        txtChatWrite = (TextView) findViewById(R.id.txtChatSend);
        txtChatRead = (TextView) findViewById(R.id.txtChatReceve);
        txtChatRead.setTextColor(Color.BLACK);
        txtChatRead.setMovementMethod(new ScrollingMovementMethod());
        butSend.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (!(txtChatWrite.getText().toString().equals(""))) {
			user.sendChatMessage(txtChatWrite.getText().toString(), kind);
			txtChatWrite.setText("");
		}
	}
	
	public void onPause(){
		super.onPause();
	}
	
	public void onResume(){
		super.onResume();
	}
	
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	IncomingChatMessage icm = (IncomingChatMessage)msg.obj;
        	txtChatRead.append(icm.getSender() + ": " + icm.getMessage() + "\n");
        }
    };
    
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		if (isgame)
			user.killMe("game");
	}
	
}
