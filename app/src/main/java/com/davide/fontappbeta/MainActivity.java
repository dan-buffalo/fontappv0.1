package com.davide.fontappbeta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERRORE_DIALOG_REQUEST = 9001;
    public SharedPreferences prefs = null;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        if(isServicesOK()){
            checkFirstRun(mAuth);
            //init();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.share){
            Toast.makeText(getApplicationContext(), "Hai cliccato Contatti", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.about){
            Toast.makeText(getApplicationContext(), "Hai cliccato Info", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void init(){
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServiceOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //l'utente può proseguire con l'utilizzo della mappa
            Log.d(TAG, "isServiceOK: Google Play Services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //c'è un errore ma si può risolvere
            Log.d(TAG, "isServiceOK: an error occured but we an fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERRORE_DIALOG_REQUEST);
            ((Dialog) dialog).show();
        } else {
            Toast.makeText(this,"You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkFirstRun(FirebaseAuth mAuth) {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode && currentUser != null) {
            //Check for the layout
            Intent register = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(register);
            //init();
            return;

        } else if (savedVersionCode == DOESNT_EXIST || currentUser == null) {
            // TODO This is a new install (or the user cleared the shared preferences)
            Intent register = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(register);

        } else if (currentVersionCode > savedVersionCode) {
            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    public static void updateUI(FirebaseUser user){

    }
}
