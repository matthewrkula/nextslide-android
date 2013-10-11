package com.mattkula.hackmitapp.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mattkula.hackmitapp.R;
import com.mattkula.hackmitapp.data.Slideshow;
import com.squareup.picasso.Picasso;

public class ControlSlideshowActivity extends Activity {
	
	private final static int backward = 0;
	private final static int forward = 1;
	
	private Slideshow slideshow = null;
	
	private Button btnForward;
	private Button btnBackward;
	private TextView tvNotes;
	private ImageView[] imgViews;
	private LinearLayout imgHolder; 
	
	private int slideCounter = 0;

	@Override
	protected void onCreate(Bundle bananas) {
		super.onCreate(bananas);
		setContentView(R.layout.activity_controlslideshow);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		slideshow = (Slideshow)getIntent().getExtras().getSerializable("slideshow");
		if(bananas != null && slideshow == null){
			slideshow = (Slideshow)bananas.getSerializable("slideshow");
		}
		
		ab.setTitle(slideshow.name);
		
		tvNotes = (TextView)findViewById(R.id.tv_notes);
		
		btnForward = (Button)findViewById(R.id.btn_forward);
		btnBackward = (Button)findViewById(R.id.btn_back);
		btnForward.setOnClickListener(new ButtonListener(forward));
		btnBackward.setOnClickListener(new ButtonListener(backward));
		
		imgHolder = (LinearLayout)findViewById(R.id.image_holder);
		imgViews = new ImageView[slideshow.slides.size()];
		for(int i=0; i < slideshow.slides.size(); i++){
			imgViews[i] = new ImageView(this);
			imgViews[i].setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			Picasso.with(this)
				.load(String.format("https://docs.google.com/viewer?url=%s&a=bi&pagenumber=%d&w=300", slideshow.url, i+1))
				.into(imgViews[i]);
		}
		
		switchSlides();
	}
	
	
	private class ButtonListener implements View.OnClickListener{
		int choice;
		
		public ButtonListener(int choice){
			this.choice = choice;
		}

		@Override
		public void onClick(View v) {
			final String choiceStr = choice == backward? "backward" : "forward";
			
			v.startAnimation(AnimationUtils.loadAnimation(ControlSlideshowActivity.this, R.anim.btn_press));
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(String.format("http://nextslide.herokuapp.com/events/%d/slideshows/%d/%s.json", slideshow.event_id, slideshow.id, choiceStr), new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(String arg0) {
					if(choice == forward)
						slideCounter++;
					else
						slideCounter--;
					
					if(slideCounter == slideshow.slides.size())
						slideCounter = 0;
					if(slideCounter == -1)
						slideCounter = slideshow.slides.size() - 1;
					
					switchSlides();
				}
				
				@Override
				public void onFailure(Throwable arg0, String s) {
					Toast.makeText(ControlSlideshowActivity.this, s, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	private void switchSlides(){
		imgHolder.removeAllViews();
		tvNotes.setText(slideshow.slides.get(slideCounter).note != null ? slideshow.slides.get(slideCounter).note : "No Notes");
		imgHolder.addView(imgViews[slideCounter]);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("slideshow", slideshow);
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
