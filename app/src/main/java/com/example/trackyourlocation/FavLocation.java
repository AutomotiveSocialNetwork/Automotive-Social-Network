package com.example.trackyourlocation;

/**
 * Created by Speed on 04/12/2017.
 */

public class FavLocation {

    @com.google.gson.annotations.SerializedName("longitude")
    private String mLongitude;
    @com.google.gson.annotations.SerializedName("latitude")
    private String mLatitude;



    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;



    /**
     * Locations constructor
     */
    public FavLocation() {

    }

    @Override
    public String toString() {
        return "Latitude : "+getLatitude()+" Longitude : "+getLongitude();
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = String.valueOf(longitude);
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = String.valueOf(latitude);
    }



    /**
     * Initializes a new Locations
     *
     * @param
     *
     * @param id
     *            The item id
     */
    public FavLocation(double longitude, double latitude, double altitude, String id) {
        //this.setText(text);

        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
   /*public String getText() {
        return mText;
    }*/

    /**
     * Sets the item text
     *
     * @param text
     *            text to set
     */
   /* public final void setText(String text) {
        mText = text;
    }*/

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mId = id;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof FavLocation && ((FavLocation) o).mId == mId;
    }
}
