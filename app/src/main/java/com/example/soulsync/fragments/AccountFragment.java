package com.example.soulsync.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.soulsync.HomeActivity;
import com.example.soulsync.LogInActivity;
import com.example.soulsync.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseFirestore db;


    ImageView logout;
    TextView logoutTxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.account_fragment, container, false);

        logout = view.findViewById(R.id.imgViewLogout);
        logoutTxt = view.findViewById(R.id.txtViewLogoutLegend);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




        //Log out button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogInActivity logIn = new LogInActivity();
                logIn.logout();
            }
        });

        logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInActivity logIn = new LogInActivity();
                logIn.logout();
            }
        });




        return view;

    }







}
