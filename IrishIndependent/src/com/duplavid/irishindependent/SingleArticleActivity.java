package com.duplavid.irishindependent;

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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleArticleActivity extends Activity {
	private Context thiscontext;
	
	private static String link;
	private static String lead;
	private static String title;
	private static String groupPosition;
	
	private ProgressDialog pd;
	
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

            setTitle(title);
            TextView t = (TextView)findViewById(R.id.title);
    		t.setText(title);
    		Typeface titletype=Typeface.createFromAsset(thiscontext.getAssets(),"fonts/DroidSerif-Regular.ttf");
    		t.setTypeface(titletype);
    		t.setBackgroundColor(Color.parseColor(MainActivity.sections.get(Integer.parseInt(groupPosition)).getColor()));
            
            //Set lead
            TextView l = (TextView)findViewById(R.id.lead);
    		l.setText(lead);
    		Typeface typeface=Typeface.createFromAsset(thiscontext.getAssets(),"fonts/DroidSerif-Italic.ttf");
    		l.setTypeface(typeface);
            
    		//Get the article itself
            RetrieveArticle task = new RetrieveArticle();
    		task.execute(new String[] { link });
        }catch(NullPointerException e){
        	finish();
        }
        

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
				Intent intent = getIntent();
				intent.putExtra(link, SingleArticleActivity.link);
				intent.putExtra(lead, SingleArticleActivity.lead);
				intent.putExtra(title, SingleArticleActivity.title);
				
				finish();
				startActivity(intent);
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
				
				finish();
				startActivity(intent);
			}else{
				StringBuilder article = new StringBuilder();
				for(int i=0;i<result.size();i++){
					article.append(result.get(i).text()+"\n\n");
				}
				
				TextView a = (TextView)findViewById(R.id.article);
				Typeface articletype=Typeface.createFromAsset(thiscontext.getAssets(),"fonts/DroidSerif-Regular.ttf");
				a.setText(article);				
				a.setTypeface(articletype);
				
				if(extraArticle != null){
					StringBuilder byline = new StringBuilder();
					for(int i=0;i<extraArticle.size();i++){
						byline.append(extraArticle.get(i).text()+"");
					}
					
					TextView b = (TextView)findViewById(R.id.byline);
					Typeface btype=Typeface.createFromAsset(thiscontext.getAssets(),"fonts/DroidSerif-Bold.ttf");
					b.setText(byline);				
					b.setTypeface(btype);
				}
				
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
	
	@Override
	public void onPause() {
	    super.onPause();

	    if(pd != null)
	    	pd.dismiss();
	    pd = null;
	}
	
	
}