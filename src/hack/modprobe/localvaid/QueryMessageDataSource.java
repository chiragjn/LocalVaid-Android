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

public class QueryMessageDataSource {

	// Database fields
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allColumns = { "_id", "fk_query", "type", "by", "content",
			"sid" };
	Context mContext;

	public QueryMessageDataSource(Context context) {
		// DataBaseHelper myDbHelper = new DataBaseHelper();
		mContext = context;
		dbHelper = new DataBaseHelper(context);

		try {

			dbHelper.createDataBase();

		} catch (IOException ioe) {
			Log.e("error sql", "1");
			throw new Error("Unable to create database");

		}

		try {

			dbHelper.openDataBase();

		} catch (SQLException sqle) {
			Log.e("error sql", "2");
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

	public QueryMessage createQueryMessage(Query parent, String content,
			int type, int by, int sid) {
		open();
		ContentValues values = new ContentValues();
		values.put("fk_query", parent._id);
		values.put("content", content);
		values.put("type", type);
		values.put("by", by);
		values.put("sid", sid);
		long insertId = database.insert("QueryMessage", null, values);
		Cursor cursor = database.query("QueryMessage", allColumns, "_id"
				+ " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		QueryMessage querymessage = cursorToQueryMessage(cursor);
		cursor.close();
		return querymessage;
	}

	public void deleteQueryMessage(QueryMessage querymessage) {
		long id = querymessage._id;
		System.out.println("Comment deleted with id: " + id);
		database.delete("QueryMessage", "_id" + " = " + id, null);
	}

	public List<QueryMessage> getQueryMessages(Query query) {
		List<QueryMessage> querymessages = new ArrayList<QueryMessage>();
		open();
		Cursor cursor = database.query("QueryMessage", allColumns, "fk_query"
				+ " = " + query._id, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			QueryMessage querymessage = cursorToQueryMessage(cursor);
			querymessages.add(querymessage);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return querymessages;
	}

	public List<QueryMessage> getAllQueryMessages() {
		List<QueryMessage> querymessages = new ArrayList<QueryMessage>();
		open();
		Cursor cursor = database.query("QueryMessage", allColumns, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			QueryMessage querymessage = cursorToQueryMessage(cursor);
			querymessages.add(querymessage);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return querymessages;
	}

	private QueryMessage cursorToQueryMessage(Cursor cursor) {
		//open();
		QueryMessage querymessage = new QueryMessage();
		querymessage._id = (int) cursor.getLong(0);
		QueryDataSource qds = new QueryDataSource(mContext);
		qds.open();
		querymessage._parent = qds.getQuery(cursor.getInt(1));
		qds.close();
		querymessage._type = cursor.getInt(2);
		querymessage._by = cursor.getInt(3);
		querymessage._content = cursor.getString(4);
		querymessage._sid = cursor.getInt(5);
		return querymessage;
	}
}