package com.duplavid.irishindependent;
 
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
/**
 * Connects to MainActivity
 * Shows sections with their children (articles)
 * 
 * @res list_articlelist, list_header
 * @author Eva Hajdu
 *
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
	
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
    	
    	ArrayList<String> descriptions = new ArrayList<String>();
    	descriptions = MainActivity.sections.get(groupPosition).getFirst5Desc();
    	ArrayList<String> titles = new ArrayList<String>();
    	titles = MainActivity.sections.get(groupPosition).getFirst5Titles();
    	ArrayList<String> pictures = new ArrayList<String>();
    	pictures = MainActivity.sections.get(groupPosition).getFirst5Pics();

		int layout;
		LayoutInflater inflater = (LayoutInflater) this._context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		layout = R.layout.list_articlelist;
		
		View rowView = inflater.inflate(layout, parent, false);
		
		TextView title = (TextView) rowView.findViewById(R.id.titleRow);
		TextView description = (TextView) rowView.findViewById(R.id.descriptionRow);
		ImageView pics = (ImageView) rowView.findViewById(R.id.pictureRow);
		
		if(pictures.get(childPosition) != null){
			final String posKey = String.valueOf(childPosition);
			final String groupKey = String.valueOf(groupPosition);
		    Bitmap bitmap = MainActivity.getBitmapFromMemCache(groupKey+'_'+posKey);
		    if (bitmap != null) {
		    	pics.setImageBitmap(bitmap);
		    	setPicRow(pics);
		    	bitmap = null;
		    } else {
		    	RetreivePictureTask task = new RetreivePictureTask(pictures.get(childPosition), pics, childPosition, groupPosition);
				task.execute(new String[] { pictures.get(childPosition) });
		    }
		}
		
		title.setTypeface(MainActivity.Regular);
		title.setText(titles.get(childPosition));
		//TextViewJustify.justifyText(((TextView)rowView.findViewById(R.id.titleRow)), 170f); 
		
		description.setText(descriptions.get(childPosition));
		//TextViewJustify.justifyText(((TextView)rowView.findViewById(R.id.descriptionRow)), 150f); 
		
		rowView.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				String article = MainActivity.sections.get(groupPosition).getFirst5Links().get(childPosition);
				String lead = MainActivity.sections.get(groupPosition).getFirst5Desc().get(childPosition);
				String title = MainActivity.sections.get(groupPosition).getFirst5Titles().get(childPosition);
				
				Intent i = new Intent(v.getContext(), SingleArticleActivity.class);
		        i.putExtra("link", article);
		        i.putExtra("lead", lead);
		        i.putExtra("title", title);
		        i.putExtra("groupPosition", Integer.toString(groupPosition));
		        i.putExtra("childPosition", Integer.toString(childPosition));
		        v.getContext().startActivity(i);
		    }
		});
		
		return rowView;
    }
    
    public void setPicRow(ImageView v){
    	v.setBackgroundResource(R.drawable.stroke_01);
    	v.getLayoutParams().height = 150;
    	v.getLayoutParams().width = 150;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        int layout;
        
        LayoutInflater inflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_header, null);
        layout = R.layout.list_header;
        View rowView = inflater.inflate(layout, parent, false);
        
        TextView header = (TextView) rowView.findViewById(R.id.list_header_title);
        TextView nav = (TextView) rowView.findViewById(R.id.list_header_empty);
        nav.setBackgroundColor(Color.parseColor(MainActivity.sections.get(groupPosition).getColor()));
        
		ImageView img = (ImageView) rowView.findViewById(R.id.list_header_image);
		img.setBackgroundColor(Color.parseColor(MainActivity.sections.get(groupPosition).getColor()));
		
		header.setBackgroundColor(Color.parseColor(MainActivity.sections.get(groupPosition).getColor()));
		header.setText(MainActivity.sections.get(groupPosition).getFullName());
		//header.setTypeface(MainActivity.Regular);
		
		img.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
		    	Intent i = new Intent(v.getContext(), SingleSectionActivity.class);
		    	i.putExtra("sectionid", Integer.toString(groupPosition));
		    	v.getContext().startActivity(i);
		    }
		});
		
		return rowView;
		
        
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    
	class RetreivePictureTask extends AsyncTask<String, Void, Drawable> {
	    private ImageView pics;
	    private Integer position;
	    private Integer groupPosition;

	    public RetreivePictureTask(String url, ImageView pics, Integer position, Integer groupPosition){
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
	    	bitmap = null;
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
    
}