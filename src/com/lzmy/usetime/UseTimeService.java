package com.lzmy.usetime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
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
	
	/**
	 * format timeSum to a string(hour+minute+second)
	 * @param mTimeSum long, the length of time  
	 * @return String a string(hour+minute+second) from mTimeSum
	 */
	
	public static String tranTimeToString(long mTimeSum){
		int hour   = (int)(mTimeSum/3600000);
		int minute    = (int)((mTimeSum%3600000)/60000);
		int second = (int)((mTimeSum%60000)/1000);
		StringBuilder builder = new StringBuilder();
		
		if(hour > 0){
			builder.append(hour+"小时");
		}
		if(minute > 0){
			builder.append(minute+"分钟");
		}
		if(second > 0){
			builder.append(second+"秒");
		}
		if(builder.length() == 0){
			return "0秒";
		}
		return builder.toString();
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
				
				//msg.arg1 0：不启动其他activity 1：启动目标activity（notification的PendingIntent）
				//msg.arg2 integer型， 分钟数
				//msg.obj  strings[2]型，notification的contentTitle和contentText
				//msg.what 0x111指定将时间写入文件 0x222指定发送notification 
				if(msg.what == 0x111){
					remebTime();
//					whenUseTooLong();
				}else if(msg.what == 0x222){
					outNotification(msg);
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
	
	/**
	 * write the new timeSum to file 
	 */
	
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
	
	private void whenUseTooLong(){
		String[] strings = new String[2];
		boolean flag = false;
		switch((int)(timeSum/1800000)){
			case 1:
				strings[0] = "你已经使用了超过30分钟！";
				strings[1] = "李小姐温馨提示！";
				strings[1] = "点击分享到人人！";
				flag = true;
				break;
			case 2:
				strings[0] = "你已经使用了超过一个小时！";
				strings[1] = "李小姐提醒您爱护眼睛！";
				strings[1] = "点击分享到人人！";
				flag = true;
				break;
			case 4:
				strings[0] = "你已经使用了超过两个小时！";
				strings[1] = "李小姐提醒您注意休息！";
				strings[1] = "点击分享到人人！";
				flag = true;
				break;
			case 6:
				strings[0] = "你已经使用了超过三个小时！";
				strings[1] = "李小姐提醒您注意身体！";
				strings[1] = "点击分享到人人！";
				flag = true;
				break;
			case 8:
				strings[0] = "你已经使用了超过四个小时！";
				strings[1] = "李小姐提醒注意手机电量！";
				strings[1] = "点击分享到人人！";
				flag = true;
				break;
			case 10:
				strings[0] = "Legendary！超过五个小时！";
				strings[1] = "李小姐已经去睡觉了！";
				strings[1] = "点击分享到人人！";
				flag = true;
				break;
		}
		if(flag == true){
			Message msg = new Message();
			msg.arg1 = 1;
			msg.obj = strings;
			msg.what = 0x222;
			outNotification(msg);
		}
		
	}
	
	/**
	 * pull a notification, whose title is strings[0], text is strings[1]. 
	 * @param msg Message. from class MissLi
	 */
	
	private void outNotification(Message msg){
		//判断msg是否合法，主要是msg.obj
		if(msg.obj == null){
			Log.e(TAG, "strings[](msg.obj) is null!");
			return ;
		}
		String[] strings = (String[])msg.obj;
		if(strings.length != 2){
			Log.e(TAG, "strings[](msg.obj) is invalid!");
			return ;
		}
		
//		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		//创建intent，用于传给ShareActivity
		Intent intent = new Intent(this, ShareRenrenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setAction(ShareRenrenActivity.ACTION_SHARE_LONGTIME);
		intent.putExtra("long_time", timeSum);
		//你懂得
		PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		//构造notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle(strings[0])
        .setContentText(strings[1]).setAutoCancel(true);
		
//		builder.setLargeIcon(b).setNumber(50);
		//判断是否需要启动ShareRenrenActivity
        if(msg.arg1 == 1){
        	builder.setContentIntent(pIntent);
        }
		//显示notification
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = builder.build();
	    manager.notify(11, notification);
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
