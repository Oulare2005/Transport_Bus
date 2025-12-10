package uqac.dim.transportbus;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class ClientDashboardActivity extends AppCompatActivity {

    private ListView tripsListView;
    private FirebaseFirestore firestore;
    private ArrayList<String> tripIds = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);

        tripsListView = findViewById(R.id.tripsListView);
        firestore = FirebaseFirestore.getInstance();

        loadAvailableTrips();
    }

    private void loadAvailableTrips() {
        firestore.collection("trips")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> tripList = new ArrayList<>();
                        tripIds.clear(); // Efface la liste des IDs existants

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String departure = document.getString("departure");
                            String destination = document.getString("destination");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String clientId = document.getString("clientId");

                            // Charger les informations du client
                            loadClientDetails(clientId, clientDetails -> {
                                String tripDetails = departure + " -> " + destination + " | " + date + " à " + time
                                        + "\nClient : " + clientDetails;
                                tripList.add(tripDetails);
                                tripIds.add(document.getId()); // Stocke l'ID du document pour référence

                                // Mettre à jour la ListView après avoir chargé toutes les données
                                if (!tripList.isEmpty()) {
                                    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tripList);
                                    tripsListView.setAdapter(adapter);
                                }
                            });
                        }

                        tripsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String selectedTrip = adapter.getItem(position);
                                String tripId = tripIds.get(position);
                                showPaymentDialog(selectedTrip, tripId);
                            }
                        });
                    } else {
                        Toast.makeText(ClientDashboardActivity.this, "Erreur lors du chargement des trajets", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadClientDetails(String clientId, OnClientDetailsLoaded listener) {
        if (clientId == null || clientId.isEmpty()) {
            listener.onLoaded("Inconnu");
            return;
        }

        firestore.collection("clients").document(clientId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nom = documentSnapshot.getString("nom");
                        String prenom = documentSnapshot.getString("prenom");
                        listener.onLoaded(prenom + " " + nom);
                    } else {
                        listener.onLoaded("Inconnu");
                    }
                })
                .addOnFailureListener(e -> listener.onLoaded("Erreur"));
    }

    private void showPaymentDialog(String tripDetails, String tripId) {
        Toast.makeText(this, "Paiement en cours pour : " + tripDetails, Toast.LENGTH_SHORT).show();

        // Simuler un paiement
        firestore.collection("transactions")
                .add(new Transaction(tripId, "ClientID", true)) // Simule une transaction réussie
                .addOnSuccessListener(documentReference -> Toast.makeText(ClientDashboardActivity.this, "Paiement effectué avec succès", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ClientDashboardActivity.this, "Erreur de paiement", Toast.LENGTH_SHORT).show());
    }

    public static class Transaction {
        public String tripId;
        public String clientId;
        public boolean status;

        public Transaction(String tripId, String clientId, boolean status) {
            this.tripId = tripId;
            this.clientId = clientId;
            this.status = status;
        }
    }

    interface OnClientDetailsLoaded {
        void onLoaded(String clientDetails);
    }
}
