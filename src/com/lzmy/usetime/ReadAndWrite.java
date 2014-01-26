package com.lzmy.usetime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Context;
import android.util.Log;

public class ReadAndWrite {
	
	/**
	 * this class only has two public static methods.
	 * @author hzy
	 */
	
	public static final String TAG = "ReadAndWrite";
	
	public static final String TEMP_NAME = "TEMP.dat";
	
	private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private static Lock readLock = rwl.readLock();
	
	public static void writeFile(Context mContext, Map<Long, Long> mMap, String nameString){
		long oldTime = System.currentTimeMillis();
		long todayTime = UseTimeService.tranTimeToToday(oldTime);
		
		Long[] longs = readTEMP(mContext);
		Log.d(TAG, longs+"");
		if(longs[0] == null){
			writeTEMP(mContext, new Long[]{todayTime, (Long)mMap.get(todayTime)});
			return ;
		}
		if(longs[0].equals(todayTime)){
			writeTEMP(mContext, new Long[]{todayTime, (Long)mMap.get(todayTime)});
			return ;
		}
		writeTEMP(mContext, new Long[]{todayTime, (Long)mMap.get(todayTime)});
		
		
		try {
			readLock.lock();
			FileOutputStream fileOut = mContext.openFileOutput(nameString, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(mMap);
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			readLock.unlock();
		}
		Log.d(TAG, "write map successful!");
		Log.d(TAG, "write cost "+ (System.currentTimeMillis() - oldTime) +"ms");
	}
	
	
	
	public static Map<Long, Long> readFile(Context mContext, String nameString){
		long oldTime = System.currentTimeMillis();
		Map<Long, Long> map = new HashMap<Long, Long>();
		FileInputStream filein = null;
		File file = new File(mContext.getFilesDir()+File.separator+nameString );
		if(file.exists() == false){
			Long[] longs = readTEMP(mContext);
			if(longs[0] != null){
				map.put(longs[0], longs[1]);
			}
			return map;
		}
		try{
			filein = mContext.openFileInput(nameString);
			ObjectInputStream in = new ObjectInputStream(filein);
			map = (Map<Long, Long>)in.readObject();
			filein.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long[] longs = readTEMP(mContext);
		if(longs[0] != null){
			map.put(longs[0], longs[1]);
		}
		Log.d(TAG, "read map successful!");
		Log.d(TAG, "read cost "+ (System.currentTimeMillis() - oldTime) +"ms");
		return map;
	}
	
	private static void writeTEMP(Context mContext, Long[] timeSum){
		long oldTime = System.currentTimeMillis();
		try {
			readLock.lock();
			FileOutputStream fileOut = mContext.openFileOutput(TEMP_NAME, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(timeSum);
			fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			readLock.unlock();
		}
		Log.d(TAG, "write TEMP successful!");
		Log.d(TAG, "write cost "+ (System.currentTimeMillis() - oldTime) +"ms");
	}
	
	private static Long[] readTEMP(Context mContext){
		long oldTime = System.currentTimeMillis();
		Long[] longs = new Long[2];
		FileInputStream filein = null;
		File file = new File(mContext.getFilesDir()+File.separator+TEMP_NAME);
		if(file.exists() == false){
			return longs;
		}
		try{
			filein = mContext.openFileInput(TEMP_NAME);
			ObjectInputStream in = new ObjectInputStream(filein);
			longs = (Long[])in.readObject();
			filein.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "read TEMP successful!");
		Log.d(TAG, "read cost "+ (System.currentTimeMillis() - oldTime) +"ms");
		return longs;
	}
}
