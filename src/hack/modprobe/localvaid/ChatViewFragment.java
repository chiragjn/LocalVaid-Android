package hack.modprobe.localvaid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ChatViewFragment extends Fragment {
	private ListView mListView;
	private Button mButtonSend;
	private EditText mEditTextMessage;
	private ChatMessageAdapter mAdapter;
	private RelativeLayout mProgressBar;
	private Query parentQuery;
	private Button mImageSend;
	private Button mAudioSend;
	private QueryMessage new_message;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MainActivity";
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".acc";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final int CAMERA_REQUEST = 1888;
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	String message;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,
			MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
			AUDIO_RECORDER_FILE_EXT_3GP };
	private String lastsave;

	public ChatViewFragment(Query query) {
		parentQuery = query;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat_view,
				container, false);
		mProgressBar = (RelativeLayout) rootView
				.findViewById(R.id.progressBar1);
		mListView = (ListView) rootView.findViewById(R.id.listView);
		mButtonSend = (Button) rootView.findViewById(R.id.btn_send);
		mImageSend = (Button) rootView.findViewById(R.id.image_message);
		mAudioSend = (Button) rootView.findViewById(R.id.audio_message);
		mEditTextMessage = (EditText) rootView.findViewById(R.id.text_message);
		QueryMessageDataSource qMDS = new QueryMessageDataSource(getActivity());
		ArrayList<QueryMessage> messages = new ArrayList<QueryMessage>(
				qMDS.getQueryMessages(parentQuery));
		qMDS.close();
		mAdapter = new ChatMessageAdapter(getActivity(), messages);
		mListView.setAdapter(mAdapter);
		//polling start
		
		
				RefreshThread refreshThread = new RefreshThread();
			    refreshThread.isStop = false;
			    refreshThread.start();
				
		//polling end
		mButtonSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				message = mEditTextMessage.getText().toString();
				if (TextUtils.isEmpty(message)) {
					return;
				}
				mEditTextMessage.setText("");
				new AsyncTask<Void, Void, Void>() {

					protected Void doInBackground(Void[] arg0) {

						try {
							doFileUpload(null, Constants.TEXT, message);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;

					};

					protected void onPostExecute(Void result) {
						mAdapter.data.add(new_message);
						mAdapter.notifyDataSetChanged();
					};

				}.execute();

				// mProgressBar.setVisibility(View.VISIBLE);
			}
		});

		mImageSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});

		mAudioSend.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mAudioSend.setBackgroundColor(Color.rgb(9, 46, 32));
					// AppLog.logString("Start Recording");
					startRecording();
					break;
				case MotionEvent.ACTION_UP:
					// AppLog.logString("stop Recording");
					mAudioSend.setBackgroundColor(Color.rgb(230, 33, 23));
					stopRecording();
					break;
				}
				return false;
			}
		});
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.chat_view_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.view_presciption) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			fm.beginTransaction()
					.replace(R.id.container,
							new PrescriptionFragment(parentQuery._prescription))
					.addToBackStack("PrescriptionFragment").commit();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("CHAT VIEW", "ON ACTIVITY CALLED");
		if (requestCode == CAMERA_REQUEST
				&& resultCode == getActivity().RESULT_OK) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			File file = Environment.getExternalStorageDirectory();
			file = new File(file, Constants.CHAT_DATA_DIRECTORY);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(file, Constants.IMAGES_DIRECTORY);
			if (!file.exists()) {
				file.mkdirs();
			}
			final File f = new File(file, random() + ".png");
			try {
				f.createNewFile();

				// Convert bitmap to byte array
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				photo.compress(CompressFormat.PNG, 50 /* ignored for PNG */, bos);
				byte[] bitmapdata = bos.toByteArray();

				// write the bytes in file
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(bitmapdata);
				fos.flush();
				fos.close();
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						try {
							doFileUpload(f, Constants.IMAGE, f.getName());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return null;
					}

					protected void onPostExecute(Void result) {
						mAdapter.data.add(new_message);
						mAdapter.notifyDataSetChanged();
					};
				}.execute();

			} catch (Exception e) {
				Log.e("CHAT VIEW", e.getMessage());
			}

		}
		// super.onActivityResult(requestCode, resultCode, data);
	}

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

	public void doFileUpload(File f, int type, String data)
			throws JSONException {
		Log.e("CHAT VIEW", "Started");
		String charset = "UTF-8";
		String requestURL = Constants.BAE_URL + "addQueryMessage/";

		MultipartUtility multipart;
		try {
			multipart = new MultipartUtility(requestURL, charset);
			multipart.addFormField("pk", "" + parentQuery._sid);
			multipart.addFormField("type", "" + type);
			if (type < 2) {
				multipart.addFilePart("audio", f);
			} else {
				multipart.addFormField("data", data);
			}
			List<String> response = multipart.finish(); // response from server.

			int message_id = Integer.parseInt(response.get(0));
			QueryMessageDataSource qMDS = new QueryMessageDataSource(
					getActivity());
			new_message = qMDS.createQueryMessage(parentQuery, data, type, 2,
					message_id);
			qMDS.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, Constants.CHAT_DATA_DIRECTORY);
		file = new File(file, Constants.AUDIO_DIRECTORY);

		if (!file.exists()) {
			file.mkdirs();
		}
		String fileName = "audio_" + random() + file_exts[currentFormat];
		return (file.getAbsolutePath() + "/" + fileName);
	}

	private void startRecording() {
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		// recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
		recorder.setAudioEncodingBitRate(128);
		recorder.setAudioSamplingRate(44100);
		lastsave = getFilename();
		recorder.setOutputFile(lastsave);
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			// AppLog.logString("Error: " + what + ", " + extra);
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			// AppLog.logString("Warning: " + what + ", " + extra);
		}
	};

	private void stopRecording() {
		if (null != recorder) {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						doFileUpload(new File(lastsave), Constants.AUDIO,
								new File(lastsave).getName());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return null;
				}

				protected void onPostExecute(Void result) {
					mAdapter.data.add(new_message);
					mAdapter.notifyDataSetChanged();
				};
			}.execute();
			recorder.stop();
			recorder.reset();
			recorder.release();

			recorder = null;
		}
	}

	
	
	private class RefreshThread extends Thread{
        public boolean isStop = false;

        public void run(){
            try{
                while(!isStop){                                             
                    getActivity().runOnUiThread(new Runnable() {                   
                        @Override
                        public void run() {
                               //List refresh logic goes here   
                                //yourListView.invalidateViews(); 

                    		QueryMessageDataSource qMDS = new QueryMessageDataSource(getActivity());
                        	ArrayList<QueryMessage> messages = new ArrayList<QueryMessage>(
                    				qMDS.getQueryMessages(parentQuery));
                        	qMDS.close();
                        	mAdapter.clear();
                    		mAdapter.addAll(messages);
                            mAdapter.notifyDataSetChanged();
                               Log.e("tag", "msg");
                        }
                    });                         
                    try{ Thread.sleep(1000); } catch(Exception ex){}
                }
        }catch(Exception e){
        }
      }//run
    }//thread
}
