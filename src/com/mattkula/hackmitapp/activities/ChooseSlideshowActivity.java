package com.mattkula.hackmitapp.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mattkula.hackmitapp.R;
import com.mattkula.hackmitapp.data.Slide;
import com.mattkula.hackmitapp.data.Slideshow;
import com.squareup.picasso.Picasso;

public class ChooseSlideshowActivity extends Activity {
	
	private long event_id = -1;
	private String event_name = "";
	ArrayList<Slideshow> slideshows;
	GridView grid;

	@Override
	protected void onCreate(Bundle bananas) {
		super.onCreate(bananas);
		setContentView(R.layout.activity_grid);
		init(bananas);
		
		grid = (GridView)findViewById(R.id.gridview);
		
		AsyncHttpClient eventGetter = new AsyncHttpClient();
        eventGetter.get("http://nextslide.herokuapp.com/events/"+event_id+"/slideshows.json", new AsyncHttpResponseHandler(){
        	@Override
        	public void onSuccess(String response) {
        		slideshows = new ArrayList<Slideshow>();
        		
        		try {
					JSONArray arr = new JSONObject(response).getJSONArray("response");
					
					for(int i=0; i<arr.length(); i++){
						JSONObject obj = arr.getJSONObject(i);
						
						long id = obj.getLong("id");
						String name = obj.getString("name");
						String first_image_url = obj.getString("first_image_url");
						String url = obj.getJSONObject("url").getJSONObject("url").getString("url");
						
						ArrayList<Slide> slides = new ArrayList<Slide>();
						JSONArray slidesArr = obj.getJSONArray("slides");
						
						for(int j=0; j < slidesArr.length(); j++){
							Slide slide = new Slide();
							slide.id = slidesArr.getJSONObject(j).getLong("id");
							slide.note = slidesArr.getJSONObject(j).getString("note");
							slide.slide_number = slidesArr.getJSONObject(j).getInt("slide_number");
							slides.add(slide);
						}
						
						Collections.sort(slides);
						
						Slideshow s = new Slideshow();
						s.id = id;
						s.name = name;
						s.first_image_url = first_image_url;
						s.slides = slides;
						s.event_id = event_id;
						s.url = url;
						slideshows.add(s);
					}
					
				} catch (JSONException e) {}
        		
        		grid.setAdapter(new SlideshowAdapter());
        	}
        });
        
        grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long id) {
				Intent i = new Intent(ChooseSlideshowActivity.this, ControlSlideshowActivity.class);
				i.putExtra("slideshow", slideshows.get(pos));
				startActivity(i);
				overridePendingTransition(R.anim.custom_window_enter_up, R.anim.custom_window_exit_up);
			}
		});
		
	}
	
	private void init(Bundle bananas){
		event_id = getIntent().getExtras().getLong("event_id");
		event_name = getIntent().getExtras().getString("event_name");
		if(bananas != null && event_id == -1){
			event_id = bananas.getLong("event_id");
			event_name = bananas.getString("event_name");
		}
		
		ActionBar ab = getActionBar();
		ab.setTitle(event_name);
		ab.setDisplayHomeAsUpEnabled(true);
		
		slideshows = new ArrayList<Slideshow>();
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
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong("event_id", event_id);
		outState.putString("event_name", event_name);
	}
	
	private class SlideshowAdapter extends BaseAdapter{
		@Override
		public View getView(int pos, View convertView, ViewGroup group) {
			View v = convertView;
			if(convertView == null){
				v = LayoutInflater.from(ChooseSlideshowActivity.this).inflate(R.layout.view_event, group, false);
			}
			ImageView iv = (ImageView)v.findViewById(R.id.event_image);
			TextView tv = (TextView)v.findViewById(R.id.event_name);
			
			tv.setText(slideshows.get(pos).name);
			Picasso.with(ChooseSlideshowActivity.this)
				.load(slideshows.get(pos).first_image_url)
				.placeholder(R.drawable.ic_launcher)
				.into(iv);
			return v;
		}
		
		@Override
		public long getItemId(int pos) {
			return slideshows.get(pos).id;
		}
		
		@Override
		public Object getItem(int pos) {
			return slideshows.get(pos);
		}
		
		@Override
		public int getCount() {
			return slideshows.size();
		}
	}
}
