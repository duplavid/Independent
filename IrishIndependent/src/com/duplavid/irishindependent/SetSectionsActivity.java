package com.duplavid.irishindependent;

import java.util.ArrayList;
import android.app.ListActivity;
import android.os.Bundle;

/**
 * Connects to the Settings menu in MainActivity
 * Connects to SectionActivityAdapter
 * When starting this activity, it shows all sections with their availability
 * It can populate the database with default values.
 * 
 * @res activity_sections
 * @author Eva Hajdu
 *
 */
public class SetSectionsActivity extends ListActivity {

	static ArrayList<Section> sectionlist;
	static DatabaseHandler db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sections);
		
		try{
			db = MainActivity.db;
			sectionlist = new ArrayList<Section>();
			sectionlist = db.getAllSections();
			
			ArrayList<String> names = new ArrayList<String>();
	
			for(int i=0; i<sectionlist.size();i++){
				names.add(sectionlist.get(i).getFullName());
			}
			
			setListAdapter(new SectionActivityAdapter(this, names, sectionlist));
		}catch(NullPointerException e){
        	finish();
		}
		//TODO: megcsinalni a konkret listat
		//TODO: lista akkor, ha nincs az adatbazisban semmi
		//TODO: lista akkor, ha mar van az adatbazisban
		
	}
	
	public static void populateDatabase(){
		db = MainActivity.db;
		sectionlist = new ArrayList<Section>();
		
		Section BreakingNews = new Section("BreakingNews", "Breaking News", "#009999", "http://www.independent.ie/breaking-news/rss", true);
		Section WorldNews = new Section("WorldNews", "World News", "#1D7373", "http://www.independent.ie/world-news/rss", true);
		Section IrishNews = new Section("IrishNews", "Irish News", "#006363", "http://www.independent.ie/irish-news/rss", true);
		Section Woman = new Section("Woman", "Woman", "#1240AB", "http://www.independent.ie/woman/rss", true);
		Section CelebNews = new Section("CelebNews", "Celeb News", "#06266F", "http://www.independent.ie/woman/celeb-news/rss", true);
		Section Fashion = new Section("Fashion", "Fashion", "#FF7400", "http://www.independent.ie/woman/fashion/rss", true);
		Section Beauty = new Section("Beauty", "Beauty", "#A64B00", "http://www.independent.ie/woman/beauty/rss", true);
		Section Love = new Section("Love", "Love & Sex", "#BF7130", "http://www.independent.ie/woman/love-sex/rss", true);
		Section Health = new Section("Health", "Diet & Fitness", "#FF6940", "http://www.independent.ie/woman/diet-fitness/rss", true);
		
		db.addSection(BreakingNews);
		db.addSection(WorldNews);
		db.addSection(IrishNews);
		db.addSection(Woman);
		db.addSection(CelebNews);
		db.addSection(Fashion);
		db.addSection(Beauty);
		db.addSection(Love);
		db.addSection(Health);
	}
	
}
