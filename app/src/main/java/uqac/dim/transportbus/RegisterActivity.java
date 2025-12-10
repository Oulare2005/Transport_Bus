package uqac.dim.transportbus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, ageEditText, emailEditText, passwordEditText;
    private RadioGroup authMethodGroup, roleGroup;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        authMethodGroup = findViewById(R.id.authMethodGroup);
        roleGroup = findViewById(R.id.roleGroup);
        registerButton = findViewById(R.id.registerButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(v -> {
            int selectedAuthMethodId = authMethodGroup.getCheckedRadioButtonId();

            if (selectedAuthMethodId == R.id.authByPhone) {
                Intent intent = new Intent(RegisterActivity.this, PhoneAuthActivity.class);
                startActivity(intent);
            } else {
                registerWithEmail();
            }
        });
    }

    private void registerWithEmail() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();

        // Vérification des champs vides
        if (firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRoleId == -1) {
            Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation du mot de passe
        if (!isPasswordValid(password)) {
            Toast.makeText(RegisterActivity.this, "Le mot de passe doit contenir au moins une lettre, un chiffre et un caractère spécial.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation du mot de passe vide
        if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Veuillez entrer un mot de passe.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRole = findViewById(selectedRoleId);
        String role = selectedRole.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = firebaseAuth.getCurrentUser().getUid();

                        firestore.collection("users")
                                .document(userId)
                                .set(new User(firstName, lastName, age, email, role))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Erreur lors de l'enregistrement des données", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Erreur d'inscription: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Vérification de la validité du mot de passe
    private boolean isPasswordValid(String password) {
        // Doit contenir au moins une lettre, un chiffre et un caractère spécial
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(regex);
    }

    private static class User {
        public String firstName, lastName, age, email, role;

        public User(String firstName, String lastName, String age, String email, String role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.email = email;
            this.role = role;
        }
    }
}
