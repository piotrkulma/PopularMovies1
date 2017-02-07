package pl.piotrkulma.popularmoviesstage1.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Piotr Kulma on 2017-02-07.
 */

public final class MovieDBReviewResponse {
    private String id;
    private String author;
    private String content;
    private String url;

    private MovieDBReviewResponse() {
    }

    public static final class MovieDBReviewResponseBuilder {
        public static MovieDBReviewResponse build(JSONObject json) throws JSONException {
            MovieDBReviewResponse response = new MovieDBReviewResponse();
            response.id = json.getString("id");
            response.author = json.getString("author");
            response.content = json.getString("content");
            response.url = json.getString("url");
            return response;
        }
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
