package com.example.martincostasravnapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder>
{
	private ArrayList<Media>          dataset;
	private RecyclerViewClickListener listener;

	public DataAdapter(ArrayList<Media> myDataset, RecyclerViewClickListener listener)
	{
		dataset = myDataset;
		this.listener = listener;
	}


	@Override
	public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.layout_opera, parent, false );

		DataViewHolder vh = new DataViewHolder( v, listener );
		return vh;
	}


	@Override
	public void onBindViewHolder(DataViewHolder holder, int position)
	{
		holder.textViewName.setText( dataset.get( position ).getTitle() );
		holder.textViewComposer.setText( dataset.get( position ).getAuthor() );
		holder.textViewSubgenre.setText( dataset.get( position ).getType() );
		holder.textViewDate.setText( dataset.get( position ).getDate() );
	}


	@Override
	public int getItemCount()
	{
		return dataset.size();
	}


	public ArrayList<Media> getDataset()
	{
		return dataset;
	}


	public interface RecyclerViewClickListener
	{
		void onClick(View view, int position);
	}

	public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		public TextView textViewName, textViewComposer, textViewSubgenre, textViewDate;
		private RecyclerViewClickListener listener;


		public DataViewHolder(View v, RecyclerViewClickListener listener)
		{
			super( v );

			textViewName = v.findViewById( R.id.textViewName );
			textViewComposer = v.findViewById( R.id.textViewComposer );
			textViewSubgenre = v.findViewById( R.id.textViewSubGenre );
			textViewDate = v.findViewById( R.id.textViewDate );

			this.listener = listener;
			v.setOnClickListener( this );
		}


		@Override
		public void onClick(View v)
		{
			listener.onClick( v, getAdapterPosition() );
		}
	}
}
