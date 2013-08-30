package com.duplavid.irishindependent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainParser {
	private static final String ns = null;
	
	public static List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
        	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
	
	private static List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List<Entry> entries = new ArrayList<Entry>();

	    parser.require(XmlPullParser.START_TAG, ns, "rss");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        
		    parser.require(XmlPullParser.START_TAG, ns, "channel");
		    while (parser.next() != XmlPullParser.END_TAG) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        // Starts by looking for the entry tag
		        if (name.equals("item")) {
		            entries.add(readEntry(parser));
		        } else {
		            skip(parser);
		        }
		    }
	    }
	    return entries;
	}
	
	// Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the tag.
	private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "item");
	    String title = null;
	    String description = null;
	    String link = null;
	    String picture = null;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("title")) {
	            title = readTitle(parser);
	        } else if (name.equals("description")) {
	        	description = readSummary(parser);
	        } else if (name.equals("link")) {
	            link = readLink(parser);
	        } else if (name.equals("enclosure")) {
	            picture = readPicture(parser);
	        } else {
	            skip(parser);
	        }
	    }
	    return new Entry(title, description, link, picture);
	}

	// Processes title tags in the feed.
	private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "title");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "title");
	    return title;
	}
	  
	// Processes link tags in the feed.
	private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "link");
	    String link = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "link");
	    return link;
	}

	// Processes description tags in the feed.
	private static String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "description");
	    String summary = readText(parser);
	    parser.require(XmlPullParser.END_TAG, ns, "description");
	    return summary;
	}
	
	// Processes description tags in the feed.
	private static String readPicture(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, ns, "enclosure");
	    @SuppressWarnings("unused")
		String picture = readAttribute(parser, "url");
	    String result = parser.getAttributeValue(ns, "url");
	    parser.require(XmlPullParser.END_TAG, ns, "enclosure");
	    return result;
	}

	// Extracts the tag's text values.
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	// Extracts the tag's attributes.
	private static String readAttribute(XmlPullParser parser, String attribute) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getAttributeValue(ns, attribute);
	        parser.nextTag();
	    }
	    return result;
	}
	  
	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}