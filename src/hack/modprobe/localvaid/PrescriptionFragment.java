package hack.modprobe.localvaid;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PrescriptionFragment extends Fragment {
	String presc;

	public PrescriptionFragment(String prescription) {
		presc = prescription;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_prescription,
				container, false);
		ListView meds = (ListView) rootView.findViewById(R.id.listView);
		ArrayList<Prescription> data = parsePres(presc);
		meds.setAdapter(new PrescriptionAdapter(getActivity(), data));
		return rootView;
	}

	private ArrayList<Prescription> parsePres(String data) {
		String temp[] = data.split("\\,");
		ArrayList<Prescription> arr= new ArrayList<Prescription>();
		for (int i = 0; i < temp.length; i++) {
			String t[] = temp[i].split("\\-");
			arr.add(new Prescription(t[0], t[1].indexOf('m') >= 0, t[1].indexOf('a') >= 0, t[1].indexOf('n') >= 0));
		}
		return arr;
	}
}
