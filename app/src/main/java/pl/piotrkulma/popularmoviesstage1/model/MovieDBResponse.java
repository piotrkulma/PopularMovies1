package pl.piotrkulma.popularmoviesstage1.model;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract;

/**
 * Created by Piotr Kulma on 2017-01-18.
 */

/**
 * Model that holds single movie data fetched from moviedb rest service.
 *
 */
public final class MovieDBResponse implements Serializable {
    private static String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";

    private String id;
    private String title;
    private String releaseDate;
    private String posterPath;
    private String voteAverage;
    private String plotSynopsis;
    private String runtime;
    private byte[] posterPhoto;

    private MovieDBTrailersResponse[] videos;
    private MovieDBReviewResponse[] reviews;

    private MovieDBResponse() {
    }

    public static final class MovieDBResponseBuilder {
        public static MovieDBResponse build(JSONObject json) throws JSONException {
            return build(json, null, null);
        }

        public static MovieDBResponse build(Cursor cursor) throws Exception {
            MovieDBResponse response = new MovieDBResponse();
            response.id = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_IDENTIFIER));
            response.title = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE));
            response.releaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE_DATE));
            response.posterPath = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PATH));
            response.voteAverage = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE));
            response.plotSynopsis = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_PLOT_SYNOPSIS));
            response.posterPhoto = cursor.getBlob(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PHOTO));

            response.videos = null;
            response.reviews = null;

            return response;
        }

        public static MovieDBResponse build(
                JSONObject json,
                MovieDBTrailersResponse[] videos,
                MovieDBReviewResponse[] reviews) throws JSONException {
            MovieDBResponse response = new MovieDBResponse();
            response.id = json.getString("id");
            response.title = json.getString("title");
            response.releaseDate = json.getString("release_date");
            response.posterPath = json.getString("poster_path");
            response.voteAverage = json.getString("vote_average");
            response.plotSynopsis = json.getString("overview");

            if(!json.isNull("runtime")) {
                response.runtime = json.getString("runtime");
            } else {
                response.runtime = null;
            }

            response.posterPhoto = null;

            response.videos = videos;
            response.reviews = reviews;

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

    public String getId() {
        return id;
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

    public String getRuntime() {
        return runtime;
    }

    public String getRuntimeFull() {
        if(runtime != null) {
            return runtime + "min";
        }

        return null;
    }

    public byte[] getPosterPhoto() {
        return posterPhoto;
    }

    public MovieDBTrailersResponse[] getVideos() {
        return videos;
    }

    public MovieDBReviewResponse[] getReviews() {
        return reviews;
    }
}
