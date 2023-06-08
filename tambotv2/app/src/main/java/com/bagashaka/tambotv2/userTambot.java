package com.bagashaka.tambotv2;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class userTambot implements Parcelable {
    private String Username;
    private String Password;
    private String Nama;

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setNama(String nama) {
        Nama = nama;
    }
    public String getPassword() {
        return Password;
    }
    public String getUsername() {
        return Username;
    }
    public String getNama() {
        return Nama;
    }

    protected userTambot(Parcel in) {
        Username=in.readString();
        Password=in.readString();
        Nama=in.readString();
    }

    public userTambot(String Username, String Password, String Nama){
        this.Username = Username;
        this.Password = Password;
        this.Nama = Nama;
    }
    public static final Creator<userTambot> CREATOR = new Creator<userTambot>() {
        @Override
        public userTambot createFromParcel(Parcel in) {
            return new userTambot(in);
        }

        @Override
        public userTambot[] newArray(int size) {
            return new userTambot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Username);
        dest.writeString(Password);
        dest.writeString(Nama);
    }
}
