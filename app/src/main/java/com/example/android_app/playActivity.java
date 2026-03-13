package com.example.android_app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

    // 1. Nos données
    String[] question_List = {
            "Question 1 (Boutons)",
            "Question 2 (Spinner)",
            "Question 3 (CheckBox)",
            "Question 4 (SeekBar)",
            "Question 5 (Boutons)"
    };

    // 0 = Boutons, 1 = Spinner, 2 = CheckBox, 3 = SeekBar
    int[] questionTypes = {0, 1, 2, 3, 0};

    String[][] choose_List = {
            {"Choose 1", "Choose 2", "Choose 3", "Choose 4"}, // Q1
            {"Option A", "Option B", "Option C", "Option D", "Option E"}, // Q2
            {"Check 1", "Check 2", "Check 3", ""}, // Q3 (Seulement 3 cases)
            {"", "", "", ""}, // Q4 (Pas besoin de texte pour la seekbar)
            {"Choose 1", "Choose 2", "Choose 3", "Choose 4"}  // Q5
    };

    String[] userAnswers = new String[5];

    // 2. Déclaration des Vues
    TextView cpt_question, text_question, seekbar_value_text;
    Button btn_choose1, btn_choose2, btn_choose3, btn_choose4, btn_next;
    LinearLayout layout_buttons, layout_spinner, layout_checkbox, layout_seekbar;
    Spinner spinner_choices;
    CheckBox cb_1, cb_2, cb_3;
    SeekBar seekbar_choices;

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

        // 3. Initialisation des Vues
        cpt_question = findViewById(R.id.cpt_question);
        text_question = findViewById(R.id.text_question);

        // Layouts
        layout_buttons = findViewById(R.id.layout_buttons);
        layout_spinner = findViewById(R.id.layout_spinner);
        layout_checkbox = findViewById(R.id.layout_checkbox);
        layout_seekbar = findViewById(R.id.layout_seekbar);

        // Composants des layouts
        btn_choose1 = findViewById(R.id.btn_choose1);
        btn_choose2 = findViewById(R.id.btn_choose2);
        btn_choose3 = findViewById(R.id.btn_choose3);
        btn_choose4 = findViewById(R.id.btn_choose4);

        spinner_choices = findViewById(R.id.spinner_choices);

        cb_1 = findViewById(R.id.cb_1);
        cb_2 = findViewById(R.id.cb_2);
        cb_3 = findViewById(R.id.cb_3);

        seekbar_choices = findViewById(R.id.seekbar_choices);
        seekbar_value_text = findViewById(R.id.seekbar_value_text);

        btn_next = findViewById(R.id.btn_next);

        // Gestionnaire pour afficher la valeur de la SeekBar en temps réel
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

        // 4. Logique du bouton "Next"
        btn_next.setOnClickListener(view -> {

            // Récupération de la valeur selon le type de question
            int type = questionTypes[current_quest];

            if (type == 1) { // Spinner
                valueChoose = spinner_choices.getSelectedItem().toString();
                isclickbtn = true;
            }
            else if (type == 2) { // CheckBox (Concatène les choix séparés par une virgule)
                valueChoose = "";
                if(cb_1.isChecked()) valueChoose += cb_1.getText() + ", ";
                if(cb_2.isChecked()) valueChoose += cb_2.getText() + ", ";
                if(cb_3.isChecked()) valueChoose += cb_3.getText() + ", ";

                // On vérifie si au moins une case est cochée
                isclickbtn = !valueChoose.isEmpty();
            }
            else if (type == 3) { // SeekBar
                valueChoose = String.valueOf(seekbar_choices.getProgress());
                isclickbtn = true; // Une seekbar a toujours une valeur
            }

            if (isclickbtn) {
                userAnswers[current_quest] = valueChoose;

                if (current_quest < question_List.length - 1) {
                    current_quest++;
                    isclickbtn = false;
                    valueChoose = "";
                    resetButtonsUI();

                    // On décoche les checkbox pour la prochaine fois
                    cb_1.setChecked(false); cb_2.setChecked(false); cb_3.setChecked(false);

                    remplirData();
                } else {
                    Toast.makeText(this, "Test terminé !", Toast.LENGTH_SHORT).show();
                    // Pour débugger, vous pouvez afficher toutes les réponses :
                    // System.out.println(Arrays.toString(userAnswers));
                }
            } else {
                Toast.makeText(this, "Veuillez choisir une réponse", Toast.LENGTH_SHORT).show();
            }
        });

        remplirData();
    }

    // 5. Remplissage et gestion de la visibilité
    void remplirData() {
        cpt_question.setText((current_quest + 1) + "/" + question_List.length);
        text_question.setText(question_List[current_quest]);

        int currentType = questionTypes[current_quest];

        // On cache tout d'abord
        layout_buttons.setVisibility(View.GONE);
        layout_spinner.setVisibility(View.GONE);
        layout_checkbox.setVisibility(View.GONE);
        layout_seekbar.setVisibility(View.GONE);

        // On affiche uniquement le bon layout
        if (currentType == 0) { // Boutons
            layout_buttons.setVisibility(View.VISIBLE);
            btn_choose1.setText(choose_List[current_quest][0]);
            btn_choose2.setText(choose_List[current_quest][1]);
            btn_choose3.setText(choose_List[current_quest][2]);
            btn_choose4.setText(choose_List[current_quest][3]);
        }
        else if (currentType == 1) { // Spinner
            layout_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choose_List[current_quest]);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_choices.setAdapter(adapter);
        }
        else if (currentType == 2) { // CheckBox
            layout_checkbox.setVisibility(View.VISIBLE);
            cb_1.setText(choose_List[current_quest][0]);
            cb_2.setText(choose_List[current_quest][1]);
            cb_3.setText(choose_List[current_quest][2]);
        }
        else if (currentType == 3) { // SeekBar
            layout_seekbar.setVisibility(View.VISIBLE);
            // On remet la seekbar à sa valeur par défaut (ex: milieu)
            seekbar_choices.setProgress(3);
            seekbar_value_text.setText("Valeur: 3");
        }
    }

    // 6. Gestion du clic sur un des 4 boutons
    public void ClickChoose(View view) {
        // Enlève le "if (!isclickbtn)" si vous voulez permettre à l'utilisateur de changer d'avis (recommandé)
        resetButtonsUI();
        Button btn_click = (Button) view;

        // Change la couleur du fond du bouton cliqué en violet clair et le texte en blanc
        btn_click.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#805F43C3")));
        btn_click.setTextColor(Color.WHITE);

        isclickbtn = true;
        valueChoose = btn_click.getText().toString();
    }

    // 7. Remise à zéro du design des boutons
    private void resetButtonsUI() {
        // Remet la couleur par défaut (Fond blanc, texte violet)
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