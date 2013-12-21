package com.lzmy.usetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenActionReceiver extends BroadcastReceiver {
	
	public static final String SCREEN_ON= "com.lzmy.usetime.action.SCREEN_ON";
	public static final String SCREEN_OFF = "com.lzmy.usetime.action.SCREEN_OFF";
	public static final String USER_PRESENT = "com.lzmy.usetime.action.USER_PRESENT";

	private String TAG = "ScreenActionReceiver";
	private boolean isRegisterReceiver = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			Log.d(TAG, "屏幕解锁广播...");
			Intent mIntent = new Intent();
			mIntent.setClass(context, UseTimeService.class);
			mIntent.setAction(SCREEN_ON);
			context.startService(mIntent);
		}else if(action.equals(Intent.ACTION_USER_PRESENT)){
			Log.d(TAG, "用户操作广播...");
			Intent mIntent = new Intent();
			mIntent.setClass(context, UseTimeService.class);
			mIntent.setAction(USER_PRESENT);
			context.startService(mIntent);
		} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			Log.d(TAG, "屏幕加锁广播...");
			Intent mIntent = new Intent();
			mIntent.setClass(context, UseTimeService.class);
			mIntent.setAction(SCREEN_OFF);
			context.startService(mIntent);
		}
	}

	
	
	public void registerScreenActionReceiver(Context mContext) {
		if (!isRegisterReceiver) {
			isRegisterReceiver = true;

			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			Log.d(TAG, "注册屏幕解锁、加锁广播接收者...");
			mContext.registerReceiver(ScreenActionReceiver.this, filter);
		}
	}

	public void unRegisterScreenActionReceiver(Context mContext) {
		if (isRegisterReceiver) {
			isRegisterReceiver = false;
			Log.d(TAG, "注销屏幕解锁、加锁广播接收者...");
			mContext.unregisterReceiver(ScreenActionReceiver.this);
		}
	}

}
