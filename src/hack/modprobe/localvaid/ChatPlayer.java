package hack.modprobe.localvaid;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ChatPlayer implements OnPreparedListener {
	private ImageButton btnPlay;
	private int current = 0;
	private boolean running = true;
	private int duration = 0;
	private MediaPlayer mPlayer;
	private SeekBar mSeekBarPlayer;
	private Context context;
	public void setupPlayer(final Context mContext, String filePath, View rootView) {
		context = mContext;
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(filePath);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		btnPlay = (ImageButton) rootView.findViewById(R.id.playpause);
		mSeekBarPlayer = (SeekBar) rootView.findViewById(R.id.progress_bar);
		mPlayer.setOnPreparedListener(this);

		mSeekBarPlayer
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser) {
							mPlayer.seekTo(progress);
						}
					}
				});

		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mPlayer.isPlaying()) {
					try {
						mPlayer.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mPlayer.start();
					btnPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause));;
					mSeekBarPlayer.postDelayed(onEverySecond, 1000);
				} else {
					mPlayer.pause();
					btnPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play));
				}

			}
		});

		
	}

	private Runnable onEverySecond = new Runnable() {
		@Override
		public void run() {
			if (true == running) {
				if (mSeekBarPlayer != null) {
					mSeekBarPlayer.setProgress(mPlayer.getCurrentPosition());
				}

				if (mPlayer.isPlaying()) {
					mSeekBarPlayer.postDelayed(onEverySecond, 1000);
				}
				else{
					btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
					mSeekBarPlayer.setProgress(0);
				}
			}
		}
	};

	

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		duration = mPlayer.getDuration();
		mSeekBarPlayer.setMax(duration);
		mSeekBarPlayer.postDelayed(onEverySecond, 1000);
	}
}
