package com.example.martincostasravnapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Implementation of headless Fragment that runs an AsyncTask to fetch data from the network.
 */
public class NetworkFragment extends Fragment
{
	public static final String TAG = "NetworkFragment";

	private DownloadCallback<String> callback;
	private NetworkTask              networkTask;

	private Socket client;


	/**
	 * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
	 * from.
	 */
	public static NetworkFragment getInstance(FragmentManager fragmentManager)
	{
		NetworkFragment networkFragment = new NetworkFragment();
		fragmentManager.beginTransaction().add( networkFragment, TAG ).commit();
		return networkFragment;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
	}


	@Override
	public void onAttach(Context context)
	{
		super.onAttach( context );
		// Host Activity will handle callbacks from task.
		callback = (DownloadCallback<String>) context;
	}


	@Override
	public void onDetach()
	{
		super.onDetach();
		// Clear reference to host Activity to avoid memory leak.
		callback = null;
	}


	@Override
	public void onDestroy()
	{
		// Cancel task when Fragment is destroyed.
		cancelNetworkActivity();
		super.onDestroy();
	}

	public int sendRequest(Request request)
	{
		cancelNetworkActivity();
		networkTask = new NetworkTask( callback );
		networkTask.execute(request);

		return 0;
	}


	/**
	 * Start non-blocking execution of NetworkTask.
	 */
	public void startDownload()
	{
		cancelNetworkActivity();
		networkTask = new NetworkTask( callback );
		networkTask.execute();
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

		private ArrayList<Media> loadedMedia;


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
			if ( !isCancelled() && params != null && params.length > 0)
			{
				try
				{
					connectToServer();

					for ( Request r : params )
					{
						sendRequestInBackground( r );
					}
				}
				catch ( Exception e )
				{
					result = new Result( e );
				}
			}
			return result;
		}


		private void connectToServer() throws IOException
		{
			String host = MainActivity.host;
			int port = MainActivity.port;

			Log.d( TAG, "Connecting to " + host + " on port " + port );
			client = new Socket( host, port );

			/*Log.d( TAG, "Just connected to " + client.getRemoteSocketAddress() );
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream( outToServer );

			out.writeUTF( "Hello from " + client.getLocalSocketAddress() );
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream( inFromServer );

			Log.d( TAG, "Server says " + in.readUTF() );
			client.close();*/
		}


		private void sendRequestInBackground(Request request) throws IOException
		{
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream( outToServer );

			try
			{
				JSONObject jsonRequest = request.toJSONObject();
				out.writeUTF( jsonRequest.toString() );
			}
			catch ( JSONException e )
			{
				e.printStackTrace();
			}

			//out.writeUTF( "" );

			//out.writeUTF( "Hello from " + client.getLocalSocketAddress() );
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream( inFromServer );
			//Log.d( TAG, "Server says " + in.readUTF() );

			try
			{
				JSONArray listJSONarray = new JSONArray( in.readUTF() );

				loadedMedia = new ArrayList<>();

				for ( int i = 0; i < listJSONarray.length(); i++ )
				{
					JSONObject jsonObject = (JSONObject) listJSONarray.get( i );
					Media media = Media.JSONtoMedia( jsonObject );

					loadedMedia.add( media );
				}
			}
			catch ( JSONException e )
			{
				e.printStackTrace();
			}

			client.close();
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

				// For this application, callback should be the main activity. Check here to avoid exceptions

				try
				{
					MainActivity mainActivity = (MainActivity) callback;

					mainActivity.setOperas( loadedMedia );
				}
				catch ( ClassCastException e )
				{
					e.printStackTrace();
				}

				callback.finishDownloading();
			}
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
