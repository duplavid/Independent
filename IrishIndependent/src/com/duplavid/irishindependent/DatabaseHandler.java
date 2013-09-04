package com.duplavid.irishindependent;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "sections";
 
    // Expenses table name
    private static final String TABLE_SECTIONS = "sections";
 
    // Expenses Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_COLOR = "color";
    private static final String KEY_URL = "url";
    private static final String KEY_STATE = "state";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SECTION_TABLE = "CREATE TABLE " + TABLE_SECTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
        		+ KEY_NAME + " TEXT,"
                + KEY_FULLNAME + " TEXT,"
                + KEY_COLOR + " TEXT,"
                + KEY_URL + " TEXT,"
                + KEY_STATE + " INTEGER" + ")";
        db.execSQL(CREATE_SECTION_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECTIONS);
 
        // Create tables again
        onCreate(db);
    }

    // Adding new section
    void addSection(Section section) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, section.getName());
        values.put(KEY_FULLNAME, section.getFullName());
        values.put(KEY_COLOR, section.getUrl());
        values.put(KEY_URL, section.getColor());
        values.put(KEY_STATE, boolToInt(section.getState()));
 
        // Inserting Row
        db.insert(TABLE_SECTIONS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single section
    Section getSection(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_SECTIONS, new String[] { KEY_ID,
        		KEY_NAME, KEY_FULLNAME, KEY_COLOR, KEY_URL, KEY_STATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        
        Section exp = new Section(
    		cursor.getString(1),
    		cursor.getString(2),
    		cursor.getString(3),
    		cursor.getString(4),
    		intToBool(Integer.parseInt(cursor.getString(5)))
    	);
        
        //Close database connection
        cursor.close();
        db.close();
        // return section
        return exp;
    }
     
    // Getting all sections regardless of state
    public ArrayList<Section> getAllSections() {
        ArrayList<Section> sectionList = new ArrayList<Section>();
        // Select All Query
        String selectQuery = "SELECT * FROM "+TABLE_SECTIONS+"";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Section section = new Section();
                section.setName(cursor.getString(1));
                section.setFullName(cursor.getString(2));
                section.setUrl(cursor.getString(3));
                section.setColor(cursor.getString(4));
                section.setState(intToBool(Integer.parseInt(cursor.getString(5))));
                // Adding section to list
                sectionList.add(section);
            } while (cursor.moveToNext());
        }
        //Close database connection
        cursor.close();
        db.close();
        // return list
        return sectionList;
    }
    
    //Get all sections that are enabled
    public ArrayList<Section> getEnabledSections() {
        ArrayList<Section> sectionList = new ArrayList<Section>();
        // Select All Query
        String selectQuery = "SELECT * FROM "+TABLE_SECTIONS+" WHERE "+KEY_STATE+" = 1";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Section section = new Section();
                section.setName(cursor.getString(1));
                section.setFullName(cursor.getString(2));
                section.setUrl(cursor.getString(3));
                section.setColor(cursor.getString(4));
                section.setState(intToBool(Integer.parseInt(cursor.getString(5))));
                // Adding section to list
                sectionList.add(section);
            } while (cursor.moveToNext());
        }
        //Close database connection
        cursor.close();
        db.close();
        // return list
        return sectionList;
    }
    

    /*
    // Getting sections by activity
    public ArrayList<Expense> getExpensesByDate(String date) {
		ArrayList<Expense> expenseList = new ArrayList<Expense>();
		String selectQuery = "SELECT * FROM "+TABLE_EXPENSES+" WHERE strftime('%Y-%m',"+KEY_DATE+") = '"+date+"' ORDER BY "+KEY_DATE+" DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(Integer.parseInt(cursor.getString(0)));
                expense.setInout(Integer.parseInt(cursor.getString(1)));
                expense.setDate(cursor.getString(2));
                expense.setAmount(Double.parseDouble(cursor.getString(3)));
                expense.setAdditional(cursor.getString(4));
                // Adding expense to list
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        //Close data
        cursor.close();
        db.close();
        // return expense list
        return expenseList;
    }
 */
    // Updating single section
    public int updateSection(Section section) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, section.getName());
        values.put(KEY_FULLNAME, section.getFullName());
        values.put(KEY_COLOR, section.getColor());
        values.put(KEY_URL, section.getUrl());
        values.put(KEY_STATE, boolToInt(section.getState()));
        // updating row
        return db.update(TABLE_SECTIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(section.getID()) });
    }
    
    public int updateState(int id, boolean state){
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(KEY_STATE, boolToInt(state));
    	
        return db.update(TABLE_SECTIONS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id)});
    }
    
    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
    
    public boolean intToBool(int b) {
    	if(b == 1){
    		return true;
    	}else{
    		return false;
    	}
    }
 
}