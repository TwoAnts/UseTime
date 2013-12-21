package com.lzmy.usetime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity"; 
	
	private TextView textView = null;
	
	private DateFormat dayFormat = null;
	private String mapName = null;
	private Map<Long, Long> mMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dayFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		textView = (TextView)findViewById(R.id.textview);
		mapName = UseTimeService.getMapName(""+Calendar.getInstance().get(Calendar.YEAR));
		
		Intent intent = new Intent(MainActivity.this, UseTimeService.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.get_hisory:
			mMap = ReadAndWrite.readFile(getApplicationContext(), mapName);
			System.gc();
			textView.setText(mapToString(mMap));
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private long getTimeFromMap(Map<Long, Long> mMap, long key){
		if(mMap.containsKey(key)){
			return (long)mMap.get(key);
		}
		return 0;
	}
	
	private String mapToString(Map<Long, Long> mMap){
		if(mMap == null){
			return null;
		}
		String todayString = dayFormat.format(System.currentTimeMillis());
		StringBuilder builder = new StringBuilder();
//		builder.append("--------------\n");
		Iterator iter = mMap.entrySet().iterator();  
		long key = 0;
		long val = 0;
		while (iter.hasNext()) {  
		    Map.Entry entry = (Map.Entry) iter.next();  
		    key = (Long)entry.getKey(); 
		    val = (Long)entry.getValue();  
		    builder.append(dayFormat.format(key)+" "+val/60000+"min\n");
		}  
		if(builder.length() == 0){
			return "至今尚无记录！";
		}
		
		return builder.toString();
	}

}
