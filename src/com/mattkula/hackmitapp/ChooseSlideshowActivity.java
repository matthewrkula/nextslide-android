package com.mattkula.hackmitapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mattkula.hackmitapp.data.Slideshow;
import com.squareup.picasso.Picasso;

public class ChooseSlideshowActivity extends Activity {
	
	private long event_id = -1;
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
						
						Slideshow s = new Slideshow();
						s.id = id;
						s.name = name;
						s.first_image_url = first_image_url;
						slideshows.add(s);
					}
					
				} catch (JSONException e) {}
        		
        		grid.setAdapter(new SlideshowAdapter());
        	}
        });
		
	}
	
	private void init(Bundle bananas){
		event_id = getIntent().getExtras().getLong("event_id");
		if(bananas != null && event_id == -1){
			event_id = bananas.getLong("event_id");
		}
		
		ActionBar ab = getActionBar();
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
