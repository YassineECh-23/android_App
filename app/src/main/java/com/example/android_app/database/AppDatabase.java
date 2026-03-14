package com.example.android_app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {QuizResult.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract QuizResultDAO quizResultDAO();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "quiz_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}