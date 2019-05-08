package com.example.martincostasravnapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static com.example.martincostasravnapp.RavnApplication.KEY_HOST;
import static com.example.martincostasravnapp.RavnApplication.KEY_PORT;


public class NetworkManager
{
	public static final String TAG = "NetworkManager";

	public static final int RESPONSE_OK    = 0;
	public static final int RESPONSE_ERROR = 1;
	public static final int RESPONSE_PUSH  = 2;
	public static final int RESPONSE_LIST  = 3;

	private static RavnApplication ravnApplication;

	private Activity                 activity;
	private DownloadCallback<String> callback;
	private NetworkTask              networkTask;

	public ArrayList<Media> loadedMedia;

	public boolean initialListCompleted;	// When the app starts, we request LIST, so it is important not to read from in until this is finished

	private Socket client;

	DataInputStream  in;
	DataOutputStream out;


	public NetworkManager(RavnApplication ravnApplication)
	{
		callback = (DownloadCallback<String>) ravnApplication;
		NetworkManager.ravnApplication = ravnApplication;
	}


	public static NetworkManager getSingleton()
	{
		return ravnApplication.getNetworkManager();
	}


	public int connect(Activity activity)
	{
		this.activity = activity;

		cancelNetworkActivity();
		networkTask = new NetworkTask( callback );
		networkTask.execute( Request.Empty() );

		return 0;
	}

	public void disconnect()
	{
		callback = null;
		cancelNetworkActivity();
	}

	public int sendRequest(Activity activity, Request request)
	{
		this.activity = activity;

		cancelNetworkActivity();
		networkTask = new NetworkTask( callback );
		networkTask.execute( request );

		return 0;
	}



	/**
	 * Cancel (and interrupt if necessary) any ongoing NetworkTask execution.
	 */
	public void cancelNetworkActivity()
	{
		if ( networkTask != null )
		{
			networkTask.cancel( true );
		}
	}


	/**
	 * Communicates with the server in background
	 */
	private class NetworkTask extends AsyncTask<Request, Integer, NetworkTask.Result>
	{

		private DownloadCallback<String> callback;

		NetworkTask(DownloadCallback<String> callback)
		{
			setCallback( callback );
		}

		void setCallback(DownloadCallback<String> callback)
		{
			this.callback = callback;
		}

		/**
		 * Wrapper class that serves as a union of a result value and an exception. When the download
		 * task has completed, either the result value or exception can be a non-null value.
		 * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
		 */
		class Result
		{
			public String    resultValue;
			public Exception exception;


			public Result(String resultValue)
			{
				this.resultValue = resultValue;
			}


			public Result(Exception exception)
			{
				this.exception = exception;
			}
		}


		/**
		 * Cancel background network operation if we do not have network connectivity.
		 */
		@Override
		protected void onPreExecute()
		{
			if ( callback != null )
			{
				NetworkInfo networkInfo = callback.getActiveNetworkInfo();
				if ( networkInfo == null || !networkInfo.isConnected() || ( networkInfo.getType() != ConnectivityManager.TYPE_WIFI && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE ) )
				{
					// If no connectivity, cancel task and update Callback with null data.
					callback.updateFromDownload( null );
					cancel( true );
				}
			}
		}


		/**
		 * Defines work to perform on the background thread.
		 */
		@Override
		protected NetworkTask.Result doInBackground(Request... params)
		{
			Result result = null;

			try
			{
				if ( client == null )
				{
					connectToServer();
				}

				else if ( !client.isConnected() )
				{
					connectToServer();
				}

				if ( !isCancelled() && params != null && params.length > 0 )
				{
					for ( Request r : params )
					{
						if ( r.getRequestCode() != Request.REQUEST_CODE_EMPTY )
						{
							sendRequestInBackground( r );
						}
					}
				}

			}
			catch ( Exception e )
			{
				result = new Result( e );
			}


			return result;
		}


		private void connectToServer() throws IOException
		{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( activity );
			String host = preferences.getString( KEY_HOST, "" );
			int port = preferences.getInt( KEY_PORT, -1 );

			assert host != null;
			if ( host.equals( "" ) || port == -1 )
			{
				// Error has occurred, host and port not set yet
				return;
			}

			Log.d( TAG, "Connecting to " + host + " on port " + port );
			client = new Socket( host, port );

			Log.d( TAG, "Just connected to " + client.getRemoteSocketAddress() );

			OutputStream outToServer = client.getOutputStream();
			out = new DataOutputStream( outToServer );

			InputStream inFromServer = client.getInputStream();
			in = new DataInputStream( inFromServer );
		}


		/**
		 * Sends request and reads the response
		 *
		 * @param request
		 * @throws IOException
		 */
		private void sendRequestInBackground(Request request) throws IOException
		{
			try
			{
				JSONObject jsonRequest = request.toJSONObject();
				out.writeUTF( jsonRequest.toString() );
			}
			catch ( JSONException e )
			{
				e.printStackTrace();
			}
		}


		/**
		 * Updates the DownloadCallback with the result.
		 */
		@Override
		protected void onPostExecute(Result result)
		{
			if ( result != null && callback != null )
			{
				if ( result.exception != null )
				{
					callback.updateFromDownload( result.exception.getMessage() );
				}
				else if ( result.resultValue != null )
				{
					callback.updateFromDownload( result.resultValue );
				}

				callback.finishDownloading();
			}

			//TODO check result

			ravnApplication.setOperas( loadedMedia, activity );
		}


		/**
		 * Override to add special behavior for cancelled AsyncTask.
		 */
		@Override
		protected void onCancelled(Result result)
		{
		}
	}


}
