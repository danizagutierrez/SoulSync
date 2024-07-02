package com.example.soulsync;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soulsync.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ActivitySignUpBinding binding;
    String password, email, fName, lName;
    StorageReference storageReference;

    FirebaseFirestore db;
    private String uniqueID = "user.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        //Click Listener for image uploader
        binding.buttonUploadPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });




        //Create user
        binding.buttonSubmitCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount();
            }
        });

    }

    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");

        //Set to get the desired content
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //launches the image selection activity located below
        launchMyImageUploaderActivity.launch(intent);
    }

    ActivityResultLauncher<Intent> launchMyImageUploaderActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //Checks to make sure user actually selected an image
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    //Makes sure the data/selected image URI are both not dull
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            //Gets selected image bitmap
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);

                            //Set selected image to preview for testing purposes, makes sure image actually shows
                            binding.imageViewPreview.setImageBitmap(selectedImageBitmap);


                            //Shows progress in the upload
                            final ProgressDialog pd = new ProgressDialog(this);
                            pd.setTitle("Uploading Image...");
                            pd.show();

                            //Creates a unique ID for the image in the Firebase Storage
                            uniqueID = UUID.randomUUID().toString();
                            StorageReference imageRef = storageReference.child("users/" + uniqueID);


                            //Sends the image to the firebase storage
                            imageRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(),"Photo uploaded", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Photo not uploaded", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                    pd.setMessage("Percentage: " + (int) progressPercent + "%");
                                }
                            });

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


    private void createAccount(){

        boolean valid = true;

        email = binding.editEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.editEmail.setError("Required.");
            valid = false;
        } else {
            binding.editEmail.setError(null);
        }

        if(binding.editPassword.getText().toString().equals(binding.editConfPassword.getText().toString())){
            password = binding.editConfPassword.getText().toString();
        }else{
            valid = false;
            binding.editConfPassword.setError("Password must match");
        }
        if (TextUtils.isEmpty(password)) {
            binding.editPassword.setError("Required.");
            valid = false;
        } else {
            binding.editPassword.setError(null);
        }

        fName = binding.editFName.getText().toString();
        lName = binding.editLName.getText().toString();


        if(valid){

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();


                                writeNewUser(user.getUid(), email, fName, lName, uniqueID);

                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUp.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });

        }



    }

    private void writeNewUser(String uid, String email, String fName, String lName, String photoID) {

        User user = new User(fName, lName, email, photoID);


        db.collection("users").document(uid).set(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "User created succesfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e ->{
            Toast.makeText(this, "Error writing the user", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            binding.editEmail.setVisibility(View.GONE);
            binding.editFName.setVisibility(View.GONE);
            binding.editLName.setVisibility(View.GONE);
            binding.editPassword.setVisibility(View.GONE);
            binding.editConfPassword.setVisibility(View.GONE);
            binding.buttonSubmitCreateUser.setVisibility(View.GONE);

            // Optionally, you can add a welcome message or other UI changes for a signed-in user
            Toast.makeText(SignUp.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        } else {
            binding.editEmail.setVisibility(View.VISIBLE);
            binding.editFName.setVisibility(View.VISIBLE);
            binding.editLName.setVisibility(View.VISIBLE);
            binding.editPassword.setVisibility(View.VISIBLE);
            binding.editConfPassword.setVisibility(View.VISIBLE);
            binding.buttonSubmitCreateUser.setVisibility(View.VISIBLE);
        }


    }

    private boolean validateForm() {
        boolean valid = true;

        String email = binding.editEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.editEmail.setError("Required.");
            valid = false;
        } else {
            binding.editEmail.setError(null);
        }

        String password = binding.editPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            binding.editPassword.setError("Required.");
            valid = false;
        } else {
            binding.editPassword.setError(null);
        }

        if (password.length() < 6) {
            binding.editPassword.setError("Password must be at least 6 characters.");
        }

        return valid;
    }


}