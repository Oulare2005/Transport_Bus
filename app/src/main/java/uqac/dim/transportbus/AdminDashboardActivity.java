package uqac.dim.transportbus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button addTripButton, editTripButton, assignDriverButton, deleteTripButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        addTripButton = findViewById(R.id.addTripButton);
        editTripButton = findViewById(R.id.editTripButton);
        assignDriverButton = findViewById(R.id.assignDriverButton);
        deleteTripButton = findViewById(R.id.deleteTripButton); // Nouveau bouton

        addTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AddTripActivity.class);
            startActivity(intent);
        });

        editTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, EditTripActivity.class);
            startActivity(intent);
        });

        assignDriverButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AssignDriverActivity.class);
            startActivity(intent);
        });

        deleteTripButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, DeleteTripActivity.class);
            startActivity(intent);
        });
    }
}
