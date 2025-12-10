package uqac.dim.transportbus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class DriverDashboardActivity extends AppCompatActivity {

    private ListView assignedTripsListView;
    private TextView noTripsMessage;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        assignedTripsListView = findViewById(R.id.assignedTripsListView);
        noTripsMessage = findViewById(R.id.noTripsMessage);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        loadAssignedTrips();
    }

    private void loadAssignedTrips() {
        String driverId = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;

        if (driverId == null) {
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("assignments")
                .whereEqualTo("driverId", driverId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<String> tripsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String tripDetails = document.getString("tripDetails");
                            if (tripDetails != null) {
                                tripsList.add(tripDetails);
                            }
                        }

                        if (tripsList.isEmpty()) {
                            noTripsMessage.setVisibility(View.VISIBLE);
                            assignedTripsListView.setVisibility(View.GONE);
                        } else {
                            noTripsMessage.setVisibility(View.GONE);
                            assignedTripsListView.setVisibility(View.VISIBLE);

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tripsList);
                            assignedTripsListView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(this, "Erreur lors du chargement des trajets", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DriverDashboard", "Erreur de connexion à Firestore", e);
                    Toast.makeText(this, "Erreur de connexion à Firestore", Toast.LENGTH_SHORT).show();
                });
    }
}
