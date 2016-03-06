package hack.modprobe.localvaid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class PrescriptionAdapter extends ArrayAdapter<Prescription> {

	private Context mContext;

	public PrescriptionAdapter(Context context, ArrayList<Prescription> data) {
		super(context, R.layout.prescription_row, data);
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
            convertView = ((LayoutInflater)mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prescription_row, null);
        }
		TextView medicine_name = (TextView)convertView.findViewById(R.id.medicine_name);
		CheckBox morning = (CheckBox)convertView.findViewById(R.id.morning);
		CheckBox afternoon = (CheckBox)convertView.findViewById(R.id.afternoon);
		CheckBox night = (CheckBox)convertView.findViewById(R.id.night);
		medicine_name.setText(getItem(position).name);
		morning.setChecked(getItem(position).m);
		afternoon.setChecked(getItem(position).a);
		night.setChecked(getItem(position).n);
		return convertView;
	}
}
