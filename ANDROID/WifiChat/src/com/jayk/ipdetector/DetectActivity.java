package com.jayk.ipdetector;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.StringTokenizer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class DetectActivity extends Activity implements OnClickListener, OnItemClickListener{

	Button detect, cancelButton;
	ProgressBar progressDetect;
	ListView listDevices;
	
	Handler handler;
	DetectTask detectTask;
	String device = "";
	String appName = "IpDetector";
	boolean stopDetectAsync = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detect);
		detect = (Button)findViewById(R.id.buttonDetect);
		cancelButton = (Button)findViewById(R.id.buttonCancel);
		progressDetect = (ProgressBar)findViewById(R.id.progressBarDetect);
		listDevices  = (ListView)findViewById(R.id.listViewDevices);
		listDevices.setAdapter(MainActivity.devicesArrayAdapter);
		
		detect.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		listDevices.setOnItemClickListener(this);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				MainActivity.devicesArrayAdapter.add(device);
			}
		};
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public void onClick(View v) {
		if(v == detect){
			detectTask = new DetectTask();
			detectTask.execute();
		}
		if(v == cancelButton){
			if(detectTask!=null){
				stopDetectAsync = true;				
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View compo, int index, long arg3) {
		String ip = listDevices.getItemAtPosition(index).toString();
		new MessageDialog(this, ip).show();
		
	}
	
	private void detectNewLocalDevice(){
		try{
			stopDetectAsync = false;
			if(!MainActivity.devicesArrayAdapter.isEmpty())MainActivity.devicesArrayAdapter.clear();
			
			// Detecting Local IP and subnet mask
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            Log.e(appName, intf.toString());
	            if(stopDetectAsync)break;
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                String ip =  inetAddress.getHostAddress();
	                StringTokenizer tokens = new StringTokenizer(ip, ".");
	                Log.e("DectActivity","parts->"+tokens.countTokens()+" - IP: "+ip);
	                if(tokens.countTokens() == 4 && ip.length() <= 15 & (!ip.startsWith("127"))){
	                	Log.e("DetectActivity", "Condition true");
	                	String subnet = ip.substring(0, ip.lastIndexOf('.'));
			
		                //discovering devices..
		                int timeout=2000;
		                for (int i=2;i<254;i++){
		                	String host=subnet + "." + i;
		                	InetAddress inetAddr = InetAddress.getByName(host);
		                	if(ip.trim().equals(host))continue;
		                	Log.e(appName,host);
		                	if(InetAddress.getByName(host).isReachable(timeout)){
		                		device = host;
		                		Log.e(appName, inetAddr.toString());
		                		Log.e(appName, ">"+inetAddr.getCanonicalHostName());
		                		
		                		handler.sendEmptyMessage(0);
		                	}
		                	if(stopDetectAsync)break;
		                }// end of loop
		                if(stopDetectAsync)break;
	                }
	            }// end of inner loop
	            
	        }// end of outer loop
		}catch(Exception ex){ex.printStackTrace();}
	}
	
	private class DetectTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDetect.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			detectNewLocalDevice();
			return null;
		}	
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDetect.setVisibility(View.INVISIBLE);
		}
	}
	
}
