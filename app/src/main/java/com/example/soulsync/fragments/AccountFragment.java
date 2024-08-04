package com.example.soulsync.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.soulsync.activities.HomeActivity;
import com.example.soulsync.activities.LogInActivity;
import com.example.soulsync.R;
import com.example.soulsync.activities.UpdateProfile;
import com.example.soulsync.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    ImageView logout, profile;
    TextView logoutTxt, userEmail, userFName, userLName;
    String photoID = "ic_user.png";
    Button update;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.account_fragment, container, false);

        logout = view.findViewById(R.id.imgViewLogout);
        logoutTxt = view.findViewById(R.id.txtViewLogoutLegend);
        profile = view.findViewById(R.id.imgViewProfilePic);
        userEmail = view.findViewById(R.id.txtViewEmailDB);
        userFName = view.findViewById(R.id.txtViewFNameDB);
        userLName = view.findViewById(R.id.txtViewLNameDB);
        update = view.findViewById(R.id.btnUpdateProfile);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
            }
        });



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        loadUserImage();

        fetchUserData();


        //Log out button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivity(intent);

            }
        });

        logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    private void fetchUserData() {

        String userId = mAuth.getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                User user = document.toObject(com.example.soulsync.models.User.class);
                                if (user != null) {
                                    String email = user.getEmail();
                                    String firstName = user.getUserFName();
                                    String lastName = user.getUserLName();

                                    userEmail.setText(email);
                                    userFName.setText(firstName);
                                    userLName.setText(lastName);



                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }


    private void loadUserImage() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();


        //Retrieving the photoID from FireStore
        db.collection("users").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if(document.exists()){
                                photoID = document.getString("photoID");

                                if (photoID!= null){

                                    try {
                                        StorageReference pathReference = storageReference.child("users/" + photoID);
                                        Log.d(TAG, "THIS IS THE SECOND OTHER " + photoID);
                                        final long ONE_MEGABYTE = 1024 * 1024;
                                        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Log.d("Firebase", "User photo downloaded");
                                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                profile.setImageBitmap(bmp);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Log.d("Firebase", "There is an error downloading the user photo");
                                            }
                                        });

                                    } catch (Exception e){
                                        Log.d("Firebase","Error:" + e);
                                    }

                                }

                            }else{
                                Toast.makeText(getContext(), "No file on records", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Log.w(TAG, "Error getting document", task.getException());
                        }
                    }
                });


    }


}


