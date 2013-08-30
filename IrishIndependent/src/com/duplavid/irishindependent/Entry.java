package com.duplavid.irishindependent;

public class Entry {
    public final String title;
    public final String link;
    public final String description;
    public final String picture;

    public Entry(String title, String description, String link, String picture) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.picture = picture;
    }
    
    public String getTitle(){
    	return this.title;
    }
    
    public String getDescription(){
    	return this.description;
    }
    
    public String getLink(){
    	return this.link;
    }
    
    public String getPicture(){
    	return this.picture;
    }
}