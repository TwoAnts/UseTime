package com.lzmy.usetime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
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
	
	private static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private static Lock readLock = rwl.readLock();
	
	public static void writeFile(Context mContext, Map<Long, Long> mMap, String nameString){
		long oldTime = System.currentTimeMillis();
		readLock.lock();
		try {
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
		Log.d(TAG, "read map successful!");
		Log.d(TAG, "read cost "+ (System.currentTimeMillis() - oldTime) +"ms");
		return map;
	}
	
}
