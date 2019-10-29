package com.davide.fontappbeta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {

    public static final int PASSWORD_STNDRD = 8;
    public static final CharSequence PASSWORD_LENGHT = "La password è troppo corta" ;
    private static final CharSequence EMAIL_NOT_VALID = "L'email non è associata a nessun account";
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private EditText username;
    private EditText password;
    private TextView getPassword;

    private int i = 0;

    private TextView signUp;
    private Button login;

    private String mText = "";

    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "onCreate: UTENTE: " + currentUser.getEmail().toString());
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this, MapActivity.class));
        }


        //editText
        username = findViewById(R.id.editTextUser);
        password = findViewById(R.id.editTextPassword);

        //buttons
        login = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.textAccountRegister);
        getPassword = findViewById(R.id.getPassword);

        //fragment Popup Password Forgotten


        //buttons onclick
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(username.getText().toString(), password.getText().toString());
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        getPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordForgotten();
            }
        });
    }

    private void signIn(String email, String pass) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(LoginActivity.this, MapActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Email o password errati",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //mStatusTextView.setText(R.string.auth_failed);
                        }

                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;
        final int[] i = {0};

        String email = username.getText().toString();
        if (TextUtils.isEmpty(email)) {
            username.setError("Email mancante");
            valid = false;
        } else {
            username.setError(null);
        }

        String pass = password.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            password.setError(" Password mancante");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }



    private void updateUI(FirebaseUser user) {

    }

    private void passwordForgotten(){
        openDialog();
    }

    private void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example");
    }


    private boolean isEmailValid(final String email){
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();
                            if (check) {
                                i = 1;
                                Log.d(TAG, "onComplete: result" + i);
                                Toast.makeText(getApplicationContext(), "Nessun account presente con questa email", Toast.LENGTH_LONG).show();
                            }
                    }
                });
        if(i == 1){
            return false;
        }
        return true;
    }

    @Override
    public void applyEmail(String email) {
        if(isEmailValid(email)){
            mAuth.sendPasswordResetEmail(email);
            Toast.makeText(getApplicationContext(), "Ti abbiamo inviato una mail per il recupero della tua password", Toast.LENGTH_LONG).show();
        }
    }

    public static User getUser(FirebaseUser user){
        return new User(user.getEmail().toString());
    }
}
