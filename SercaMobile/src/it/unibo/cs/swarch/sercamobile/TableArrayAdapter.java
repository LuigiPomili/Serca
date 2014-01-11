package it.unibo.cs.swarch.sercamobile;

import it.unibo.cs.swarch.sercamobile.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TableArrayAdapter extends ArrayAdapter<String> implements OnClickListener{
	
	private final Activity context;
	private final String[] tablename;
	private final String[] userconn;
	private String clicked;
	private SingletonUser user = SingletonUser.getInstance();
	private Object taa = null;
	private Handler tlahandler = null;

	public TableArrayAdapter(Activity context, String[] tablename, String[] userconn, Handler tlahanhandler) {
		super(context, R.layout.tablelist, tablename);
		this.context = context;
		this.tablename = tablename;
		this.userconn = userconn;
		taa = this;
		this.tlahandler = tlahanhandler; 
	}

	static class ViewHolder {
		protected TextView txtNameTable;
		protected TextView txtUserConn;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.tablelist, null, true);
			holder = new ViewHolder();
			holder.txtNameTable = (TextView) rowView.findViewById(R.id.tablename);
			holder.txtUserConn = (TextView) rowView.findViewById(R.id.numconn);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		holder.txtNameTable.setText(tablename[position]);
		if (userconn[position].equals("1")) {
			holder.txtUserConn.setText(userconn[position] + "  player allowed on to the table");
		}else{
			holder.txtUserConn.setText(userconn[position] + "  players allowed on to the table");
		}
		((LinearLayout)holder.txtNameTable.getParent()).setOnClickListener(this);
		return rowView;
	}

	public void onClick(View v) {
		Message msg = new Message();
		if (((TextView)((LinearLayout)v).getChildAt(1)).getText().charAt(0) == '0'){
			msg.arg2 = 1;
		}
		msg.arg1 = 1;
		msg.obj = ((TextView)((LinearLayout)v).getChildAt(0)).getText().toString();
		tlahandler.sendMessage(msg);
	}

}
