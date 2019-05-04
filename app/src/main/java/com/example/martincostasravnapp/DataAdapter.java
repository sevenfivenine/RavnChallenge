package com.example.martincostasravnapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder>
{
	private Opera[] dataset;

	public static class DataViewHolder extends RecyclerView.ViewHolder {
		public TextView textViewName, textViewComposer, textViewSubgenre;

		public DataViewHolder(View v) {
			super(v);

			textViewName = v.findViewById( R.id.textViewName );
			textViewComposer = v.findViewById( R.id.textViewComposer );
			textViewSubgenre = v.findViewById( R.id.textViewSubGenre );
		}
	}

	public DataAdapter(Opera[] myDataset) {
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
		holder.textViewName.setText( dataset[position].getName());
		holder.textViewComposer.setText( dataset[position].getComposer());
		holder.textViewSubgenre.setText( dataset[position].getSubgenre());
	}

	@Override
	public int getItemCount() {
		return dataset.length;
	}
}
