package com.example.android_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {

    TextView text_final_results;
    Button btn_home;

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

        // 1. On récupère les données envoyées par playActivity
        String[] receivedAnswers = getIntent().getStringArrayExtra("ANSWERS");

        // 2. On construit le texte à afficher
        if (receivedAnswers != null) {
            StringBuilder resultText = new StringBuilder();

            for (int i = 0; i < receivedAnswers.length; i++) {
                resultText.append("Question ").append(i + 1).append(" : \n");

                // On gère le cas où la réponse est vide (par exemple si une checkbox n'a pas été cochée)
                String answer = receivedAnswers[i];
                if (answer == null || answer.trim().isEmpty()) {
                    answer = "Aucune réponse";
                }

                resultText.append("➔ ").append(answer).append("\n\n");
            }

            // On affiche le texte final dans le TextView
            text_final_results.setText(resultText.toString());
        } else {
            text_final_results.setText("Erreur : Aucune donnée reçue.");
        }

        // 3. Bouton pour retourner au menu principal
        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            // Efface l'historique des pages pour ne pas empiler les MainActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}