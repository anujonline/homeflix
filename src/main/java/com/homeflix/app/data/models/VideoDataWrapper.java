package com.homeflix.app.data.models;

import java.util.List;

public record VideoDataWrapper(String label, List<VideoData> videoData) {
}
