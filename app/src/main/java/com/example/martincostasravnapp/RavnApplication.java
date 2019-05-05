package com.example.martincostasravnapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

public class RavnApplication extends Application implements DownloadCallback
{
	private NetworkManager networkManager;

	private boolean downloading;

	private ArrayList<Media> operas = new ArrayList<>();

	@Override
	public void onCreate()
	{
		super.onCreate();

		// Connect to the server to prepare to exchange data
		if ( !downloading )
		{
			downloading = true;
			networkManager = new NetworkManager( this );
		}
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged( newConfig );
	}


	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
	}

	@Override
	public NetworkInfo getActiveNetworkInfo()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo;
	}

	@Override
	public void updateFromDownload(Object result)
	{

	}


	@Override
	public void onProgressUpdate(int progressCode, int percentComplete)
	{
		switch ( progressCode )
		{
			// You can add UI behavior for progress updates here.
			case Progress.ERROR:
				break;
			case Progress.CONNECT_SUCCESS:
				break;
			case Progress.GET_INPUT_STREAM_SUCCESS:
				break;
			case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
				break;
			case Progress.PROCESS_INPUT_STREAM_SUCCESS:
				break;
		}
	}


	@Override
	public void finishDownloading()
	{
		downloading = false;
		if ( networkManager != null )
		{
			networkManager.cancelNetworkActivity();
		}
	}


	public NetworkManager getNetworkManager()
	{
		return networkManager;
	}


	public ArrayList<Media> getOperas()
	{
		return operas;
	}


	/**
	 * Set the list of operas and update the UI to reflect changes
	 *
	 * @param operas
	 * @param activity
	 */
	public void setOperas(ArrayList<Media> operas, Activity activity)
	{
		this.operas = operas;

		if ( activity instanceof MainActivity )
		{
			MainActivity mainActivity = (MainActivity) activity;

			mainActivity.refreshUI();
		}
	}
}
