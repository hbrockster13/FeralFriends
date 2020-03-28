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
     * GPS location of colony
     */
    private String mLocation;

    public FeralFriend()
    {
        this(UUID.randomUUID());
//TODO: May need to query to get the next or correct ID or make sure this random one doesn't exist
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

    public String getLocation()
    {
        return mLocation;
    }

    public void setLocation(String location)
    {
        this.mLocation = location;
    }
}//End of FeralFriends
