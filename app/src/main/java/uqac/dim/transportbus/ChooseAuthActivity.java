package uqac.dim.transportbus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseAuthActivity extends AppCompatActivity {

    private Button emailAuthButton, phoneAuthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_auth);

        emailAuthButton = findViewById(R.id.emailAuthButton);
        phoneAuthButton = findViewById(R.id.phoneAuthButton);

        emailAuthButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        phoneAuthButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseAuthActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        });
    }
}
