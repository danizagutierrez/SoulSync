package com.example.soulsync.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soulsync.R;
import com.example.soulsync.activities.AddJournal;
import com.example.soulsync.adapters.JournalAdapter;
import com.example.soulsync.databinding.JournalEntryFragmentBinding;
import com.example.soulsync.models.Journal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JournalEntryFragment extends Fragment {

    private final String TAG = "READING DATA";
    private RecyclerView recyclerView;
    private JournalAdapter adapter;

    private List<Journal> journalList;
    JournalEntryFragmentBinding binding;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = JournalEntryFragmentBinding.inflate(getLayoutInflater());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        journalList = new ArrayList<>();
        adapter = new JournalAdapter(journalList);
        recyclerView.setAdapter(adapter);

        //Glide.with(this).asGif().load(R.drawable.watergif).into(binding.backgroundImage);

        fetchJournalEntries();

        binding.fabAddJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddJournal.class);
                //startActivity(intent);
                addJournalLauncher.launch(intent);
            }
        });

        return binding.getRoot();
    }

    private final ActivityResultLauncher<Intent> addJournalLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Fetch the data again when returning from AddJournal
                    fetchJournalEntries();
                }
            }
    );

    private void fetchJournalEntries(){
        String useriD = mAuth.getUid();

        db.collection("journals")
                .whereEqualTo("userID", useriD)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            journalList.clear(); // Clear the list before adding new data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Journal entry = document.toObject(Journal.class);
                                journalList.add(entry);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fetch the data again when the fragment becomes visible
        fetchJournalEntries();
    }

}
