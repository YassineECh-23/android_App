package com.example.android_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_app.database.AppDatabase;
import com.example.android_app.database.QuizResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ResultActivity extends AppCompatActivity {

    TextView text_final_results;
    Button btn_home, btn_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.card_results), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        text_final_results = findViewById(R.id.text_final_results);
        btn_home = findViewById(R.id.btn_home);
        btn_history = findViewById(R.id.btn_history);

        String[] receivedAnswers = getIntent().getStringArrayExtra("ANSWERS");

        if (receivedAnswers != null) {
            StringBuilder resultText = new StringBuilder();
            for (int i = 0; i < receivedAnswers.length; i++) {
                resultText.append("Question ").append(i + 1).append(" : \n");
                String answer = receivedAnswers[i];
                if (answer == null || answer.trim().isEmpty()) answer = "Aucune réponse";
                resultText.append("➔ ").append(answer).append("\n\n");
            }
            text_final_results.setText(resultText.toString());
            saveResultToDatabase(receivedAnswers);
        } else {
            text_final_results.setText("Erreur : Aucune donnée reçue.");
        }

        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btn_history.setOnClickListener(v ->
                startActivity(new Intent(ResultActivity.this, HistoryActivity.class))
        );
    }

    private void saveResultToDatabase(String[] answers) {
        String date = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault())
                .format(new Date());

        QuizResult result = new QuizResult(
                date,
                QuizResult.answersToString(answers),
                answers.length
        );

        AppDatabase db = AppDatabase.getInstance(this);
        Disposable disposable = db.quizResultDAO()
                .insertAsync(result)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Toast.makeText(this, "✅ Résultat sauvegardé !", Toast.LENGTH_SHORT).show(),
                        throwable -> Toast.makeText(this, "❌ Erreur de sauvegarde", Toast.LENGTH_SHORT).show()
                );
    }
}