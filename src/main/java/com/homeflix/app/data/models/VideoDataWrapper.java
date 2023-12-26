package com.homeflix.app.data.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class VideoDataWrapper {
    private String label;
    private List<VideoData> videoData;

    public VideoDataWrapper setLabel(String label) {
        this.label = label;
        return this;
    }

    public VideoDataWrapper setVideoData(List<VideoData> videoData) {
        this.videoData = videoData;
        return this;
    }

    public String label() {
        return this.label;
    }

    public List<VideoData> videoData() {
        return this.videoData;
    }
}
