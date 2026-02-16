package com.bh.android.deathgrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DeathGripActivity extends Activity implements OnTouchListener, OnClickListener {
	
	private final boolean DBG = FeatureList.DBG;
	private final String LOG_TAG = "BH_DeathGripActivity";

	
	private Context mContext = null;

	private final int SOUND_PRESS_BUTTON = 0;
	private final int SOUND_VIEW_MORE_APPS = 2;
	private final int SOUND_ZERO_BAR = 3;

	public void playSound(int sound) {
		MediaPlayer mp = null;
		switch (sound) {
		case SOUND_PRESS_BUTTON:
			mp = MediaPlayer.create(this, R.raw.info);
			break;
		case SOUND_VIEW_MORE_APPS:
			mp = MediaPlayer.create(this, R.raw.ohya);
			break;
			
		case SOUND_ZERO_BAR:
			mp = MediaPlayer.create(this, R.raw.wrong);
			break;			
		}

		if(mp != null) {
			mp.start();
		}
		
	}
	
	private static final int MENU_ITEM_VIEW_MORE_APPS = 3;
	private static final int MENU_ITEM_ABOUT = 20;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		
		// menu.add(0, MENU_ITEM_ABOUT, 0, R.string.menu_about)
		// .setOnMenuItemClickListener(mAbout);

		menu.add(0, MENU_ITEM_VIEW_MORE_APPS, 0, R.string.menu_more_apps)
				.setOnMenuItemClickListener(mMoreApps).setIcon(
						android.R.drawable.ic_menu_view);

		menu.add(0, MENU_ITEM_ABOUT, 0, R.string.menu_about)
				.setOnMenuItemClickListener(mAbout).setIcon(
						android.R.drawable.ic_menu_info_details);

		return true;
	}

	private MenuItem.OnMenuItemClickListener mMoreApps = new MenuItem.OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub

			playSound(SOUND_PRESS_BUTTON);

			Uri uri = Uri.parse("market://search?q=pub:BH_Lin");
			Intent launchIntent = new Intent(Intent.ACTION_VIEW, uri);
			startActivitySafely(launchIntent);
			// where pkg_name is the full package path for an application

			return true;
		}
	};

	// BH_Lin@20100617 ------------------------------------------------------->
	// purpose: about me
	private MenuItem.OnMenuItemClickListener mAbout = new MenuItem.OnMenuItemClickListener() {
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			if (DBG)Log.v(LOG_TAG, "mMoreApps");
			playSound(SOUND_PRESS_BUTTON);

			LayoutInflater factory = LayoutInflater.from(mContext);
			final View textEntryView = factory.inflate(R.layout.about, null);

			final TextView appVersion = (TextView) textEntryView
					.findViewById(R.id.app_version);
			appVersion.setText(getSoftwareVersion());
			
			final TextView sendMail = (TextView) textEntryView
					.findViewById(R.id.act_sendmail);

			// text4 illustrates constructing a styled string containing a
			// link without using HTML at all. Again, for a fixed string
			// you should probably be using a string resource, not a
			// hardcoded value.

			SpannableString ss = new SpannableString(getText(R.string.sendmail)
					.toString());

			ss.setSpan(new URLSpan("mailto:binghuanlin@gmail"), 0, 
					getText(R.string.sendmail).toString().length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			sendMail.setText(ss);
			sendMail.setMovementMethod(LinkMovementMethod.getInstance());

			new AlertDialog.Builder(mContext).setIcon(
					R.drawable.icon)
					.setTitle(R.string.app_name).setView(textEntryView)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
								}
							}).show();

			return true;
		}
	};

	private String getSoftwareVersion() {
		String appVersion = "1.00";
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			Log.v(LOG_TAG, packageInfo.versionName);
			appVersion = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(LOG_TAG, "Package name not found", e);
		}
		;

		return appVersion;
	}

	// BH_Lin@20100617 -------------------------------------------------------<

	void startActivitySafely(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log
					.e(
							LOG_TAG,
							"Secret Setter does not have the permission to launch "
									+ intent
									+ ".Make sure to create a MAIN intent-filter for the corresponding activity"
									+ "or use the exported attribute for this activity");
		}
	}
	
    private handleSignalBar mHandleSignalBar = new handleSignalBar();
    private int mSignalStrenth = 5;
    private boolean startMax = false;
    private final class handleSignalBar implements Runnable {
        public void run() {
        	
        	if(isInTouching() == true) {
        		Message msg = new Message();
        		msg.what = WM_CHANGE_SIGNALBAR;
        		
        		if(isInUsingBumper() == false) {
            		if(mSignalStrenth != 0) {
                		mSignalStrenth = mSignalStrenth - 1;
                		if(mSignalStrenth < 0) {
                			mSignalStrenth = 0;
                		} else {
                			//playSound(SOUND_PRESS_BUTTON);
                		}
                		
                		msg.arg1 = mSignalStrenth;
                		mHandler.sendMessage(msg);
            		}
        		} else {
            		if(mSignalStrenth < 6) {
            			
            			if(startMax == false) {
            				startMax = true;
            				mSignalStrenth = mSignalStrenth - 1;
                    		if(mSignalStrenth < 0) {
                    			mSignalStrenth = 0;
                    		} else {
                    			//playSound(SOUND_PRESS_BUTTON);
                    		}
                    		
                    		msg.arg1 = mSignalStrenth;
                    		mHandler.sendMessage(msg);	
            			} else {
            				mSignalStrenth = mSignalStrenth + 1;
                    		if(mSignalStrenth == 6) {
                    			startMax = false;
                    		} else {
                    			//playSound(SOUND_PRESS_BUTTON);
                    		}
                    		
                    		msg.arg1 = mSignalStrenth;
                    		mHandler.sendMessage(msg);
            			}
                		
            		}
        		}
        	} else {
        		
        		if(isInUsingBumper() == false) {
        			if(mSignalStrenth <= 5) {
                   		Message msg = new Message();
                		msg.what = WM_CHANGE_SIGNALBAR;
                		msg.arg1 = 5;
                		mSignalStrenth = 5;
                		mHandler.sendMessage(msg);
            		}
        		} else {
        			if(mSignalStrenth >= 4) {
                   		Message msg = new Message();
                		msg.what = WM_CHANGE_SIGNALBAR;
                		msg.arg1 = 5;
                		mSignalStrenth = 5;
                		mHandler.sendMessage(msg);
            		}
        		}
        		
        	}
        	
        	mHandler.postDelayed(mHandleSignalBar, QUERY_TIME_DEFAULT);
        }
    }
    
    private final int QUERY_TIME_DEFAULT = 1500;
    
	private final int WM_CHANGE_SIGNALBAR = 1001;
	
    private Handler mHandler = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		switch(msg.what) {
    		case WM_CHANGE_SIGNALBAR:
    			switch(msg.arg1) {

    			case 1:
    				mSignalBar.setImageResource(R.drawable.pic_sys_signal_1);
    				break;
    			case 2:
    				mSignalBar.setImageResource(R.drawable.pic_sys_signal_2);
    				break;
    			case 3:
    				mSignalBar.setImageResource(R.drawable.pic_sys_signal_3);
    				break;
    			case 4:
    				mSignalBar.setImageResource(R.drawable.pic_sys_signal_4);
    				break;
    			case 5:
    				mSignalBar.setImageResource(R.drawable.pic_sys_signal_5);
    				break;
    				
    			case 6:
    				mSignalBar.setImageResource(R.drawable.pic_sys_signal_over);
    				playSound(SOUND_VIEW_MORE_APPS);
    				break;    				

    			case 0:
    				mSignalBar.setImageResource(R.drawable.pic_sys_signal_0);
    				playSound(SOUND_ZERO_BAR);
    			default:
    				break;
    			}
    			
    			break;
    		}
    	}
    };
    
	private ImageView mDeathArea = null;
	private ImageView mSignalBar = null;
	private ImageView mHome = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	if(DBG)Log.v(LOG_TAG, "onCreate");
    	
    	
    	
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        
        setContentView(R.layout.iphone_layout);

        mDeathArea = (ImageView) findViewById(R.id.img_touch_area);
        mDeathArea.setOnTouchListener(this);
        mDeathArea.setOnClickListener(this);
        
        mHome = (ImageView) findViewById(R.id.img_home);
        mHome.setOnClickListener(this);
        
        mContext = this;
        
        mSignalBar = (ImageView) findViewById(R.id.img_signal_strength);
        
        mHandler.post(mHandleSignalBar);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(DBG)Log.v(LOG_TAG, "onResume");
    	
    	mIsOnTouch = false;
    }    
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if(DBG)Log.v(LOG_TAG, "onPause");

    }  
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if(DBG)Log.v(LOG_TAG, "onStop");

    }   
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(DBG)Log.v(LOG_TAG, "onDestroy");

    }
    
    private boolean mIsOnTouch = false;
    private void setInTouching(boolean enable) {
    	mIsOnTouch = enable;
    }
    
    private boolean isInTouching() {
    	return mIsOnTouch;
    }
    
    private boolean mInUsingBumper = false;
    private void setInUsingBumper(boolean enable) {
    	mInUsingBumper = enable;
    }
    
    private boolean isInUsingBumper() {
    	return mInUsingBumper;
    }
    
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		int action = event.getAction();
		
		int id = v.getId();
		if (id == R.id.img_touch_area) {
			if(action == MotionEvent.ACTION_DOWN) {
				if(DBG)Log.v(LOG_TAG, "ACTION_DOWN");
				
				setInTouching(true);
				
			} else if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_CANCEL) {
				
				if(DBG)Log.v(LOG_TAG, "ACTION_UP");
				setInTouching(false);
			}
			return false;
		} else {
			return false;
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.img_touch_area) {

		} else if (id == R.id.img_home) {
			
			new AlertDialog.Builder(mContext)
            .setMessage(getText(R.string.dialog_msg))
            .setPositiveButton(R.string.dialog_option_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	
                    /* User clicked OK so do some stuff */                	
                	RelativeLayout rl = (RelativeLayout)findViewById(R.id.iphone_layout);
                	rl.setBackgroundResource(R.drawable.bg_with_bummper);
                	setInUsingBumper(true);
                }
            })
            .setNegativeButton(R.string.dialog_option_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Cancel so do some stuff */
                    	RelativeLayout rl = (RelativeLayout)findViewById(R.id.iphone_layout);
                    	rl.setBackgroundResource(R.drawable.bg_original);
                    	setInUsingBumper(false);
                    }
                })
            .show();
			
		}
	}
}