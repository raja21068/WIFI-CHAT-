package com.jayk.ipdetector;

import java.net.Socket;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MessageDialog extends Dialog{

	String ipAddress;
	String message;
	EditText messageText, nameText;
	
	public MessageDialog(Context context, String ip) {
		super(context);
		ipAddress = ip;
	}
	@Override
	public void show() {
		setContentView(R.layout.dialog_message_send);
		TextView ipText = (TextView)findViewById(R.id.textViewIp);
		messageText = (EditText)findViewById(R.id.editTextMessage);
		Button sendButton = (Button)findViewById(R.id.buttonSend);
		nameText = (EditText)findViewById(R.id.editTextName);
		
		ipText.setText(ipAddress);
		if(MainActivity.deviceAlias.containsKey(ipAddress)){
			MainActivity.deviceAlias.get(ipAddress);
		}
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("Wifi Chat", "Sending..");
				
				new SendMessageTask().execute(ipAddress,messageText.getText().toString());
				dismiss();
			}
		});
		
		super.show();
		
	}
	
	private class SendMessageTask extends AsyncTask<String, Void, String>{
		
		@Override
		protected String doInBackground(String... params) {
			String ip = params[0];
			String message = params[1];
			try{
				Socket socket = new Socket(ip,MainActivity.SERVER_PORT);
				socket.getOutputStream().write((message+"\n").getBytes());
		        socket.getOutputStream().flush();
		        socket.close();
		        if(!nameText.getText().toString().trim().equals(""))MainActivity.deviceAlias.put(ip, nameText.getText().toString());
		        return message;
			}catch(Exception ex){
				ex.printStackTrace();
				return "Sending Failed..";
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			MainActivity.adapterMessages.add("Me: "+result);
		}
	}

}
