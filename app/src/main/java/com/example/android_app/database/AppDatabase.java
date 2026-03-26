package com.example.android_app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {QuizResult.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract QuizResultDAO quizResultDAO();

    private static volatile AppDatabase INSTANCE;

    // Migration de la version 1 vers 2 : on ajoute les 2 nouvelles colonnes
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE quiz_results ADD COLUMN score INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE quiz_results ADD COLUMN diagnostic TEXT NOT NULL DEFAULT ''");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "quiz_database"
                            )
                            .addMigrations(MIGRATION_1_2)// ← ajout de la migration
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}