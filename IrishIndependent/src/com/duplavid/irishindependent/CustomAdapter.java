package com.duplavid.irishindependent;
 
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
/**
 * @res activity_main, list_articlelist
 * @author Eva Hajdu
 *
 */
public class CustomAdapter extends ArrayAdapter<String> {
	private final Context context;

	private final ArrayList<String> descriptions;
	private final ArrayList<String> titles;
	private final ArrayList<String> pictures;
	private final ArrayList<String> links;
	
	private Section thisSection;
	private String sectionid;

	private ViewGroup prt;
 
	public CustomAdapter(Context context, ArrayList<String> descriptions, 
			ArrayList<String> titles,
			ArrayList<String> pictures,
			ArrayList<String> links,
			Section thisSection,
			String sectionid
		) {
		super(context, R.layout.activity_main, descriptions);
		this.context = context;
		this.descriptions = descriptions;
		this.titles = titles;
		this.pictures = pictures;
		this.links = links;
		
		this.thisSection = thisSection;
		this.sectionid = sectionid;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		this.prt = parent;
		int layout;
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		layout = R.layout.list_articlelist;
		
		View rowView = inflater.inflate(layout, parent, false);
		TextView title = (TextView) rowView.findViewById(R.id.titleRow);
		TextView description = (TextView) rowView.findViewById(R.id.descriptionRow);
		ImageView pics = (ImageView) rowView.findViewById(R.id.pictureRow);
		
		if(pictures.get(position) != null && isNetworkAvailable(context)){
			final String imageKey = String.valueOf(position);
		    final Bitmap bitmap = MainActivity.getBitmapFromMemCache(String.valueOf(sectionid)+"_"+imageKey);
		    if (bitmap != null) {
		    	pics.setImageBitmap(bitmap);
		    	setPicRow(pics);
		    } else {
		    	RetrievePictureTask task = new RetrievePictureTask(pictures.get(position), pics, position, Integer.parseInt(sectionid));
				task.execute(new String[] { pictures.get(position) });
		    }
		}
		
		
		title.setTypeface(MainActivity.Regular);
		title.setText(titles.get(position));
		
		description.setText(descriptions.get(position));
		
		rowView.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				String article = links.get(position);
				String lead = descriptions.get(position);
				String title = titles.get(position);
				
				Intent i = new Intent(context, SingleArticleActivity.class);
		        i.putExtra("link", article);
		        i.putExtra("lead", lead);
		        i.putExtra("title", title);
		        i.putExtra("groupPosition", sectionid);
		        i.putExtra("childPosition", Integer.toString(position));
		        context.startActivity(i);
		    }
		});
		
		return rowView;
	}
	
    public void setPicRow(ImageView v){
    	v.setBackgroundResource(R.drawable.stroke_01);
    	v.getLayoutParams().height = 150;
    	v.getLayoutParams().width = 150;
    }
	
	class RetrievePictureTask extends AsyncTask<String, Void, Drawable> {
	    private ImageView pics;
	    private Integer position;
	    private Integer groupPosition;

	    public RetrievePictureTask(String url, ImageView pics, Integer position, Integer groupPosition){
	    	this.pics = pics;
	    	this.position = position;
	    	this.groupPosition = groupPosition;
	    }
	    
	    protected Drawable doInBackground(String... urls) {
	    	Drawable response = null;
	        try {
				Drawable d = ImageOperations(urls[0]);
				response = d;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return response;
	    }

	    protected void onPostExecute(Drawable result) {
	    	pics.setImageDrawable(result);
	    	setPicRow(pics);
	    	Bitmap bitmap = ((BitmapDrawable) result).getBitmap();
	    	MainActivity.addBitmapToMemoryCache(groupPosition.toString()+'_'+position.toString(), bitmap);    	
	    }
	    
	    private Drawable ImageOperations(String url) {
	        try {
	            InputStream is = (InputStream) this.fetch(url);
	            Drawable d = Drawable.createFromStream(is, "src");
	            return d;
	        } catch (MalformedURLException e) {
	            return null;
	        } catch (IOException e) {
	            return null;
	        }
	    }
	    
	    public Object fetch(String address) throws MalformedURLException,
	    	IOException {
	        URL url = new URL(address);
	        Object content = url.getContent();
	        return content;
	    } 
	}
	
	public boolean isNetworkAvailable(Context ctx){
	    ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()&& cm.getActiveNetworkInfo().isAvailable()&& cm.getActiveNetworkInfo().isConnected()){
	        return true;
	    }else{
	        return false;
	    }
	}
	
}