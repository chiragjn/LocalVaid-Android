package hack.modprobe.localvaid;

import java.util.Locale;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class SelectLanguageFragment extends Fragment {
	
	public String[] languages = {"English","हिंदी","मराठी","ગુજરાતી","தமிழ்"};
	public String[] locale_strings = {"en","hi","mr","gu","ta"};
	SharedPreferences sp;
	Editor editor = AppController.getInstance().prefs.edit();
	public SelectLanguageFragment(){
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_select_language, container,
				false);
		ListView select_language_listview =  (ListView)rootView.findViewById(R.id.select_language_listview);
		final TextView select_language_textview = (TextView)rootView.findViewById(R.id.select_language_textview);
		final Button select_language_button = (Button)rootView.findViewById(R.id.select_language_button);
		
		select_language_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//commit shared preferences
				//start tabbed fragment
				//remove current fragment4
				editor.commit();
				if(AppController.getInstance().prefs.contains(Constants.LOGIN_KEY)) {
					getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new MainMenuFragment()).commit();
				}
				else {
					getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, new RegisterFragment()).commit();
				}
			}
		});
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_multiple_choice,languages);
		select_language_listview.setItemsCanFocus(true);
		select_language_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		select_language_listview.setAdapter(adapter);
		select_language_listview.setClickable(true);
		select_language_listview.setSelection(0);
		select_language_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//Toast.makeText(getActivity(), "Select Locale is" + locale_strings[position], Toast.LENGTH_SHORT).show();
				Locale locale = new Locale(locale_strings[position]);
				Locale.setDefault(locale);
				Configuration config = new Configuration();
				config.locale = locale;
				getContext().getResources().updateConfiguration(config,getContext().getResources().getDisplayMetrics());
				((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
				select_language_textview.setText(R.string.select_language);
				select_language_button.setText(R.string.okay);
				getActivity().onConfigurationChanged(config);
			    //set into shared preferences
				editor.putString(Constants.LANGUAGE_KEY, locale_strings[position]);
				//Intent refresh = new Intent(getContext(), MainActivity.class);
			    //refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    //refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    //getContext().startActivity(refresh);	
			}
			
			
		});
		return rootView;
	}
}
