package com.duplavid.irishindependent;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Main class for working with sections.
 * After an Entry has been parsed, a Section can be populated.
 * A Section holds the cache of that section -
 * TODO should be rewritten to use only one cache, not many!
 * 
 * @author Eva Hajdu
 *
 */
public class Section{
	public ArrayList<String> descriptions;
	public ArrayList<String> links;
	public ArrayList<String> titles;
	public ArrayList<String> pictures;
	
	public int id;
	public String name;
	public String fullname;
	public String color;
	public String url;
	public boolean state;
	
	private LruCache<String, Bitmap> mMemoryCache;
	
    // Empty constructor
    public Section(){
		//Create cache
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int cacheSize = maxMemory / 8;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	    
    	descriptions = new ArrayList<String>();
		links = new ArrayList<String>();
		titles = new ArrayList<String>();
		pictures = new ArrayList<String>();
		state = true;
    }
    
	public Section(int id, String name, String fullname, String color, String url, boolean state){
		//Create cache
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int cacheSize = maxMemory / 8;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
	    };
		
		descriptions = new ArrayList<String>();
		links = new ArrayList<String>();
		titles = new ArrayList<String>();
		pictures = new ArrayList<String>();
		this.id = id;
		this.name = name;
		this.fullname = fullname;
		this.color = color;
		this.url = url;
		this.state = state;
	}
	
	public Section(String name, String fullname, String color, String url, boolean state){
		//Create cache
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int cacheSize = maxMemory / 8;
	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
	    };
		
		descriptions = new ArrayList<String>();
		links = new ArrayList<String>();
		titles = new ArrayList<String>();
		pictures = new ArrayList<String>();
		this.name = name;
		this.fullname = fullname;
		this.color = color;
		this.url = url;
		this.state = state;
	}
	
	public int getID(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getFullName(){
		return this.fullname;
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public String getColor(){
		return this.color;
	}
	
	public boolean getState(){
		return this.state;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setFullName(String fullname){
		this.fullname = fullname;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public void setColor(String color){
		this.color = color;
	}
	
	public void setState(boolean state){
		this.state = state;
	}
	
	public ArrayList<String> getFirst5Desc(){
		ArrayList<String> five = new ArrayList<String>();
		int count = 5;
		
		if(this.descriptions.size() < 5){
			count = this.descriptions.size();
		}
		for(int i=0;i<count;i++){
			five.add(this.descriptions.get(i));
		}
		
		return five;
	}
	
	public ArrayList<String> getFirst5Links(){
		ArrayList<String> five = new ArrayList<String>();
		int count = 5;
		
		if(this.links.size() < 5){
			count = this.links.size();
		}
		for(int i=0;i<count;i++){
			five.add(this.links.get(i));
		}
		
		return five;
	}
	
	public ArrayList<String> getFirst5Titles(){
		ArrayList<String> five = new ArrayList<String>();
		int count = 5;
		
		if(this.titles.size() < 5){
			count = this.titles.size();
		}
		for(int i=0;i<count;i++){
			five.add(this.titles.get(i));
		}
		
		return five;
	}
	
	public ArrayList<String> getFirst5Pics(){
		ArrayList<String> five = new ArrayList<String>();
		int count = 5;
		
		if(this.pictures.size() < 5){
			count = this.pictures.size();
		}
		for(int i=0;i<count;i++){
			five.add(this.pictures.get(i));
		}
		
		return five;
	}

	
	//Caching images
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
}