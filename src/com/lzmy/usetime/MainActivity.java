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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity"; 
	
	private TextView timetxtv = null;
	private TextView historytxtv = null;
	private Button refreshBtn = null;
	private Button shareRenrenBtn = null;
	
	private long todayTime = 0;
	private long time = 0;
	
	private DateFormat dayFormat = null;
	private String mapName = null;
	private Map<Long, Long> mMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_test);
		initView();
		
		todayTime = UseTimeService.tranTimeToToday(System.currentTimeMillis());
		
		mapName = UseTimeService.getMapName(""+Calendar.getInstance().get(Calendar.YEAR));
		refreshMap();
		
		Intent intent = new Intent(MainActivity.this, UseTimeService.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intent);
	}
	
	private void initView(){
		dayFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		timetxtv = (TextView)findViewById(R.id.timetxtv);
		historytxtv = (TextView)findViewById(R.id.historytxtv);
		
		refreshBtn = (Button)findViewById(R.id.refresh_btn);
		refreshBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				refreshMap();
			}
		});
		
		shareRenrenBtn = (Button)findViewById(R.id.share_btn);
		shareRenrenBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, ShareRenrenActivity.class);
				intent.putExtra("use_time", time);
				intent.setAction(ShareRenrenActivity.ACTION_SHARE_DAYTIME);
				startActivity(intent);
			}
		});
	}
	
	private void refreshMap(){
		mMap = ReadAndWrite.readFile(getApplicationContext(), mapName);
		if(mMap.containsKey(todayTime)){
			time = mMap.get(todayTime);
			timetxtv.setText(UseTimeService.tranTimeToString(time));
		}
		historytxtv.setText(mapToString(mMap));
		System.gc();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		// TODO Auto-generated method stub
//		switch (item.getItemId()) {
//		case R.id.get_hisory:
//			refreshMap();
//			break;
//
//		default:
//			break;
//		}
//		return super.onMenuItemSelected(featureId, item);
//	}
	
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
		if(iter.hasNext()){
			iter.next();
		}
		while (iter.hasNext()) {  
		    Map.Entry entry = (Map.Entry) iter.next();  
		    key = (Long)entry.getKey(); 
		    val = (Long)entry.getValue();  
		    builder.append(dayFormat.format(key)+"  "+UseTimeService.tranTimeToString(val)+"\n");
		}  
		if(builder.length() == 0){
			return "至今尚无记录！";
		}
		
		return builder.toString();
	}

}
