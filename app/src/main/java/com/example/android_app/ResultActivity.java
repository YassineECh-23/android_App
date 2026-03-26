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

        // 1. Récupération des réponses
        String[] receivedAnswers = getIntent().getStringArrayExtra("ANSWERS");

        if (receivedAnswers != null && receivedAnswers.length == 10) {

            // 2. Calcul du score et du diagnostic
            int scoreStress = calculerScoreStress(receivedAnswers);
            String diagnostic = genererDiagnostic(scoreStress);

            // 3. Construction de l'affichage final
            StringBuilder resultText = new StringBuilder();

            // On affiche le diagnostic en premier, bien en évidence
            resultText.append(" BILAN DU TEST :\n");
            resultText.append(diagnostic).append("\n\n");
            resultText.append("-----------------------------------\n\n");
            resultText.append("Détail de vos réponses :\n\n");

            // On boucle pour afficher le rappel des réponses
            for (int i = 0; i < receivedAnswers.length; i++) {
                resultText.append("Question ").append(i + 1).append(" : \n");
                String answer = receivedAnswers[i];
                if (answer == null || answer.trim().isEmpty()) answer = "Aucune réponse";
                resultText.append("➔ ").append(answer).append("\n\n");
            }

            text_final_results.setText(resultText.toString());

            // 4. Sauvegarde dans la base de données
            // (On sauvegarde les réponses telles quelles pour l'historique)
            saveResultToDatabase(receivedAnswers, scoreStress, diagnostic);

        } else {
            text_final_results.setText("Erreur : Données incomplètes.");
        }

        // --- Écouteurs des boutons ---
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

    // --- L'ALGORITHME D'ÉVALUATION ---

    private int calculerScoreStress(String[] answers) {
        int score = 0;
        try {
            // Q1 — Année d'études (Master = plus de pression)
            if ("Master 1 / 2".equals(answers[0]))  score += 1;

            // Q2 — Stress global
            if      ("Légèrement stressé(e)".equals(answers[1]))       score += 1;
            else if ("Très stressé(e)".equals(answers[1]))              score += 3;
            else if ("Au bord de l'épuisement".equals(answers[1]))      score += 5;

            // Q3 — Sommeil (SeekBar 0-5) : moins bon = plus de score
            if (answers[2] != null && !answers[2].isEmpty()) {
                int sommeil = Integer.parseInt(answers[2]);
                score += (5 - sommeil);
            }

            // Q4 — Heures de travail hors cours
            if      ("2 à 4 heures".equals(answers[3]))      score += 1;
            else if ("Plus de 4 heures".equals(answers[3]))  score += 2;

            // Q5 — Symptômes physiques (NON = 0 pt)
            if (answers[4] != null && !answers[4].equals("NON")) {
                if (answers[4].contains("Fatigue intense"))           score += 2;
                if (answers[4].contains("Troubles de concentration")) score += 1;
                if (answers[4].contains("Maux"))                      score += 2;
                if (answers[4].contains("Autre"))                     score += 1;
            }

            // Q6 — Soutien de l'entourage
            if ("Souvent".equals(answers[5]))  score += 1;
            else if ("Rarement".equals(answers[5])) score += 2;
            else if ("Jamais".equals(answers[5]))   score += 3;

            // Q7 — Dépassé par la charge
            if      ("Parfois".equals(answers[6]))     score += 1;
            else if ("Souvent".equals(answers[6]))     score += 2;
            else if ("Constamment".equals(answers[6])) score += 4;

            // Q8 — Motivation (SeekBar 0-5) : moins motivé = plus de score
            if (answers[7] != null && !answers[7].isEmpty()) {
                int motivation = Integer.parseInt(answers[7]);
                score += (5 - motivation);
            }

            // Q9 — Fréquence de détente
            if ("1 fois par mois".equals(answers[8]))    score += 1;
            else if ("Jamais".equals(answers[8]))           score += 3;

            // Q10 — Refuge en cas de stress
            if (answers[9] != null) {
                if (answers[9].contains("Écrans"))                    score += 1;
                if (answers[9].contains("Isolement"))                 score += 3;
                if (answers[9].contains("Autre"))                     score += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    private String genererDiagnostic(int score) {
        // Le score maximum possible avec cet algorithme est d'environ 30 points
        if (score < 10) {
            return "🟢 FAIBLE RISQUE (Score: " + score + ")\nVous semblez avoir un bon équilibre de vie étudiant. Continuez à préserver votre sommeil et vos moments de détente !";
        } else if (score <= 18) {
            return "🟠 RISQUE MODÉRÉ (Score: " + score + ")\nVous présentez quelques signes de fatigue ou de tension. Pensez à lever un peu le pied, à parler de vos difficultés et à aménager votre emploi du temps.";
        } else {
            return "🔴 ALERTE SANTÉ (Score: " + score + ")\nVotre charge mentale et votre niveau de stress semblent très élevés. Il est vivement recommandé d'en parler au service de médecine préventive de votre université ou à un professionnel de santé. Ne restez pas seul(e).";
        }
    }

    // --- SAUVEGARDE BDD ---
    private void saveResultToDatabase(String[] answers, int score, String diagnostic) {
        String date = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.getDefault()).format(new Date());

        QuizResult result = new QuizResult(
                date,
                QuizResult.answersToString(answers),
                answers.length,
                score,
                diagnostic
        );

        AppDatabase db = AppDatabase.getInstance(this);
        Disposable disposable = db.quizResultDAO()
                .insertAsync(result)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Toast.makeText(this, "Résultat sauvegardé !", Toast.LENGTH_SHORT).show(),
                        throwable -> Toast.makeText(this, "Erreur de sauvegarde", Toast.LENGTH_SHORT).show()
                );
    }
}