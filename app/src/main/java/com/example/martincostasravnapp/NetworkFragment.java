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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;


/**
 * Implementation of headless Fragment that runs an AsyncTask to fetch data from the network.
 */
public class NetworkFragment extends Fragment
{
	public static final String TAG = "NetworkFragment";

	private DownloadCallback<String> callback;
	private DownloadTask             downloadTask;

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
		cancelDownload();
		super.onDestroy();
	}


	/**
	 * Start non-blocking execution of DownloadTask.
	 */
	public void startDownload()
	{
		cancelDownload();
		downloadTask = new DownloadTask( callback );
		downloadTask.execute();
	}


	/**
	 * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
	 */
	public void cancelDownload()
	{
		if ( downloadTask != null )
		{
			downloadTask.cancel( true );
		}
	}

	/**
	 * Implementation of AsyncTask designed to fetch data from the network.
	 */
	private class DownloadTask extends AsyncTask<Void, Integer, DownloadTask.Result>
	{

		private DownloadCallback<String> callback;

		DownloadTask(DownloadCallback<String> callback) {
			setCallback(callback);
		}

		void setCallback(DownloadCallback<String> callback) {
			this.callback = callback;
		}

		/**
		 * Wrapper class that serves as a union of a result value and an exception. When the download
		 * task has completed, either the result value or exception can be a non-null value.
		 * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
		 */
		 class Result {
			public String resultValue;
			public Exception exception;
			public Result(String resultValue) {
				this.resultValue = resultValue;
			}
			public Result(Exception exception) {
				this.exception = exception;
			}
		}

		/**
		 * Cancel background network operation if we do not have network connectivity.
		 */
		@Override
		protected void onPreExecute() {
			if (callback != null) {
				NetworkInfo networkInfo = callback.getActiveNetworkInfo();
				if (networkInfo == null || !networkInfo.isConnected() ||
							(networkInfo.getType() != ConnectivityManager.TYPE_WIFI
									 && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
					// If no connectivity, cancel task and update Callback with null data.
					callback.updateFromDownload(null);
					cancel(true);
				}
			}
		}

		/**
		 * Defines work to perform on the background thread.
		 */
		@Override
		protected DownloadTask.Result doInBackground(Void... params) {
			Result result = null;
			if (!isCancelled()) {
				try {
					connectToServer();
				} catch(Exception e) {
					result = new Result(e);
				}
			}
			return result;
		}

		private void connectToServer() throws IOException
		{
			String host = MainActivity.host;
			int port = MainActivity.port;

			Log.d( TAG, "Connecting to " + host + " on port " + port );
			Socket client = new Socket( host, port );

			Log.d( TAG, "Just connected to " + client.getRemoteSocketAddress() );
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream( outToServer );

			out.writeUTF( "Hello from " + client.getLocalSocketAddress() );
			InputStream inFromServer = client.getInputStream();
			DataInputStream in = new DataInputStream( inFromServer );

			Log.d( TAG, "Server says " + in.readUTF() );
			client.close();
		}


		/**
		 * Updates the DownloadCallback with the result.
		 */
		@Override
		protected void onPostExecute(Result result) {
			if (result != null && callback != null) {
				if (result.exception != null) {
					callback.updateFromDownload(result.exception.getMessage());
				} else if (result.resultValue != null) {
					callback.updateFromDownload(result.resultValue);
				}
				callback.finishDownloading();
			}
		}

		/**
		 * Override to add special behavior for cancelled AsyncTask.
		 */
		@Override
		protected void onCancelled(Result result) {
		}
	}


}
