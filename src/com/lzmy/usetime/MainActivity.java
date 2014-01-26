package com.lzmy.usetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static final String TAG = "MainActivity"; 
	
	private TextView timetxtv = null;
	private TextView historytxtv = null;
	private Button refreshBtn = null;
	private Button shareRenrenBtn = null;
	
	//今天0时0分0秒的long型时间，每点击刷新，会被刷新
	private long todayTime = 0;
	//今天使用时间，每点击刷新，会被刷新
	private long time = 0;
	
	//将日期转化为yyyy-MM-dd
	private DateFormat dayFormat = null;
	//文件的名字，通过UseTimeService的静态公共方法获得，会随着年份的变化而变化，一般不会变
	private String mapName = null;
	//通过读取文件获得，每点击刷新，会被刷新
	private Map<Long, Long> mMap = null;
	
	ArrayList<long[]> list = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_test);
		list = new ArrayList<long[]>();
		initView();
		
		todayTime = UseTimeService.tranTimeToToday(System.currentTimeMillis());
		
		mapName = UseTimeService.getMapName(""+Calendar.getInstance().get(Calendar.YEAR));
		initTxtFromMap();
		
		Intent intent = new Intent(MainActivity.this, UseTimeService.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(intent);
		
		intent.setClass(MainActivity.this, LineChartActivity.class);
		intent.putExtra("list", list);
//		startActivity(intent);
	}
	
	private void initView(){
		dayFormat = new SimpleDateFormat ("yyyy-MM-dd");
		timetxtv = (TextView)findViewById(R.id.timetxtv);
		
		refreshBtn = (Button)findViewById(R.id.refresh_btn);
		historytxtv = (TextView)findViewById(R.id.historytxtv);
		refreshBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//length大于7是指不是 "至今尚无记录"或""，用来判断是否为第一次读取map
				if(historytxtv.length() > 7){
					refreshMap();
				}else{
					initTxtFromMap();
				}
				
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
	
	//第一次读取map使用
	private void initTxtFromMap(){
		mMap = ReadAndWrite.readFile(getApplicationContext(), mapName);
		//更新今日使用时间
		if(mMap.containsKey(todayTime)){
			time = mMap.get(todayTime);
			timetxtv.setText(UseTimeService.tranTimeToString(time));
		}
		
		//更新历史时间
		historytxtv.setText(mapToString(mMap));
		System.gc();
	}
	
	//刷新，在第一次读取map后使用
	private void refreshMap(){
		if(todayTime != UseTimeService.tranTimeToToday(System.currentTimeMillis())){
			todayTime = UseTimeService.tranTimeToToday(System.currentTimeMillis());
		}
		mMap = ReadAndWrite.readFile(getApplicationContext(), mapName);
		//更新今日使用时间
		if(mMap.containsKey(todayTime)){
			timetxtv.setText(UseTimeService.tranTimeToString(mMap.get(todayTime)));
		}
		
		//更新历史时间
		if(mMap.containsKey(todayTime)){
			//仅对字符串进行更改，不再遍历map
			if(time != getTimeFromMap(mMap, todayTime)){
				time = getTimeFromMap(mMap, todayTime);
				StringBuilder builder = new StringBuilder(historytxtv.getText()); 
				//若已存在今日日期，仅更新时间；否则，插入新的今日时间
				if(builder.indexOf(dayFormat.format(todayTime)) != -1){
					builder.replace(builder.indexOf(" ")+2, //注意，空格数为2
							builder.indexOf("\n"), 
							UseTimeService.tranTimeToString(time));
				}else{
					builder.insert(0, dayFormat.format(todayTime)
										+"  "//注意，空格数为2
										+UseTimeService.tranTimeToString(time)
										+"\n");
				}
				historytxtv.setText(builder.toString());
			}
		}
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
	
	//由于map.get方法没有默认值，这里，使用函数对其包装一下，使得其默认值为0
	private long getTimeFromMap(Map<Long, Long> mMap, long key){
		if(mMap.containsKey(key)){
			return (long)mMap.get(key);
		}
		return 0;
	}
	
	//在第一次读取map时调用，将读取的map存进list，并排序，遍历 输出
	private String mapToString(Map<Long, Long> mMap){
		if(mMap == null){
			return "至今尚无记录！";
		}
		StringBuilder builder = new StringBuilder();
		
		//通过ArrayList构造函数把map.entrySet()转换成list 
		List<Map.Entry<Long,Long>> mappingList = new ArrayList<Map.Entry<Long,Long>>(mMap.entrySet()); 
		//通过比较器实现比较排序 
		Collections.sort(mappingList, new Comparator<Map.Entry<Long,Long>>(){ 
			public int compare(Map.Entry<Long,Long> mapping1,Map.Entry<Long,Long> mapping2){ 
				return -mapping1.getKey().compareTo(mapping2.getKey()); 
			} 
		}); 
		list.clear();
		  
		//遍历list,输出  格式："yyyy-MM-dd"+"  "+"时间"+"\n"
		for(Map.Entry<Long,Long> mapping:mappingList){ 
			builder.append(dayFormat.format(mapping.getKey())
					  +"   "//注意空格数为3
					  +UseTimeService.tranTimeToString(mapping.getValue())
					  +"\n");
			list.add(new long[]{mapping.getKey(), mapping.getValue()});
		} 
		  
//		builder.append("--------------\n");
//		Iterator iter = mMap.entrySet().iterator();  
//		long key = 0;
//		long val = 0;
////		if(iter.hasNext()){
////			iter.next();
////		}
//		while (iter.hasNext()) {  
//		    Map.Entry entry = (Map.Entry) iter.next();  
//		    key = (Long)entry.getKey(); 
//		    val = (Long)entry.getValue();  
//		    builder.append(dayFormat.format(key)+"  "+UseTimeService.tranTimeToString(val)+"\n");
//		}  
		if(builder.length() == 0){
			return "至今尚无记录！";
		}
		
		return builder.toString();
	}

}
