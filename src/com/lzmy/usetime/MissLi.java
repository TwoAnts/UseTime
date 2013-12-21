package com.lzmy.usetime;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;

public class MissLi {
	
	public static final String TAG = "MissLi";
	
	private long delay = 0;
	private Handler handler = null; 
	
	
	private Timer mTimer = null;
	private boolean isStart = false;
	
	public MissLi(Handler handler){
		this(60000, handler);
	}
	
	public MissLi(long delay, Handler handler){
		this.delay = delay;
		this.handler = handler;
	}
	
	
	
	public void start(){
		mTimer = new Timer();
		System.gc();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0x111);
			}
		}, delay, delay);
		Log.d(TAG, "mTimer is started!");
		isStart = true;
	}
	 
	
	public boolean running(){
		return isStart;
	}
	
	public void cancel(){
		if(mTimer != null){
			mTimer.cancel();
		}
		isStart = false;
		Log.d(TAG, "mTimer is cancel!");
	}
	
}
