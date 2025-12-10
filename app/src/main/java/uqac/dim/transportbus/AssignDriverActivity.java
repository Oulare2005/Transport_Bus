package uqac.dim.transportbus;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssignDriverActivity extends AppCompatActivity {

    private Spinner driverSpinner, tripSpinner;
    private Button assignButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_driver);

        driverSpinner = findViewById(R.id.driverSpinner);
        tripSpinner = findViewById(R.id.tripSpinner);
        assignButton = findViewById(R.id.assignDriverButton);

        firestore = FirebaseFirestore.getInstance();

        loadDrivers();
        loadTrips();

        assignButton.setOnClickListener(v -> {
            String selectedDriver = driverSpinner.getSelectedItem() != null ? driverSpinner.getSelectedItem().toString() : null;
            String selectedTrip = tripSpinner.getSelectedItem() != null ? tripSpinner.getSelectedItem().toString() : null;

            if (selectedDriver == null || selectedTrip == null) {
                Toast.makeText(AssignDriverActivity.this, "Veuillez sélectionner un chauffeur et un trajet", Toast.LENGTH_SHORT).show();
                return;
            }

            assignDriverToTrip(selectedDriver, selectedTrip);
        });
    }

    // Charger la liste des chauffeurs
    private void loadDrivers() {
        firestore.collection("users")
                .whereEqualTo("role", "Driver")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<String> driverList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fullName = document.getString("firstName") + " " + document.getString("lastName");
                            driverList.add(fullName);
                        }

                        ArrayAdapter<String> driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, driverList);
                        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        driverSpinner.setAdapter(driverAdapter);
                    } else {
                        Toast.makeText(AssignDriverActivity.this, "Erreur lors du chargement des chauffeurs", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Charger la liste des trajets
    private void loadTrips() {
        firestore.collection("trips")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<String> tripList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String tripDetails = document.getString("departure") + " -> " + document.getString("destination") + " | " + document.getString("date");
                            tripList.add(tripDetails);
                        }

                        ArrayAdapter<String> tripAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tripList);
                        tripAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        tripSpinner.setAdapter(tripAdapter);
                    } else {
                        Toast.makeText(AssignDriverActivity.this, "Erreur lors du chargement des trajets", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Assigner un chauffeur à un trajet
    private void assignDriverToTrip(String driverName, String tripDetails) {
        String driverId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (driverId == null) {
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> assignment = new HashMap<>();
        assignment.put("driverId", driverId);  // Assurez-vous d'ajouter le driverId ici
        assignment.put("driverName", driverName);
        assignment.put("tripDetails", tripDetails);

        firestore.collection("assignments")
                .add(assignment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Trajet assigné avec succès", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors de l'assignation", Toast.LENGTH_SHORT).show());
    }
}
