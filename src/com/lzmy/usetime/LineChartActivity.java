package com.lzmy.usetime;

import java.util.ArrayList;

import com.lzmy.chart.LineChartView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ScrollView;

public class LineChartActivity extends Activity{

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_chart);
		LineChartView lineChartView =  (LineChartView)findViewById(R.id.mlinechart);
		Intent intent = getIntent();
		ArrayList<long[]> list = (ArrayList<long[]>)intent.getSerializableExtra("list");
		if(list == null){
			return;
		}
		
		lineChartView.setData(list);
		lineChartView.invalidate();
	
	}

}
