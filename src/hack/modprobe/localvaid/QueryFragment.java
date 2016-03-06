package hack.modprobe.localvaid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class QueryFragment extends Fragment {

	Subuser self;
	View view;
	ListView queryListView;
	List<Query> queries;
	List<String> strings;
	ArrayAdapter<String> adapter;

	public QueryFragment(Subuser fam) {
		self = fam;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		((MainActivity)getActivity()).getSupportActionBar().setTitle(self._name);
		view = inflater.inflate(R.layout.fragment_query, container, false);
		queryListView = (ListView) view.findViewById(R.id.queryListView);

		QueryDataSource sds = new QueryDataSource(getActivity());
		sds.open();
		queries = sds.getQueries(self);
		sds.close();
		strings = new ArrayList<String>();
		for (Query object : queries) {
			strings.add(object != null ? object.toString() : null);
		}
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1,
				strings);

		queryListView.setAdapter(adapter);

		queryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.container,
						new ChatViewFragment(queries.get(arg2)))
				.addToBackStack("CHATFRAGMENT")
				.commit();
	
			}
		});

		Button but = (Button) view.findViewById(R.id.add_query);
		but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				StringRequest post = new StringRequest(Request.Method.POST,
						Constants.BAE_URL + "addQuery/",
						new Response.Listener<String>() {

							@Override
							public void onResponse(String resp) {
								int id = Integer.parseInt(resp);
								QueryDataSource qDS = new QueryDataSource(
										getActivity());
								Query q = qDS.createQuery(self, "None", "None", "Crocin-a",id);
								qDS.close();
								getActivity()
										.getSupportFragmentManager()
										.beginTransaction()
										.replace(R.id.container,
												new ChatViewFragment(q))
										.addToBackStack("CHATFRAGMENT")
										.commit();
								// update Adapter
								// call chat fragment

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
						map.put("pk", String.valueOf(self._sid));
						map.put("symp", "None");
						map.put("diag", "None");
						map.put("presc", "Crocin-a");
						map.put("sms", "0");
						
						return map;
					}
				};
				AppController.getInstance().getRequestQueue().add(post);

				
			}
		});

		return view;
	}

}
