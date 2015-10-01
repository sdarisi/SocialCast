package com.socialcast.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by sdarisi on 8/19/15.
 */
public class UtilMethods {
    public static void saveJSONToPreferences(Context context, JSONObject token, String prefName) {
        SharedPreferences preferences = context.getSharedPreferences("com.socialcast", Context.MODE_PRIVATE);
        preferences.edit().putString(prefName, token.toString()).apply();
    }

    public static JSONObject getJSONFromPreferences(Context context, String prefName) {
        SharedPreferences preferences = context.getSharedPreferences("com.socialcast", Context.MODE_PRIVATE);
        String prefString = preferences.getString(prefName, "");
        if (prefString != "") {
            try {
                return new JSONObject(prefString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void writeObjectToFile(Context context, String fileName, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object readObjectFromFile(Context context, String fileName) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;
    }
}
