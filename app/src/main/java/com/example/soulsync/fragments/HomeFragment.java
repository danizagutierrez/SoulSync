package com.example.soulsync.fragments;

import static android.content.ContentValues.TAG;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.soulsync.R;
import com.example.soulsync.models.Journal;
import com.example.soulsync.models.User;
import com.example.soulsync.models.WaterIntake;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    public TextView quoteTxt, authorTxt, titleTxt;

    private FirebaseAuth mAuth;
    private View breathingCircle;
    private TextView breathingText;
    private FloatingActionButton waterIntake;
    private Handler handler = new Handler();
    private boolean isInhale = true;
    String uid, dateTxt;
    FirebaseFirestore db;

    private MediaPlayer inhalePlayer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        quoteTxt = view.findViewById(R.id.quote);
        quoteTxt.setAlpha(0f);
        quoteTxt.setTranslationY(50);
        quoteTxt.animate().alpha(1f).translationY(0).setDuration(1000).setStartDelay(500).start();
        authorTxt = view.findViewById(R.id.author);
        titleTxt = view.findViewById(R.id.homeTitle);
        waterIntake = view.findViewById(R.id.addWater);
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateTxt = dateFormat.format(currentDate);


        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        breathingCircle = view.findViewById(R.id.breathingCircle);
        breathingText = view.findViewById(R.id.breathingText);


        // Initialize MediaPlayer for inhale and exhale sounds
        inhalePlayer = MediaPlayer.create(getActivity(), R.raw.forestsound);


        startBreathingExercise();
        fetchName(uid);
        fetchDailyQuote();

        waterIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAddWaterDialog();
            }
        });

        return view;
    }

    private void ShowAddWaterDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_water, null);

        // Initialize dialog components
        EditText editTextWaterAmount = dialogView.findViewById(R.id.editTextWaterAmount);
        Button btnAddWater = dialogView.findViewById(R.id.btnAddWater);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set onClickListener for the Add button
        btnAddWater.setOnClickListener(v -> {

            String waterAmount = editTextWaterAmount.getText().toString().trim();

            int waterInt= Integer.parseInt(waterAmount);

            addWaterLog(waterInt);

            if (!waterAmount.isEmpty()) {
                // Handle adding water amount here, e.g., update the log or database
                Toast.makeText(getActivity(), waterAmount + " cups added", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Close the dialog
            } else {
                Toast.makeText(getActivity(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener for the Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    private void addWaterLog(int waterAmount) {


        WaterIntake waterEntry = new WaterIntake(uid,waterAmount,dateTxt );

        db.collection("water").add(waterEntry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Water log added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error writing the water log", Toast.LENGTH_SHORT).show();
                });
    }


    private void startBreathingExercise() {

        final int inhaleDuration = 4000; // 4 seconds
        final int exhaleDuration = 4000; // 4 seconds

        // Animation to scale the circle for inhaling
        ObjectAnimator inhaleAnimator = ObjectAnimator.ofFloat(breathingCircle, "scaleX", 1f, 1.5f);
        inhaleAnimator.setDuration(inhaleDuration);
        inhaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator inhaleAnimatorY = ObjectAnimator.ofFloat(breathingCircle, "scaleY", 1f, 1.5f);
        inhaleAnimatorY.setDuration(inhaleDuration);
        inhaleAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Animation to scale the circle for exhaling
        ObjectAnimator exhaleAnimator = ObjectAnimator.ofFloat(breathingCircle, "scaleX", 1.5f, 1f);
        exhaleAnimator.setDuration(exhaleDuration);
        exhaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator exhaleAnimatorY = ObjectAnimator.ofFloat(breathingCircle, "scaleY", 1.5f, 1f);
        exhaleAnimatorY.setDuration(exhaleDuration);
        exhaleAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start the breathing cycle
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isInhale) {
                    breathingText.setText("Inhale");
                    inhaleAnimator.start();
                    playSound(inhalePlayer);
                    inhaleAnimatorY.start();
                } else {
                    breathingText.setText("Exhale");
                    exhaleAnimator.start();
                    playSound(inhalePlayer);
                    exhaleAnimatorY.start();
                }

                isInhale = !isInhale; // Toggle between inhale and exhale

                // Repeat the cycle
                handler.postDelayed(this, inhaleDuration + exhaleDuration);
            }
        }, inhaleDuration);
    }

    private void playSound(MediaPlayer player){
        if (player != null) {
            player.seekTo(0); // Start the sound from the beginning
            player.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Stop the breathing cycle when activity is destroyed
    }

    private void fetchName(String uid) {

        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the firstName field
                                String firstName = document.getString("userFName");

                                titleTxt.setText("Hello "+ firstName + "!");
                            } else {
                                Log.w(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting document.", task.getException());
                        }
                    }
                });

    }

    public void fetchDailyQuote() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://zenquotes.io/api/today";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the error
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        try {
                            // Parse the JSON response
                            JSONArray jsonArray = new JSONArray(responseData);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            // Extract the quote and author
                            String quote = jsonObject.getString("q");
                            String author = jsonObject.getString("a");

                            // Update your UI with the quote
                            updateQuoteUI(quote, author);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                }
            }
        });
    }

    private void updateQuoteUI(String quote, String author) {

        if (quoteTxt != null) {
            quoteTxt.setText(quote);
            authorTxt.setText(author);
        }

    }
}
