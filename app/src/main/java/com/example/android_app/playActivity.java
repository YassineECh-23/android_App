package com.example.android_app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class playActivity extends AppCompatActivity {

    // --- 1. DONNÉES DU QUESTIONNAIRE ---
    String[] question_List = {
            " plusieurs types de questions (pour mettre en valeur la variété des composants graphiques) ; (Boutons)",
            "Question 2 (Spinner)",
            "Question 3 (CheckBox)",
            "Question 4 (SeekBar)",
            "Question 5 (Boutons)"
    };

    // 0 = Boutons, 1 = Spinner, 2 = CheckBox, 3 = SeekBar
    int[] questionTypes = {0, 1, 2, 3, 0};

    // Les choix pour chaque question
    String[][] choose_List = {
            {"Choose 1", "Choose 2", "Choose 3", "Choose 4"}, // Q1
            {"Option A", "Option B", "Option C", "Option D", "Option E"}, // Q2
            {"Check 1", "Check 2", "Check 3", ""}, // Q3 (Seulement 3 cases)
            {"", "", "", ""}, // Q4 (Pas besoin de texte pour la seekbar)
            {"Choose 1", "Choose 2", "Choose 3", "Choose 4"}  // Q5
    };

    // Tableau pour stocker les 5 réponses de l'utilisateur
    String[] userAnswers = new String[5];


    // --- 2. DÉCLARATION DES VUES ---
    TextView cpt_question, text_question, seekbar_value_text;
    Button btn_choose1, btn_choose2, btn_choose3, btn_choose4, btn_next;
    LinearLayout layout_buttons, layout_spinner, layout_checkbox, layout_seekbar;
    Spinner spinner_choices;
    CheckBox cb_1, cb_2, cb_3;
    SeekBar seekbar_choices;
    ImageView image_back;

    int current_quest = 0;
    boolean isclickbtn = false;
    String valueChoose = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- 3. INITIALISATION DES VUES (findViewById) ---
        cpt_question = findViewById(R.id.cpt_question);
        text_question = findViewById(R.id.text_question);

        // Conteneurs (Layouts)
        layout_buttons = findViewById(R.id.layout_buttons);
        layout_spinner = findViewById(R.id.layout_spinner);
        layout_checkbox = findViewById(R.id.layout_checkbox);
        layout_seekbar = findViewById(R.id.layout_seekbar);

        // Boutons
        btn_choose1 = findViewById(R.id.btn_choose1);
        btn_choose2 = findViewById(R.id.btn_choose2);
        btn_choose3 = findViewById(R.id.btn_choose3);
        btn_choose4 = findViewById(R.id.btn_choose4);
        btn_next = findViewById(R.id.btn_next);

        // Autres composants
        spinner_choices = findViewById(R.id.spinner_choices);
        cb_1 = findViewById(R.id.cb_1);
        cb_2 = findViewById(R.id.cb_2);
        cb_3 = findViewById(R.id.cb_3);
        seekbar_choices = findViewById(R.id.seekbar_choices);
        seekbar_value_text = findViewById(R.id.seekbar_value_text);
        image_back = findViewById(R.id.image_back);


        // --- 4. ÉCOUTEURS D'ÉVÉNEMENTS (Listeners) ---

        // Gestion du bouton retour
        image_back.setOnClickListener(v -> {
            if (current_quest > 0) {
                current_quest--;
                // On réinitialise l'état pour permettre de valider à nouveau
                isclickbtn = true; 
                valueChoose = userAnswers[current_quest];
                remplirData();
            } else {
                // Si on est à la première question, on ferme l'activité
                finish();
            }
        });

        // Mettre à jour le texte quand la SeekBar bouge
        seekbar_choices.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar_value_text.setText("Valeur: " + progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Logique du bouton "Next"
        btn_next.setOnClickListener(view -> {

            // Étape A : Récupération de la valeur selon le type de question
            int type = questionTypes[current_quest];

            if (type == 1) { // Si c'est un Spinner
                valueChoose = spinner_choices.getSelectedItem().toString();
                isclickbtn = true;
            }
            else if (type == 2) { // Si ce sont des CheckBox
                valueChoose = "";
                if(cb_1.isChecked()) valueChoose += cb_1.getText() + " ";
                if(cb_2.isChecked()) valueChoose += cb_2.getText() + " ";
                if(cb_3.isChecked()) valueChoose += cb_3.getText() + " ";

                // Valide le clic seulement si au moins une case est cochée
                isclickbtn = !valueChoose.trim().isEmpty();
            }
            else if (type == 3) { // Si c'est la SeekBar
                valueChoose = String.valueOf(seekbar_choices.getProgress());
                isclickbtn = true;
            }

            // Étape B : Traitement de la réponse
            if (isclickbtn) {
                // On sauvegarde la réponse dans le tableau
                userAnswers[current_quest] = valueChoose;

                // Si ce n'est pas la dernière question
                if (current_quest < question_List.length - 1) {
                    current_quest++; // On passe à la question suivante
                    
                    // Si on a déjà une réponse pour la question suivante (cas d'un retour en arrière)
                    if (userAnswers[current_quest] != null) {
                        isclickbtn = true;
                        valueChoose = userAnswers[current_quest];
                    } else {
                        isclickbtn = false;
                        valueChoose = "";
                    }

                    remplirData(); // On charge les nouvelles données
                } else {
                    // FIN DU QUESTIONNAIRE : On envoie les données vers ResultActivity
                    Intent intent = new Intent(playActivity.this, ResultActivity.class);
                    intent.putExtra("ANSWERS", userAnswers);
                    startActivity(intent);
                    finish(); // On ferme cette page pour empêcher le retour en arrière
                }
            } else {
                Toast.makeText(this, "Veuillez choisir une réponse", Toast.LENGTH_SHORT).show();
            }
        });

        // On charge la première question au démarrage de l'activité
        remplirData();
    }


    // --- 5. MÉTHODES UTILITAIRES ---

    // Remplissage des données et gestion de la visibilité des layouts
    void remplirData() {
        // Mise à jour de l'en-tête
        cpt_question.setText((current_quest + 1) + "/" + question_List.length);
        text_question.setText(question_List[current_quest]);

        int currentType = questionTypes[current_quest];
        String savedAnswer = userAnswers[current_quest];

        // On cache tous les conteneurs par défaut
        layout_buttons.setVisibility(View.GONE);
        layout_spinner.setVisibility(View.GONE);
        layout_checkbox.setVisibility(View.GONE);
        layout_seekbar.setVisibility(View.GONE);

        // Réinitialisation de l'UI des boutons
        resetButtonsUI();
        
        // On affiche uniquement le conteneur adapté à la question
        if (currentType == 0) { // Type 0 : Boutons
            layout_buttons.setVisibility(View.VISIBLE);
            btn_choose1.setText(choose_List[current_quest][0]);
            btn_choose2.setText(choose_List[current_quest][1]);
            btn_choose3.setText(choose_List[current_quest][2]);
            btn_choose4.setText(choose_List[current_quest][3]);
            
            // Si on a une réponse sauvegardée, on la sélectionne visuellement
            if (savedAnswer != null) {
                if (savedAnswer.equals(btn_choose1.getText().toString())) highlightButton(btn_choose1);
                else if (savedAnswer.equals(btn_choose2.getText().toString())) highlightButton(btn_choose2);
                else if (savedAnswer.equals(btn_choose3.getText().toString())) highlightButton(btn_choose3);
                else if (savedAnswer.equals(btn_choose4.getText().toString())) highlightButton(btn_choose4);
            }
        }
        else if (currentType == 1) { // Type 1 : Spinner
            layout_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choose_List[current_quest]);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_choices.setAdapter(adapter);
            
            if (savedAnswer != null) {
                int spinnerPosition = adapter.getPosition(savedAnswer);
                spinner_choices.setSelection(spinnerPosition);
            }
        }
        else if (currentType == 2) { // Type 2 : CheckBox
            layout_checkbox.setVisibility(View.VISIBLE);
            cb_1.setText(choose_List[current_quest][0]);
            cb_2.setText(choose_List[current_quest][1]);
            cb_3.setText(choose_List[current_quest][2]);
            
            cb_1.setChecked(false);
            cb_2.setChecked(false);
            cb_3.setChecked(false);

            if (savedAnswer != null) {
                if (savedAnswer.contains(cb_1.getText().toString())) cb_1.setChecked(true);
                if (savedAnswer.contains(cb_2.getText().toString())) cb_2.setChecked(true);
                if (savedAnswer.contains(cb_3.getText().toString())) cb_3.setChecked(true);
            }
        }
        else if (currentType == 3) { // Type 3 : SeekBar
            layout_seekbar.setVisibility(View.VISIBLE);
            int progress = 3;
            if (savedAnswer != null) {
                try { progress = Integer.parseInt(savedAnswer); } catch (Exception e) {}
            }
            seekbar_choices.setProgress(progress);
            seekbar_value_text.setText("Valeur: " + progress);
        }
    }

    // Gestion du clic sur l'un des 4 boutons de réponse (Type 0)
    public void ClickChoose(View view) {
        resetButtonsUI(); // On remet tous les boutons à la normale
        Button btn_click = (Button) view;
        highlightButton(btn_click);

        isclickbtn = true;
        valueChoose = btn_click.getText().toString(); // On capture le texte du bouton
    }
    
    private void highlightButton(Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#805F43C3")));
        btn.setTextColor(Color.WHITE);
    }

    // Remise à zéro de l'apparence des boutons (Fond blanc, texte violet)
    private void resetButtonsUI() {
        ColorStateList defaultTint = ColorStateList.valueOf(Color.WHITE);
        int defaultTextColor = Color.parseColor("#5F43C3");

        btn_choose1.setBackgroundTintList(defaultTint);
        btn_choose1.setTextColor(defaultTextColor);

        btn_choose2.setBackgroundTintList(defaultTint);
        btn_choose2.setTextColor(defaultTextColor);

        btn_choose3.setBackgroundTintList(defaultTint);
        btn_choose3.setTextColor(defaultTextColor);

        btn_choose4.setBackgroundTintList(defaultTint);
        btn_choose4.setTextColor(defaultTextColor);
    }
}