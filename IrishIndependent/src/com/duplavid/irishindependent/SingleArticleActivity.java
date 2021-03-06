package com.duplavid.irishindependent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Shows a single article.
 * If the article fails to load, it tries again once.
 * Creates three cache files with the groupPosition and childPosition.
 * Caches article, byline and picture source.
 * 
 * @res single_article
 * @author Eva Hajdu
 *
 */
public class SingleArticleActivity extends Activity {
	private Context thiscontext;
	
	private static String link;
	private static String lead;
	private static String title;
	private static String groupPosition;
	private static String childPosition;
	
	private ProgressDialog pd;
	
	private static int countTry;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_article);

        if(isNetworkAvailable() == true){
	        try{
	            thiscontext = this;
	            pd = new ProgressDialog(thiscontext);
	            
	            Intent i = getIntent();
	            
	            SingleArticleActivity.link = i.getStringExtra("link");
	            SingleArticleActivity.lead = i.getStringExtra("lead");
	            SingleArticleActivity.title = i.getStringExtra("title");
	            SingleArticleActivity.groupPosition = i.getStringExtra("groupPosition");
	            SingleArticleActivity.childPosition = i.getStringExtra("childPosition");
	
	            //Set above title
	            TextView t = (TextView)findViewById(R.id.title);
	    		t.setText(title);
	    		t.setTypeface(MainActivity.Regular);
	    		TableRow titlerow = (TableRow)findViewById(R.id.titlerow);
	    		titlerow.setBackgroundColor(Color.parseColor(MainActivity.sections.get(Integer.parseInt(groupPosition)).getColor()));
	            
	    		//Set the down row
	    		TextView t2 = (TextView)findViewById(R.id.titleDown);
	     		t2.setTypeface(MainActivity.Regular);
	     		Button shareButton = (Button)findViewById(R.id.shareButton);
	     		shareButton.setTypeface(MainActivity.Regular);
	     		TableRow titlerow2 = (TableRow)findViewById(R.id.titledownrow);
	     		titlerow2.setBackgroundColor(Color.parseColor(MainActivity.sections.get(Integer.parseInt(groupPosition)).getColor()));
	            
	            //Set lead
	            TextView l = (TextView)findViewById(R.id.lead);
	    		l.setText(lead);
	    		l.setTypeface(MainActivity.Italic);
	            
	    		//Get the article, byline and piclink itself
	    		BufferedReader in = null;
	    		BufferedReader ln = null;
	    		BufferedReader bn = null;
	    		String line;
	    		StringBuilder articleBuilder = new StringBuilder();
	    		StringBuilder picBuilder = new StringBuilder();
	    		StringBuilder bylineBuilder = new StringBuilder();
	    		
	    		//Get article from cache
	            try {
	                in = new BufferedReader(new FileReader(new File(thiscontext.getCacheDir(), groupPosition+"_"+childPosition)));
	                while ((line = in.readLine()) != null) articleBuilder.append(line+"\n");
	                createArticle(articleBuilder);
	            } catch (FileNotFoundException e) {
	                RetrieveArticle task = new RetrieveArticle();
	        		task.execute(new String[] { link });
	            } catch (IOException e) {
	            	//e.printStackTrace();
	            }
	            
	            //Get byline
	            try{
	            	bn = new BufferedReader(new FileReader(new File(thiscontext.getCacheDir(), "byline_"+groupPosition+"_"+childPosition)));
	            	while ((line = bn.readLine()) != null) bylineBuilder.append(line);
	                getByline(bylineBuilder);
	            }catch(FileNotFoundException e){
	            	e.printStackTrace();
	            } catch (IOException e) {
					e.printStackTrace();
				}
	            
	            //Get picture
	            try{
	            	ln = new BufferedReader(new FileReader(new File(thiscontext.getCacheDir(), "piclink_"+groupPosition+"_"+childPosition)));
	            	while ((line = ln.readLine()) != null) picBuilder.append(line);
	                getPic(picBuilder);
	            }catch(FileNotFoundException e){
	            	e.printStackTrace();
	            } catch (IOException e) {
					e.printStackTrace();
				}
	    		
	        }catch(NullPointerException e){
	        	Toast.makeText(thiscontext, "We're sorry, there's an error in this article, it can't be opened.", Toast.LENGTH_SHORT).show();
	        	finish();
	        }
        }else{
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Your mobile isn't connected to the internet. Please check your connection and try again.")
			.setTitle("No connection");
	
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
        }
        
    }
	
	@Override
	public void onPause() {
	    super.onPause();

	    if(pd != null)
	    	pd.dismiss();
	    pd = null;
	}
	
	public void getBackToMain(View v){
		finish();
	}
	
	public void createArticle(StringBuilder stringBuilder){
		TextView a = (TextView)findViewById(R.id.article);
		a.setText(stringBuilder);				
		a.setTypeface(MainActivity.Regular);
	}
	
	public void getPic(StringBuilder piclink){
		ImageView img = (ImageView)findViewById(R.id.articleimage);
		RetreivePictureTask task = new RetreivePictureTask(img);
		task.execute(new String[] { piclink.toString() });
	}
	
	public void getPic(String piclink){
		ImageView img = (ImageView)findViewById(R.id.articleimage);
		RetreivePictureTask task = new RetreivePictureTask(img);
		task.execute(new String[] { piclink });
	}
	
	public void getByline(StringBuilder byline){
		TextView b = (TextView)findViewById(R.id.byline);
		b.setText(byline);				
		b.setTypeface(MainActivity.Bold);
	}
	
	class RetrieveArticle extends AsyncTask<String, Void, Elements>{
		String picture = null;
		Elements extraArticle = null;
		
		@Override
		protected void onPreExecute() {
			pd.setTitle("Loading...");
			pd.setMessage("Please wait.");
			pd.setCancelable(true);
			pd.setIndeterminate(true);
			pd.show();
		}
		
		@Override
		protected Elements doInBackground(String... urls) {
			Elements article = null;
			Document doc;
			try {
				doc = Jsoup.connect(urls[0]).get();
				
				if(doc.select(".date") != null){
					extraArticle = doc.select(".date");
				}
				
				article = doc.select(".body p:not(.date, .lead)");
				
				if(doc.select(".imgWrapper img").first() != null){
					picture = doc.select(".imgWrapper img").first().attr("src");
				}
			} catch (Exception e) {
				cancelArticle();
			}
			return article;
		}
		
		@Override
		protected void onPostExecute(Elements result) {
			if (pd != null){
				pd.dismiss();
			}
			
			if(result == null){
				cancelArticle();
			}else{
				StringBuilder article = new StringBuilder();
				for(int i=0;i<result.size();i++){
					article.append(result.get(i).text()+"\n\n");
				}
				
				createArticle(article);
				
				//Save the article in file
			    try {
			        String filename = groupPosition+"_"+childPosition;			        
			        FileWriter out = new FileWriter(new File(thiscontext.getCacheDir(), filename));
		            out.write(article.toString());
		            out.close();
			    }catch (IOException e) {
			        e.printStackTrace();
			    }
			    
				//Get byline
				if(extraArticle != null){
					StringBuilder byline = new StringBuilder();
					for(int i=0;i<extraArticle.size();i++){
						byline.append(extraArticle.get(i).text()+"");
					}
					
					getByline(byline);
					
					//Save byline in file
					try {
				        String filename = "byline_"+groupPosition+"_"+childPosition;			        
				        FileWriter out = new FileWriter(new File(thiscontext.getCacheDir(), filename));
			            out.write(byline.toString());
			            out.close();
				    }catch (IOException e) {
				        e.printStackTrace();
				    }
				}
				
				//Get picture
				if(picture != null){
					//Retrieve the picture
					getPic(picture);
					
					//Save picture in file
					try {
				        String filename = "piclink_"+groupPosition+"_"+childPosition;			        
				        FileWriter out = new FileWriter(new File(thiscontext.getCacheDir(), filename));
			            out.write(picture);
			            out.close();
				    }catch (IOException e) {
				        e.printStackTrace();
				    }
				}
			}
		}
		
		private void cancelArticle(){
			countTry++;
			System.out.println(countTry);
			if(countTry > 1){
				cancel(true);
				finish();
				Toast.makeText(thiscontext, "We're sorry, there's an error in this article, it can't be opened.", Toast.LENGTH_SHORT).show();
			}else{
				Intent intent = getIntent();
				intent.putExtra(link, SingleArticleActivity.link);
				intent.putExtra(lead, SingleArticleActivity.lead);
				intent.putExtra(title, SingleArticleActivity.title);
				intent.putExtra(groupPosition, SingleArticleActivity.groupPosition);
				intent.putExtra(childPosition, SingleArticleActivity.childPosition);
				countTry++;
				finish();
				startActivity(intent);
			}
		}
		
	}
	
	class RetreivePictureTask extends AsyncTask<String, Void, Drawable> {
	    private ImageView pic;

	    public RetreivePictureTask(ImageView pic){
	    	this.pic = pic;
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
	    	BitmapDrawable bd=(BitmapDrawable) result;
	    	pic.getLayoutParams().height = bd.getBitmap().getHeight();
	    	pic.setImageDrawable(result);
	    	
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
	
	//Check for network
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
			= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	//Share article
	public void shareArticle(View v){
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");		
		String shareBody = SingleArticleActivity.link+"\n\n"+SingleArticleActivity.lead;
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, SingleArticleActivity.title);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
}