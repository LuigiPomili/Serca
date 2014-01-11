package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.sercamobile.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserArrayAdapter extends ArrayAdapter<String>{
	
	private final Activity context;
	private final String[] username;
	private final String[] score;
	private final String[] status;
	
	public UserArrayAdapter(Activity context, String[] username , String[] score, String[] status) {
		super(context, R.layout.userlist, username);
		this.context = context;
		this.username = username;
		this.score = score;
		this.status = status;
	}
	
	static class ViewHolder_User {
		protected TextView txtUsername;
		protected TextView txtScore;
		protected TextView txtStatus;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder_User holder1;
		View rowView1 = convertView;
		if (rowView1 == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView1 = inflater.inflate(R.layout.userlist, null, true);
			holder1 = new ViewHolder_User();
			holder1.txtUsername = (TextView) rowView1.findViewById(R.id.txtUsername);
			holder1.txtScore = (TextView) rowView1.findViewById(R.id.txtScore);
			holder1.txtStatus = (TextView) rowView1.findViewById(R.id.txtStatus);
			rowView1.setTag(holder1);
		} else {
			holder1 = (ViewHolder_User) rowView1.getTag();
		}
		
		holder1.txtUsername.setText(username[position]);
		holder1.txtScore.setText("Score: " + score[position]);
		holder1.txtStatus.setText("Status: " + status[position]);
		return rowView1;
	}

}
