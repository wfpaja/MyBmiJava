package com.example.mybmi.roomdata;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BmiData.class}, version = 1)
public abstract class BmiDatabase extends RoomDatabase {
    private static BmiDatabase INSTANCE;
    public static synchronized BmiDatabase getDataBase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, BmiDatabase.class, "bmi_database")
                    .build();
        }
        return INSTANCE;
    }
    public abstract BmiDataDao bmiDataDao();
}
