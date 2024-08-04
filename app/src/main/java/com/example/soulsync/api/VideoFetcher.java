package com.example.soulsync.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.soulsync.models.PexelsVideoResponse;
import com.example.soulsync.models.VideoItem;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoFetcher {

    private static final String API_KEY = "ZQ2jfGZ9Gue0E7ORwzDKJq5s8FiHXcmlDHTY0LPh9wzZTX5BN9leKjWz";
    private static final String BASE_URL = "https://api.pexels.com/videos/search";
    private static final String TAG = "VideoFetcher";
    private String query = "mindfulness";

    public interface VideoResponseListener {
        void onVideoResponse(ArrayList<VideoItem> arrayList);
        void onFailure(String e);
    }

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void fetchVideos(final VideoResponseListener videoResponseListener) {
        String url = String.format("%s?query=%s", BASE_URL, query);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network request failed", e);
                videoResponseListener.onFailure("Request Failed" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() ) {

                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("videos");
                        ArrayList<VideoItem> arrayList = new ArrayList<>();

                        for(int i = 0; i<jsonArray.length(); i++){

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            VideoItem video = new VideoItem();
                            video.setId(jsonObject1.getString("id"));

                            String title = jsonObject1.getJSONObject("user").getString("name");
                            video.setTitle("Mindfulness with " + title);
                            video.setThumbnailUrl(jsonObject1.getString("image"));
                            video.setVideoUrl(jsonObject1.getJSONArray("video_files").getJSONObject(0).getString("link"));
                            arrayList.add(video);
                        }
                        videoResponseListener.onVideoResponse(arrayList);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }


}
