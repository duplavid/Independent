package com.duplavid.irishindependent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ListView;

public class SingleSectionActivity extends Activity {
	private Context thiscontext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try{
			thiscontext = this;
			Intent i = getIntent(); 
			String sectionid = i.getStringExtra("sectionid");
			
			Section thissection = MainActivity.sections.get(Integer.parseInt(sectionid));
			
			setTitle(thissection.getFullName());
			View titleView = getWindow().findViewById(android.R.id.title);
			if (titleView != null) {
			  ViewParent parent = titleView.getParent();
			  if (parent != null && (parent instanceof View)) {
			    View parentView = (View)parent;
			    parentView.setBackgroundColor(Color.parseColor(thissection.getColor()));
				
			  }
			}

			//Create our list and custom adapter
			SeparatedListAdapter adapter = new SeparatedListAdapter(this, sectionid);

			adapter.addSection(thissection.getFullName(), new CustomAdapter(
				thiscontext, thissection.descriptions,
				thissection.titles, 
				thissection.pictures, 
				thissection.links,
				thissection,
				sectionid
			));
			
			ListView lst = new ListView(this);
			lst.setAdapter(adapter);
			this.setContentView(lst);
		}catch(NullPointerException e){
			finish();
		}

		
		
	}
}