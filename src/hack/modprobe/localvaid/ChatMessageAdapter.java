package hack.modprobe.localvaid;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMessageAdapter extends ArrayAdapter<QueryMessage> {
	private final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2,
			OTHER_IMAGE = 3, MY_AUDIO = 4, OTHER_AUDIO = 5;
	private Context mContext;
	private File MAIN_DIRECTORY;
	private File IMAGES_DIRECTORY;
	private File AUDIO_DIRECTORY;
	ArrayList<QueryMessage> data;
	public ChatMessageAdapter(Context context, ArrayList<QueryMessage> data) {
		super(context, R.layout.item_mine_message, data);
		mContext = context;
		MAIN_DIRECTORY = Environment.getExternalStorageDirectory();
		MAIN_DIRECTORY = new File(MAIN_DIRECTORY, Constants.CHAT_DATA_DIRECTORY);
		IMAGES_DIRECTORY = new File(MAIN_DIRECTORY,Constants.IMAGES_DIRECTORY);
		AUDIO_DIRECTORY = new File(MAIN_DIRECTORY,Constants.AUDIO_DIRECTORY);
		this.data =  data;
	}

	@Override
	public int getViewTypeCount() {
		return 6;
	}

	@Override
	public int getItemViewType(int position) {
		QueryMessage item = getItem(position);

		if (item.isMine() && item.type() == Constants.TEXT)
			return MY_MESSAGE;
		else if (!item.isMine() && item.type() == Constants.TEXT)
			return OTHER_MESSAGE;
		else if (item.isMine() && item.type() == Constants.IMAGE)
			return MY_IMAGE;
		else if (!item.isMine() && item.type() == Constants.IMAGE)
			return OTHER_IMAGE;
		else if (item.isMine() && item.type() == Constants.AUDIO)
			return MY_AUDIO;
		else
			return OTHER_AUDIO;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int viewType = getItemViewType(position);
		if (viewType == MY_MESSAGE) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_mine_message, parent, false);
			TextView textView = (TextView) convertView.findViewById(R.id.text);
			textView.setText(getItem(position).getContent());

		} else if (viewType == OTHER_MESSAGE) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_other_message, parent, false);
			TextView textView = (TextView) convertView.findViewById(R.id.text);
			textView.setText(getItem(position).getContent());
		} else if (viewType == MY_IMAGE) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_mine_image, parent, false);
			ImageView img = (ImageView) convertView
					.findViewById(R.id.chatimage);
			
			String filePath = (new File(IMAGES_DIRECTORY,getItem(position).getContent())).getAbsolutePath();
			img.setImageBitmap(BitmapFactory.decodeFile(filePath));

		} else if (viewType == OTHER_IMAGE) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_other_image, parent, false);
			ImageView img = (ImageView) convertView
					.findViewById(R.id.chatimage);
			String filePath = (new File(IMAGES_DIRECTORY,getItem(position).getContent())).getAbsolutePath();
			img.setImageBitmap(BitmapFactory.decodeFile(filePath));

		} else if (viewType == MY_AUDIO) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_mine_audio, parent, false);
			String filePath = (new File(AUDIO_DIRECTORY,getItem(position).getContent())).getAbsolutePath();
			new ChatPlayer().setupPlayer(mContext, filePath, convertView);

		} else {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_other_audio, parent, false);
			String filePath = (new File(AUDIO_DIRECTORY,getItem(position).getContent())).getAbsolutePath();
			new ChatPlayer().setupPlayer(mContext, filePath, convertView);
		}

		return convertView;
	}
}
