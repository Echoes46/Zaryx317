package io.zaryx.sql.youtube;


import com.google.api.services.youtube.YouTube;

import java.util.List;

public class YoutubeManager {

    private YouTubeAPIClient youTubeAPIClient;
    private YouTube youTube;

    public YoutubeManager() {
        youTubeAPIClient = new YouTubeAPIClient();
        youTube = youTubeAPIClient.getYouTube();
    }

    public YouTube getYouTube() {
        return youTube;
    }

    public List<String> getComments(String videoId) {
        try {
            return youTubeAPIClient.getComments(videoId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
