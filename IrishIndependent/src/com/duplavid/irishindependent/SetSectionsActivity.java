package com.duplavid.irishindependent;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
		Section Sport = new Section("Sport", "Sport", "#A64B00", "http://www.independent.ie/sport/rss", true);
		Section Soccer = new Section("Soccer", "Soccer", "#A64B00", "http://www.independent.ie/sport/soccer/rss", true);
		Section Gaelic = new Section("GaelicFootball", "Gaelic Football", "#A64B00", "http://www.independent.ie/sport/gaelic-football/rss", true);
		Section Hurling = new Section("Hurling", "Hurling", "#A64B00", "http://www.independent.ie/sport/hurling/rss", true);
		Section Rugby = new Section("Rugby", "Rugby", "#A64B00", "http://www.independent.ie/sport/rugby/rss", true);
		Section Golf = new Section("Golf", "Golf", "#A64B00", "http://www.independent.ie/sport/golf/rss", true);
		Section Horse = new Section("Horse", "Horse Racing", "#A64B00", "http://www.independent.ie/sport/horse-racing/rss", true);
		Section Other = new Section("OtherSports", "Other Sports", "#A64B00", "http://www.independent.ie/sport/other-sports/rss", true);
		Section Cycling = new Section("Cycling", "Cycling", "#A64B00", "http://www.independent.ie/sport/other-sports/cycling/rss", true);
		Section Business = new Section("Business", "Business", "#A64B00", "http://www.independent.ie/business/rss", true);
		Section IrishBusiness = new Section("IrishBusiness", "Irish Business News", "#A64B00", "http://www.independent.ie/business/irish/rss", true);
		Section WorldBusiness = new Section("WorldBusiness", "World Business News", "#A64B00", "http://www.independent.ie/business/world/rss", true);
		Section Technology = new Section("Technology", "Technology", "#A64B00", "http://www.independent.ie/business/technology/rss", true);
		Section Farming = new Section("Farming", "Farming", "#A64B00", "http://www.independent.ie/business/farming/rss", true);
		Section SmallBusiness = new Section("SmallBusiness", "Small Business", "#A64B00", "http://www.independent.ie/business/small-business/rss", true);
		Section Media = new Section("Media", "Media", "#A64B00", "http://www.independent.ie/business/media/rss", true);
		Section PersonalFinance = new Section("PersonalFinance", "Personal Finance", "#A64B00", "http://www.independent.ie/business/personal-finance/rss", true);
		Section CommercialProperty = new Section("CommercialProperty", "Commercial Property", "#A64B00", "http://www.independent.ie/business/commercial-property/rss", true);
		Section Entertainment = new Section("Entertainment", "Entertainment", "#A64B00", "http://www.independent.ie/entertainment/rss", true);
		Section Movies = new Section("Movies", "Movies", "#A64B00", "http://www.independent.ie/entertainment/movies/rss", true);
		Section Music = new Section("Music", "Music", "#A64B00", "http://www.independent.ie/entertainment/music/rss", true);
		Section TVRadio = new Section("TVRadio", "TV-Radio", "#A64B00", "http://www.independent.ie/entertainment/tv-radio/rss", true);
		Section Festivals = new Section("Festivals", "Festivals", "#A64B00", "http://www.independent.ie/entertainment/festivals/rss", true);
		Section Bookarts = new Section("Bookarts", "Book-Arts", "#A64B00", "http://www.independent.ie/entertainment/books-arts/rss", true);
		Section Competitions = new Section("Competitions", "Competitions", "#A64B00", "http://competitions.independent.ie/rss", true);
		Section Lifestyle = new Section("Lifestyle", "Lifestyle", "#A64B00", "http://www.independent.ie/lifestyle/rss", true);
		Section HealthLifestyle = new Section("LifestyleHealth", "Lifestyle - Health", "#A64B00", "http://www.independent.ie/lifestyle/health/rss", true);
		Section MothersBabies = new Section("MothersBabies", "Mothers-Babies", "#A64B00", "http://www.independent.ie/lifestyle/mothers-babies/rss", true);
		Section Education = new Section("Education", "Education", "#A64B00", "http://www.independent.ie/lifestyle/education/rss", true);
		Section Food = new Section("Food", "Food-Drink", "#A64B00", "http://www.independent.ie/lifestyle/food-drink/rss", true);
		Section Home = new Section("Home", "Home", "#A64B00", "http://www.independent.ie/lifestyle/property-homes/rss", true);
		Section Motoring = new Section("Motoring", "Motoring", "#A64B00", "http://www.independent.ie/lifestyle/motoring/rss", true);
		Section Travel = new Section("Travel", "Travel", "#A64B00", "http://www.independent.ie/lifestyle/travel/rss", true);
		Section Trending = new Section("Trending", "Trending", "#A64B00", "http://www.independent.ie/lifestyle/ThreeTrending/rss", true);
		
		db.addSection(BreakingNews);
		db.addSection(WorldNews);
		db.addSection(IrishNews);
		db.addSection(Woman);
		db.addSection(CelebNews);
		db.addSection(Fashion);
		db.addSection(Beauty);
		db.addSection(Love);
		db.addSection(Health);
		
		db.addSection(Sport);
		db.addSection(Soccer);
		db.addSection(Gaelic);
		db.addSection(Hurling);
		db.addSection(Rugby);
		db.addSection(Golf);
		db.addSection(Horse);
		db.addSection(Other);
		db.addSection(Cycling);
		db.addSection(Business);
		db.addSection(IrishBusiness);
		db.addSection(WorldBusiness);
		db.addSection(Technology);
		db.addSection(Farming);
		db.addSection(SmallBusiness);
		db.addSection(Media);
		db.addSection(PersonalFinance);
		db.addSection(CommercialProperty);
		db.addSection(Entertainment);
		db.addSection(Movies);
		db.addSection(Music);
		db.addSection(TVRadio);
		db.addSection(Festivals);
		db.addSection(Bookarts);
		db.addSection(Competitions);
		db.addSection(Lifestyle);
		db.addSection(HealthLifestyle);
		db.addSection(MothersBabies);
		db.addSection(Education);
		db.addSection(Food);
		db.addSection(Home);
		db.addSection(Motoring);
		db.addSection(Travel);
		db.addSection(Trending);		
		
	}
	
	public void getBackToMain(View v){
		Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
        
        finish();
	}
	
	@Override
    public void onBackPressed() {
        //start activity here
        super.onBackPressed();   
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
        
        finish();

    }
	
	
}
