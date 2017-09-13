package com.jayk.ipdetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class MessagesActivity extends Activity implements OnItemClickListener{
	
	ListView listMessages;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		
		listMessages = (ListView)findViewById(R.id.listViewMessages);
		listMessages.setOnItemClickListener(this);
		listMessages.setAdapter(MainActivity.adapterMessages);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		try{
			String ob = listMessages.getItemAtPosition(index).toString();
			String ip = ob.substring(ob.indexOf('(')+1,ob.indexOf(')'));
			new MessageDialog(this, ip).show();
		}catch(Exception ex){ex.printStackTrace();}
		
	}
	
	static void refreshList(){}
}
