package com.iamlaky.emergency119.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.iamlaky.emergency119.R;

public class SubscribtionActivity extends AppCompatActivity {

    AppCompatButton btn_activate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscribtion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_activate = findViewById(R.id.btn_activate);

        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubscribtionActivity.this, PaymentSuccessActivity.class);

                intent.putExtra("amount", "$25.00");
                intent.putExtra("ref_num", "#SLI-88954210");
                intent.putExtra("merchant", "SafeLife Insurance");

                startActivity(intent);
                finish();
            }
        });
    }
}