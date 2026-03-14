package com.example.android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners programmatically for better reliability
        findViewById(R.id.btn_play).setOnClickListener(v -> 
                startActivity(new Intent(MainActivity.this, playActivity.class)));

        findViewById(R.id.btn_history).setOnClickListener(v -> 
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));

        findViewById(R.id.btn_exit).setOnClickListener(v -> 
                finishAffinity());
    }

    /**
     * @deprecated Use programmatic listeners in onCreate instead.
     */
    public void main_btn(View view) {
        // This method is kept for backward compatibility if XML still references it,
        // but programmatic listeners will take precedence.
    }
}