package com.example.soulsync.activities;

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
import android.view.View;
import android.widget.Toast;

import com.example.soulsync.R;
import com.example.soulsync.databinding.ActivityUpdateProfileBinding;
import com.example.soulsync.fragments.AccountFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;


public class UpdateProfile extends AppCompatActivity {

    ActivityUpdateProfileBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    private String uniqueID = "user.jpeg";
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        uid = mAuth.getUid();

        binding.buttonUploadPictureU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        binding.btnUpdateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfile.this, AccountFragment.class);
                startActivity(intent);
            }
        });

        binding.btnSubmitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewID(uid, uniqueID);
                Intent intent = new Intent(UpdateProfile.this, AccountFragment.class);
                startActivity(intent);
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
                            binding.imageViewPreviewU.setImageBitmap(selectedImageBitmap);


                            //Shows progress in the upload
                            final ProgressDialog pd = new ProgressDialog(this);
                            pd.setTitle("Uploading Image...");
                            pd.show();

                            //Creates a unique ID for the image in the Firebase Storage
                            uniqueID = UUID.randomUUID().toString();
                            StorageReference imageRef = storageReference.child("users/" + uniqueID);

                            //writeNewID(uid, uniqueID);
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

    private void writeNewID(String uid, String photoId) {

        db.collection("users").document(uid)
                .update("photoID", photoId)
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(this, "Photo ID updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(this, "Error updating Photo ID", Toast.LENGTH_SHORT).show();
                });

    }


}