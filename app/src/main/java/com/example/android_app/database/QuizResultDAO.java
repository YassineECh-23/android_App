package com.example.android_app.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface QuizResultDAO {

    @Query("SELECT * FROM quiz_results ORDER BY id DESC")
    List<QuizResult> getAllResults();

    @Query("SELECT * FROM quiz_results ORDER BY id DESC")
    Single<List<QuizResult>> getAllResultsAsync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(QuizResult result);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAsync(QuizResult result);

    @Delete
    int delete(QuizResult result);

    @Query("DELETE FROM quiz_results")
    void deleteAll();
}