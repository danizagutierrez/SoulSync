package com.example.soulsync.models;

import java.util.List;

public class PexelsVideoResponse {
    private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public class Video {
        private int id;
        private String url;
        private List<VideoFile> video_files;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<VideoFile> getVideo_files() {
            return video_files;
        }

        public void setVideo_files(List<VideoFile> video_files) {
            this.video_files = video_files;
        }

        public class VideoFile {
            private String link;

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }
        }
    }
}
