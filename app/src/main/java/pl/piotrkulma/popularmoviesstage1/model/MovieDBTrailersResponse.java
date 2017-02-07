package pl.piotrkulma.popularmoviesstage1.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Piotr Kulma on 2017-02-07.
 */

public final class MovieDBTrailersResponse {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;
    private String size;
    private String siteUrl;

    private MovieDBTrailersResponse() {
    }

    public static final class MovieDBTrailersResponseBuilder {
        public static String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";

        public static MovieDBTrailersResponse build(JSONObject json) throws JSONException {
            MovieDBTrailersResponse response = new MovieDBTrailersResponse();
            response.id = json.getString("id");
            response.key = json.getString("key");
            response.name = json.getString("name");
            response.size = json.getString("size");
            response.type = json.getString("type");
            response.site = json.getString("site");
            response.siteUrl = YOUTUBE_VIDEO_URL + response.key;
            return response;
        }
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getSiteUrl() {
        return siteUrl;
    }
}
