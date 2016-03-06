package hack.modprobe.localvaid;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MainActivity";
	private BroadcastReceiver mRegistrationBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			String lang = AppController.getInstance().prefs.getString(
					Constants.LANGUAGE_KEY, "none");
			if (lang.equals("none")) {
				getSupportFragmentManager().beginTransaction()
						.add(R.id.container, new SelectLanguageFragment())
						.commit();
			} else {
				Locale locale = new Locale(lang);
				Locale.setDefault(locale);
				Configuration config = new Configuration();
				config.locale = locale;
				getResources().updateConfiguration(config,
						getResources().getDisplayMetrics());
				getSupportActionBar().setTitle(R.string.app_name);
				if(AppController.getInstance().prefs.contains(Constants.LOGIN_KEY)) {
					getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new MainMenuFragment()).commit();
				}
				else {
					getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new RegisterFragment()).commit();
				}
			}

		}
//		TelephonyManager tMgr1 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		String mPhoneNumber1 = tMgr1.getLine1Number();
//		mPhoneNumber1 = "9930113199";// HardCoded for now, take from user later
//		Editor e = AppController.getInstance().prefs.edit();
//		e.putString("mobile", mPhoneNumber1);
//		e.commit();
//		e.apply();

//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected Void doInBackground(Void... params) {
//				// TODO Auto-generated method stub
//				String charset = "UTF-8";
//				String requestURL = Constants.BAE_URL + "addSup/";
//
//				MultipartUtility multipart;
//				try {
//					multipart = new MultipartUtility(requestURL, charset);
//					multipart.addFormField("mobile", AppController
//							.getInstance().prefs.getString("mobile", "lel"));
//					List<String> response = multipart.finish(); // response from
//																// server.
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return null;
//			}
//		}.execute();
//
//		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//
//				SharedPreferences sharedPreferences = PreferenceManager
//						.getDefaultSharedPreferences(context);
//				boolean sentToken = sharedPreferences.getBoolean(
//						QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//				if (sentToken) {
//					Log.e("GCM", "Done");
//				} else {
//					Log.e("GCM", "Not Done");
//				}
//			}
//		};
//
//		if (checkPlayServices()) {
//			// Start IntentService to register this application with GCM.
//			Intent intent = new Intent(MainActivity.this,
//					RegistrationIntentService.class);
//			startService(intent);
//		}

	}

	@Override
	protected void onResume() {

		Log.e("MAIN", "RESUME");
//		LocalBroadcastManager.getInstance(this).registerReceiver(
//				mRegistrationBroadcastReceiver,
//				new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
		super.onResume();
	}

	@Override
	protected void onPause() {
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(
//				mRegistrationBroadcastReceiver);
		super.onPause();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
//	private boolean checkPlayServices() {
//		GoogleApiAvailability apiAvailability = GoogleApiAvailability
//				.getInstance();
//		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//		if (resultCode != ConnectionResult.SUCCESS) {
//			if (apiAvailability.isUserResolvableError(resultCode)) {
//				apiAvailability.getErrorDialog(this, resultCode,
//						PLAY_SERVICES_RESOLUTION_REQUEST).show();
//			} else {
//				Log.i(TAG, "This device is not supported.");
//				finish();
//			}
//			return false;
//		}
//		return true;
//	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Log.e("Activity", "ON ACTIVITY CALLED");
		super.onActivityResult(arg0, arg1, arg2);
	}

}
