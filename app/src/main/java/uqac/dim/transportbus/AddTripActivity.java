package uqac.dim.transportbus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddTripActivity extends AppCompatActivity {

    private EditText departureEditText, destinationEditText, dateEditText, timeEditText;
    private Button addButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        departureEditText = findViewById(R.id.departureEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        addButton = findViewById(R.id.addTripButton);

        firestore = FirebaseFirestore.getInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String departure = departureEditText.getText().toString().trim();
                String destination = destinationEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();
                String time = timeEditText.getText().toString().trim();

                if (departure.isEmpty() || destination.isEmpty() || date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(AddTripActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> trip = new HashMap<>();
                    trip.put("departure", departure);
                    trip.put("destination", destination);
                    trip.put("date", date);
                    trip.put("time", time);

                    firestore.collection("trips")
                            .add(trip)
                            .addOnSuccessListener(documentReference -> Toast.makeText(AddTripActivity.this, "Trajet ajouté avec succès", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(AddTripActivity.this, "Erreur lors de l'ajout du trajet", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
