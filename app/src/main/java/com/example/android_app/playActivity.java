package com.example.android_app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    String[] question_List = {
            "1. En quelle année d'études êtes-vous ?",
            "2. Comment évaluez-vous votre niveau de stress global ces derniers temps ?",
            "3. Évaluez la qualité de votre sommeil (0=Insomnie, 5=Excellent)",
            "4. Combien d'heures travaillez-vous en dehors des cours par jour ?",
            "5. Avez-vous ressenti un de ces symptômes récemment ?",
            "6. Vous sentez-vous soutenu(e) par votre entourage (amis, famille) ?",
            "7. À quelle fréquence vous sentez-vous dépassé(e) par la charge de travail ?",
            "8. Évaluez votre motivation actuelle pour vos études (0=Nulle, 5=Maximale)",
            "9. À quelle fréquence pratiquez-vous une activité de détente (sport, loisir) ?",
            "10. En cas de stress, vers quoi vous tournez-vous le plus ?"
    };

    int[] questionTypes = {4, 0, 3, 1, 2, 4, 0, 3, 1, 2};

    String[][] choose_List = {
            {"Licence 1 / 2", "Licence 3", "Master 1 / 2", "Autre"},
            {"Très serein(e)", "Légèrement stressé(e)", "Très stressé(e)", "Au bord de l'épuisement"},
            {"", "", "", ""},
            {"Moins d'1 heure", "1 à 2 heures", "2 à 4 heures", "Plus de 4 heures"},
            // Q5 : cb_1..cb_4 = symptômes, cb_5 = NON (géré séparément)
            {"Fatigue intense", "Troubles de concentration", "Maux de tête/Ventre", "Autre"},
            {"Toujours", "Souvent", "Rarement", "Jamais"},
            {"Jamais", "Parfois", "Souvent", "Constamment"},
            {"", "", "", ""},
            {"Jamais", "1 fois par mois", "1 à 2 fois par semaine", "Presque tous les jours"},
            {"Sport ou Sorties", "Écrans (Réseaux, Jeux)", "Isolement (Rester seul(e))", "Autre"}
    };

    String[] userAnswers = new String[10];

    TextView cpt_question, text_question, seekbar_value_text;
    Button btn_choose1, btn_choose2, btn_choose3, btn_choose4, btn_next;
    LinearLayout layout_buttons, layout_spinner, layout_checkbox, layout_seekbar, layout_radio;
    Spinner spinner_choices;
    CheckBox cb_1, cb_2, cb_3, cb_4, cb_5;
    View divider_cb;
    SeekBar seekbar_choices;
    RadioGroup rg_choices;
    RadioButton rb_1, rb_2, rb_3, rb_4;
    ImageView image_back;

    int current_quest = 0;
    boolean isclickbtn = false;
    String valueChoose = "";
    private boolean isUpdatingCheckboxes = false;

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

        cpt_question       = findViewById(R.id.cpt_question);
        text_question      = findViewById(R.id.text_question);
        layout_buttons     = findViewById(R.id.layout_buttons);
        layout_spinner     = findViewById(R.id.layout_spinner);
        layout_checkbox    = findViewById(R.id.layout_checkbox);
        layout_seekbar     = findViewById(R.id.layout_seekbar);
        layout_radio       = findViewById(R.id.layout_radio);
        btn_choose1        = findViewById(R.id.btn_choose1);
        btn_choose2        = findViewById(R.id.btn_choose2);
        btn_choose3        = findViewById(R.id.btn_choose3);
        btn_choose4        = findViewById(R.id.btn_choose4);
        btn_next           = findViewById(R.id.btn_next);
        spinner_choices    = findViewById(R.id.spinner_choices);
        cb_1               = findViewById(R.id.cb_1);
        cb_2               = findViewById(R.id.cb_2);
        cb_3               = findViewById(R.id.cb_3);
        cb_4               = findViewById(R.id.cb_4);
        cb_5               = findViewById(R.id.cb_5);
        divider_cb         = findViewById(R.id.divider_cb);
        seekbar_choices    = findViewById(R.id.seekbar_choices);
        seekbar_value_text = findViewById(R.id.seekbar_value_text);
        rg_choices         = findViewById(R.id.rg_choices);
        rb_1               = findViewById(R.id.rb_1);
        rb_2               = findViewById(R.id.rb_2);
        rb_3               = findViewById(R.id.rb_3);
        rb_4               = findViewById(R.id.rb_4);
        image_back         = findViewById(R.id.image_back);

        setupQ5Listeners();

        image_back.setOnClickListener(v -> {
            if (current_quest > 0) {
                current_quest--;
                isclickbtn  = true;
                valueChoose = userAnswers[current_quest];
                remplirData();
            } else {
                finish();
            }
        });

        seekbar_choices.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean f) {
                seekbar_value_text.setText("Valeur: " + p);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        btn_next.setOnClickListener(view -> {
            int type = questionTypes[current_quest];

            if (type == 1) {
                valueChoose = spinner_choices.getSelectedItem().toString();
                isclickbtn  = true;

            } else if (type == 2) {
                valueChoose = "";
                if (current_quest == 4) { // Q5
                    if (cb_5.isChecked()) {
                        valueChoose = "NON";
                    } else {
                        if (cb_1.isChecked()) valueChoose += cb_1.getText() + " / ";
                        if (cb_2.isChecked()) valueChoose += cb_2.getText() + " / ";
                        if (cb_3.isChecked()) valueChoose += cb_3.getText() + " / ";
                        if (cb_4.isChecked()) valueChoose += cb_4.getText() + " / ";
                        if (valueChoose.endsWith(" / "))
                            valueChoose = valueChoose.substring(0, valueChoose.length() - 3);
                    }
                } else { // Q10
                    if (cb_1.isChecked()) valueChoose += cb_1.getText() + " / ";
                    if (cb_2.isChecked()) valueChoose += cb_2.getText() + " / ";
                    if (cb_3.isChecked()) valueChoose += cb_3.getText() + " / ";
                    if (cb_4.isChecked()) valueChoose += cb_4.getText() + " / ";
                    if (valueChoose.endsWith(" / "))
                        valueChoose = valueChoose.substring(0, valueChoose.length() - 3);
                }
                isclickbtn = !valueChoose.trim().isEmpty();

            } else if (type == 3) {
                valueChoose = String.valueOf(seekbar_choices.getProgress());
                isclickbtn  = true;

            } else if (type == 4) {
                int selectedId = rg_choices.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    valueChoose = ((RadioButton) findViewById(selectedId)).getText().toString();
                    isclickbtn  = true;
                } else {
                    isclickbtn = false;
                }
            }

            if (isclickbtn) {
                userAnswers[current_quest] = valueChoose;
                if (current_quest < question_List.length - 1) {
                    current_quest++;
                    if (userAnswers[current_quest] != null) {
                        isclickbtn  = true;
                        valueChoose = userAnswers[current_quest];
                    } else {
                        isclickbtn  = false;
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

    private void setupQ5Listeners() {
        // NON coché → désactive et décoche tous les symptômes
        cb_5.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isUpdatingCheckboxes) return;
            isUpdatingCheckboxes = true;
            if (isChecked) {
                cb_1.setChecked(false); cb_1.setEnabled(false);
                cb_2.setChecked(false); cb_2.setEnabled(false);
                cb_3.setChecked(false); cb_3.setEnabled(false);
                cb_4.setChecked(false); cb_4.setEnabled(false);
            } else {
                cb_1.setEnabled(true); cb_2.setEnabled(true);
                cb_3.setEnabled(true); cb_4.setEnabled(true);
            }
            isUpdatingCheckboxes = false;
        });

        // Un symptôme coché → décoche et désactive NON
        CompoundButton.OnCheckedChangeListener symptomListener = (btn, isChecked) -> {
            if (isUpdatingCheckboxes) return;
            isUpdatingCheckboxes = true;
            if (isChecked) {
                cb_5.setChecked(false);
                cb_5.setEnabled(false);
            } else {
                // Réactive NON seulement si aucun symptôme n'est coché
                if (!cb_1.isChecked() && !cb_2.isChecked()
                        && !cb_3.isChecked() && !cb_4.isChecked()) {
                    cb_5.setEnabled(true);
                }
            }
            isUpdatingCheckboxes = false;
        };

        cb_1.setOnCheckedChangeListener(symptomListener);
        cb_2.setOnCheckedChangeListener(symptomListener);
        cb_3.setOnCheckedChangeListener(symptomListener);
        cb_4.setOnCheckedChangeListener(symptomListener);
    }

    void remplirData() {
        cpt_question.setText((current_quest + 1) + "/" + question_List.length);
        text_question.setText(question_List[current_quest]);

        int currentType    = questionTypes[current_quest];
        String savedAnswer = userAnswers[current_quest];

        layout_buttons.setVisibility(View.GONE);
        layout_spinner.setVisibility(View.GONE);
        layout_checkbox.setVisibility(View.GONE);
        layout_seekbar.setVisibility(View.GONE);
        layout_radio.setVisibility(View.GONE);
        resetButtonsUI();

        if (currentType == 0) {
            layout_buttons.setVisibility(View.VISIBLE);
            btn_choose1.setText(choose_List[current_quest][0]);
            btn_choose2.setText(choose_List[current_quest][1]);
            btn_choose3.setText(choose_List[current_quest][2]);
            btn_choose4.setText(choose_List[current_quest][3]);
            if (savedAnswer != null) {
                if (savedAnswer.equals(btn_choose1.getText().toString()))      highlightButton(btn_choose1);
                else if (savedAnswer.equals(btn_choose2.getText().toString())) highlightButton(btn_choose2);
                else if (savedAnswer.equals(btn_choose3.getText().toString())) highlightButton(btn_choose3);
                else if (savedAnswer.equals(btn_choose4.getText().toString())) highlightButton(btn_choose4);
            }

        } else if (currentType == 1) {
            layout_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, choose_List[current_quest]);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_choices.setAdapter(adapter);
            if (savedAnswer != null) spinner_choices.setSelection(adapter.getPosition(savedAnswer));

        } else if (currentType == 2) {
            layout_checkbox.setVisibility(View.VISIBLE);
            cb_1.setText(choose_List[current_quest][0]);
            cb_2.setText(choose_List[current_quest][1]);
            cb_3.setText(choose_List[current_quest][2]);
            cb_4.setText(choose_List[current_quest][3]);

            // Afficher cb_5 et le séparateur seulement pour Q5
            boolean isQ5 = (current_quest == 4);
            cb_5.setVisibility(isQ5 ? View.VISIBLE : View.GONE);
            divider_cb.setVisibility(isQ5 ? View.VISIBLE : View.GONE);

            // Reset complet sans déclencher les listeners
            isUpdatingCheckboxes = true;
            cb_1.setChecked(false); cb_1.setEnabled(true);
            cb_2.setChecked(false); cb_2.setEnabled(true);
            cb_3.setChecked(false); cb_3.setEnabled(true);
            cb_4.setChecked(false); cb_4.setEnabled(true);
            cb_5.setChecked(false); cb_5.setEnabled(true);
            isUpdatingCheckboxes = false;

            // Restaurer la réponse sauvegardée
            if (savedAnswer != null) {
                if (isQ5 && savedAnswer.equals("NON")) {
                    isUpdatingCheckboxes = true;
                    cb_5.setChecked(true);
                    cb_1.setEnabled(false); cb_2.setEnabled(false);
                    cb_3.setEnabled(false); cb_4.setEnabled(false);
                    isUpdatingCheckboxes = false;
                } else {
                    if (savedAnswer.contains(cb_1.getText().toString())) cb_1.setChecked(true);
                    if (savedAnswer.contains(cb_2.getText().toString())) cb_2.setChecked(true);
                    if (savedAnswer.contains(cb_3.getText().toString())) cb_3.setChecked(true);
                    if (savedAnswer.contains(cb_4.getText().toString())) cb_4.setChecked(true);
                    if (isQ5 && (cb_1.isChecked() || cb_2.isChecked()
                            || cb_3.isChecked() || cb_4.isChecked())) {
                        isUpdatingCheckboxes = true;
                        cb_5.setEnabled(false);
                        isUpdatingCheckboxes = false;
                    }
                }
            }

        } else if (currentType == 3) {
            layout_seekbar.setVisibility(View.VISIBLE);
            int progress = 3;
            if (savedAnswer != null && !savedAnswer.isEmpty()) {
                try { progress = Integer.parseInt(savedAnswer); } catch (Exception ignored) {}
            }
            seekbar_choices.setProgress(progress);
            seekbar_value_text.setText("Valeur: " + progress);

        } else if (currentType == 4) {
            layout_radio.setVisibility(View.VISIBLE);
            rg_choices.clearCheck();
            rb_1.setText(choose_List[current_quest][0]);
            rb_2.setText(choose_List[current_quest][1]);
            rb_3.setText(choose_List[current_quest][2]);
            rb_4.setText(choose_List[current_quest][3]);
            rb_1.setVisibility(rb_1.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
            rb_2.setVisibility(rb_2.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
            rb_3.setVisibility(rb_3.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
            rb_4.setVisibility(rb_4.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
            if (savedAnswer != null) {
                if (savedAnswer.equals(rb_1.getText().toString()))      rb_1.setChecked(true);
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
        isclickbtn  = true;
        valueChoose = btn_click.getText().toString();
    }

    private void highlightButton(Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#805F43C3")));
        btn.setTextColor(Color.WHITE);
    }

    private void resetButtonsUI() {
        ColorStateList defaultTint  = ColorStateList.valueOf(Color.WHITE);
        int defaultTextColor = Color.parseColor("#5F43C3");
        btn_choose1.setBackgroundTintList(defaultTint); btn_choose1.setTextColor(defaultTextColor);
        btn_choose2.setBackgroundTintList(defaultTint); btn_choose2.setTextColor(defaultTextColor);
        btn_choose3.setBackgroundTintList(defaultTint); btn_choose3.setTextColor(defaultTextColor);
        btn_choose4.setBackgroundTintList(defaultTint); btn_choose4.setTextColor(defaultTextColor);
    }
}