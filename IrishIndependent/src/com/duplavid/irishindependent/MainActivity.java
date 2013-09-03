package com.duplavid.irishindependent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

/**
 * Has menus (refresh and settings -> it connects to SetSectionsActivity)
 * Oncreate -> connects to ExpandableListAdapter - populates the available sections
 * Has inner class: DownloadWebPageTask for populating the sections with the links & picturelinks & descriptions & titles
 * 
 * @res exp_mainactivity
 * @author Eva Hajdu
 *
 */
public class MainActivity extends Activity {
	public static Context thiscontext;
	ArrayList<String> links;
	String [] urls;
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";
	public static DatabaseHandler db;
	public static ArrayList<Section> sections;
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    
	public ProgressDialog pd;
	
	public Map<String,?> createItem(String title, String caption) {
		Map<String, String> item = new HashMap<String, String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exp_mainactivity);
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
		
		pd = new ProgressDialog(this.getApplicationContext());
		db = new DatabaseHandler(this);
		sections = new ArrayList<Section>();
		sections = db.getEnabledSections();
		
		//TODO: elso bejelentkezes!
		
		if(sections.size() == 0){
			SetSectionsActivity.populateDatabase();
			sections = db.getEnabledSections();
			//Intent intent = new Intent(this, SetSectionsActivity.class);
			//startActivity(intent);
		}
		
		thiscontext = this;
		
		int size = sections.size();
		urls = new String[size];
		int i = 0;

		for(Section sec : sections){
			urls[i] = sec.getUrl();
			i++;
		}

		getRSS();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onPause() {
	    super.onPause();

	    if(pd != null){
	    	pd.dismiss();
	    }	
	    pd = null;
	}	

	public void getRSS() {
		pd = new ProgressDialog(thiscontext);
		DownloadWebPageTask task = new DownloadWebPageTask(sections);
		task.execute(urls);
	}

	/**
	 * Populates the static <Section> ArrayList with all things that are in the RSS.
	 * Connects to MainParser
	 * 
	 * @author Eva Hajdu
	 *
	 */
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		private ArrayList<Section> objects;
		
		@Override
		protected void onPreExecute() {
			pd.setTitle("Loading...");
			pd.setMessage("Please wait.");
			pd.setCancelable(true);
			pd.setIndeterminate(true);
			pd.show();
		}
		
		public DownloadWebPageTask(ArrayList<Section> sections){
			this.objects = sections;
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			int j = 0;
			
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();
					List<Entry> list = MainParser.parse(content);

					for(int i=0;i<list.size();i++){
						Section o = this.objects.get(j);
						o.descriptions.add(list.get(i).getDescription());
						o.links.add(list.get(i).getLink());
						o.titles.add(list.get(i).getTitle());
						o.pictures.add(list.get(i).getPicture());
					}
					j++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (pd != null) { 
		        pd.dismiss();
		    }
			showList();
		}
	}
	
	/**
	 * Connects to ExpandableListAdapter
	 * Shows all available sections and their first 5 things.
	 * listDataHeader -> list of the header titles
	 * listDataChild -> List of the links
	 */
	public void showList(){
		ArrayList<String> listDataHeader = new ArrayList<String>();
		HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
		for(int i=0;i<MainActivity.sections.size();i++){
			listDataHeader.add(MainActivity.sections.get(i).getFullName());
			
			List<String> links = new ArrayList<String>();
			for(int j=0;j<MainActivity.sections.get(i).getFirst5Links().size();j++){
				links.add(MainActivity.sections.get(i).getFirst5Links().get(j));
			}			
			
			listDataChild.put(listDataHeader.get(i), links);
		}
		
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
		
	}
	
	/**
	 * Set up menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_refresh:
	    	getRSS();
	        return true;
	    case R.id.action_settings:
	    	settings();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Menumethod Refresh
	 */
	public void refresh(){
		getRSS();
	}
	
	/**
	 * Menumethod Settings
	 */
	public void settings(){
		Intent intent = new Intent(thiscontext, SetSectionsActivity.class);
		startActivity(intent);
	}

}
