/**
 * The purpose of this class is to have a model of instance data that we should keep for each object
 */
package com.example.feralfriends.models;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class FeralFriend implements Serializable
{
    /**
     * User ID in Amazon AWS Cognito Pool
     */
    private String userID;
    /**
     * Unique Identifier for the feral friend
     */
    private UUID mID;
    /**
     * This is the name of the feral colony
     * TODO: If a name is not given then the name could be its ID
     */
    private String title;
    /**
     * This is the last date that the friend was helped
     */
    private Date mDate;
    /**
     * Indicates if the friend has been fed
     */
    private boolean mFed;
    /**
     * Has the Friend been trapped, neutered and return
     */
    private boolean mTNRed;
    /**
     * GPS longitude of colony
     */
    private double mLongitude;
    /**
     * GPS Latitude of colony
     */
    private double mLatitude;

    /**
     * Description details
     */
    private String mDetails;

    /**
     * Number of animals at location
     */
    private int mNumberOfFriend;

    public FeralFriend()
    {
        setUserID(null);
        setID(UUID.randomUUID());
        setDate(new Date());
        setFed(false);
        setDetails("No description.");
        setLatitude(0.0);
        setLongitude(0.0);
        setTitle("No Title");
        setTNRed(false);
        setNumberOfFriends(1);
    }

    public FeralFriend(String userID, String mID, double mLatitude, double mLongitude, String title, String mDetails, String lastFed, boolean mTNRed, int mNumberOfFriend)
    {
        this.userID = userID;
        this.mID = UUID.fromString(mID);
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.title = title;
        this.mDetails = mDetails;

        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        try
        {
            this.mDate = dateFormatter.parse(lastFed);
        }
        catch(ParseException pe)
        {
            Log.e("FeralFriend Model", pe.getMessage());
        }

        this.mTNRed = mTNRed;
        this.mNumberOfFriend = mNumberOfFriend;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public UUID getID()
    {
        return mID;
    }

    public void setID(UUID mID)
    {
        this.mID = mID;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getDate()
    {
        return mDate;
    }

    public void setDate(Date mDate)
    {
        this.mDate = mDate;
    }

    public boolean isFed()
    {
        return mFed;
    }

    public void setFed(boolean mFed)
    {
        this.mFed= mFed;
    }

    public String getPhotoFileName()
    {
        return "IMG_" + getID().toString() + ".jpg";
    }

    public boolean isTNRed()
    {
        return mTNRed;
    }

    public void setTNRed(boolean mTNRed)
    {
        this.mTNRed = mTNRed;
    }

    public double getLongitude()
    {
        return mLongitude;
    }

    public void setLongitude(double mLongitude)
    {
        this.mLongitude = mLongitude;
    }

    public double getLatitude()
    {
        return mLatitude;
    }

    public void setLatitude(double mLatitude)
    {
        this.mLatitude = mLatitude;
    }

    public String getDetails()
    {
        return mDetails;
    }

    public void setDetails(String mDetails)
    {
        this.mDetails = mDetails;
    }

    public int getNumberOfFriends()
    {
        return mNumberOfFriend;
    }

    public void setNumberOfFriends(int mNumberOfFriend)
    {
        this.mNumberOfFriend = mNumberOfFriend;
    }

    public String buildReport()
    {
        String report = "FRIEND REPORT:"
                + "\nUserID: " + this.getUserID()
                + "\nMarkerID: " + this.getID()
                + "\nTitle: " + (this.getTitle().isEmpty() ? "NO TITLE PROVIDED" : this.getTitle())
                + "\nLast Fed: " + this.getDate().toString()
                + "\nLocation: " + this.getLatitude() + ", " + this.getLongitude()
                + "\nBeen TNR: " + (this.isTNRed() ? "YES" : "NO")
                + "\nDetails: " + this.getDetails()
                + "\nNum Friends: " + this.getNumberOfFriends();
        return report;
    }

    public Document asDocument()
    {
        Document document = new Document();

        if(userID != null)
        {
            document.put("UserId", getUserID());
        }

        document.put("MarkerId", getID().toString());
        document.put("Lat", getLatitude());
        document.put("Lng", getLongitude());
        document.put("Title", getTitle());
        document.put("Description", getDetails());
        document.put("LastFed", getDate().toString());
        document.put("TNR", isTNRed());
        document.put("NumFriends", getNumberOfFriends());

        return document;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }

        if(o == null || o.getClass() != this.getClass())
        {
            return false;
        }

        FeralFriend friend = (FeralFriend) o;

        if(friend.mID.equals(this.mID) && (Double.compare(friend.mLatitude, this.mLatitude) == 0) && (Double.compare(friend.mLongitude, this.mLongitude) == 0) &&
            friend.title.equals(this.title) && friend.mDetails.equals(this.mDetails) && friend.mDate.equals(this.mDate) && (friend.mTNRed && this.mTNRed) &&
            friend.mNumberOfFriend == this.mNumberOfFriend)
        {
            return true;
        }

        return false;
    }
}//End of FeralFriends