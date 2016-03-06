package hack.modprobe.localvaid;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class FamilyFragment extends Fragment {

	View view;
	ListView familyListView;
	List<Subuser> family;
	List<String> strings;
	ArrayAdapter<String> adapter;
	public FamilyFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		((MainActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.family));
		view = inflater.inflate(R.layout.fragment_family, container, false);
		familyListView = (ListView) view.findViewById(R.id.familyListView);
		SubuserDataSource sds = new SubuserDataSource(getActivity());
        sds.open();
		family = sds.getAllSubusers();
		sds.close();
		strings = new ArrayList<String>();
		for (Subuser object : family) {
		    strings.add(object != null ? object.toString() : null);
		}
		adapter = new ArrayAdapter<String>(getActivity(),
	              android.R.layout.simple_list_item_1, android.R.id.text1, strings);
		familyListView.setAdapter(adapter);
		
		familyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.e("pressed", String.valueOf(arg2));
				Subuser fam = family.get(arg2);
				getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new QueryFragment(fam)).addToBackStack("query").commit();
				
			}
		});
		
		Button but = (Button) view.findViewById(R.id.add_family);
		but.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, new AddFamilyFragment()).addToBackStack("add").commit();
				
			}
		});
		return view;
	}
	
	

}
