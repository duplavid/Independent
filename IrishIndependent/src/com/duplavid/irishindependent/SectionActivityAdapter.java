package com.duplavid.irishindependent;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Connects to SetSectionsActivity
 * @res activity_sections
 * @author Eva Hajdu
 *
 */
public class SectionActivityAdapter extends ArrayAdapter<String> {

	private ArrayList<Section> sections;
	private Context context;
	
	public SectionActivityAdapter(Context context,
			ArrayList<String> names,
			ArrayList<Section> sections
		){
		super(context, R.layout.activity_sections, names);
		this.context = context;
		this.sections = sections;
		
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		int layout;
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		layout = R.layout.list_sectionlist;
		View rowView = inflater.inflate(layout, parent, false);
		
		TextView title = (TextView) rowView.findViewById(R.id.headerrow);
		title.setTypeface(MainActivity.Regular);
		title.setText(sections.get(position).getFullName());
		
		final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkboxrow);
		checkBox.setChecked(sections.get(position).getState());
		
		checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(checkBox.isChecked()){
                	MainActivity.db.updateState((position+1), true);
                    System.out.println((position+1)+" Checked");
                }else{
                	MainActivity.db.updateState((position+1), false);
                    System.out.println((position+1)+" Un-Checked");
                }
            }
        });
		
		return rowView;
	}
	
}
