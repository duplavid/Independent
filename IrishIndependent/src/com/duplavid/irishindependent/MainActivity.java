package com.duplavid.irishindependent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.duplavid.irishindependent.SingleArticleActivity.RetrieveArticle;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;

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
	
	ArrayList<String> links;
	String [] urls;
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";
	public static DatabaseHandler db;
	public static ArrayList<Section> sections;
	
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	
	public ProgressDialog pd;
	private static String pdText;
	
	final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	final static int cacheSize = maxMemory / 8;
	
	public static Typeface Regular;
	public static Typeface Bold;
	public static Typeface Italic;
	
	public static Context thiscontext;

	private static LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(1024 * 1024 * 2) {
		@Override
		public int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}
	};

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
		thiscontext = this;
		expListView = (ExpandableListView) findViewById(R.id.lvExp);
		
		sections = new ArrayList<Section>();
		
		pd = new ProgressDialog(this.getApplicationContext());
		pd.setCancelable(false);
		db = new DatabaseHandler(this);

		//Set typefaces
		Regular = Typeface.createFromAsset(this.getAssets(),"fonts/DroidSerif-Regular.ttf");
		Bold = Typeface.createFromAsset(this.getAssets(),"fonts/DroidSerif-Bold.ttf");
		Italic = Typeface.createFromAsset(this.getAssets(),"fonts/DroidSerif-Italic.ttf");
	
		//Check if the came from the Settings list
		Intent i = getIntent();

		if(i.hasExtra("settings") ){
			if(isNetworkAvailable() == true){
				getRSS();
			}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Without connection, the section list can't be refreshed. The app will show you the latest cached sectionlist.")
				.setTitle("No connection");
		
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						getFromCache();
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				
			}
			getIntent().removeExtra("settings");
		}else{
			//Check if there's a section cache file, if not, get the RSS
			//Get article from cache
			getFromCache();
		}
		
	}
	
	public void getFromCache(){
		try {
            FileInputStream fis = thiscontext.openFileInput("sectioncache");
            ObjectInputStream is = new ObjectInputStream(fis);
            sections = (ArrayList) is.readObject();
            is.close();
            showList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            getRSS();
        } catch (IOException e) {
        	e.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Check for network
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
			= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
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
		if(isNetworkAvailable() == true){
			setSections();
			//Delete the cache every time we refresh the RSS
			clearApplicationData();
			mMemoryCache.evictAll();
			pd = new ProgressDialog(this);
			DownloadWebPageTask task = new DownloadWebPageTask(sections);
			task.execute(urls);
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Your mobile isn't connected to the internet. Please check your connection and try again.")
			.setTitle("No connection");
	
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
	
	public void setSections(){
		sections = db.getEnabledSections();
			
		//TODO: elso bejelentkezes!
		if(sections.size() == 0){
			SetSectionsActivity.populateDatabase();
			sections = db.getEnabledSections();
			pdText = "First time setup - you can set the sections you want to see in the Settings later.";
		}else{
			pdText = "Loading sections list...";
		}
			
		int size = sections.size();
		urls = new String[size];
		int i = 0;
		
		for(Section sec : sections){
			urls[i] = sec.getUrl();
			i++;
		}
	}

	/**
	 * Populates the static <Section> ArrayList with all things that are in the RSS.
	 * Connects to MainParser
	 * 
	 * @author Eva Hajdu
	 *
	 */
	private class DownloadWebPageTask extends AsyncTask<String, Integer, String> {
		
		private ArrayList<Section> objects;
	
		@Override
		protected void onPreExecute() {
			int length = sections.size();
			
			pd.setTitle("Loading...");
			pd.setMessage(pdText);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMax(0);
			pd.setMax(length);
			pd.setIndeterminate(false);
			pd.setCancelable(false);
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
					
					publishProgress((int) j);
					j++;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			if(pd != null){
				pd.setProgress(0);
				pd.setProgress(progress[0]);
			}
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
		
		//Save cache section file
		FileOutputStream fos;
		try {
			fos = thiscontext.openFileOutput("sectioncache", Context.MODE_PRIVATE);
			ObjectOutputStream os;
			os = new ObjectOutputStream(fos);
			os.writeObject(MainActivity.sections);
			os.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		Intent intent = new Intent(this.getApplicationContext(), SetSectionsActivity.class);
		startActivity(intent);
	}

	//Caching images
	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	//Delete file cache
	public void clearApplicationData() {
		File cache = getCacheDir();
		File appDir = new File(cache.getParent());
		if(appDir.exists()){
			String[] children = appDir.list();
			for(String s : children){
				if(s.equals("cache")){
					deleteDir(new File(appDir, s));
				}
			}
		}
	}
	 
	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	

}
