package com.example.soulsync.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soulsync.R;
import com.example.soulsync.fragments.JournalEntryFragment;
import com.example.soulsync.models.Journal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddJournal extends AppCompatActivity {

    Button save;
    EditText titletxt, contenttxt;
    String datetxt;
    TextView contentJournalSave;
    StorageReference storageReference;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        save = findViewById(R.id.saveButtonJournal);
        titletxt = findViewById(R.id.editTextTitleJournal);
        contenttxt = findViewById(R.id.editTxtContent);
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        datetxt = dateFormat.format(currentDate);
        contentJournalSave = findViewById(R.id.editTxtContent);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = mAuth.getCurrentUser();
                writeNewJournal(user.getUid(), titletxt.getText().toString(), contenttxt.getText().toString(), datetxt);

                Intent intent = new Intent(AddJournal.this, HomeActivity.class);
                intent.putExtra("action", "SHOW_JOURNAL_ENTRY_FRAGMENT");
                startActivity(intent);
                finish();
            }
        });


    }


    private void writeNewJournal(String uid, String title, String content, String date){

        Journal journalEntry = new Journal(uid,title, content, date);

        db.collection("journals").add(journalEntry)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Journal added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error writing the journal", Toast.LENGTH_SHORT).show();
                });
    }
}