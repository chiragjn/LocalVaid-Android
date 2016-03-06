package hack.modprobe.localvaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BingFragment extends Fragment implements LocationListener {
	int titleString;
	private final Context mContext;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	protected LocationManager locationManager;
	Location location_for_map;
	public BingFragment() {
		this.mContext = getContext();
	}

	BingFragment(int titleString, Context context) {
		this.titleString = titleString;
		this.mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		((MainActivity) getActivity()).getSupportActionBar().setTitle(
				titleString);
		View view = inflater.inflate(R.layout.fragment_bing, container, false);
		final WebView map = (WebView) view.findViewById(R.id.map);
		// LocationManager locationManager = (LocationManager) getActivity()
		// .getSystemService(Context.LOCATION_SERVICE);
		// String provider = locationManager.getBestProvider(new Criteria(),
		// true);
		// final Location location = locationManager
		// .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		// Log.e("Location",location.toString());
		location_for_map = getLocation();
		if(location_for_map == null)
		{
			showSettingsAlert();
		}
		else
		{
			map.setWebViewClient(new WebViewClient() {
				public void onPageFinished(WebView w, String url) {
					map.loadUrl("javascript:setLat('" + location_for_map.getLatitude()
							+ "')");
					map.loadUrl("javascript:setLong('" + location_for_map.getLongitude()
							+ "')");
					//map.loadUrl("javascript:setSearchTerm('" + searchTerm + "')");
					map.loadUrl("javascript:GetMap()");
				}
			});
			map.getSettings().setJavaScriptEnabled(true);
			map.loadUrl("file:///android_asset/bing.html");
			map.addJavascriptInterface(new JavaScriptInterface(getActivity()),
					"Android");
		}
		return view;
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(mContext.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	
	 public void showSettingsAlert(){
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
	      
	        // Setting Dialog Title
	        alertDialog.setTitle(getResources().getString(R.string.unable_to_get_location));
	  
	        // Setting Dialog Message
	        alertDialog.setMessage(getResources().getString(R.string.ask_for_settings));
	  
	        // On pressing Settings button
	        alertDialog.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                mContext.startActivity(intent);
	            }
	        });
	  
	        // on pressing cancel button
	        alertDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            getFragmentManager().popBackStack();
	            }
	        });
	  
	        // Showing Alert Message
	        alertDialog.show();
	    }
	
}