package org.iitb.moodi.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by udiboy on 5/11/15.
 */
public class EventsResponse {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("genre")
    public Genre[] genres;
}