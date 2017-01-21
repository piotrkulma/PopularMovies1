package pl.piotrkulma.popularmoviesstage1.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Piotr Kulma on 2017-01-18.
 */

/**
 * Model that holds single movie data fetched from moviedb rest service.
 *
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

    public String getReleaseDateYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return Integer.toString(sdf.parse(getReleaseDate()).getYear() + 1900);
        } catch (ParseException e) {
            return getReleaseDate();
        }
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

    public String getVoteAgerageFull() {
        return getVoteAverage() + "/10";
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }
}
