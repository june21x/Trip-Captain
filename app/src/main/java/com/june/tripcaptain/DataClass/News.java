package com.june.tripcaptain.DataClass;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class News implements Parcelable {
    private String id;
    private Date date;
    private String author;
    private String imageUriStr;
    private String title;
    private String content;

    public News(String id, Date date, String author, String imageUriStr, String title, String content) {
        this.id = id;
        this.date = date;
        this.author = author;
        this.imageUriStr = imageUriStr;
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUriStr() {
        return imageUriStr;
    }

    public void setImageUriStr(String imageUriStr) {
        this.imageUriStr = imageUriStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeLong(date.getTime());
        parcel.writeString(author);
        parcel.writeString(imageUriStr);
        parcel.writeString(title);
        parcel.writeString(content);
    }

    // example constructor that takes a Parcel and gives you an object populated with it's values
    public News(Parcel in) {
        id = in.readString();
        date = new Date(in.readLong());
        author = in.readString();
        imageUriStr = in.readString();
        title = in.readString();
        content = in.readString();

    }
}
