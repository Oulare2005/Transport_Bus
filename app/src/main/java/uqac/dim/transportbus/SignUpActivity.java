package uqac.dim.transportbus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signUpButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialisation des vues
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);

        // Initialisation de Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Gestion du clic sur le bouton
        signUpButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            checkEmailAndSignUp(email, password);
        });
    }

    private void checkEmailAndSignUp(String email, String password) {
        // Vérifier si l'adresse e-mail existe déjà
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean emailExists = !task.getResult().getSignInMethods().isEmpty();
                        if (emailExists) {
                            Toast.makeText(SignUpActivity.this, "Cet e-mail est déjà utilisé", Toast.LENGTH_SHORT).show();
                        } else {
                            // Si l'e-mail n'existe pas, créer un nouvel utilisateur
                            createUser(email, password);
                        }
                    } else {
                        Log.e("SignUpActivity", "Erreur lors de la vérification de l'email", task.getException());
                        Toast.makeText(SignUpActivity.this, "Erreur lors de la vérification de l'email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Ajouter le rôle à la base de données Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            userMap.put("role", "Client"); // Le rôle peut être "Admin", "Driver", ou "Client"

                            db.collection("users").document(user.getUid())
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUpActivity.this, "Erreur d'inscription", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Gestion des erreurs
                        Toast.makeText(SignUpActivity.this, "Erreur d'inscription", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
