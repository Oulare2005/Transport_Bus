package uqac.dim.transportbus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {

    private Spinner countryCodeSpinner;
    private EditText phoneNumberEditText, otpEditText;
    private Button sendOtpButton, verifyOtpButton;
    private FirebaseAuth firebaseAuth;

    private String verificationId; // ID de vérification pour la gestion de l'OTP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        // Initialisation des composants UI
        countryCodeSpinner = findViewById(R.id.countryCodeSpinner);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        otpEditText = findViewById(R.id.otpEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        firebaseAuth = FirebaseAuth.getInstance();

        // Remplissage du Spinner avec les codes pays
        String[] countryCodes = {"+1", "+33", "+91", "+237"}; // Ajouter d'autres codes si nécessaire
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countryCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);

        // Action lors du clic sur le bouton "Envoyer OTP"
        sendOtpButton.setOnClickListener(v -> {
            String selectedCode = countryCodeSpinner.getSelectedItem().toString();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer votre numéro de téléphone", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullPhoneNumber = selectedCode + phoneNumber;

            // Validation du numéro de téléphone
            if (PhoneValidator.isValidPhoneNumber(fullPhoneNumber, "")) { // Validation globale
                sendVerificationCode(fullPhoneNumber);
            } else {
                Toast.makeText(this, "Numéro de téléphone invalide", Toast.LENGTH_SHORT).show();
            }
        });

        // Action lors du clic sur le bouton "Vérifier OTP"
        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (!otp.isEmpty() && verificationId != null) {
                verifyCode(otp);
            } else {
                Toast.makeText(PhoneAuthActivity.this, "Veuillez entrer un code OTP valide", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        // Affichage du numéro avant d'envoyer le code (pour débogage)
        Log.d("PhoneAuth", "Numéro de téléphone complet : " + phoneNumber);

        // Création des options pour l'authentification par téléphone
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber) // Numéro à vérifier
                .setTimeout(60L, TimeUnit.SECONDS) // Durée d'attente avant échec
                .setActivity(this) // Activité actuelle pour la gestion des callbacks
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // Lorsque la vérification est réussie sans code SMS (par exemple, pour les tests ou les appareils compatibles)
                        Toast.makeText(PhoneAuthActivity.this, "Vérification terminée", Toast.LENGTH_SHORT).show();
                        Log.d("PhoneAuth", "Vérification terminée : " + credential.getSmsCode());
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // Gestion de l'échec de la vérification
                        Toast.makeText(PhoneAuthActivity.this, "Échec de la vérification : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("PhoneAuth", "Erreur de vérification", e); // Affiche l'erreur dans les logs
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        // Code envoyé avec succès
                        Toast.makeText(PhoneAuthActivity.this, "Code envoyé", Toast.LENGTH_SHORT).show();
                        Log.d("PhoneAuth", "Code envoyé avec ID de vérification : " + verificationId);
                        PhoneAuthActivity.this.verificationId = verificationId;
                    }
                })
                .build();

        // Démarre l'envoi du code OTP
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Connexion réussie, rediriger l'utilisateur vers l'écran principal ou un autre écran
                        Toast.makeText(PhoneAuthActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                        // Redirigez vers l'activité d'accueil ou le tableau de bord
                    } else {
                        // Si l'authentification échoue, afficher un message d'erreur
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(PhoneAuthActivity.this, "Code de vérification incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
