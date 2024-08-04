package com.example.soulsync.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.soulsync.api.VideoFetcher;
import com.example.soulsync.activities.VideoPlayActivity;
import com.example.soulsync.adapters.VideoAdapter;
import com.example.soulsync.databinding.MindfulnessFragmentBinding;
import com.example.soulsync.models.VideoItem;

import java.util.ArrayList;
import java.util.List;


public class MindfulnessFragment extends Fragment {

    private VideoAdapter adapter;
    private List<VideoItem> videoItems = new ArrayList<VideoItem>();

    String title = "Something";

    public MindfulnessFragmentBinding binding;
    private VideoFetcher videoFetcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = MindfulnessFragmentBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        videoFetcher = new VideoFetcher();

        fetchVideos();
        return binding.getRoot();
    }



    public void fetchVideos(){
        videoFetcher.fetchVideos(new VideoFetcher.VideoResponseListener() {
            @Override
            public void onVideoResponse(ArrayList<VideoItem> arrayList) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new VideoAdapter(arrayList, getActivity());
                        binding.recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(VideoItem video) {

                                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                                intent.putExtra("url", video.getVideoUrl());
                                startActivity(intent);
                            }
                        });

                    }
                });
            }

            @Override
            public void onFailure(String e) {

            }
        });
    }


}
