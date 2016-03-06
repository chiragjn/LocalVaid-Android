package hack.modprobe.localvaid;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class AddFamilyFragment extends Fragment {

	View view;

	EditText nameEditText;
	EditText ageEditText;
	EditText weightEditText;
	EditText heightftEditText;
	EditText heightinEditText;

	RadioGroup genderRadioGroup;

	Button submitButton;

	String name;
	String age;
	String weight;
	String heightft;
	String heightin;
	String gender;

	public AddFamilyFragment() {
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_add_family, container, false);
		nameEditText = (EditText) view
				.findViewById(R.id.add_family_name_edittext);
		ageEditText = (EditText) view
				.findViewById(R.id.add_family_age_edittext);
		weightEditText = (EditText) view
				.findViewById(R.id.add_family_weight_edittext);
		heightftEditText = (EditText) view
				.findViewById(R.id.add_family_heightft_edittext);
		heightinEditText = (EditText) view
				.findViewById(R.id.add_family_heightin_edittext);
		submitButton = (Button) view.findViewById(R.id.add_family_button);
		genderRadioGroup = (RadioGroup) view
				.findViewById(R.id.add_family_gender_radio_group);
		submitButton.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				name = nameEditText.getText().toString();
				age = ageEditText.getText().toString();
				weight = weightEditText.getText().toString();
				heightft = heightftEditText.getText().toString();
				heightin = heightinEditText.getText().toString();
				int selectedId = genderRadioGroup.getCheckedRadioButtonId();
				gender = selectedId == R.id.add_family_gender_male_radio ? "1"
						: "2";
				if (!name.isEmpty() && !age.isEmpty() && !weight.isEmpty()
						&& !heightft.isEmpty() && !heightin.isEmpty()) {
					StringRequest post = new StringRequest(Request.Method.POST,
							Constants.BAE_URL + "addSub/",
							new Response.Listener<String>() {

								@Override
								public void onResponse(String resp) {
									int id = Integer.parseInt(resp);
									// Toast.makeText(getActivity(), resp,
									// Toast.LENGTH_LONG).show();
									SubuserDataSource sds = new SubuserDataSource(
											getActivity());
									sds.open();
									// Subuser tmp =
									// sds.createSubuser(name,Integer.parseInt(gender),Integer.parseInt(age),Integer.parseInt(weight),Integer.parseInt(heightft),Integer.parseInt(heightin),Integer.parseInt(resp));
									Subuser tmp = sds.createSubuser(name,
											Integer.parseInt(gender),
											Integer.parseInt(age),
											Integer.parseInt(weight),
											Integer.parseInt(heightft),
											Integer.parseInt(heightin), id);
									Log.e("Size ", sds.getAllSubusers().size()
											+ "");
									sds.close();
									updateVaccinationCalendar(name, age);
									getActivity().getFragmentManager()
											.popBackStack();
									getActivity().getFragmentManager()
											.popBackStack();
									getActivity()
											.getSupportFragmentManager()
											.beginTransaction()
											.replace(R.id.container,
													new FamilyFragment())
											.addToBackStack("FamilyFragment")
											.commit();
								}

								private void updateVaccinationCalendar(
										String name, String ag) {


//									String name = "Roshan";
									int age = Integer.parseInt(ag);
									
									String reminderTitle, reminderDesc; int reminderDays;

									if(age >= 1 && age <= 5){
										int noOfYears = 5 - age;
									    reminderDays = noOfYears*365;
									    reminderTitle = "Vaccination: POLIO";
									    reminderDesc = "LocalVaid Vaccination reminder for "+name;
										Calendar c = Calendar.getInstance();
									    int day = c.get(Calendar.DAY_OF_MONTH);
									    int month = c.get(Calendar.MONTH);
									    int year = c.get(Calendar.YEAR);
										
									    GregorianCalendar calDate = new GregorianCalendar(year, month, day);
									    GregorianCalendar endDate = new GregorianCalendar(year, month, day);
										for(int i = 0; i < noOfYears; i++){
										  
									    
									    final ContentValues event = new ContentValues();
										event.put(Events.CALENDAR_ID, 1);
									    event.put(Events.TITLE, reminderTitle);
									    event.put(Events.DESCRIPTION, reminderDesc);

									    event.put(Events.DTSTART, calDate.getTimeInMillis());
									    event.put(Events.DTEND, calDate.getTimeInMillis());
									    event.put(Events.ALL_DAY, 1);   // 0 for false, 1 for true
									    event.put(Events.HAS_ALARM, 1); // 0 for false, 1 for true

									    String timeZone = TimeZone.getDefault().getID();
									    event.put(Events.EVENT_TIMEZONE, timeZone);
					
									    Uri baseUri;
									    if (Build.VERSION.SDK_INT >= 8) {
									        baseUri = Uri.parse("content://com.android.calendar/events");
									    } else {
									        baseUri = Uri.parse("content://calendar/events");
									    }
					
									    Uri uri = getContext().getContentResolver().insert(baseUri, event);
									 // get the event ID that is the last element in the Uri
									    long eventID = Long.parseLong(uri.getLastPathSegment());

									    // add 10 minute reminder for the event
									    ContentValues reminders = new ContentValues();
									    reminders.put(Reminders.EVENT_ID, eventID);
									    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
									    reminders.put(Reminders.MINUTES, 5);

									    Uri uriReminder = getContext().getContentResolver().insert(Reminders.CONTENT_URI, reminders);

									    calDate.add(Calendar.DAY_OF_MONTH, 365);
										}
										
									}
								    reminderTitle = "Vaccination: INFLUENZA";
								    reminderDesc = "LocalVaid annual Vaccination reminder for "+name;
									Calendar c = Calendar.getInstance();
								    int day = c.get(Calendar.DAY_OF_MONTH);
								    int month = c.get(Calendar.MONTH);
								    int year = c.get(Calendar.YEAR);
									
								    GregorianCalendar calDate = new GregorianCalendar(year, month, day);
								    GregorianCalendar endDate = new GregorianCalendar(year, month, day);
									for(int i = 0; i < 10 - age; i++){
									  
								    
								    final ContentValues event = new ContentValues();
									event.put(Events.CALENDAR_ID, 1);
								    event.put(Events.TITLE, reminderTitle);
								    event.put(Events.DESCRIPTION, reminderDesc);

								    event.put(Events.DTSTART, calDate.getTimeInMillis());
								    event.put(Events.DTEND, calDate.getTimeInMillis());
								    event.put(Events.ALL_DAY, 1);   // 0 for false, 1 for true
								    event.put(Events.HAS_ALARM, 1); // 0 for false, 1 for true

								    String timeZone = TimeZone.getDefault().getID();
								    event.put(Events.EVENT_TIMEZONE, timeZone);

								    Uri baseUri;
								    if (Build.VERSION.SDK_INT >= 8) {
								        baseUri = Uri.parse("content://com.android.calendar/events");
								    } else {
								        baseUri = Uri.parse("content://calendar/events");
								    }

								    Uri uri = getContext().getContentResolver().insert(baseUri, event);
								 // get the event ID that is the last element in the Uri
								    long eventID = Long.parseLong(uri.getLastPathSegment());

								    // add 10 minute reminder for the event
								    ContentValues reminders = new ContentValues();
								    reminders.put(Reminders.EVENT_ID, eventID);
								    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
								    reminders.put(Reminders.MINUTES, 5);

								    Uri uriReminder = getContext().getContentResolver().insert(Reminders.CONTENT_URI, reminders);
								    calDate.add(Calendar.DAY_OF_MONTH, 365);

								}
								}
							}, new ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError arg0) {

								}
							}) {
						@Override
						protected Map<String, String> getParams()
								throws AuthFailureError {
							Map<String, String> map = new HashMap<String, String>();
							// Log.e("Mobile",
							// AppController.getInstance().prefs.getString("mobile",
							// "none"));
							map.put("mobile", AppController.getInstance().prefs
									.getString("mobile", "9930113199"));
							map.put("gender", gender);
							map.put("age", age);
							map.put("heigntFt", heightft);
							map.put("heigntIn", heightin);
							map.put("weight", weight);
							map.put("name", name);
							return map;
						}
					};
					AppController.getInstance().getRequestQueue().add(post);
				} else {

				}
			}
		});

		return view;
	}

}
