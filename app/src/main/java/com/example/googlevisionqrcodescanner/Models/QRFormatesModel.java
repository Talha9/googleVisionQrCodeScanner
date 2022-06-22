package com.example.googlevisionqrcodescanner.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class QRFormatesModel implements Parcelable {
    String formateName;
    int img;

    public QRFormatesModel(String formateName, int img) {
        this.formateName = formateName;
        this.img = img;
    }

    protected QRFormatesModel(Parcel in) {
        formateName = in.readString();
        img = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formateName);
        dest.writeInt(img);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QRFormatesModel> CREATOR = new Creator<QRFormatesModel>() {
        @Override
        public QRFormatesModel createFromParcel(Parcel in) {
            return new QRFormatesModel(in);
        }

        @Override
        public QRFormatesModel[] newArray(int size) {
            return new QRFormatesModel[size];
        }
    };

    public String getFormateName() {
        return formateName;
    }

    public void setFormateName(String formateName) {
        this.formateName = formateName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
