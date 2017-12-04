package com.example.trackyourlocation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter to bind a Locations List to a view
 */
public class LocationAdapter extends ArrayAdapter<Locations> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public LocationAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final Locations currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            //TODO explore what is the parent ???
        }

        row.setTag(currentItem);
        final TextView longitudeTv=(TextView)row.findViewById(R.id.longitudeTv);
        final TextView latitudeTv=(TextView)row.findViewById(R.id.latitudeTv);
        final TextView altitudeTv=(TextView)row.findViewById(R.id.altitudeTv);

        longitudeTv.setText(String.format("%.02f",Double.valueOf(currentItem.getLongitude())));
        latitudeTv.setText(String.format("%.02f",Double.valueOf(currentItem.getLatitude())));
        altitudeTv.setText(String.format("%.02f",Double.valueOf(currentItem.getAltitude())));


        return row;
    }

}