package com.example.martincostasravnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity implements View.OnClickListener
{

	private EditText titleEditText, authorEditText, typeEditText, dateEditText;
	private Button saveButton;


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

		saveButton.setOnClickListener( this );
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

				// Send ADD or EDIT request
				NetworkManager networkManager = NetworkManager.getSingleton();
				networkManager.sendRequest( this, Request.generateAddRequest( record ) );

				Intent intent = new Intent( this, MainActivity.class );
				startActivity( intent );
			}
		}
	}
}
