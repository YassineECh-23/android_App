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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    // --- 1. LES 10 QUESTIONS (SANTÉ MENTALE ÉTUDIANT) ---
    String[] question_List = {
            "1. En quelle année d'études êtes-vous ?",                                  // Type 4 (Radio)
            "2. Comment évaluez-vous votre niveau de stress global ces derniers temps ?", // Type 0 (Boutons)
            "3. Évaluez la qualité de votre sommeil (0=Insomnie, 5=Excellent)",           // Type 3 (SeekBar)
            "4. Combien d'heures travaillez-vous en dehors des cours par jour ?",         // Type 1 (Spinner)
            "5. Avez-vous ressenti un de ces symptômes récemment ?",                      // Type 2 (CheckBox)
            "6. Vous sentez-vous soutenu(e) par votre entourage (amis, famille) ?",       // Type 4 (Radio)
            "7. À quelle fréquence vous sentez-vous dépassé(e) par la charge de travail ?",// Type 0 (Boutons)
            "8. Évaluez votre motivation actuelle pour vos études (0=Nulle, 5=Maximale)", // Type 3 (SeekBar)
            "9. À quelle fréquence pratiquez-vous une activité de détente (sport, loisir) ?", // Type 1 (Spinner)
            "10. En cas de stress, vers quoi vous tournez-vous le plus ?"                 // Type 2 (CheckBox)
    };

    // 0 = Boutons, 1 = Spinner, 2 = CheckBox, 3 = SeekBar, 4 = RadioButton
    int[] questionTypes = {4, 0, 3, 1, 2, 4, 0, 3, 1, 2};

    String[][] choose_List = {
            {"Licence 1 / 2", "Licence 3", "Master 1 / 2", "Autre"}, // Q1 (Radio)
            {"Très serein(e)", "Légèrement stressé(e)", "Très stressé(e)", "Au bord de l'épuisement"}, // Q2 (Boutons)
            {"", "", "", ""}, // Q3 (SeekBar, pas besoin de texte)
            {"Moins d'1 heure", "1 à 2 heures", "2 à 4 heures", "Plus de 4 heures"}, // Q4 (Spinner)
            {"Fatigue intense", "Troubles de concentration", "Maux de tête/Ventre", "NON/Autre"}, // Q5 (CheckBox - 3 choix max dans notre UI)
            {"Toujours", "Souvent", "Rarement", "Jamais"}, // Q6 (Radio)
            {"Jamais", "Parfois", "Souvent", "Constamment"}, // Q7 (Boutons)
            {"", "", "", ""}, // Q8 (SeekBar)
            {"Jamais", "1 fois par mois", "1 à 2 fois par semaine", "Presque tous les jours"}, // Q9 (Spinner)
            {"Sport ou Sorties", "Écrans (Réseaux, Jeux)", "Isolement (Rester seul(e))", "Autre"} // Q10 (CheckBox)
    };

    String[] userAnswers = new String[10]; // Tableau de 10 réponses

    // --- 2. DÉCLARATION DES VUES ---
    TextView cpt_question, text_question, seekbar_value_text;
    Button btn_choose1, btn_choose2, btn_choose3, btn_choose4, btn_next;
    LinearLayout layout_buttons, layout_spinner, layout_checkbox, layout_seekbar, layout_radio;
    Spinner spinner_choices;
    CheckBox cb_1, cb_2, cb_3, cb_4;
    SeekBar seekbar_choices;
    RadioGroup rg_choices;
    RadioButton rb_1, rb_2, rb_3, rb_4;
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

        // --- 3. INITIALISATION ---
        cpt_question = findViewById(R.id.cpt_question);
        text_question = findViewById(R.id.text_question);

        layout_buttons = findViewById(R.id.layout_buttons);
        layout_spinner = findViewById(R.id.layout_spinner);
        layout_checkbox = findViewById(R.id.layout_checkbox);
        layout_seekbar = findViewById(R.id.layout_seekbar);
        layout_radio = findViewById(R.id.layout_radio); // Nouveau

        btn_choose1 = findViewById(R.id.btn_choose1);
        btn_choose2 = findViewById(R.id.btn_choose2);
        btn_choose3 = findViewById(R.id.btn_choose3);
        btn_choose4 = findViewById(R.id.btn_choose4);
        btn_next = findViewById(R.id.btn_next);

        spinner_choices = findViewById(R.id.spinner_choices);
        cb_1 = findViewById(R.id.cb_1);
        cb_2 = findViewById(R.id.cb_2);
        cb_3 = findViewById(R.id.cb_3);
        cb_4 = findViewById(R.id.cb_4);
        seekbar_choices = findViewById(R.id.seekbar_choices);
        seekbar_value_text = findViewById(R.id.seekbar_value_text);

        rg_choices = findViewById(R.id.rg_choices);
        rb_1 = findViewById(R.id.rb_1);
        rb_2 = findViewById(R.id.rb_2);
        rb_3 = findViewById(R.id.rb_3);
        rb_4 = findViewById(R.id.rb_4);

        image_back = findViewById(R.id.image_back);

        // --- 4. LISTENERS ---
        image_back.setOnClickListener(v -> {
            if (current_quest > 0) {
                current_quest--;
                isclickbtn = true;
                valueChoose = userAnswers[current_quest];
                remplirData();
            } else {
                finish();
            }
        });

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

        btn_next.setOnClickListener(view -> {
            int type = questionTypes[current_quest];

            if (type == 1) { // Spinner
                valueChoose = spinner_choices.getSelectedItem().toString();
                isclickbtn = true;
            } else if (type == 2) { // CheckBox
                valueChoose = "";
                if (cb_1.isChecked()) valueChoose += cb_1.getText() + " / ";
                if (cb_2.isChecked()) valueChoose += cb_2.getText() + " / ";
                if (cb_3.isChecked()) valueChoose += cb_3.getText() + " / ";
                if (cb_4.isChecked()) valueChoose += cb_4.getText() + " / ";

                if (valueChoose.endsWith(" / ")) valueChoose = valueChoose.substring(0, valueChoose.length() - 3);
                isclickbtn = !valueChoose.trim().isEmpty();
            } else if (type == 3) { // SeekBar
                valueChoose = String.valueOf(seekbar_choices.getProgress());
                isclickbtn = true;
            } else if (type == 4) { // RadioButton
                int selectedId = rg_choices.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadio = findViewById(selectedId);
                    valueChoose = selectedRadio.getText().toString();
                    isclickbtn = true;
                } else {
                    isclickbtn = false;
                }
            }

            if (isclickbtn) {
                userAnswers[current_quest] = valueChoose;

                if (current_quest < question_List.length - 1) {
                    current_quest++;

                    if (userAnswers[current_quest] != null) {
                        isclickbtn = true;
                        valueChoose = userAnswers[current_quest];
                    } else {
                        isclickbtn = false;
                        valueChoose = "";
                    }
                    remplirData();
                } else {
                    Intent intent = new Intent(playActivity.this, ResultActivity.class);
                    intent.putExtra("ANSWERS", userAnswers);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, "Veuillez choisir une réponse", Toast.LENGTH_SHORT).show();
            }
        });

        remplirData();
    }

    // --- 5. REMPLISSAGE ---
    void remplirData() {
        cpt_question.setText((current_quest + 1) + "/" + question_List.length);
        text_question.setText(question_List[current_quest]);

        int currentType = questionTypes[current_quest];
        String savedAnswer = userAnswers[current_quest];

        layout_buttons.setVisibility(View.GONE);
        layout_spinner.setVisibility(View.GONE);
        layout_checkbox.setVisibility(View.GONE);
        layout_seekbar.setVisibility(View.GONE);
        layout_radio.setVisibility(View.GONE); // Cache les radios par défaut

        resetButtonsUI();

        if (currentType == 0) {
            layout_buttons.setVisibility(View.VISIBLE);
            btn_choose1.setText(choose_List[current_quest][0]);
            btn_choose2.setText(choose_List[current_quest][1]);
            btn_choose3.setText(choose_List[current_quest][2]);
            btn_choose4.setText(choose_List[current_quest][3]);

            if (savedAnswer != null) {
                if (savedAnswer.equals(btn_choose1.getText().toString())) highlightButton(btn_choose1);
                else if (savedAnswer.equals(btn_choose2.getText().toString())) highlightButton(btn_choose2);
                else if (savedAnswer.equals(btn_choose3.getText().toString())) highlightButton(btn_choose3);
                else if (savedAnswer.equals(btn_choose4.getText().toString())) highlightButton(btn_choose4);
            }
        } else if (currentType == 1) {
            layout_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, choose_List[current_quest]);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_choices.setAdapter(adapter);

            if (savedAnswer != null) {
                int spinnerPosition = adapter.getPosition(savedAnswer);
                spinner_choices.setSelection(spinnerPosition);
            }
        } else if (currentType == 2) {
            layout_checkbox.setVisibility(View.VISIBLE);
            cb_1.setText(choose_List[current_quest][0]);
            cb_2.setText(choose_List[current_quest][1]);
            cb_3.setText(choose_List[current_quest][2]);
            cb_4.setText(choose_List[current_quest][3]);


            cb_1.setChecked(false);
            cb_2.setChecked(false);
            cb_3.setChecked(false);
            cb_4.setChecked(false);


            if (savedAnswer != null) {
                if (savedAnswer.contains(cb_1.getText().toString())) cb_1.setChecked(true);
                if (savedAnswer.contains(cb_2.getText().toString())) cb_2.setChecked(true);
                if (savedAnswer.contains(cb_3.getText().toString())) cb_3.setChecked(true);
                if (savedAnswer.contains(cb_4.getText().toString())) cb_4.setChecked(true);
            }
        } else if (currentType == 3) {
            layout_seekbar.setVisibility(View.VISIBLE);
            int progress = 3;
            if (savedAnswer != null && !savedAnswer.isEmpty()) {
                try { progress = Integer.parseInt(savedAnswer); } catch (Exception e) {}
            }
            seekbar_choices.setProgress(progress);
            seekbar_value_text.setText("Valeur: " + progress);
        } else if (currentType == 4) { // NOUVEAU : Type RadioButton
            layout_radio.setVisibility(View.VISIBLE);
            rg_choices.clearCheck(); // Décoche tout par défaut
            rb_1.setText(choose_List[current_quest][0]);
            rb_2.setText(choose_List[current_quest][1]);
            rb_3.setText(choose_List[current_quest][2]);
            rb_4.setText(choose_List[current_quest][3]);

            // Masque les boutons radio vides (au cas où il y a moins de 4 options)
            rb_1.setVisibility(rb_1.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
            rb_2.setVisibility(rb_2.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
            rb_3.setVisibility(rb_3.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
            rb_4.setVisibility(rb_4.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);

            if (savedAnswer != null) {
                if (savedAnswer.equals(rb_1.getText().toString())) rb_1.setChecked(true);
                else if (savedAnswer.equals(rb_2.getText().toString())) rb_2.setChecked(true);
                else if (savedAnswer.equals(rb_3.getText().toString())) rb_3.setChecked(true);
                else if (savedAnswer.equals(rb_4.getText().toString())) rb_4.setChecked(true);
            }
        }
    }

    public void ClickChoose(View view) {
        resetButtonsUI();
        Button btn_click = (Button) view;
        highlightButton(btn_click);

        isclickbtn = true;
        valueChoose = btn_click.getText().toString();
    }

    private void highlightButton(Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#805F43C3")));
        btn.setTextColor(Color.WHITE);
    }

    private void resetButtonsUI() {
        ColorStateList defaultTint = ColorStateList.valueOf(Color.WHITE);
        int defaultTextColor = Color.parseColor("#5F43C3");

        btn_choose1.setBackgroundTintList(defaultTint); btn_choose1.setTextColor(defaultTextColor);
        btn_choose2.setBackgroundTintList(defaultTint); btn_choose2.setTextColor(defaultTextColor);
        btn_choose3.setBackgroundTintList(defaultTint); btn_choose3.setTextColor(defaultTextColor);
        btn_choose4.setBackgroundTintList(defaultTint); btn_choose4.setTextColor(defaultTextColor);
    }
}