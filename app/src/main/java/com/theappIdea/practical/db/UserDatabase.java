package com.theappIdea.practical.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.theappIdea.practical.model.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
abstract class UserDatabase extends RoomDatabase {
    abstract UserDao userDao();
    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile UserDatabase INSTANCE;

    static UserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class, "user_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
