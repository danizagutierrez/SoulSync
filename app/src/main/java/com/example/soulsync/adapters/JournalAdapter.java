package com.example.soulsync.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soulsync.R;
import com.example.soulsync.models.Journal;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    //private static Context context;
    List<Journal> journalList;

    public JournalAdapter( List<Journal> journalList) {

        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_card, parent, false);
        return new JournalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {

        Journal item = journalList.get(position);
        holder.title.setText(item.getTitleJournal());
        holder.content.setText(item.getContentJournal());
        holder.date.setText(item.getDateJournal());

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder{
        TextView title, content, date;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleJournal);
            content = itemView.findViewById(R.id.contentJournal);
            date = itemView.findViewById(R.id.dateJournal);

        }
    }
}
