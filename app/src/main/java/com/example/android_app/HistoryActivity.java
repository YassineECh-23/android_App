package com.example.android_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
                                recyclerView.setAdapter(new HistoryAdapter(results));
                            }
                        },
                        throwable -> {
                            text_empty.setText("Erreur lors du chargement.");
                            text_empty.setVisibility(View.VISIBLE);
                        }
                );
    }

    static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
        private final List<QuizResult> results;

        HistoryAdapter(List<QuizResult> results) { this.results = results; }

        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, int position) {
            holder.bind(results.get(position), position + 1);
        }

        @Override
        public int getItemCount() { return results.size(); }

        static class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView text_session_number, text_date, text_answers;

            HistoryViewHolder(View itemView) {
                super(itemView);
                text_session_number = itemView.findViewById(R.id.text_session_number);
                text_date = itemView.findViewById(R.id.text_date);
                text_answers = itemView.findViewById(R.id.text_answers);
            }

            void bind(QuizResult result, int num) {
                text_session_number.setText("Session #" + num);
                text_date.setText(result.date);
                String[] answers = result.answersToArray();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < answers.length; i++)
                    sb.append("Q").append(i + 1).append(" : ").append(answers[i]).append("\n");
                text_answers.setText(sb.toString().trim());
            }
        }
    }
}