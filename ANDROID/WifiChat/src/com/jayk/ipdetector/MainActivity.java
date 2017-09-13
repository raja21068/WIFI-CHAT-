package com.jayk.ipdetector;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	Button detectMenuButton, messageMenuButton , buttonExit;
	Intent detectIntent, messageIntent;
	
	public static int SERVER_PORT = 9090;
	String appName = "Wifi Chat";
	
	public static Handler handler;
	String addr = "";
	String msgText = "";
	
	public static Hashtable<String, String> deviceAlias = new Hashtable<String, String>();
	public static ArrayAdapter<String> adapterMessages; //Message Activity is Using It.
	public static ArrayAdapter<String> devicesArrayAdapter; //Detect Device Activity is Using it because it will not remove devices.
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		detectMenuButton = (Button) findViewById(R.id.buttonDevicesDetect);
		messageMenuButton= (Button) findViewById(R.id.buttonMessageMenu);
		buttonExit = (Button)findViewById(R.id.buttonExit);
		
		detectIntent = new Intent("com.jayk.ipdetector.DETECTACTIVITY");
		messageIntent = new Intent("com.jayk.ipdetector.MESSAGESACTIVITY");
		
		detectMenuButton.setOnClickListener(this);
		messageMenuButton.setOnClickListener(this);
		buttonExit.setOnClickListener(this);
		adapterMessages = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		devicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1); 
		
		
		handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				if(deviceAlias.containsKey(addr)){
					String value = deviceAlias.get(addr);
					addr = value+"("+addr+"):";
				}else{
					addr = "("+addr+")";
				}
				adapterMessages.add(addr+msgText);
				//startActivity(messageIntent);
			}
		};
		
		new ServerTask().execute();
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		if(v == detectMenuButton){
			startActivity(detectIntent);
		}
		else if(v == messageMenuButton){
			startActivity(messageIntent);
		}
		else if(v == buttonExit){
			finish();
		}
	}
	
	
	private class ServerTask extends AsyncTask<Void, Void, Void>{
		boolean running = true;
		
		@Override
		protected Void doInBackground(Void... params) {
			try{ServerSocket server = new ServerSocket(SERVER_PORT);
			while(running){
				try{
					Socket socket = server.accept();
					Log.e(appName, "Accepted "+socket);
					InputStream in = socket.getInputStream();
					String add = socket.getInetAddress().getHostAddress();
					addr = add;
					msgText = readLine(in);
					
					handler.sendEmptyMessage(0);
					socket.close();
					
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					// Vibrate for 500 milliseconds
					v.vibrate(500);
					ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
					toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); 
				}catch(Exception ex){ex.printStackTrace();}
			}
			}catch(Exception ex){ex.printStackTrace();}
			return null;
		}
		
		String readLine(InputStream in)throws Exception{
			StringBuilder str = new StringBuilder();
			char ch = (char)in.read();
			while(ch != '\n'){
				str.append(ch);
				ch = (char)in.read();
			}
			return str.toString();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), "Server Stoped", Toast.LENGTH_LONG).show();
		}
	}
	
}
