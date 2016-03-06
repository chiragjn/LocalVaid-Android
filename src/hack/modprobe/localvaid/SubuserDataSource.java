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

public class SubuserDataSource {

  // Database fields
  private SQLiteDatabase database;
  private DataBaseHelper dbHelper;
  private String[] allColumns = { "_id","name","gender","age","weight", "heightFt", "heightIn","sid" };

  public SubuserDataSource(Context context) {
	  //DataBaseHelper myDbHelper = new DataBaseHelper();
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

  public Subuser createSubuser(String name, int gender, int age, int weight, int heightFt, int heightIn, int sid) {
	  open();
    ContentValues values = new ContentValues();
    //values.put("_id", 1);
    values.put("name", name);
    values.put("gender", gender);
    values.put("age", age);
    values.put("weight", weight);
    values.put("heightFt", heightFt);
    values.put("heightIn", heightIn);
    values.put("sid", sid);
    long insertId = database.insert("Subuser", null,
        values);
    Cursor cursor = database.query("Subuser",
        allColumns, "_id" + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Subuser newsubuser = cursorToSubUser(cursor);
    cursor.close();
    return newsubuser;
  }
  
  
  public Subuser getSubuser(int id){
	  open();
	  Cursor cursor = database.query("Subuser",
		        allColumns, "_id" + " = " + id, null,
		        null, null, null);
		    cursor.moveToFirst();
		    Subuser newsubuser = cursorToSubUser(cursor);
		    cursor.close();
		    return newsubuser;
  }
  

  public void deleteSubuser(Subuser subuser) {
    long id = subuser._id;
    System.out.println("Comment deleted with id: " + id);
    database.delete("Subuser", "_id"
        + " = " + id, null);
  }

  public List<Subuser> getAllSubusers() {
	  open();
    List<Subuser> subusers = new ArrayList<Subuser>();

    Cursor cursor = database.query("Subuser",
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      Subuser subuser = cursorToSubUser(cursor);
      subusers.add(subuser);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return subusers;
  }

  private Subuser cursorToSubUser(Cursor cursor) {
	//open();
    Subuser subuser = new Subuser();
    subuser._id=(int)cursor.getLong(0);
    subuser._name = cursor.getString(1);
    subuser._gender = cursor.getInt(2);
    subuser._age = cursor.getInt(3);
    subuser._weight = cursor.getInt(4);
    subuser._heightFt = cursor.getInt(5);
    subuser._heightIn = cursor.getInt(6);
    subuser._sid = cursor.getInt(7);
    return subuser;
  }
}