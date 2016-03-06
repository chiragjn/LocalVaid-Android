package hack.modprobe.localvaid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class AlarmService extends WakeIntentService {

	public AlarmService() {
		super("AlarmService");
	}

	@Override
	void doReminderWork(Intent intent) {
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
		note = builder
				.setContentIntent(pi)
				.setSound(alarmSound)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(getResources().getString(R.string.reminder))
				.setWhen(5000)
				.setAutoCancel(true)
				.setContentTitle(getResources().getString(R.string.reminder))
				.setContentText(
						getResources().getString(R.string.reminder_description))
				.build();
		int id = 123456789;
		manager.notify(id, note);

	}
}