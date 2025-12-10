package uqac.dim.transportbus;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class EditTripActivity extends AppCompatActivity {

    private Spinner tripSpinner;
    private EditText newDepartureEditText, newDestinationEditText, newDateEditText, newTimeEditText;
    private Button updateButton;
    private DatabaseReference tripsDatabase;
    private ArrayList<String> tripIds = new ArrayList<>(); // Liste des IDs des trajets
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        // Initialisation des composants de l'interface utilisateur
        tripSpinner = findViewById(R.id.tripSpinner);
        newDepartureEditText = findViewById(R.id.newDepartureEditText);
        newDestinationEditText = findViewById(R.id.newDestinationEditText);
        newDateEditText = findViewById(R.id.newDateEditText);
        newTimeEditText = findViewById(R.id.newTimeEditText);
        updateButton = findViewById(R.id.updateTripButton);

        // Référence à la base de données Firebase
        tripsDatabase = FirebaseDatabase.getInstance().getReference("trips");

        // Charger les trajets disponibles
        loadTrips();

        // Action du bouton de mise à jour
        updateButton.setOnClickListener(v -> {
            int selectedPosition = tripSpinner.getSelectedItemPosition();
            if (selectedPosition < 0 || selectedPosition >= tripIds.size()) {
                Toast.makeText(EditTripActivity.this, "Veuillez sélectionner un trajet", Toast.LENGTH_SHORT).show();
                return;
            }

            // Récupérer l'ID du trajet sélectionné
            String selectedTripId = tripIds.get(selectedPosition);
            String newDeparture = newDepartureEditText.getText().toString().trim();
            String newDestination = newDestinationEditText.getText().toString().trim();
            String newDate = newDateEditText.getText().toString().trim();
            String newTime = newTimeEditText.getText().toString().trim();

            // Vérifier si les champs sont remplis
            if (newDeparture.isEmpty() || newDestination.isEmpty() || newDate.isEmpty() || newTime.isEmpty()) {
                Toast.makeText(EditTripActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                // Mettre à jour le trajet dans Firebase
                try {
                    tripsDatabase.child(selectedTripId).child("departure").setValue(newDeparture);
                    tripsDatabase.child(selectedTripId).child("destination").setValue(newDestination);
                    tripsDatabase.child(selectedTripId).child("date").setValue(newDate);
                    tripsDatabase.child(selectedTripId).child("time").setValue(newTime);

                    Toast.makeText(EditTripActivity.this, "Trajet mis à jour avec succès", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(EditTripActivity.this, "Erreur de mise à jour: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Méthode pour charger les trajets depuis Firebase
    private void loadTrips() {
        tripsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> tripsList = new ArrayList<>();
                tripIds.clear(); // Réinitialiser les IDs des trajets

                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = tripSnapshot.getValue(Trip.class); // Récupérer les données du trajet
                    if (trip != null) {
                        String tripDetails = trip.getDeparture() + " -> " + trip.getDestination();
                        tripsList.add(tripDetails); // Ajouter les détails du trajet à la liste
                        tripIds.add(tripSnapshot.getKey()); // Ajouter l'ID du trajet à la liste des IDs
                    }
                }

                // Afficher les trajets dans le Spinner ou un message si aucun trajet n'est trouvé
                if (tripsList.isEmpty()) {
                    Toast.makeText(EditTripActivity.this, "Aucun trajet trouvé", Toast.LENGTH_SHORT).show();
                } else {
                    // Adapter pour afficher les trajets dans le Spinner
                    adapter = new ArrayAdapter<>(EditTripActivity.this, android.R.layout.simple_spinner_item, tripsList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tripSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // En cas d'erreur lors de la lecture de Firebase
                Toast.makeText(EditTripActivity.this, "Erreur lors du chargement des trajets", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Erreur lors du chargement des trajets", databaseError.toException());
            }
        });
    }
}
