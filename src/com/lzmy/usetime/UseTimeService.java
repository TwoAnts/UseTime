package com.lzmy.usetime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class UseTimeService extends Service{
	
	public static final String TAG = "UseTimeService";
	
	private DateFormat dayFormat = null;
	private ScreenActionReceiver mScreenReceiver = null;
	private Handler handler = null;
	private MissLi li = null; 
	
	private long todayTime = 0;
	private long timeSum = 0;
	private Map<Long, Long> mMap = null; 
	private String mapName = null;
	
	public static String getMapName(String year){
		return "Y"+year+".dat";
	}
	
	public static long tranTimeToToday(long mTime){
		return mTime/86400000*86400000-(23-Calendar.ZONE_OFFSET)*3600000;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		dayFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		mScreenReceiver = new ScreenActionReceiver();
		mScreenReceiver.registerScreenActionReceiver(getApplicationContext());
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == 0x111){
					remebTime();
				}
			}
	
		};
		
		li = new MissLi(handler);
		li.start();
		
		todayTime = tranTimeToToday(System.currentTimeMillis());
		//history的文件名为Y年份.dat
		mapName = "Y"+Calendar.getInstance().get(Calendar.YEAR)+".dat";
		
		mMap = ReadAndWrite.readFile(getApplicationContext(), mapName);
		timeSum  = getTimeFromMap(mMap, todayTime);
		
		
	}
	
	private void remebTime(){
		
		if(todayTime != tranTimeToToday(System.currentTimeMillis())){
			todayTime = tranTimeToToday(System.currentTimeMillis());
		}
		
		timeSum += 60000;
		mMap.put(todayTime, timeSum);
		ReadAndWrite .writeFile(getApplicationContext(), mMap, mapName);
		Log.d(TAG, "remebTime");
		
	}
	
	private long getTimeFromMap(Map<Long, Long> mMap, long key){
		if(mMap.containsKey(key)){
			return (long)mMap.get(key);
		}
		return 0;
	}
	
	private String getToday(){
		return dayFormat.format(System.currentTimeMillis());
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent == null){
			return super.onStartCommand(intent, flags, startId);
		}
		if(intent.getAction() == null){
			return super.onStartCommand(intent, flags, startId);
		}
		if(intent.getAction().equals(ScreenActionReceiver.SCREEN_OFF)){
			li.cancel();
		}else if((intent.getAction().equals(ScreenActionReceiver.SCREEN_ON)
				|| intent.getAction().equals(ScreenActionReceiver.SCREEN_ON))
				  && li.running() == false){
			li.start();
		}

		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mScreenReceiver.unRegisterScreenActionReceiver(getApplicationContext());
	}


}
