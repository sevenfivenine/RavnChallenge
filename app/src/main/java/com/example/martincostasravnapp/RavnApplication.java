package com.example.martincostasravnapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class RavnApplication extends Application implements DownloadCallback
{
	public static final String host = "192.168.7.106";
	public static final int    port = 8381;

	public static final String KEY_HOST = "host";
	public static final String KEY_PORT = "port";

	public static final String           TAG    = "RavnApplication";
	public              MenuItem         connectionStatusMenuItem;
	private             NetworkManager   networkManager;
	private             boolean          connected;
	private             ArrayList<Media> operas = new ArrayList<>();
	public              Thread           pushThread;
	public              boolean          pushThreadInterrupted;
	private             Activity         activity;


	@Override
	public void onCreate()
	{
		super.onCreate();

		// For this application, the port will always be the same
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt( KEY_PORT, port );
		editor.apply();
	}


	/**
	 * Attempt to connect to the given IP address
	 */
	public void connectToHost(Activity activity)
	{
		// Connect to the server to prepare to exchange data
		networkManager = new NetworkManager( this );
		networkManager.connect( activity );

		startListeningForPush();
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


	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}


	@Override
	public NetworkInfo getActiveNetworkInfo()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo;
	}


	public void startListeningForPush()
	{
		pushThread = new Thread( new Runnable()
		{
			public void run()
			{

				while ( !pushThreadInterrupted )
				{
					if ( networkManager == null || networkManager.in == null )
					{
						continue;
					}

					try
					{
						if ( networkManager != null && networkManager.in != null && networkManager.in.available() > 0 )
						{
							String responseString = networkManager.in.readUTF();

							try
							{
								int responseCode = Integer.parseInt( responseString );

								if ( responseCode == NetworkManager.RESPONSE_PUSH )
								{
									updateFromPush();

									// On the first push, we are officially connected
									// But also wait for UI to catch up to change the icon
									if ( !connected )
									{
										boolean waitForUI = true;

										while ( waitForUI )
										{
											if ( connectionStatusMenuItem != null )
											{
												setConnected( true );
												waitForUI = false;
											}
										}
									}
								}
							}

							catch ( NumberFormatException e )
							{
								e.printStackTrace();
							}
						}
					}
					catch ( IOException e )
					{
						e.printStackTrace();
					}
				}
			}
		} );

		pushThread.start();
	}


	public void updateFromPush() throws IOException
	{
		String responseData = networkManager.in.readUTF();

		try
		{
			JSONArray listJSONarray = new JSONArray( responseData );

			networkManager.loadedMedia = new ArrayList<>();

			for ( int i = 0; i < listJSONarray.length(); i++ )
			{
				JSONObject jsonObject = (JSONObject) listJSONarray.get( i );
				Media media = Media.JSONtoMedia( jsonObject );

				networkManager.loadedMedia.add( media );
			}

			setOperas( networkManager.loadedMedia, activity );
		}
		catch ( JSONException e )
		{
			e.printStackTrace();
		}
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
	 */
	public void setOperas(ArrayList<Media> operas, Activity activity)
	{
		this.operas = operas;

		if ( activity instanceof MainActivity )
		{
			final MainActivity mainActivity = (MainActivity) activity;

			// If we are not on the UI thread, send task to UI thread
			if ( Looper.myLooper() != Looper.getMainLooper() )
			{
				mainActivity.runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						mainActivity.refreshUI();
					}
				} );
			}

			else
			{
				mainActivity.refreshUI();
			}
		}
	}


	public boolean isConnected()
	{
		return connected;
	}


	public void setConnected(boolean connected)
	{
		this.connected = connected;

		final boolean uiConnected = connected;

		if ( connectionStatusMenuItem != null )
		{
			Log.d( TAG, "Trying to run!!!!!" );

			// If we are not on the UI thread, send task to UI thread
			if ( Looper.myLooper() != Looper.getMainLooper() )
			{
				activity.runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						Log.d( TAG, "RUNNING!!!!!" + uiConnected + activity.getLocalClassName() );

						if ( uiConnected )
						{
							connectionStatusMenuItem.setIcon( R.drawable.ic_connection_status_connected );
						}

						else
						{
							connectionStatusMenuItem.setIcon( R.drawable.ic_connection_status_disconnected );
						}
					}
				} );
			}

			else
			{
				Log.d( TAG, "RUNNING!!!!!" + uiConnected + activity.getLocalClassName() );

				if ( uiConnected )
				{
					connectionStatusMenuItem.setIcon( R.drawable.ic_connection_status_connected );
				}

				else
				{
					connectionStatusMenuItem.setIcon( R.drawable.ic_connection_status_disconnected );
				}
			}
		}
	}


}
