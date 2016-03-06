package hack.modprobe.localvaid;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RegisterFragment extends Fragment {

	Button sendButton;
	Button verifyButton;
	EditText userNumber;
	EditText userOTP;
	EditText userLocation;
	String number;
	String place;
	String correctOTP = "";

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_register, container, false);

		sendButton = (Button) v.findViewById(R.id.send_number_button);
		verifyButton = (Button) v.findViewById(R.id.verify_button);
		userNumber = (EditText) v.findViewById(R.id.number_text);
		userOTP = (EditText) v.findViewById(R.id.otp_text);
		userLocation = (EditText) v.findViewById(R.id.location);

		sendButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				number = userNumber.getText().toString();
				place = userLocation.getText().toString();
				correctOTP = "";
				if (nullTest(number) && nullTest(place)) {
					for (int i = 0; i < number.length(); i++) {
						if (i % 2 == 0) {
							correctOTP += number.charAt(i);
						}
					}
					send(v);
				} else {
					Toast.makeText(getContext(), "Please Enter",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		verifyButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String code = userOTP.getText().toString();
				if (nullTest(code)) {
					if (code.equals(correctOTP)) {

						String url = Constants.BAE_URL + "addSup/";

						StringRequest postRequest = new StringRequest(
								Request.Method.POST, url,
								new Response.Listener<String>() {
									@Override
									public void onResponse(String response) {
										try {
											if (response.equalsIgnoreCase("done")) {
												Editor editor = AppController
														.getInstance().prefs
														.edit();
												editor.putString(
														Constants.LOGIN_KEY,
														"1");
												editor.putString("mobile",
														number);
												editor.putString("location",
														place);
												editor.commit();
												getActivity()
														.getSupportFragmentManager()
														.beginTransaction()
														.replace(
																R.id.container,
																new MainMenuFragment())
														.commit();
											}
										} catch (Exception e) {
											e.printStackTrace();
											// Toast.makeText(getContext(),
											// e.toString(),
											// Toast.LENGTH_LONG).show();
										}
									}
								}, new Response.ErrorListener() {
									@Override
									public void onErrorResponse(
											VolleyError error) {
										error.printStackTrace();
										// Toast.makeText(getContext(),
										// error.toString(),
										// Toast.LENGTH_LONG).show();

									}
								}) {
							@Override
							protected Map<String, String> getParams() {
								Map<String, String> params = new HashMap<String, String>();
								// the POST parameters:
								params.put("mobile", number);
								params.put("location", place);
								Log.e("params", params.toString());
								return params;
							}
						};
						Volley.newRequestQueue(getContext()).add(postRequest);
						
					} else {
						Toast.makeText(getContext(), correctOTP,
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		return v;
	}

	public boolean nullTest(String str) {
		if (str == null || str.equals(""))
			return false;
		return true;
	}

	@SuppressLint("NewApi")
	public void send(View view) {

		String url = Constants.BAE_URL + "bob/";

		StringRequest postRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							if (response.equals("done")) {
								Toast.makeText(getContext(),
										"SMS with otp has been sent",
										Toast.LENGTH_LONG).show();
								sendButton.setVisibility(View.GONE);
								userNumber.setVisibility(View.GONE);
								userLocation.setVisibility(View.GONE);
								userOTP.setVisibility(View.VISIBLE);
								verifyButton.setVisibility(View.VISIBLE);
							}
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(getContext(), e.toString(),
									Toast.LENGTH_LONG).show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						Toast.makeText(getContext(), error.toString(),
								Toast.LENGTH_LONG).show();

					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				// the POST parameters:
				params.put("mobile", number);
				params.put("location", place);
				Log.e("params", params.toString());
				return params;
			}
		};
		Volley.newRequestQueue(getContext()).add(postRequest);
	}

}
