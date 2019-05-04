package com.example.martincostasravnapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
{
	private RecyclerView               recyclerView;
	private RecyclerView.Adapter       adapter;
	private RecyclerView.LayoutManager layoutManager;

	private Opera[] placeholder;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// Placeholder data before server connection
		Opera placeholderItem = new Opera( "Lorem Ipsum", "Dolor sit amet", "consectetur " );
		placeholder = new Opera[] { placeholderItem, placeholderItem, placeholderItem, placeholderItem };

		recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);




		// Improves performance; size of this view will never change
		recyclerView.setHasFixedSize(true);

		layoutManager = new LinearLayoutManager( this);
		recyclerView.setLayoutManager(layoutManager);
		adapter = new DataAdapter(placeholder);
		recyclerView.setAdapter(adapter);
	}
}
