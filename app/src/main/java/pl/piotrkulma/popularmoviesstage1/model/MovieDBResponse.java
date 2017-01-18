package pl.piotrkulma.popularmoviesstage1.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Piotr Kulma on 2017-01-18.
 */

public final class MovieDBResponse implements Serializable {
    private static String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";

    private String title;
    private String releaseDate;
    private String posterPath;
    private String voteAverage;
    private String plotSynopsis;

    private MovieDBResponse() {
    }

    public static final class MovieDBResponseBuilder {
        public static MovieDBResponse build(JSONObject json) throws JSONException {
            MovieDBResponse response = new MovieDBResponse();
            response.title = json.getString("title");
            response.releaseDate = json.getString("release_date");
            response.posterPath = json.getString("poster_path");
            response.voteAverage = json.getString("vote_average");
            response.plotSynopsis = json.getString("overview");
            return response;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPosterUrl() {
        return POSTER_BASE_URL + this.getPosterPath();
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }
}
