package com.duplavid.irishindependent;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * 
 * @res list_header
 * @author Eva Hajdu
 *
 */
public class HeaderAdapter extends ArrayAdapter<String>{
	public Context context;
	public ArrayList<String> items;
	public String sectionid;
	
	public HeaderAdapter(Context context, ArrayList<String> items, String sectionid) {
		super(context, R.layout.list_header);
		this.context = context;
		this.items = items;
		this.sectionid = sectionid;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int layout;
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		layout = R.layout.plain_header;
		
		View rowView = inflater.inflate(layout, parent, false);
		
		//Set header
		TextView header = (TextView) rowView.findViewById(R.id.list_header_title);
		//Set the title row's color
		TableRow titlerow = (TableRow) rowView.findViewById(R.id.titlerow);
		titlerow.setBackgroundColor(Color.parseColor(MainActivity.sections.get(Integer.parseInt(sectionid)).getColor()));
		header.setText(this.items.get(position));
		header.setTypeface(MainActivity.Regular);
		
		return rowView;
	}
	
	
	
}