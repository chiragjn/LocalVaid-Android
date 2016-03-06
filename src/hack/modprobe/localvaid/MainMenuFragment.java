package hack.modprobe.localvaid;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainMenuFragment extends Fragment {
	Query q;
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MAINMENU";
	public MainMenuFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences.getBoolean(
						QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
				if (sentToken) {
					Log.e("GCM", "Done");
				} else {
					Log.e("GCM", "Not Done");
				}
			}
		};

		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(getActivity(),
					RegistrationIntentService.class);
			getActivity().startService(intent);
		}
		// TODO
		// fakeData();
		// ------------------------------

		// try{
		// SubuserDataSource sds = new SubuserDataSource(getActivity());
		// sds.open();
		// Subuser tmp = sds.createSubuser(random(), 1, 1, 1, 1, 1, 5);
		// Log.e("Size ", sds.getAllSubusers().size() + "");
		// sds.close();
		// QueryDataSource qds = new QueryDataSource(getActivity());
		// qds.open();
		// q = qds.createQuery(tmp, "Body Pain", "Cold and Cough",
		// "methanol-a,ethanol-mn,gay-man", 2);
		// Log.e("Size ", qds.getQueries(tmp).size() + "");
		// qds.close();
		// }catch(Exception ignored){
		// Subuser s = new Subuser(1, random(), 1, 1, 1, 1, 1, 5);
		// q = new Query(1, "K", "K","methanol-a,ethanol-mn,gay-man", s, 2);
		// }
		// -------------------------
		View rootView = inflater.inflate(R.layout.fragment_main_menu,
				container, false);
		ImageButton nearby_hospital_and_stores_button = (ImageButton) rootView
				.findViewById(R.id.nearby_hospital_and_stores_button);
		ImageButton diagnosis_button = (ImageButton) rootView
				.findViewById(R.id.diagnosis_button);
		ImageButton emergency_button = (ImageButton) rootView
				.findViewById(R.id.emergency_button);

		ImageButton settings_button = (ImageButton) rootView
				.findViewById(R.id.profile_button);

		settings_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, new SelectLanguageFragment())
						.addToBackStack("LangaugeFragment").commit();

			}
		});

		diagnosis_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				fm.beginTransaction()
						.replace(R.id.container, new FamilyFragment())
						.addToBackStack("ChatFragment").commit();
			}
		});

		emergency_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAlert();
			}
		});

		nearby_hospital_and_stores_button
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						FragmentManager fm = getActivity()
								.getSupportFragmentManager();
						fm.beginTransaction()
								.replace(
										R.id.container,
										new BingFragment(
												R.string.nearby_hospital_and_medical_stores,
												getContext()))
								.addToBackStack("BingFragment").commit();

					}
				});

		return rootView;
	}

	@Override
	public void onResume() {
		((MainActivity) getActivity()).getSupportActionBar().setTitle(
				R.string.app_name);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mRegistrationBroadcastReceiver,
				new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
		super.onResume();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				mRegistrationBroadcastReceiver);
		super.onPause();
	}

	public void showAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

		// Setting Dialog Title
		alertDialog.setTitle(getResources().getString(R.string.are_you_sure));

		// Setting Dialog Message
		alertDialog.setMessage(getResources().getString(
				R.string.you_will_be_connected_to_an_ngo));

		// On pressing Settings button
		alertDialog.setPositiveButton(
				getResources().getString(R.string.yes_call_now),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_DIAL);
						intent.setData(Uri.parse("tel:"
								+ Constants.EXOTEL_EMERGENCY_NUMBER));
						getContext().startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton(
				getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	// void fakeData()
	// {
	// SubuserDataSource sds = new SubuserDataSource(getActivity());
	// sds.open();
	// Subuser tmp = sds.createSubuser(random(), 1, 1, 1, 1, 1);
	// Log.e("Size ", sds.getAllSubusers().size() + "");
	// sds.close();
	// QueryDataSource qds = new QueryDataSource(getActivity());
	// qds.open();
	// q = qds.createQuery(tmp, "Body Pain", "Cold and Cough",
	// "Kill Yourself");
	// Log.e("Size ", qds.getQueries(tmp).size() + "");
	// qds.close();
	// QueryMessageDataSource qMDS = new QueryMessageDataSource(getActivity());
	// qMDS.createQueryMessage(q, "GAY FAG", Constants.TEXT, 1);
	// qMDS.createQueryMessage(q, "TU GAY FAG", Constants.TEXT, 2);
	// qMDS.createQueryMessage(q, "KILL YO-SELF", Constants.TEXT, 1);
	// qMDS.createQueryMessage(q, "LE YE SUN", Constants.TEXT, 2);
	// qMDS.createQueryMessage(q, "Skin to Bone.mp3", Constants.AUDIO, 2);
	// qMDS.createQueryMessage(q, "Robot Boy.mp3", Constants.AUDIO, 1);
	// qMDS.createQueryMessage(q, "GAY FAG", Constants.TEXT, 1);
	// qMDS.createQueryMessage(q, "TU GAY FAG", Constants.TEXT, 2);
	// qMDS.createQueryMessage(q, "KILL YO-SELF", Constants.TEXT, 1);
	// qMDS.createQueryMessage(q, "GAY FAG", Constants.TEXT, 1);
	// qMDS.createQueryMessage(q, "TU GAY FAG", Constants.TEXT, 2);
	// qMDS.createQueryMessage(q, "KILL YO-SELF", Constants.TEXT, 1);
	// qMDS.createQueryMessage(q, "chirag_jain.jpg", Constants.IMAGE, 1);
	// qMDS.createQueryMessage(q, "images.jpg", Constants.IMAGE, 2);
	// qMDS.createQueryMessage(q, "shyam_mehta.jpg", Constants.IMAGE, 1);
	// qMDS.close();
	// }

	public String random() {
		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		int randomLength = 8;
		char tempChar;
		for (int i = 0; i < randomLength; i++) {
			tempChar = (char) (generator.nextInt(122 - 97) + 97);
			randomStringBuilder.append(tempChar);
		}
		return randomStringBuilder.toString();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("MAIN MENU", "ON ACTIVITY CALLED");
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability
				.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(AppController.getInstance());
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(getActivity(), resultCode,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				getActivity().finish();
			}
			return false;
		}
		return true;
	}
}
