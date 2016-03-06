package hack.modprobe.localvaid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QueryDataSource {

  // Database fields
  private SQLiteDatabase database;
  private DataBaseHelper dbHelper;
  private String[] allColumns = {"_id","fk_subuser","symptoms","diagnosis","prescription","sid"};
  Context mContext;
  public QueryDataSource(Context context) {
	  //DataBaseHelper myDbHelper = new DataBaseHelper();
	  mContext = context;
      dbHelper = new DataBaseHelper(context);
      
      try {

      	dbHelper.createDataBase();

	} catch (IOException ioe) {
		Log.e("error sql","1");
		throw new Error("Unable to create database");

	}

	try {

		dbHelper.openDataBase();

	}catch(SQLException sqle){
		Log.e("error sql","2");
		throw sqle;

	}
    
  }

  public void open() throws SQLException {
	  dbHelper.close();
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public Query createQuery(Subuser parent,String symptoms, String diagnosis, String prescription, int sid) {
	  open();
    ContentValues values = new ContentValues();
    values.put("fk_subuser", parent._id);
    values.put("symptoms", symptoms);
    values.put("diagnosis", diagnosis);
    values.put("prescription", prescription);
    values.put("sid", sid);
    long insertId = database.insert("Query", null,
        values);
    Cursor cursor = database.query("Query",
        allColumns, "_id" + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Query query = cursorToQuery(cursor);
    cursor.close();
    return query;
  }
  
  
  public Query getQuery(int id){
	  open();
	  Cursor cursor = database.query("Query",
		        allColumns, "_id" + " = " + id, null,
		        null, null, null);
		    cursor.moveToFirst();
		    Query query = cursorToQuery(cursor);
		    cursor.close();
		    return query;
  }

  public void deleteQuery(Query query) {
    long id = query._id;
    System.out.println("Comment deleted with id: " + id);
    database.delete("Query", "_id"
        + " = " + id, null);
  }
  
  
  public List<Query> getQueries(Subuser subuser) {
	  open();
	    List<Query> queries = new ArrayList<Query>();

	    Cursor cursor = database.query("Query",
		        allColumns, "fk_subuser" + " = " + subuser._id, null,
		        null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Query query = cursorToQuery(cursor);
	      queries.add(query);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return queries;
	  }

  public List<Query> getAllQueries() {
	  open();
    List<Query> queries = new ArrayList<Query>();

    Cursor cursor = database.query("Query",
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Query query = cursorToQuery(cursor);
      queries.add(query);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return queries;
  }

  private Query cursorToQuery(Cursor cursor) {
	//open();
    Query query = new Query();
    query._id=(int)cursor.getLong(0);
    SubuserDataSource sds = new SubuserDataSource(mContext);
    sds.open();
    query._parent=sds.getSubuser(cursor.getInt(1));
    sds.close();
    query._symptoms = cursor.getString(2);
    query._diagnosis = cursor.getString(3);
    query._prescription = cursor.getString(4);
    query._sid = cursor.getInt(5);
    return query;
  }

public int updateQuery(int qid, String presc) {
	// TODO Auto-generated method stub
	open();
	ContentValues values = new ContentValues();
    values.put("prescription", presc);
	return database.update("Query", values, "sid = ?", new String[] { ""+qid });
	
}

public Query getQueryServer(int sid) {
	// TODO Auto-generated method stub
	open();
	Cursor cursor = database.query("Query",
	        allColumns, "sid" + " = " + sid, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Query query = cursorToQuery(cursor);
	    cursor.close();
	    return query;
	
}
}