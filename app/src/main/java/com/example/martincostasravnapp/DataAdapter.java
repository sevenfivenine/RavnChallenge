package com.example.martincostasravnapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder>
{
	private ArrayList<Media> dataset;

	public static class DataViewHolder extends RecyclerView.ViewHolder {
		public TextView textViewName, textViewComposer, textViewSubgenre, textViewDate;

		public DataViewHolder(View v) {
			super(v);

			textViewName = v.findViewById( R.id.textViewName );
			textViewComposer = v.findViewById( R.id.textViewComposer );
			textViewSubgenre = v.findViewById( R.id.textViewSubGenre );
			textViewDate = v.findViewById( R.id.textViewDate );
		}
	}

	public DataAdapter(ArrayList<Media> myDataset) {
		dataset = myDataset;
	}

	@Override
	public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from( parent.getContext()).inflate(R.layout.layout_opera, parent, false);

		DataViewHolder vh = new DataViewHolder( v);
		return vh;
	}

	@Override
	public void onBindViewHolder(DataViewHolder holder, int position) {
		holder.textViewName.setText( dataset.get( position ).getTitle());
		holder.textViewComposer.setText( dataset.get( position ).getAuthor());
		holder.textViewSubgenre.setText( dataset.get( position ).getType());
		holder.textViewDate.setText( dataset.get( position ).getDate() );
	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}
}
