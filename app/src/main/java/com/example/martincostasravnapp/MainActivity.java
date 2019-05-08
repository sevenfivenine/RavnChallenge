package com.example.martincostasravnapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import static com.example.martincostasravnapp.Media.KEY_AUTHOR;
import static com.example.martincostasravnapp.Media.KEY_DATE;
import static com.example.martincostasravnapp.Media.KEY_ID;
import static com.example.martincostasravnapp.Media.KEY_TITLE;
import static com.example.martincostasravnapp.Media.KEY_TYPE;
import static com.example.martincostasravnapp.Media.ORDER_ASCENDING;
import static com.example.martincostasravnapp.Media.ORDER_DESCENDING;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private static final String TAG = "MainActivity";

	private RecyclerView               recyclerView;
	private RecyclerView.Adapter       adapter;
	private RecyclerView.LayoutManager layoutManager;

	private FloatingActionButton addMediaFab;

	private View         background;
	private LinearLayout bottomSheet;

	private TextView titleAZtextView, titleZAtextView, authorAZtextView, authorZAtextView, typeAZtextView, typeZAtextView, dateAscendingTextView, dateDescendingTextView;

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

		background = findViewById( R.id.bg );

		bottomSheet = findViewById( R.id.sortBottomSheet );
		BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from( bottomSheet );
		bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );

		bottomSheetBehavior.setBottomSheetCallback( new BottomSheetBehavior.BottomSheetCallback()
		{
			@Override
			public void onStateChanged(@NonNull View view, int i)
			{
				if ( i == BottomSheetBehavior.STATE_COLLAPSED )
				{
					background.setVisibility( View.GONE );
				}
			}


			@Override
			public void onSlide(@NonNull View view, float v)
			{
				background.setVisibility( View.VISIBLE );
				background.setAlpha( v );
			}
		} );

		titleAZtextView = findViewById( R.id.sortTitleAZ );
		titleZAtextView = findViewById( R.id.sortTitleZA );
		authorAZtextView = findViewById( R.id.sortAuthorAZ );
		authorZAtextView = findViewById( R.id.sortAuthorZA );
		typeAZtextView = findViewById( R.id.sortTypeAZ );
		typeZAtextView = findViewById( R.id.sortTypeZA );
		dateAscendingTextView = findViewById( R.id.sortDateAscending );
		dateDescendingTextView = findViewById( R.id.sortDateDescending );

		titleAZtextView.setOnClickListener( this );
		titleZAtextView.setOnClickListener( this );
		authorAZtextView.setOnClickListener( this );
		authorZAtextView.setOnClickListener( this );
		typeAZtextView.setOnClickListener( this );
		typeZAtextView.setOnClickListener( this );
		dateAscendingTextView.setOnClickListener( this );
		dateDescendingTextView.setOnClickListener( this );

		recyclerView = (RecyclerView) findViewById( R.id.mainRecyclerView );

		// Improves performance; size of this view will never change
		recyclerView.setHasFixedSize( true );

		layoutManager = new LinearLayoutManager( this );
		recyclerView.setLayoutManager( layoutManager );
		//adapter = new DataAdapter( placeholder );
		//recyclerView.setAdapter( adapter );

		application.setOperas( application.getOperas(), this );
	}


	@Override
	protected void onResume()
	{
		super.onResume();


		//refreshUI();
	}


	@Override
	public void onClick(View v)
	{
		NetworkManager networkManager = NetworkManager.getSingleton();
		Request request = new Request();
		request.setRequestCode( Request.REQUEST_CODE_SORT );
		boolean send = false;

		switch ( v.getId() )
		{
			case R.id.sortTitleAZ:
				send = true;
				request.setField( KEY_TITLE );
				request.setOrder( ORDER_ASCENDING );
				break;

			case R.id.sortTitleZA:
				send = true;
				request.setField( KEY_TITLE );
				request.setOrder( ORDER_DESCENDING );
				break;


			case R.id.sortAuthorAZ:
				send = true;
				request.setField( KEY_AUTHOR );
				request.setOrder( ORDER_ASCENDING );
				break;

			case R.id.sortAuthorZA:
				send = true;
				request.setField( KEY_AUTHOR );
				request.setOrder( ORDER_DESCENDING );
				break;


			case R.id.sortTypeAZ:
				send = true;
				request.setField( KEY_TYPE );
				request.setOrder( ORDER_ASCENDING );
				break;

			case R.id.sortTypeZA:
				send = true;
				request.setField( KEY_TYPE );
				request.setOrder( ORDER_DESCENDING );
				break;


			case R.id.sortDateAscending:
				send = true;
				request.setField( KEY_DATE );
				request.setOrder( ORDER_ASCENDING );
				break;

			case R.id.sortDateDescending:
				send = true;
				request.setField( KEY_DATE );
				request.setOrder( ORDER_DESCENDING );
				break;


			default:
				break;
		}

		if ( send )
		{
			networkManager.sendRequest( this, request );

			BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from( bottomSheet );
			bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );
		}
	}


	@Override
	public void onBackPressed()
	{
		BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from( bottomSheet );

		if ( bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED )
		{
			bottomSheetBehavior.setState( BottomSheetBehavior.STATE_HIDDEN );
		}

		else
		{
			application.finishDownloading();

			// If we are going back to MenuActivity, disconnect
			NetworkManager networkManager = NetworkManager.getSingleton();
			networkManager.disconnect();

			super.onBackPressed();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.activity_main_menu, menu );
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if ( item.getItemId() == R.id.sortMenuItem )
		{
			BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from( bottomSheet );
			bottomSheetBehavior.setState( BottomSheetBehavior.STATE_EXPANDED );

			return true;
		}

		else
		{
			return super.onOptionsItemSelected( item );
		}
	}


	public void refreshUI()
	{
		DataAdapter.RecyclerViewClickListener listener = new DataAdapter.RecyclerViewClickListener()
		{
			@Override
			public void onClick(View v, int position)
			{
				Intent intent = new Intent( MainActivity.this, EditActivity.class );

				DataAdapter dataAdapter = (DataAdapter) recyclerView.getAdapter();
				Media media = dataAdapter.getDataset().get( position );

				TextView textViewName = v.findViewById( R.id.textViewName );
				TextView textViewComposer = v.findViewById( R.id.textViewComposer );
				TextView textViewSubgenre = v.findViewById( R.id.textViewSubGenre );
				TextView textViewDate = v.findViewById( R.id.textViewDate );

				String title = (String) textViewName.getText();
				String author = (String) textViewComposer.getText();
				String type = (String) textViewSubgenre.getText();
				String date = (String) textViewDate.getText();
				UUID id = media.getId();

				intent.putExtra( KEY_TITLE, title );
				intent.putExtra( KEY_AUTHOR, author );
				intent.putExtra( KEY_TYPE, type );
				intent.putExtra( KEY_DATE, date );
				intent.putExtra( KEY_ID, id.toString() );
				startActivity( intent );
			}
		};

		if ( application.getOperas() != null )
		{
			adapter = new DataAdapter( application.getOperas(), listener );
			recyclerView.setAdapter( adapter );
		}


		NetworkManager networkManager = NetworkManager.getSingleton();
		//networkManager.initialListCompleted = true;
	}

}
