package uqac.dim.transportbus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteTripActivity extends AppCompatActivity {

    private EditText tripIdEditText;
    private Button deleteTripButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_trip);

        tripIdEditText = findViewById(R.id.tripIdEditText);
        deleteTripButton = findViewById(R.id.deleteTripButton);
        firestore = FirebaseFirestore.getInstance();

        deleteTripButton.setOnClickListener(v -> {
            String tripId = tripIdEditText.getText().toString();

            if (tripId.isEmpty()) {
                Toast.makeText(DeleteTripActivity.this, "Veuillez entrer l'ID du trajet à supprimer", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("trips").document(tripId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DeleteTripActivity.this, "Trajet supprimé avec succès", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DeleteTripActivity.this, "Erreur lors de la suppression : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
