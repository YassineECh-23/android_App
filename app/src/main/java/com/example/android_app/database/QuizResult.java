package com.example.android_app.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_results")
public class QuizResult {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "answers")
    public String answers;

    @ColumnInfo(name = "total_questions")
    public int totalQuestions;

    public QuizResult(String date, String answers, int totalQuestions) {
        this.date = date;
        this.answers = answers;
        this.totalQuestions = totalQuestions;
    }

    public static String answersToString(String[] answers) {
        return String.join("|", answers);
    }

    public String[] answersToArray() {
        return answers.split("\\|", -1);
    }
}