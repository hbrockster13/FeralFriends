/**
 * The purpose of this class is to have a model of instance data that we should keep for each object
 */
package com.example.feralfriends.models;
import java.util.Date;
import java.util.UUID;

public class FeralFriend
{
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
     * What user was the first friend or created the colony
     */
    private String mFirstFriend;
    /**
     * Has the Friend been trapped, neutered and return
     */
    private boolean mTNRed;
    /**
     * GPS longitude of colony
     */
    private String mLongitude;
    /**
     * GPS Latitude of colony
     */
    private String mLatitude;

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
        this(UUID.randomUUID());
        setFed(false);
        setDate(new Date());
        setDetails("-- No Details at the moment --");
        setFirstFriend("Some Crazy cat lady");
        setLatitude("0");
        setLongitude("0");
        setTitle("No Title");
        setTNRed(false);
    }

    public FeralFriend(UUID ID)
    {
        mID = ID;
        mDate = new Date();

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

    public String getFirstFriend()
    {
        return mFirstFriend;
    }

    public void setFirstFriend(String mFirstFriend)
    {
        this.mFirstFriend = mFirstFriend;
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

    public String getLongitude()
    {
        return mLongitude;
    }

    public void setLongitude(String mLongitude)
    {
        this.mLongitude = mLongitude;
    }

    public String getLatitude()
    {
        return mLatitude;
    }

    public void setLatitude(String mLatitude)
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
                + "\nTitle: " + (this.getTitle().isEmpty()? "NO TITLE PROVIDED" : this.getTitle())
                + "\nLast Fed: " + this.getDate().toString()
                + "\nLocation: " + this.getLongitude() + ", " + this.getLatitude()
                + "\nBeen TNR:" + (this.isTNRed()? "YES" : "NO")
                + "\nDetails: " + this.getDetails();
        return report;
    }
}//End of FeralFriends