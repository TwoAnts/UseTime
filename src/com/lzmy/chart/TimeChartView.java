package com.lzmy.chart;

import org.achartengine.chart.LineChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.view.View;

public class TimeChartView extends View{

	public TimeChartView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private LineChart mLineChart = null;
	
	private XYMultipleSeriesDataset dataset = null;
	
	private XYMultipleSeriesRenderer seriesRenderer = null;
	
}
