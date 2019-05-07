package com.example.martincostasravnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

import static com.example.martincostasravnapp.Media.KEY_AUTHOR;
import static com.example.martincostasravnapp.Media.KEY_DATE;
import static com.example.martincostasravnapp.Media.KEY_ID;
import static com.example.martincostasravnapp.Media.KEY_TITLE;
import static com.example.martincostasravnapp.Media.KEY_TYPE;

public class EditActivity extends AppCompatActivity implements View.OnClickListener
{

	private EditText titleEditText, authorEditText, typeEditText, dateEditText;
	private Button saveButton, deleteButton;

	private UUID id;

	private enum Mode {
		ADD_MODE, EDIT_MODE
	};

	private Mode mode;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_edit );

		titleEditText = findViewById( R.id.titleEditText );
		authorEditText = findViewById( R.id.authorEditText );
		typeEditText = findViewById( R.id.typeEditText );
		dateEditText = findViewById( R.id.dateEditText );

		saveButton = findViewById( R.id.saveButton );
		deleteButton = findViewById( R.id.deleteButton );

		saveButton.setOnClickListener( this );
		deleteButton.setOnClickListener( this );

		mode = Mode.ADD_MODE;

		Intent intent = getIntent();

		if ( intent.hasExtra( KEY_TITLE ) && intent.hasExtra( KEY_AUTHOR ) && intent.hasExtra( KEY_TYPE ) && intent.hasExtra( KEY_DATE ) && intent.hasExtra( KEY_ID ) )
		{
			String title = intent.getStringExtra( KEY_TITLE );
			String author = intent.getStringExtra( KEY_AUTHOR );
			String type = intent.getStringExtra( KEY_TYPE );
			String date = intent.getStringExtra( KEY_DATE );
			UUID id = UUID.fromString(intent.getStringExtra( KEY_ID ));

			titleEditText.setText( title );
			authorEditText.setText( author );
			typeEditText.setText( type );
			dateEditText.setText( date );

			this.id = id;

			mode = Mode.EDIT_MODE;
		}

	}


	@Override
	public void onClick(View v)
	{
		if ( v.equals( saveButton ) )
		{
			// If all fields are blank, no new record is created
			if ( titleEditText.getText().length() == 0 && authorEditText.getText().length() == 0 && typeEditText.getText().length() == 0 && dateEditText.getText().length() == 0 )
			{
				Toast.makeText( getApplicationContext(), R.string.no_record, Toast.LENGTH_SHORT ).show();

				Intent intent = new Intent( this, MainActivity.class );
				startActivity( intent );
			}

			// Otherwise, ALL fields must be filled to add or edit a record. If not, tell the user to add more information
			else if ( titleEditText.getText().length() == 0 || authorEditText.getText().length() == 0 || typeEditText.getText().length() == 0 || dateEditText.getText().length() == 0 )
			{
				Toast.makeText( getApplicationContext(), R.string.empty_fields, Toast.LENGTH_SHORT ).show();
			}

			// All fields are filled out. We are adding or editing a record
			else
			{
				Media record = new Media();
				record.setTitle( titleEditText.getText().toString() );
				record.setAuthor( authorEditText.getText().toString() );
				record.setType( typeEditText.getText().toString() );
				record.setDate( dateEditText.getText().toString() );

				if ( mode == Mode.ADD_MODE )
				{
					// Send ADD request
					NetworkManager networkManager = NetworkManager.getSingleton();
					networkManager.sendRequest( this, Request.generateAddRequest( record ) );

					Intent intent = new Intent( this, MainActivity.class );
					startActivity( intent );
				}

				else if ( mode == Mode.EDIT_MODE )
				{
					record.setId( id );

					// Send UPDATE request
					NetworkManager networkManager = NetworkManager.getSingleton();
					networkManager.sendRequest( this, Request.generateUpdateRequest( record ) );

					Intent intent = new Intent( this, MainActivity.class );
					startActivity( intent );
				}
			}
		}

		else if ( v.equals( deleteButton ) )
		{
			// Send REMOVE request
			NetworkManager networkManager = NetworkManager.getSingleton();
			networkManager.sendRequest( this, Request.generateRemoveRequest( id ) );

			Intent intent = new Intent( this, MainActivity.class );
			startActivity( intent );
		}
	}
}
