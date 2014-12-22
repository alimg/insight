package com.fmakdemir.insight.utils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * @author fma
 * @date 21.12.2014.
 */
public class AudioAsynctask {

	public void play(AssetFileDescriptor afd) {
//		String url = "http://........"; // your URL here
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mediaPlayer.prepare();
			mediaPlayer.start();

//			mediaPlayer.setDataSource(url);
//			mediaPlayer.prepare(); // might take long! (for buffering, etc)
		} catch (Exception e) {
			e.printStackTrace();
		}
//		mediaPlayer.start();
	}
}
