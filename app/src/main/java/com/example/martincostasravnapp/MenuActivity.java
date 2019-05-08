package com.example.martincostasravnapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.martincostasravnapp.RavnApplication.KEY_HOST;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener
{

	private EditText ipEditText;
	private Button   connectButton;

	private RavnApplication application;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_menu );

		ipEditText = findViewById( R.id.ipEditText );
		connectButton = findViewById( R.id.connectButton );

		connectButton.setOnClickListener( this );

		application = (RavnApplication) getApplication();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
		if ( preferences.contains( KEY_HOST ) )
		{
			String host = preferences.getString( KEY_HOST, "" );

			ipEditText.setText( host );

			application.connectToHost(this, host);

			Intent intent = new Intent( this, MainActivity.class );
			startActivity( intent );
		}

	}


	@Override
	public void onClick(View v)
	{
		if ( v.equals( connectButton ) )
		{
			// No IP Address entered, prompt the user to enter one
			if ( ipEditText.getText().length() == 0 )
			{
				Toast.makeText( getApplicationContext(), R.string.no_ip, Toast.LENGTH_SHORT ).show();
			}

			else
			{
				String host = ipEditText.getText().toString();

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString( KEY_HOST, host );
				editor.apply();

				application.connectToHost(this, host);

				Intent intent = new Intent( this, MainActivity.class );
				startActivity( intent );
			}
		}
	}
}
