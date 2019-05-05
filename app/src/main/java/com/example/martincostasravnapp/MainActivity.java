package com.example.martincostasravnapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements DownloadCallback
{
	public static final  String host = "192.168.7.106";
	public static final  int    port = 8381;
	private static final String TAG  = "MainActivity";

	private RecyclerView               recyclerView;
	private RecyclerView.Adapter       adapter;
	private RecyclerView.LayoutManager layoutManager;

	private Opera[] placeholder;

	private boolean downloading;

	private NetworkFragment networkFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// Placeholder data before server connection
		Opera placeholderItem = new Opera( "Lorem Ipsum", "Dolor sit amet", "consectetur " );
		placeholder = new Opera[]{placeholderItem, placeholderItem, placeholderItem, placeholderItem};

		recyclerView = (RecyclerView) findViewById( R.id.mainRecyclerView );

		// Improves performance; size of this view will never change
		recyclerView.setHasFixedSize( true );

		layoutManager = new LinearLayoutManager( this );
		recyclerView.setLayoutManager( layoutManager );
		adapter = new DataAdapter( placeholder );
		recyclerView.setAdapter( adapter );

		// Connect to the server to retrieve data
		if ( !downloading )
		{
			downloading = true;
			networkFragment = NetworkFragment.getInstance( getSupportFragmentManager() );
			networkFragment.startDownload();
		}
	}


	@Override
	public void updateFromDownload(Object result)
	{

	}


	@Override
	public NetworkInfo getActiveNetworkInfo()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo;
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
		if ( networkFragment != null )
		{
			networkFragment.cancelDownload();
		}
	}
}
