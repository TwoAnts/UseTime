package com.lzmy.usetime;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MissLi {
	
	public static final String TAG = "MissLi";
	
	private long delay = 0;
	private Handler handler = null; 
	
	
	private Timer mTimer = null;
	private boolean isStart = false;
	private int minsum = 0;
	
	private boolean flag = false;
	
	public MissLi(Handler handler){
		this(60000, handler);
	}
	
	public MissLi(long delay, Handler handler){
		this.delay = delay;
		this.handler = handler;
	}
	
	
	
	public void start(){
		if(isStart == true){
			Log.d(TAG, "MissLi is running!");
			return ;
		}
		final String[] strings = new String[2];
		flag = false;
		minsum = 0;
		//test
//		Message msg = new Message();
//		msg.arg1 = 1;
//		strings[0] = "哈哈，notification已发送！";
//		strings[1] = "点击启动人人分享。注意：这只是测试";
//		msg.obj = strings;
//		msg.what = 0x222;
//		handler.sendMessage(msg);
		
		mTimer = new Timer();
		System.gc();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				//更新时间
				handler.sendEmptyMessage(0x111);
				
				//判断是否发送通知
				minsum++;
				switch(minsum){
					case 30:
						strings[0] = "你已经使用了超过30分钟！";
						strings[1] = "李小姐温馨提示！";
						flag = true;
						break;
					case 60:
						strings[0] = "你已经使用了超过一个小时！";
						strings[1] = "李小姐提醒您爱护眼睛！";
						flag = true;
						break;
					case 120:
						strings[0] = "你已经使用了超过两个小时！";
						strings[1] = "李小姐提醒您注意休息！";
						flag = true;
						break;
					case 180:
						strings[0] = "你已经使用了超过三个小时！";
						strings[1] = "李小姐提醒您注意身体！";
						flag = true;
						break;
					case 240:
						strings[0] = "你已经使用了超过四个小时！";
						strings[1] = "李小姐提醒注意手机电量！";
						flag = true;
						break;
					case 300:
						strings[0] = "Legendary！超过五个小时！";
						strings[1] = "李小姐已经去睡觉了！";
						flag = true;
						break;
				}
				
				if (flag) {
					Message msg = new Message();
					//msg.arg1 0：不启动其他activity 1：启动目标activity（notification的PendingIntent）
					//msg.arg2 integer型， 分钟数
					//msg.obj  strings[2]型，notification的contentTitle和contentText
					//msg.what 0x222指定发送notification 0x111指定将时间写入文件
					msg.what = 0x222;
					msg.obj = strings;
					msg.arg1 = 1;
					handler.sendMessage(msg);
				}
			}
		}, delay, delay);
		Log.d(TAG, "mTimer is started!");
		isStart = true;
	}
	 
	
	public boolean running(){
		return isStart;
	}
	
	public int getMinSum(){
		return minsum;
	}
	
	public void cancel(){
		if(mTimer != null){
			mTimer.cancel();
		}
		isStart = false;
		minsum = 0;
		Log.d(TAG, "mTimer is cancel!");
	}
	
}
