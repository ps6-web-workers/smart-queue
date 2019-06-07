package com.example.studentapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.studentapp.Models.User;

public class UserLocalStore {
    private final static String SP_NAME = "userDetails";
    private SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("loggin", user.getLoggin());
        spEditor.putInt("abonnement", user.getAbonnement());
        spEditor.putString("firstName", user.getFirstName());
        spEditor.putString("lastName", user.getLastName());
        spEditor.apply();
    }

    public User getStoredUser() {
        String loggin = userLocalDatabase.getString("loggin", "");
        int abonnement = userLocalDatabase.getInt("abonnement", -1);
        String firstName = userLocalDatabase.getString("firstName", "");
        String lastName = userLocalDatabase.getString("lastName", "");
        return new User(loggin, abonnement, firstName, lastName);
    }

    public void clearStoredUser() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }

    public void updateAbonnement(int queueId) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putInt("abonnement", queueId);
        spEditor.apply();
    }
}
