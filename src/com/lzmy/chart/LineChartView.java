package com.lzmy.chart;

import java.text.AttributedCharacterIterator.Attribute;
import java.text.DateFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LineChartView extends View{
	
	/**
	 *only used by this application.
	 * data is list<Map.Entry<Long,Long>>
	 *@author hzy
	 *@since 2013/12/25
	 */
	
	private Context context = null;
	
	private Path path = null;
	private Paint paint = null;
	private PathEffect pathEffect = null;
	private int color = 0x00000000;
	
	private ArrayList<long[]> list = null;
	private Format dayFormat = null; 
	
	
	public LineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		
		
		path = new Path();
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		
		paint.setColor(color);
		
		dayFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
	}
	
	public void setData(ArrayList<long[]> mlist){
		if(mlist == null){
			return ;
		}
		
		this.list = mlist;
//		this.invalidate();
		Log.d("usetime", "setData");
	}
	
	
	
	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(this.list == null){
			return ;
		}
		super.draw(canvas);
		
		
		int height = canvas.getHeight();
		int width = (int)(canvas.getWidth()*0.85);
		int bottomX = (int)(canvas.getWidth()*0.05);
		int bottomY = (int)(canvas.getHeight()*0.05);
		
		int betweenY = (int)((canvas.getHeight() - bottomY - bottomY)/6);
		
		long maxHeight = (int)findMaxLong();
		if(maxHeight <= 0){
			return ;
		}
		
		int length = bottomY + bottomY;
		
		long[] entry = list.get(0); 
		
		path.moveTo((float)(bottomX+getX(entry[1], maxHeight, width)), bottomY);
		
		for(long[] mEntry : list){
			if(mEntry == entry){
				continue;
			}
			path.lineTo((float)(bottomX+getX(mEntry[1], maxHeight, width)), betweenY);
			length += betweenY;
		}
		
		canvas.drawPath(path, paint);
		
		path.reset();
		
		path.lineTo(0, ((length > height)?length:height));
		
		canvas.drawPath(path, paint);
		
		Paint textPaint = new Paint();
		textPaint.setStyle(Paint.Style.FILL);
		
		for(long[] mEntry : list){
			canvas.drawTextOnPath(dayFormat.format(mEntry[0]), path, 5, 0, textPaint);
			canvas.translate(0, betweenY);
		}
		
		Log.d("usetime", "draw");
	}
	
	private float getX(long value, long maxHeight, int height){
		return (float)(value/maxHeight*height);
	}

	
	private long findMaxLong(){
		if(this.list == null){
			return -1;
		}
		
		long maxLong = -1;
		
		for(long[] entry: this.list){
			if(entry[1] > maxLong){
				maxLong = entry[1];
			}
		}
		
		return maxLong;
	}

	

}
