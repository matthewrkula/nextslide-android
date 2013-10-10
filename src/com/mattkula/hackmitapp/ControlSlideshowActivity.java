package com.mattkula.hackmitapp;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ControlSlideshowActivity extends Activity {
	
	private final static int backward = 0;
	private final static int forward = 1;
	
	private long event_id = -1;
	private long slideshow_id = -1;
	private String slideshow_name = "";
	
	private Button btnForward;
	private Button btnBackward;

	@Override
	protected void onCreate(Bundle bananas) {
		super.onCreate(bananas);
		setContentView(R.layout.activity_controlslideshow);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		event_id = getIntent().getExtras().getLong("event_id");
		slideshow_id = getIntent().getExtras().getLong("slideshow_id");
		slideshow_name = getIntent().getExtras().getString("slideshow_name");
		if(bananas != null && event_id != -1 && slideshow_id != -1){
			event_id = bananas.getLong("event_id");
			slideshow_id = bananas.getLong("slideshow_id");
			slideshow_name = bananas.getString("slideshow_name");
		}
		
		ab.setTitle(slideshow_name);
		
		btnForward = (Button)findViewById(R.id.btn_forward);
		btnBackward = (Button)findViewById(R.id.btn_back);
		btnForward.setOnClickListener(new ButtonListener(forward));
		btnBackward.setOnClickListener(new ButtonListener(backward));
	}
	
	
	private class ButtonListener implements View.OnClickListener{
		int choice;
		
		public ButtonListener(int choice){
			this.choice = choice;
		}

		@Override
		public void onClick(View v) {
			String choiceStr = "";
			if(choice == backward)
				choiceStr = "backward";
			else
				choiceStr = "forward";
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(String.format("http://nextslide.herokuapp.com/events/%d/slideshows/%d/%s.json", event_id, slideshow_id, choiceStr), new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					Toast.makeText(ControlSlideshowActivity.this, "Hooray", Toast.LENGTH_SHORT).show();
					super.onSuccess(arg0);
				}
				
				@Override
				public void onFailure(Throwable arg0, String s) {
					Toast.makeText(ControlSlideshowActivity.this, s, Toast.LENGTH_SHORT).show();
					super.onFailure(arg0, s);
				}
			});
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong("event_id", event_id);
		outState.putLong("slideshow_id", slideshow_id);
		outState.putString("slideshow_name", slideshow_name);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.custom_window_enter, R.anim.custom_window_exit);
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
}
