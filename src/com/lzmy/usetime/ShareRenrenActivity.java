package com.lzmy.usetime;

import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennResponse;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renn.rennsdk.RennExecutor.CallBack;
import com.renn.rennsdk.exception.RennException;
import com.renn.rennsdk.param.PutFeedParam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShareRenrenActivity extends Activity implements OnClickListener{
	
	/**
	 * If you want to feed a new message(about your use time) to RenRen, 
	 * you should set action (e.g. ACTION_SHARE_LONGTIME) in this activity.
	 * And you should put (long)time to intent with key "use_time".
	 * @author hzy
	 */
	
	public static final String TAG = "ShareRenrenActivity";
	
	//表示持续使用时间的长度
	public static final String ACTION_SHARE_LONGTIME = "com.lzmy.tellmewakeandlock.acton.sha_longtime";
	//表示每天使用的时间
	public static final String ACTION_SHARE_DAYTIME = "com.lzmy.tellmewakeandlock.acton.sha_daytime";
	
	private Button loginBtn = null;
	private Button logoutBtn = null;
	private Button shareBtn = null;
	private TextView shareTxtv = null;
	private EditText shareConEdt = null;
	
	private RennClient rennClient;
	private ProgressDialog mProgressDialog;
	private Intent mIntent = null;
	
	private static final String APP_ID = "245854";

	private static final String API_KEY = "15690d970b8643ff826813f74a102403";

	private static final String SECRET_KEY = "d8e9a9df03444449b7f4523f96fba88a";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		rennClient = RennClient.getInstance(this);
		rennClient.init(APP_ID, API_KEY, SECRET_KEY);
		
		rennClient
				.setScope("read_user_blog read_user_photo read_user_status read_user_album "
						+ "read_user_feed read_user_comment read_user_share publish_blog publish_share "
						+ "send_notification photo_upload status_update create_album "
						+ "publish_comment publish_feed operate_like");
		// rennClient
		// .setScope("read_user_blog read_user_photo read_user_status read_user_album "
		// + "read_user_comment publish_blog publish_share "
		// + "send_notification photo_upload status_update create_album "
		// + "publish_feed");
		// rennClient.setScope("read_user_blog read_user_status");
		rennClient.setTokenType("mac");
		
		this.setTitle("人人分享");
		mIntent = getIntent();
		if(mIntent.getAction() == null){
			Log.d(TAG, "action is invalid!");
		}
		setContentView(R.layout.activity_share_renren);
		initView();
		
	}

	private void initView() {
		loginBtn = (Button)findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(this);
		logoutBtn = (Button)findViewById(R.id.logout_btn);
		logoutBtn.setOnClickListener(this);
		shareBtn = (Button)findViewById(R.id.share_btn);
		shareBtn.setOnClickListener(this);
		shareTxtv = (TextView)findViewById(R.id.share_renren_txtv);
		shareConEdt = (EditText)findViewById(R.id.share_renren_edt);
		shareTxtv.setText(shareTxtv.getText() + "#...#");
		long time = 0;
		String title = null;
		if(mIntent.getAction().equals(ACTION_SHARE_DAYTIME)){
   		 	time = mIntent.getLongExtra("use_time", 0);
   		 	title = UseTimeService.tranTimeToString(time)+" 你敢信！？";
   	 	}else if(mIntent.getAction().equals(ACTION_SHARE_LONGTIME)){
   	 		time = mIntent.getLongExtra("use_time", 0);
   	 		title = UseTimeService.tranTimeToString(time)+" 你敢信！？";
   	 	}
        if(time != 0){ 
        	shareConEdt.setText(title);
         	Log.d(TAG, "edt set txt");
         }
         
		if (rennClient.isLogin()) {
			loginBtn.setVisibility(View.GONE);
			logoutBtn.setVisibility(View.VISIBLE);
			shareBtn.setVisibility(View.VISIBLE);
			shareTxtv.setVisibility(View.VISIBLE);
			shareConEdt.setVisibility(View.VISIBLE);
			
		} else {
			loginBtn.setVisibility(View.VISIBLE);
			logoutBtn.setVisibility(View.GONE);
			shareBtn.setVisibility(View.GONE);
			shareTxtv.setVisibility(View.GONE);
			shareConEdt.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.login_btn:
				rennClient.setLoginListener(new LoginListener() {
					@Override
					public void onLoginSuccess() {
						// TODO Auto-generated method stub
						Toast.makeText(ShareRenrenActivity.this, "登录成功",
								Toast.LENGTH_SHORT).show();
						loginBtn.setVisibility(View.GONE);
						logoutBtn.setVisibility(View.VISIBLE);
						shareBtn.setVisibility(View.VISIBLE);
						shareTxtv.setVisibility(View.VISIBLE);
						shareConEdt.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoginCanceled() {
						loginBtn.setVisibility(View.VISIBLE);
						logoutBtn.setVisibility(View.GONE);
						shareBtn.setVisibility(View.GONE);
						shareTxtv.setVisibility(View.GONE);
						shareConEdt.setVisibility(View.GONE);
					}
				});
				rennClient.login(this);
				break;
			case R.id.logout_btn:
				rennClient.logout();
				Log.d(TAG,""+rennClient.isLogin());
				loginBtn.setVisibility(View.VISIBLE);
				logoutBtn.setVisibility(View.GONE);
				shareBtn.setVisibility(View.GONE);
				shareTxtv.setVisibility(View.GONE);
				shareConEdt.setVisibility(View.GONE);
				break;
		}
		
		if(rennClient.isLogin() == false){
			return;
		}
		
		switch(v.getId()){
			case R.id.share_btn:
					//message title description targeturl 为必选	
				 	PutFeedParam param = new PutFeedParam();
	                param.setTitle("呵呵");
				 	String shareCon = shareConEdt.getText().toString();
				 	
				 	if(!TextUtils.isEmpty(shareCon)){
				 		param.setMessage("#呵呵#" + shareCon);
				 	}else{
				 		Toast.makeText(ShareRenrenActivity.this, "内容不能为空!", Toast.LENGTH_SHORT).show();
				 		return;
				 	}
	                param.setDescription("闹着玩的！");
//	                param.setActionName("actionName");
//	                param.setActionTargetUrl("http://www.renren.com");
//	                param.setSubtitle("subtitle");
	                param.setImageUrl("http://pic.pp3.cn/uploads/20121001j/bz/138.jpg");
	                param.setTargetUrl("http://www.renren.com");
	                
	                if (mProgressDialog == null) {
	                    mProgressDialog = new ProgressDialog(ShareRenrenActivity.this);
	                    mProgressDialog.setCancelable(true);
	                    mProgressDialog.setTitle("请等待");
	                    mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
	                    mProgressDialog.setMessage("正在发布新鲜事");
	                    mProgressDialog.show();
	                }
	                try {
	                    rennClient.getRennService().sendAsynRequest(param, new CallBack() {    
	                        
	                        @Override
	                        public void onSuccess(RennResponse response) {
	                            Toast.makeText(ShareRenrenActivity.this, "发布成功", Toast.LENGTH_SHORT).show();  
	                            if (mProgressDialog != null) {
	                                mProgressDialog.dismiss();
	                                mProgressDialog = null;
	                            }                           
	                        }
	                        
	                        @Override
	                        public void onFailed(String errorCode, String errorMessage) {
	                            Toast.makeText(ShareRenrenActivity.this, 
	                            		"发布失败\n"+errorCode+":"+errorMessage, Toast.LENGTH_LONG).show();
	                            if (mProgressDialog != null) {
	                                mProgressDialog.dismiss();
	                                mProgressDialog = null;
	                            }                            
	                        }
	                    });
	                } catch (RennException e1) {
	                    // TODO Auto-generated catch block
	                    e1.printStackTrace();
	                }
	                break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		rennClient.logout();
	}


}
