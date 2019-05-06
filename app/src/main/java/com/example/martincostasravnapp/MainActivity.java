package com.example.martincostasravnapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
	public static final  String host = "192.168.7.106";
	public static final  int    port = 8381;
	private static final String TAG  = "MainActivity";

	private RecyclerView               recyclerView;
	private RecyclerView.Adapter       adapter;
	private RecyclerView.LayoutManager layoutManager;

	private FloatingActionButton addMediaFab;

	private RavnApplication application;

	private Opera[] placeholder;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		application = (RavnApplication) getApplication();
		application.setMainActivity( this );

		// Placeholder data before server connection
		Opera placeholderItem = new Opera( "Lorem Ipsum", "Dolor sit amet", "consectetur " );
		placeholder = new Opera[]{placeholderItem, placeholderItem, placeholderItem, placeholderItem};

		addMediaFab = findViewById( R.id.addMediaFab );
		addMediaFab.setOnClickListener( new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				// Open EditActivity to add a new media record
				Intent intent = new Intent( MainActivity.this, EditActivity.class );
				startActivity( intent );
			}
		} );

		recyclerView = (RecyclerView) findViewById( R.id.mainRecyclerView );

		// Improves performance; size of this view will never change
		recyclerView.setHasFixedSize( true );

		layoutManager = new LinearLayoutManager( this );
		recyclerView.setLayoutManager( layoutManager );
		//adapter = new DataAdapter( placeholder );
		//recyclerView.setAdapter( adapter );


	}


	@Override
	protected void onResume()
	{
		super.onResume();

		// First, request the most current list of data
		NetworkManager networkManager = NetworkManager.getSingleton();
		networkManager.sendRequest( this, Request.generateListRequest() );

		refreshUI();
	}


	public void refreshUI()
	{
		adapter = new DataAdapter( application.getOperas() );
		recyclerView.setAdapter( adapter );

		NetworkManager networkManager = NetworkManager.getSingleton();
		//networkManager.initialListCompleted = true;
	}


}
