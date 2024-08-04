package com.example.soulsync.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.soulsync.R;
import com.example.soulsync.databinding.ActivityLogInBinding;
import com.example.soulsync.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    ActivityLogInBinding binding;
    private FirebaseAuth mAuth;
    String email, password;
    FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;

    //private GoogleSignInButton googleBtn;
    private GoogleSignInClient gClient;
    GoogleSignInAccount gAccount;

    String gmail, fName, lName;
    String photoID = "ic_user.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your web client ID
                .requestEmail()
                .build();

        gClient = GoogleSignIn.getClient(this, gso);
        gAccount = GoogleSignIn.getLastSignedInAccount(this);

        binding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });


        //Load GIF for background
        Glide.with(this).asGif().load(R.drawable.backgif).into(binding.backgroundImage);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //log in button
        binding.btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.editTxtUsername.getText().toString();
                password = binding.editTxtPassword.getText().toString();
                signIn(email, password);
            }
        });



        //Sing up button
        binding.txtViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void signInWithGoogle() {

        gClient.signOut().addOnCompleteListener(this, task -> {
            // Now start the sign-in intent
            Intent signInIntent = gClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                gmail = account.getEmail();
                fName = account.getGivenName();
                lName = account.getFamilyName();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In failed
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            writeNewUser(user.getUid(), gmail, fName, lName, photoID);

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }



    private void signIn(String email, String password) {


        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LogInActivity.this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            updateUI(null);

                        }

                    }
                });
    }



    private void updateUI(FirebaseUser user) {

        if(user == null){

            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
            //logout(this);
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userId", user.getUid()); // Optional: store user ID
            editor.apply();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void writeNewUser(String uid, String email, String fName, String lName, String photoID) {

        User user = new User(fName, lName, email, photoID);


        db.collection("users").document(uid).set(user).addOnSuccessListener(aVoid -> {

            Log.d(TAG, "User created succesfully");
        }).addOnFailureListener(e ->{
            Log.d(TAG, "Error writing the user");
        });
    }


 //   @Override
//    public void onStart() {
//        super.onStart();
//
//        // Check SharedPreferences for login state
//        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
//
//        if (isLoggedIn) {
//            // User is logged in, proceed to HomeActivity
//            Intent intent = new Intent(this, HomeActivity.class);
//            intent.putExtra("openFragment", "HomeFragment");
//            startActivity(intent);
//            finish(); // Close the login activity
//        } else {
//            // Check if user is signed in with Firebase (this is already handled in your code)
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser != null) {
//                updateUI(currentUser);
//            }
//            else{
//
//            }
//        }
//
//    }

    public void logout(Context context) {
        // Firebase sign out

        if (mAuth != null) {
            mAuth.signOut();
        }


        // Google sign out
        if (gClient != null) {
            gClient.signOut().addOnCompleteListener((Activity) context, task -> {
                // Clear the login state in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Clear all saved data
                editor.apply();

                // Redirect to the LogInActivity
                Intent intent = new Intent(context, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        } else {
            //Handle case where gClient is not initialized
            Log.e("Logout", "GoogleSignInClient not initialized");

            // Perform the same actions as above to ensure the user is logged out
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Clear all saved data
            editor.apply();

            Intent intent = new Intent(context, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }

    }




}