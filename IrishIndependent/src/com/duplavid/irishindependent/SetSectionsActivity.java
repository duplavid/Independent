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
	private ArrayList<Section> sectionlist;
	private static DatabaseHandler db;
	
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
		
	}
	
	public static void populateDatabase(){
		db = MainActivity.db;
		
		Section BreakingNews = new Section("BreakingNews", "Breaking News", "#006363", "http://www.independent.ie/breaking-news/rss", true);
		Section WorldNews = new Section("WorldNews", "World News", "#1D7373", "http://www.independent.ie/world-news/rss", true);
		Section IrishNews = new Section("IrishNews", "Irish News", "#009999", "http://www.independent.ie/irish-news/rss", true);
		
		Section Woman = new Section("Woman", "Woman", "#7109aa", "http://www.independent.ie/woman/rss", true);
		Section CelebNews = new Section("CelebNews", "Celeb News", "#7e1cb4", "http://www.independent.ie/woman/celeb-news/rss", true);
		Section Fashion = new Section("Fashion", "Fashion", "#8c21c6", "http://www.independent.ie/woman/fashion/rss", true);
		Section Beauty = new Section("Beauty", "Beauty", "#a33bdc", "http://www.independent.ie/woman/beauty/rss", true);
		Section Love = new Section("Love", "Love & Sex", "#af46e8", "http://www.independent.ie/woman/love-sex/rss", true);
		Section Health = new Section("Health", "Diet & Fitness", "#b846f6", "http://www.independent.ie/woman/diet-fitness/rss", true);
		
		Section Sport = new Section("Sport", "Sport", "#0f701d", "http://www.independent.ie/sport/rss", true);
		Section Soccer = new Section("Soccer", "Soccer", "#0f7d1e", "http://www.independent.ie/sport/soccer/rss", true);
		Section Gaelic = new Section("GaelicFootball", "Gaelic Football", "#108921", "http://www.independent.ie/sport/gaelic-football/rss", true);
		Section Hurling = new Section("Hurling", "Hurling", "#0f9421", "http://www.independent.ie/sport/hurling/rss", true);
		Section Rugby = new Section("Rugby", "Rugby", "#1d9e2f", "http://www.independent.ie/sport/rugby/rss", true);
		Section Golf = new Section("Golf", "Golf", "#2da43e", "http://www.independent.ie/sport/golf/rss", true);
		Section Horse = new Section("Horse", "Horse Racing", "#43ab52", "http://www.independent.ie/sport/horse-racing/rss", true);
		Section Other = new Section("OtherSports", "Other Sports", "#50b75e", "http://www.independent.ie/sport/other-sports/rss", true);
		Section Cycling = new Section("Cycling", "Cycling", "#5fbe6d", "http://www.independent.ie/sport/other-sports/cycling/rss", true);
		
		Section Business = new Section("Business", "Business", "#1240ab", "http://www.independent.ie/business/rss", true);
		Section IrishBusiness = new Section("IrishBusiness", "Irish Business News", "#224caf", "http://www.independent.ie/business/irish/rss", true);
		Section WorldBusiness = new Section("WorldBusiness", "World Business News", "#325bb9", "http://www.independent.ie/business/world/rss", true);
		Section Technology = new Section("Technology", "Technology", "#406aca", "http://www.independent.ie/business/technology/rss", true);
		Section Farming = new Section("Farming", "Farming", "#4872d3", "http://www.independent.ie/business/farming/rss", true);
		Section SmallBusiness = new Section("SmallBusiness", "Small Business", "#4f7add", "http://www.independent.ie/business/small-business/rss", true);
		Section Media = new Section("Media", "Media", "#527fea", "http://www.independent.ie/business/media/rss", true);
		Section PersonalFinance = new Section("PersonalFinance", "Personal Finance", "#5a88f2", "http://www.independent.ie/business/personal-finance/rss", true);
		Section CommercialProperty = new Section("CommercialProperty", "Commercial Property", "#6f98f7", "http://www.independent.ie/business/commercial-property/rss", true);
		
		Section Entertainment = new Section("Entertainment", "Entertainment", "#834b14", "http://www.independent.ie/entertainment/rss", true);
		Section Movies = new Section("Movies", "Movies", "#925418", "http://www.independent.ie/entertainment/movies/rss", true);
		Section Music = new Section("Music", "Music", "#a5601c", "http://www.independent.ie/entertainment/music/rss", true);
		Section TVRadio = new Section("TVRadio", "TV-Radio", "#b56a20", "http://www.independent.ie/entertainment/tv-radio/rss", true);
		Section Festivals = new Section("Festivals", "Festivals", "#bd6e21", "http://www.independent.ie/entertainment/festivals/rss", true);
		Section Bookarts = new Section("Bookarts", "Book-Arts", "#ce7824", "http://www.independent.ie/entertainment/books-arts/rss", true);
		Section Competitions = new Section("Competitions", "Competitions", "#d77d25", "http://competitions.independent.ie/rss", true);
		
		Section Lifestyle = new Section("Lifestyle", "Lifestyle", "#9e194d", "http://www.independent.ie/lifestyle/rss", true);
		Section HealthLifestyle = new Section("LifestyleHealth", "Lifestyle - Health", "#ae1852", "http://www.independent.ie/lifestyle/health/rss", true);
		Section MothersBabies = new Section("MothersBabies", "Mothers-Babies", "#bf215e", "http://www.independent.ie/lifestyle/mothers-babies/rss", true);
		Section Education = new Section("Education", "Education", "#d02969", "http://www.independent.ie/lifestyle/education/rss", true);
		Section Food = new Section("Food", "Food-Drink", "#dd286e", "http://www.independent.ie/lifestyle/food-drink/rss", true);
		Section Home = new Section("Home", "Home", "#e83a7d", "http://www.independent.ie/lifestyle/property-homes/rss", true);
		Section Motoring = new Section("Motoring", "Motoring", "#f14b8b", "http://www.independent.ie/lifestyle/motoring/rss", true);
		Section Travel = new Section("Travel", "Travel", "#f45693", "http://www.independent.ie/lifestyle/travel/rss", true);
		Section Trending = new Section("Trending", "Trending", "#fc69a2", "http://www.independent.ie/lifestyle/ThreeTrending/rss", true);
		
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
		setsectionsFinished();
	}
	
	@Override
    public void onBackPressed() {
        //start activity here
        super.onBackPressed();
        
        setsectionsFinished();
    }
	
	public void setsectionsFinished(){
		Intent main = new Intent(getApplicationContext(), MainActivity.class);
		main.putExtra("settings", "settings");
        startActivity(main);
        
        finish();
	}
	
	
}
