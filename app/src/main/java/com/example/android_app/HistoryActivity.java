package com.example.android_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_app.database.AppDatabase;
import com.example.android_app.database.QuizResult;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView text_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_history);
        text_empty = findViewById(R.id.text_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadHistory();
    }

    private void loadHistory() {
        AppDatabase db = AppDatabase.getInstance(this);
        Disposable disposable = db.quizResultDAO()
                .getAllResultsAsync()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isEmpty()) {
                                text_empty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                text_empty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                // On passe à l'adapter une méthode (Listener) qui sera appelée quand on clique sur l'enveloppe
                                recyclerView.setAdapter(new HistoryAdapter(results, (result, sessionNumber) -> {
                                    showEmailPopup(result, sessionNumber);
                                }));
                            }
                        },
                        throwable -> {
                            text_empty.setText("Erreur lors du chargement.");
                            text_empty.setVisibility(View.VISIBLE);
                        }
                );
    }

    // --- LOGIQUE DE LA POPUP ET DE L'E-MAIL ---

    private void showEmailPopup(QuizResult result, int sessionNumber) {
        // 1. On prépare la popup
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_send_email, null);
        builder.setView(dialogView);

        EditText editPrenom = dialogView.findViewById(R.id.edit_prenom);
        EditText editNom = dialogView.findViewById(R.id.edit_nom);
        EditText editEmailMedecin = dialogView.findViewById(R.id.edit_email_medecin);

        // 2. On récupère les infos sauvegardées (SharedPreferences) pour pré-remplir les champs
        SharedPreferences prefs = getSharedPreferences("MindTrackPrefs", MODE_PRIVATE);
        editPrenom.setText(prefs.getString("prenom_user", ""));
        editNom.setText(prefs.getString("nom_user", ""));
        editEmailMedecin.setText(prefs.getString("email", ""));

        // 3. Configuration du bouton "Envoyer"
        builder.setPositiveButton("Envoyer", (dialog, which) -> {
            String prenom = editPrenom.getText().toString().trim();
            String nom = editNom.getText().toString().trim();
            String email = editEmailMedecin.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "L'e-mail du médecin est obligatoire", Toast.LENGTH_SHORT).show();
                return;
            }

            // On sauvegarde ces infos pour la prochaine fois
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("prenom_user", prenom);
            editor.putString("nom_user", nom);
            editor.putString("email_medecin", email);
            editor.apply();

            // On lance l'application d'e-mail
            sendEmailViaIntent(prenom, nom, email, result, sessionNumber);
        });

        // Bouton "Annuler"
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void sendEmailViaIntent(String prenom, String nom, String email, QuizResult result, int sessionNumber) {
        // L'objet de l'e-mail
        String subject = "Résultats MindTrack - Session #" + sessionNumber + " - " + prenom + " " + nom;

        // Construction du corps du message
        StringBuilder body = new StringBuilder();
        body.append("Bonjour,\n\n");
        body.append("Voici les résultats du questionnaire de santé passé le ").append(result.date).append(".\n\n");


        String[] answers = result.answersToArray();
        for (int i = 0; i < answers.length; i++) {
            body.append("Question ").append(i + 1).append(" : ").append(answers[i]).append("\n");
        }

        body.append("\nCordialement,\n").append(prenom).append(" ").append(nom);

        // Lancement de l'Intent (Ouvre Gmail, Outlook, etc.)
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body.toString());

        try {
            startActivity(Intent.createChooser(intent, "Envoyer via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Aucune application d'e-mail configurée sur ce téléphone.", Toast.LENGTH_LONG).show();
        }
    }


    // --- ADAPTER DU RECYCLER VIEW ---

    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
        private final List<QuizResult> results;
        private final OnEmailClickListener emailClickListener;

        // Interface pour écouter le clic sur l'enveloppe
        public interface OnEmailClickListener {
            void onEmailClick(QuizResult result, int sessionNumber);
        }

        HistoryAdapter(List<QuizResult> results, OnEmailClickListener listener) {
            this.results = results;
            this.emailClickListener = listener;
        }

        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, int position) {
            QuizResult currentResult = results.get(position);
            int sessionNum = position + 1;

            holder.bind(currentResult, sessionNum);

            // On connecte le clic sur le bouton au Listener
            holder.btn_send_email.setOnClickListener(v -> {
                emailClickListener.onEmailClick(currentResult, sessionNum);
            });
        }

        @Override
        public int getItemCount() { return results.size(); }

        static class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView text_session_number, text_date, text_answers;
            ImageButton btn_send_email;

            HistoryViewHolder(View itemView) {
                super(itemView);
                text_session_number = itemView.findViewById(R.id.text_session_number);
                text_date = itemView.findViewById(R.id.text_date);
                text_answers = itemView.findViewById(R.id.text_answers);
                btn_send_email = itemView.findViewById(R.id.btn_send_email); // On récupère notre nouveau bouton
            }

            void bind(QuizResult result, int num) {
                text_session_number.setText("Session #" + num);
                text_date.setText(result.date);

                // Affichage du bilan EN PREMIER
                if (result.diagnostic != null && !result.diagnostic.isEmpty()) {
                    text_answers.setText(result.diagnostic + "\n\n──────────────────\n\n");
                }

                // Puis le détail des réponses
                String[] answers = result.answersToArray();
                StringBuilder sb = new StringBuilder();

                // Ajoute le bilan au début si présent
                if (result.diagnostic != null && !result.diagnostic.isEmpty()) {
                    sb.append(result.diagnostic).append("\n\n──────────────\n\n");
                }

                for (int i = 0; i < answers.length; i++)
                    sb.append("Q").append(i + 1).append(" : ").append(answers[i]).append("\n");

                text_answers.setText(sb.toString().trim());
            }
        }
    }
}