package com.example.trackyourlocation;

/**
 * Represents an item in a ToDo list
 */
public class Locations {

    @com.google.gson.annotations.SerializedName("longitude")
    private String mLongitude;
    @com.google.gson.annotations.SerializedName("latitude")
    private String mLatitude;
    @com.google.gson.annotations.SerializedName("altitude")
    private String mAltitude;


    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;



    /**
     * Locations constructor
     */
    public Locations() {

    }

    @Override
    public String toString() {
        return "Latitude : "+getLatitude()+" Longitude : "+getLongitude()+" Altitude :"+getAltitude();
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

    public String getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double altitude) {
        mAltitude = String.valueOf(altitude);
    }

    /**
     * Initializes a new Locations
     *
     * @param
     *
     * @param id
     *            The item id
     */
    public Locations(double longitude, double latitude, double altitude, String id) {
        //this.setText(text);
        this.setAltitude(altitude);
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

    /**
     * Indicates if the item is marked as completed
     */
   /* public boolean isComplete() {
        return mComplete;
    }
*/
    /**
     * Marks the item as completed or incompleted
     */
    /*public void setComplete(boolean complete) {
        mComplete = complete;
    }
*/
    @Override
    public boolean equals(Object o) {
        return o instanceof Locations && ((Locations) o).mId == mId;
    }
}