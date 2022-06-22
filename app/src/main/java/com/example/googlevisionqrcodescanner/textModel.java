package com.example.googlevisionqrcodescanner;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class textModel implements Parcelable {
    String txt;
    Bitmap img;


    public textModel(String txt, Bitmap img) {
        this.txt = txt;
        this.img = img;
    }

    protected textModel(Parcel in) {
        txt = in.readString();
        img = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txt);
        dest.writeParcelable(img, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<textModel> CREATOR = new Creator<textModel>() {
        @Override
        public textModel createFromParcel(Parcel in) {
            return new textModel(in);
        }

        @Override
        public textModel[] newArray(int size) {
            return new textModel[size];
        }
    };

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
