package com.duplavid.irishindependent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Shows a single article.
 * If the article fails to load, it tries again once.
 * Creates a cache file with the groupPosition and childPosition.
 * 
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

        try{
            thiscontext = this;
            pd = new ProgressDialog(thiscontext);
            
            Intent i = getIntent();
            
            SingleArticleActivity.link = i.getStringExtra("link");
            SingleArticleActivity.lead = i.getStringExtra("lead");
            SingleArticleActivity.title = i.getStringExtra("title");
            SingleArticleActivity.groupPosition = i.getStringExtra("groupPosition");
            SingleArticleActivity.childPosition = i.getStringExtra("childPosition");

            setTitle(title);
            TextView t = (TextView)findViewById(R.id.title);
    		t.setText(title);
    		t.setTypeface(MainActivity.Regular);
    		t.setBackgroundColor(Color.parseColor(MainActivity.sections.get(Integer.parseInt(groupPosition)).getColor()));
            
            //Set lead
            TextView l = (TextView)findViewById(R.id.lead);
    		l.setText(lead);
    		l.setTypeface(MainActivity.Italic);
            
    		//Get the article itself
    		BufferedReader in = null;
    		String line;
    		StringBuilder stringBuilder = new StringBuilder();
            try {
                in = new BufferedReader(new FileReader(new File(thiscontext.getCacheDir(), groupPosition+"_"+childPosition)));
                while ((line = in.readLine()) != null) stringBuilder.append(line+"\n");
                createArticle(stringBuilder);                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                RetrieveArticle task = new RetrieveArticle();
        		task.execute(new String[] { link });
            } catch (IOException e) {
            	e.printStackTrace();
            }
    		
        }catch(NullPointerException e){
        	finish();
        }
        
    }
	
	@Override
	public void onPause() {
	    super.onPause();

	    if(pd != null)
	    	pd.dismiss();
	    pd = null;
	}
	
	public void createArticle(StringBuilder stringBuilder){
		TextView a = (TextView)findViewById(R.id.article);
		a.setText(stringBuilder);				
		a.setTypeface(MainActivity.Regular);
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
			// TODO Auto-generated method stub
			Document doc;
			try {
				doc = Jsoup.connect(urls[0]).get();
				
				if(doc.select(".byline") != null){
					extraArticle = doc.select(".byline");
				}
				
				article = doc.select(".w50 p:not(.byline)");
				
				if(doc.select(".imgWrapper img").first() != null){
					picture = doc.select(".imgWrapper img").first().attr("src");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				countTry++;
				if(countTry > 1){
					finish();
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
			return article;
		}
		
		@Override
		protected void onPostExecute(Elements result) {
			if (pd != null){
				pd.dismiss();
			}
			
			if(result == null){
				Intent intent = getIntent();
				intent.putExtra(link, SingleArticleActivity.link);
				intent.putExtra(lead, SingleArticleActivity.lead);
				intent.putExtra(title, SingleArticleActivity.title);
				intent.putExtra(groupPosition, SingleArticleActivity.groupPosition);
				intent.putExtra(childPosition, SingleArticleActivity.childPosition);
				
				finish();
				startActivity(intent);
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
					
					TextView b = (TextView)findViewById(R.id.byline);
					b.setText(byline);				
					b.setTypeface(MainActivity.Bold);
				}
				
				//Get picture
				if(picture != null){
					//Retrieve the picture
					ImageView img = (ImageView)findViewById(R.id.articleimage);
					RetreivePictureTask task = new RetreivePictureTask(img);
					task.execute(new String[] { picture });
				}
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
	
	
}