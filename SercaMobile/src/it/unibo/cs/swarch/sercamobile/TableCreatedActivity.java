package it.unibo.cs.swarch.sercamobile;

import java.util.List;

import it.unibo.cs.swarch.sercamobile.R;
import it.unibo.cs.swarch.protocol.simplexml.classes.CreateTableReply;
import it.unibo.cs.swarch.protocol.simplexml.classes.Gameover;
import it.unibo.cs.swarch.protocol.simplexml.classes.HandFinished;
import it.unibo.cs.swarch.protocol.simplexml.classes.MoveReply;
import it.unibo.cs.swarch.protocol.simplexml.classes.SubscriptionReply;
import it.unibo.cs.swarch.protocol.simplexml.classes.SubscriptionReply.UsersAlreadySubscribed.SubscribedUser;
import it.unibo.cs.swarch.protocol.simplexml.classes.UserHasJoinedTheTable;
import it.unibo.cs.swarch.protocol.simplexml.classes.UserHasLeftTheTable;
import it.unibo.cs.swarch.protocol.simplexml.classes.UsersList.User;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TableCreatedActivity extends Activity implements OnClickListener{
	
	TextView[] playerslist = new TextView[4];
	ImageView[] cardslist = new ImageView[13];
	ImageView[] ontablecards = new ImageView[4];
	SingletonUser user;
	String firstplayer = null;
	boolean gamestarted = false;
	String[] cardsidslist = new String[13];
	int playerindex = 0;
	boolean iswatcher = false;
	List<SubscribedUser> watcherplayerlist;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    user = SingletonUser.getInstance();
	    setContentView(R.layout.table_started);

	    for(int i = 0; i < 13; i++){
	    	cardslist[i] = (ImageView) findViewById(R.id.img1 + i);
	    }
	    String abc = String.valueOf(R.id.img1) + " " + String.valueOf(R.id.img2) + " " + String.valueOf(R.id.img3) + " "
	    		+ String.valueOf(R.id.img4) + " " + String.valueOf(R.id.img5) + " " + String.valueOf(R.id.img6) + " "
	    		+ String.valueOf(R.id.img7) + " " + String.valueOf(R.id.img8) + " " + String.valueOf(R.id.img9) + " "
	    		+ String.valueOf(R.id.img10) + " " + String.valueOf(R.id.img11) + " " + String.valueOf(R.id.img12) + " "
	    		+ String.valueOf(R.id.img13);
	    if (!user.isSubscription()){
		    playerslist[0] = (TextView) findViewById(R.id.txtTavUsr1);
		    playerslist[1] = (TextView) findViewById(R.id.txtTavUsr2);
		    playerslist[2] = (TextView) findViewById(R.id.txtTavUsr3);
		    playerslist[3] = (TextView) findViewById(R.id.txtTavUsr4);
		    ontablecards[0] = (ImageView) findViewById(R.id.imgGioc1);
		    ontablecards[1] = (ImageView) findViewById(R.id.imgGioc2);
		    ontablecards[2] = (ImageView) findViewById(R.id.imgGioc3);
		    ontablecards[3] = (ImageView) findViewById(R.id.imgGioc4);
		    playerslist[0].setText(user.getUsername());
	    	CreateTableReply ctr = user.getCreateTableReplyResults();
	    	gamestarted = ctr.isGameIsStarted();
	    	firstplayer = ctr.getFirstPlayer();
	    	if (firstplayer != null)
				for(int i = 0; i < 4; i++)
					if (playerslist[i].getText().equals(firstplayer)){
        				playerslist[i].setTextColor(Color.RED);
        				break;
					}
	    	Bitmap cardbitmap = null;
	    	String cardstring = null;
	    	for(int i = 0; i < 13; i++){
	    		cardstring = ctr.getCardsList().getCard().get(i).toLowerCase();
	    		cardstring = cardstring.charAt(cardstring.length() - 1)
	    						+ cardstring.substring(0, cardstring.length() - 1);
	    		cardbitmap = BitmapFactory.decodeResource(getResources(),
    					getResources().getIdentifier("drawable/" + cardstring, null, getPackageName()));
	    		cardslist[i].setImageBitmap(cardbitmap);
	    		cardsidslist[i] = ctr.getCardsList().getCard().get(i);
	    		cardslist[i].setOnClickListener(this);
	    	}
	    	for(int i = 0; i < user.getBotNo(); i++){
	    		playerslist[i + 1].setText("Bot" + i);
	    	}
	    }else{
	    	SubscriptionReply sr = user.getSubscriptionReplyResults();
	    	List<SubscribedUser> players = sr.getUsersAlreadySubscribed().getSubscribedUser();
	    	if (sr.getUsersAlreadySubscribed().getSubscribedUser().get(0).getCardsList() != null)
	    		iswatcher = true;
	    	if (!iswatcher)
	    	for (int i = 0; i < players.size(); i++){
	    		if (players.get(i).getUid().equals(user.getUsername())){
	    	    	Bitmap cardbitmap = null;
	    	    	String cardstring = null;
	    			for(int j = 0; j < players.get(i).getCardsList().getCard().size(); j++){
	    				cardstring = players.get(i).getCardsList().getCard().get(j).toLowerCase();
	    	    		cardstring = cardstring.charAt(cardstring.length() - 1)
	    	    						+ cardstring.substring(0, cardstring.length() - 1);
	    	    		cardbitmap = BitmapFactory.decodeResource(getResources(),
	        					getResources().getIdentifier("drawable/" + cardstring, null, getPackageName()));
	    	    		cardslist[j].setImageBitmap(cardbitmap);
	    	    		cardsidslist[j] = players.get(i).getCardsList().getCard().get(j);
	    	    		cardslist[j].setOnClickListener(this);
	    			}
	    		int actualindex = -1;
	    		for (int j = 0; j < 4; j++){
					actualindex = (i + j) % 4;
	    			switch (j) {
					case 0:
						playerslist[actualindex] = (TextView) findViewById(R.id.txtTavUsr1);
						ontablecards[actualindex] = (ImageView) findViewById(R.id.imgGioc1);
						playerslist[actualindex].setText(user.getUsername());
						break;
					case 1:
						playerslist[actualindex] = (TextView) findViewById(R.id.txtTavUsr2);
						ontablecards[actualindex] = (ImageView) findViewById(R.id.imgGioc2);
						if (actualindex < (players.size()) && (players.get(actualindex) != null))
							playerslist[actualindex].setText(players.get(actualindex).getUid());
						break;
					case 2:
						playerslist[actualindex] = (TextView) findViewById(R.id.txtTavUsr3);
						ontablecards[actualindex] = (ImageView) findViewById(R.id.imgGioc3);
						if (actualindex < (players.size()) && (players.get(actualindex) != null))
							playerslist[actualindex].setText(players.get(actualindex).getUid());
						break;
					default:
						playerslist[actualindex] = (TextView) findViewById(R.id.txtTavUsr4);
						ontablecards[actualindex] = (ImageView) findViewById(R.id.imgGioc4);
						if (actualindex < (players.size()) && (players.get(actualindex) != null))
							playerslist[actualindex].setText(players.get(actualindex).getUid());
						break;
					}
	    		}
	    		playerindex = i;
	    			
	    		}
	    	}else{
			    playerslist[0] = (TextView) findViewById(R.id.txtTavUsr1);
			    playerslist[1] = (TextView) findViewById(R.id.txtTavUsr2);
			    playerslist[2] = (TextView) findViewById(R.id.txtTavUsr3);
			    playerslist[3] = (TextView) findViewById(R.id.txtTavUsr4);
			    ontablecards[0] = (ImageView) findViewById(R.id.imgGioc1);
			    ontablecards[1] = (ImageView) findViewById(R.id.imgGioc2);
			    ontablecards[2] = (ImageView) findViewById(R.id.imgGioc3);
			    ontablecards[3] = (ImageView) findViewById(R.id.imgGioc4);
			    for (int i = 0; i < players.size(); i++){
			    	playerslist[i].setText(players.get(i).getUid());
			    }
			    Bitmap cardbitmap = null;
    	    	String cardstring = null;
    	    	for (int i = 0; i < players.size(); i++)
    	    			if (players.get(i).getCardsList().getCard().remove(players.get(i).getCardOnTable())){
    	    				String cardstring2 = players.get(i).getCardOnTable().toLowerCase();
            				cardstring2 = cardstring2.charAt(cardstring2.length() - 1)
            	    						+ cardstring2.substring(0, cardstring2.length() - 1);
    	    				ontablecards[i].setImageBitmap(BitmapFactory.decodeResource(getResources(),
            						getResources().getIdentifier("drawable/" + cardstring2, null, getPackageName())));
    	    			}
			    for (int i = 0; i < players.get(0).getCardsList().getCard().size(); i++){
			    	cardstring = players.get(0).getCardsList().getCard().get(i).toLowerCase();
    	    		cardstring = cardstring.charAt(cardstring.length() - 1)
    	    						+ cardstring.substring(0, cardstring.length() - 1);
    	    		cardbitmap = BitmapFactory.decodeResource(getResources(),
        					getResources().getIdentifier("drawable/" + cardstring, null, getPackageName()));
    	    		cardslist[i].setImageBitmap(cardbitmap);
    	    		cardsidslist[i] = players.get(0).getCardsList().getCard().get(i);
			    }
				for (int i = players.get(0).getCardsList().getCard().size(); i < 13; i++){
					cardbitmap = BitmapFactory.decodeResource(getResources(),
        					getResources().getIdentifier("drawable/cover", null, getPackageName()));
					cardslist[i].setImageBitmap(cardbitmap);
				}
			    watcherplayerlist = players;
	    	}
	    }
    	if (!gamestarted)
    		setCardsToClickable(false);
	    user.setTableCreatedhandler(handler);
	}

	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	//MoveReply ok
        	if (msg.obj instanceof MoveReply){
        		if (msg.arg1 == 0){
        			MoveReply mr = (MoveReply) msg.obj;
        			if (mr.getMoveOf().equals(user.getUsername())){
        				int clickedcard = -1;
        				for (int i = 0; i < 13; i++)
        					if (cardsidslist[i].equals(mr.getCard())){
        						clickedcard = i;
        						break;
        					}
        				String cardstring = mr.getCard().toLowerCase();
        				cardstring = cardstring.charAt(cardstring.length() - 1)
        	    						+ cardstring.substring(0, cardstring.length() - 1);
        				ontablecards[playerindex].setImageBitmap(BitmapFactory.decodeResource(getResources(),
        						getResources().getIdentifier("drawable/" + cardstring, null, getPackageName())));
        				cardslist[clickedcard].setImageBitmap(BitmapFactory.decodeResource(getResources(),
        						getResources().getIdentifier("drawable/cover", null, getPackageName())));
        				cardsidslist[clickedcard] = "covered";
        			}else{
        				int playerindex2 = -1;
        				for(int i = 0; i < 4; i++)
        					if (playerslist[i].getText().equals(mr.getMoveOf())){
        						playerindex2 = i;
        						break;
        					}
        				String cardstring = mr.getCard().toLowerCase();
        				cardstring = cardstring.charAt(cardstring.length() - 1)
        	    						+ cardstring.substring(0, cardstring.length() - 1);
        				ontablecards[playerindex2].setImageBitmap(BitmapFactory.decodeResource(getResources(),
        						getResources().getIdentifier("drawable/" + cardstring, null, getPackageName())));
        				if (iswatcher){
        					watcherplayerlist.get(playerindex2).getCardsList().getCard().remove(mr.getCard());
        					Bitmap cardbitmap = null;
        	    	    	cardstring = null;
        	    	    	playerindex2 = -1;
            				for(int i = 0; i < 4; i++)
            					if (playerslist[i].getText().equals(mr.getNextTurnOf())){
            						playerindex2 = i;
            						break;
            					}
        	    	    	List<String> nextplayercards = watcherplayerlist.get(playerindex2).getCardsList().getCard();
        					for (int i = 0; i < nextplayercards.size(); i++){
        				    	cardstring = nextplayercards.get(i).toLowerCase();
        	    	    		cardstring = cardstring.charAt(cardstring.length() - 1)
        	    	    						+ cardstring.substring(0, cardstring.length() - 1);
        	    	    		cardbitmap = BitmapFactory.decodeResource(getResources(),
        	        					getResources().getIdentifier("drawable/" + cardstring, null, getPackageName()));
        	    	    		cardslist[i].setImageBitmap(cardbitmap);
        	    	    		cardsidslist[i] = nextplayercards.get(i);
        					}
        					for (int i = nextplayercards.size(); i < 13; i++){
        						cardbitmap = BitmapFactory.decodeResource(getResources(),
        	        					getResources().getIdentifier("drawable/cover", null, getPackageName()));
        						cardslist[i].setImageBitmap(cardbitmap);
        					}
        				}
        			}
        			for(int i = 0; i < 4; i++)
        				if (playerslist[i].getText().equals(mr.getNextTurnOf()))
                			playerslist[i].setTextColor(Color.RED);
        				else
        					playerslist[i].setTextColor(Color.BLACK);
        			setCardsToClickable(mr.getNextTurnOf().equals(user.getUsername()));
        		}else{
        			Context context = getApplicationContext();
        			CharSequence text = "Move failed";
        			int duration = Toast.LENGTH_LONG;
        			Toast toast = Toast.makeText(context, text, duration);
        			toast.show();
        		}
        	}else if(msg.obj instanceof UserHasJoinedTheTable){
        		UserHasJoinedTheTable uhjtt = (UserHasJoinedTheTable)msg.obj;
        		if (firstplayer != null){
            		firstplayer = uhjtt.getFirstPlayer();
        			for(int i = 0; i < 4; i++)
        				if (playerslist[i].getText().equals(firstplayer)){
                			playerslist[i].setTextColor(Color.RED);
                			break;
        				}
        		}
        		gamestarted = uhjtt.isGameStarted();
        		if (!uhjtt.getUserThatHasJoined().equals(user.getUsername())){
        		int playerindex2 = -1;
        		for (int i = 0; i < 4; i++)
        			if (playerslist[i].getText().length() == 0){
        				playerindex2 = i;
        				break;
        			}
        			if (playerindex2 != -1)
        				playerslist[playerindex2].setText(uhjtt.getUserThatHasJoined());
        		}
        		setCardsToClickable(gamestarted);
        	}else if (msg.obj instanceof HandFinished){
        		HandFinished hf = (HandFinished)msg.obj;
        		for(int i = 0; i < 4; i++){
        			ontablecards[i].setImageBitmap(BitmapFactory.decodeResource(getResources(),
    						getResources().getIdentifier("drawable/blankcover", null, getPackageName())));
        		}
    			Context context = getApplicationContext();
    			CharSequence text = "The Hand Winner Is: " + hf.getHandWinner();
    			int duration = Toast.LENGTH_LONG;
    			Toast toast = Toast.makeText(context, text, duration);
    			toast.show();
        	}else if (msg.obj instanceof Gameover){
        		Gameover go = (Gameover)msg.obj;
        		Context context = getApplicationContext();
        		CharSequence text = null;
        		if (go.getReason().equals("there is a winner"))
        			text = "Game Over, and the winner is: " + go.getWinnerIs();
        		else
        			text = "Game Over, because " + go.getReason();
    			int duration = Toast.LENGTH_LONG;
    			Toast toast = Toast.makeText(context, text, duration);
    			toast.show();
        	}else if (msg.obj instanceof UserHasLeftTheTable){
        		UserHasLeftTheTable uhltt = (UserHasLeftTheTable)msg.obj;
        		Context context = getApplicationContext();
    			CharSequence text = uhltt.getUserThatHasLeft() + "Has Left The Table";
    			int duration = Toast.LENGTH_LONG;
    			Toast toast = Toast.makeText(context, text, duration);
    			toast.show();
    			int playerindex2 = -1;
    			for(int i = 0; i < 4; i++)
    				if (playerslist[i].getText().equals(uhltt.getUserThatHasLeft())){
    					playerindex2 = i;
    					break;
    				}
        		playerslist[playerindex2].setText(uhltt.getBotName());
        	}
        }
	};
	
	public void setCardsToClickable(boolean value){
		for(int i = 0; i < 13; i++){
			if (value && (cardsidslist[i].equals("covered")))
				cardslist[i].setClickable(false);
			else
				cardslist[i].setClickable(value);
		}
	}
	
	public void onClick(View v) {
		setCardsToClickable(false);
		user.move(cardsidslist[v.getId() - cardslist[0].getId()]);
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		user.killMe("game");
	}

}
