package com.mattkula.hackmitapp;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mattkula.hackmitapp.data.Event;
import com.squareup.picasso.Picasso;

public class MainActivity extends FragmentActivity {
	
    PebbleKit.PebbleDataReceiver pListener;
    BroadcastReceiver pConnectionReceiver;
    
    GridView grid;
    
    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle bananas) {
        super.onCreate(bananas);
        setContentView(R.layout.activity_grid);
        
        grid = (GridView)findViewById(R.id.gridview);
        
        AsyncHttpClient eventGetter = new AsyncHttpClient();
        eventGetter.get("http://nextslide.herokuapp.com/events.json", new AsyncHttpResponseHandler(){
        	@Override
        	public void onSuccess(String response) {
        		events = new ArrayList<Event>();
        		
        		try {
					JSONArray arr = new JSONArray(response);
					
					for(int i=0; i<arr.length(); i++){
						JSONObject obj = arr.getJSONObject(i);
						events.add(new Event(obj.getLong("id"), obj.getString("name"), obj.getString("image")));
					}
					
				} catch (JSONException e) {}
        		
        		grid.setAdapter(new EventAdapter());
        	}
        	
        });
        
        grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
				Intent i = new Intent(MainActivity.this, ChooseSlideshowActivity.class);
				i.putExtra("event_id", events.get(pos).id);
				startActivity(i);
//				overridePendingTransition(android.R.anim.slide_in_left, R.anim.custom_window_exit);
				overridePendingTransition(R.anim.custom_window_enter_up, R.anim.custom_window_exit_up);
			}
		});
        
    }
    
    @Override
	protected void onResume() {
    	
    	pListener = new PebbleKit.PebbleDataReceiver(UUID.fromString(PreferenceManager.mUUID)) {
			
			@Override
			public void receiveData(Context context, int transactionId,
					PebbleDictionary data) {
				if(data.getUnsignedInteger(0) == 1){
					//forward();
				}else{
					//backward();
				}
				Log.e("DEBUG", data.toJsonString());
			}
		};
		
//		PebbleKit.registerPebbleConnectedReceiver(this, new PebbleConnectionReceiver());
		
		IntentFilter filter = new IntentFilter("com.getpebble.action.PEBBLE_CONNECTED");
		pConnectionReceiver = new PebbleConnectionReceiver();
		registerReceiver(pConnectionReceiver, filter);
		
		PebbleKit.registerReceivedDataHandler(this, pListener);
    	
		super.onResume();
	}
    
    @Override
	protected void onPause() {
    	unregisterReceiver(pConnectionReceiver);
		super.onPause();
	}
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	private class EventAdapter extends BaseAdapter{
		@Override
		public View getView(int pos, View convertView, ViewGroup group) {
			View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_event, group, false);
			ImageView iv = (ImageView)v.findViewById(R.id.event_image);
			TextView tv = (TextView)v.findViewById(R.id.event_name);
			
			tv.setText(events.get(pos).name);
			Picasso.with(MainActivity.this)
				.load(events.get(pos).image)
				.placeholder(R.drawable.ic_launcher)
				.into(iv);
			return v;
		}
		
		@Override
		public long getItemId(int pos) {
			return events.get(pos).id;
		}
		
		@Override
		public Object getItem(int pos) {
			return events.get(pos);
		}
		
		@Override
		public int getCount() {
			return events.size();
		}
	}
}
