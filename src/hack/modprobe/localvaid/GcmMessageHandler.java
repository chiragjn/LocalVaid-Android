package hack.modprobe.localvaid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandler extends GcmListenerService {
	public static final int MESSAGE_NOTIFICATION_ID = 435345;

	@Override
	public void onMessageReceived(String from, Bundle dataB) {
		try{
		String type = dataB.getString("type");
		String data = dataB.getString("data");
		String topic = "", message = "";
		Log.e("DATA",data);
		if (type.equals("1")) {
			// New prescription
			parsePres(data);
			topic = getResources().getString(R.string.new_prescription);
			message = getResources().getString(
					R.string.prescription_notification_description);
			QueryDataSource qds = new QueryDataSource(getApplicationContext());
			qds.open();
			qds.updateQuery(Integer.parseInt(dataB.getString("qid")), data);
			qds.close();
			addReminders(data);
		} else {
			// New message
			topic = getResources().getString(R.string.new_message);
			message = getResources().getString(
					R.string.message_notification_description);
			QueryMessageDataSource qmds = new QueryMessageDataSource(
					getApplicationContext());
			QueryDataSource qds = new QueryDataSource(getApplicationContext());
			qds.open();
			Query q = qds.getQueryServer(Integer.parseInt(dataB
					.getString("qid")));
			qds.close();

			qmds.open();
			qmds.createQueryMessage(q, data, 2, 1,
					Integer.parseInt(dataB.getString("sid")));
			qmds.close();
		}
		Log.e("GCM", "Recieved");
		createNotification(topic, message);
		}catch(Exception ignored)
		{
			
		}
		
	}

	private ArrayList<Prescription> parsePres(String data) {
		String temp[] = data.split("\\,");
		ArrayList<Prescription> arr = new ArrayList<Prescription>();
		for (int i = 0; i < temp.length; i++) {
			String t[] = temp[i].split("\\-");
			arr.add(new Prescription(t[0], t[1].indexOf('m') >= 0, t[1]
					.indexOf('a') >= 0, t[1].indexOf('n') >= 0));
		}
		return arr;
	}

	// Creates notification based on title and body received
	private void createNotification(String title, String body) {

		Context context = getBaseContext();
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		Notification note = new Notification(R.drawable.ic_launcher, "Alarm",
				System.currentTimeMillis());

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		note = builder.setContentIntent(pi).setSound(alarmSound)
				.setSmallIcon(R.drawable.ic_launcher).setTicker(title)
				.setAutoCancel(true).setContentTitle(title)
				.setContentText(body).build();
		int id = 123456789;
		manager.notify(id, note);

	}

	private void addReminders(String data) {
		// TODO Auto-generated method stub

//		Intent i = new Intent(this, OnAlarmReceiver.class);
//		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
//				PendingIntent.FLAG_ONE_SHOT);
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 10);
//		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//				pi);
		String supRequest = data;//"Crocin-MAN, Dispirin-AN, D Cold-MN";
		String[] supReqArr = supRequest.split(",");
		for(int k = 0; k < supReqArr.length; k++){
			
			String request = supReqArr[k];//"Crocin-MAN";
			String reqArr[] = request.split("-");
		    String reminderTitle, reminderDesc; int reminderDays;
		    reminderDays = 7;
		    reminderTitle = "Medicine: "+reqArr[0];
		    reminderDesc = "LocalVaid Medicine reminder";
		    
			for(int i = 0; i < reqArr[1].length(); i++){
		    
				char dosage = reqArr[1].charAt(i); int hour = 9;
				switch(dosage){
					case 'm':  hour = 9;break;
					case 'a':  hour = 15;break;
					case 'n':  hour = 21;break;
				}
			    Calendar c = Calendar.getInstance();
			    int day = c.get(Calendar.DAY_OF_MONTH);
			    int month = c.get(Calendar.MONTH);
			    int year = c.get(Calendar.YEAR);
				
			    GregorianCalendar calDate = new GregorianCalendar(year, month, day, hour, 0);
			    GregorianCalendar endDate = new GregorianCalendar(year, month, day, hour, 0);  
			    
			    final ContentValues event = new ContentValues();
				event.put(Events.CALENDAR_ID, 1);
			    event.put(Events.TITLE, reminderTitle);
			    event.put(Events.DESCRIPTION, reminderDesc);
			    
			    endDate.add(Calendar.DAY_OF_MONTH, reminderDays);
			    event.put(Events.DTSTART, calDate.getTimeInMillis());
			    event.put(Events.DTEND, endDate.getTimeInMillis());
			    event.put(Events.ALL_DAY, 0);   // 0 for false, 1 for true
			    event.put(Events.HAS_ALARM, 1); // 0 for false, 1 for true

			    String timeZone = TimeZone.getDefault().getID();
			    event.put(Events.EVENT_TIMEZONE, timeZone);

			    Uri baseUri;
			    if (Build.VERSION.SDK_INT >= 8) {
			        baseUri = Uri.parse("content://com.android.calendar/events");
			    } else {
			        baseUri = Uri.parse("content://calendar/events");
			    }

			    Uri uri = getApplicationContext().getContentResolver().insert(baseUri, event);
			 // get the event ID that is the last element in the Uri
			    long eventID = Long.parseLong(uri.getLastPathSegment());

			    // add 10 minute reminder for the event
			    ContentValues reminders = new ContentValues();
			    reminders.put(Reminders.EVENT_ID, eventID);
			    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
			    reminders.put(Reminders.MINUTES, 5);

			    Uri uriReminder = getApplicationContext().getContentResolver().insert(Reminders.CONTENT_URI, reminders);
			    

			}
		}
	}

}