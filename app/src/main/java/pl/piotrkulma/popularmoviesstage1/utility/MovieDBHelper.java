package pl.piotrkulma.popularmoviesstage1.utility;

/**
 * Created by Piotr Kulma on 2017-01-18.
 */

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBReviewResponse;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBTrailersResponse;

/**
 * This class is a helper utility to access themoviedb.org API.
 *
 */
public final class MovieDBHelper {
    private String LOGGING_KEY = MovieDBHelper.class.getName();

    public static String QUERY_SORT_ORDER_TOP_RATED = "top_rated";
    public static String QUERY_SORT_ORDER_POPULAR = "popular";
    public static String MOVIE_DB_API_KEY = "api_key";
    public static String MOVIE_DB_VIDEOS = "/videos";
    public static String MOVIE_DB_REVIEWS = "/reviews";
    public static String MOVIE_DB_API_URL = "https://api.themoviedb.org/3/movie/";

    private String apiKeyValue;

    public enum SortOrder {
        TOP_RATED, POPULAR, FAVORITE
    }

    public MovieDBHelper(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    /**
     * Fetching movie data from moviedb service into model's array.
     *
     * @param sortOrder sort order (top rated, most popular)
     * @return
     */
    public MovieDBResponse[] getMovies(SortOrder sortOrder) {
        JSONObject jsonReposnse = getMoviesJSON(sortOrder);
        MovieDBResponse returnList[] = null;

        try {
            JSONArray responseArray = jsonReposnse.getJSONArray("results");

            if(responseArray!= null && responseArray.length() > 0) {
                returnList = new MovieDBResponse[responseArray.length()];
                for(int i=0; i<responseArray.length(); i++) {
                    JSONObject element = responseArray.getJSONObject(i);
                    returnList[i] = MovieDBResponse.MovieDBResponseBuilder.build(element);
                }
            }
        } catch (JSONException e) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + e.getMessage());
            returnList = new MovieDBResponse[0];
        } catch (NullPointerException npe) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + npe.getMessage());
            returnList = new MovieDBResponse[0];
        }

        return returnList;
    }

    /**
     * Fetching movie data from cursor model's array.
     *
     * @param cursor
     * @return
     */
    public MovieDBResponse[] getMovies(Cursor cursor) {
        MovieDBResponse returnList[];
        try {
            int i = 0;
            returnList = new MovieDBResponse[cursor.getCount()];

            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                returnList[i++] = MovieDBResponse.MovieDBResponseBuilder.build(cursor);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from cursor: " + e.getMessage());
            returnList = new MovieDBResponse[0];
        }

        return returnList;
    }

    public MovieDBResponse getMovieWithAllDetails(String id) {
        JSONObject jsonReposnse = getMovieDetailJSON(id);
        MovieDBResponse response = null;
        try {
            String movieId = jsonReposnse.getString("id");
            response = MovieDBResponse.MovieDBResponseBuilder.build(
                    jsonReposnse,
                    getTrailers(movieId),
                    getReviews(movieId));
        } catch (JSONException e) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + e.getMessage());
        } catch (NullPointerException npe) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + npe.getMessage());
        }

        return response;
    }

    private MovieDBTrailersResponse[] getTrailers(String id) {
        JSONObject jsonReposnse = getTrailersJSON(id);
        MovieDBTrailersResponse returnList[] = null;

        try {
            JSONArray responseArray = jsonReposnse.getJSONArray("results");

            if(responseArray!= null && responseArray.length() > 0) {
                returnList = new MovieDBTrailersResponse[responseArray.length()];
                for(int i=0; i<responseArray.length(); i++) {
                    JSONObject element = responseArray.getJSONObject(i);
                    returnList[i] = MovieDBTrailersResponse.MovieDBTrailersResponseBuilder.build(element);
                }
            }
        } catch (JSONException e) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + e.getMessage());
            returnList = new MovieDBTrailersResponse[0];
        } catch (NullPointerException npe) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + npe.getMessage());
            returnList = new MovieDBTrailersResponse[0];
        }

        return returnList;
    }

    private MovieDBReviewResponse[] getReviews(String id) {
        JSONObject jsonReposnse = getReviewsJSON(id);
        MovieDBReviewResponse returnList[] = null;

        try {
            JSONArray responseArray = jsonReposnse.getJSONArray("results");

            if(responseArray!= null && responseArray.length() > 0) {
                returnList = new MovieDBReviewResponse[responseArray.length()];
                for(int i=0; i<responseArray.length(); i++) {
                    JSONObject element = responseArray.getJSONObject(i);
                    returnList[i] = MovieDBReviewResponse.MovieDBReviewResponseBuilder.build(element);
                }
            }
        } catch (JSONException e) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + e.getMessage());
            returnList = new MovieDBReviewResponse[0];
        } catch (NullPointerException npe) {
            Log.e(LOGGING_KEY, "ERROR while creating responses list from moviedb API: " + npe.getMessage());
            returnList = new MovieDBReviewResponse[0];
        }

        return returnList;
    }

    private JSONObject getMoviesJSON(SortOrder sortOrder) {
        URL apiRequestURL = buildMoviesListURL(resolveSortOrder(sortOrder));

        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(apiRequestURL);
        } catch (IOException e) {
            Log.e(LOGGING_KEY, "ERROR fetch response from moviedb API: " + e.getMessage());
        }

        if(response != null) {
            try {
                return new JSONObject(response);
            } catch (JSONException e) {
                Log.e(LOGGING_KEY, "ERROR cannot create JSONObject from service response: " + e.getMessage());

            }
        }
        return null;
    }

    private JSONObject getReviewsJSON(String id) {
        URL apiRequestURL = buildReviewsURL(id);

        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(apiRequestURL);
        } catch (IOException e) {
            Log.e(LOGGING_KEY, "ERROR fetch response from moviedb API: " + e.getMessage());
        }

        if(response != null) {
            try {
                return new JSONObject(response);
            } catch (JSONException e) {
                Log.e(LOGGING_KEY, "ERROR cannot create JSONObject from service response: " + e.getMessage());

            }
        }
        return null;
    }

    private JSONObject getTrailersJSON(String id) {
        URL apiRequestURL = buildVideosURL(id);

        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(apiRequestURL);
        } catch (IOException e) {
            Log.e(LOGGING_KEY, "ERROR fetch response from moviedb API: " + e.getMessage());
        }

        if(response != null) {
            try {
                return new JSONObject(response);
            } catch (JSONException e) {
                Log.e(LOGGING_KEY, "ERROR cannot create JSONObject from service response: " + e.getMessage());

            }
        }
        return null;
    }

    private JSONObject getMovieDetailJSON(String id) {
        URL apiRequestURL = buildMovieDetailURL(id);

        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(apiRequestURL);
        } catch (IOException e) {
            Log.e(LOGGING_KEY, "ERROR fetch response from moviedb API: " + e.getMessage());
        }

        if(response != null) {
            try {
                return new JSONObject(response);
            } catch (JSONException e) {
                Log.e(LOGGING_KEY, "ERROR cannot create JSONObject from service response: " + e.getMessage());

            }
        }
        return null;
    }

    private URL buildMoviesListURL(String sortOrder) {
        Uri builtUri = Uri
                .parse(MOVIE_DB_API_URL + sortOrder).buildUpon()
                .appendQueryParameter(MOVIE_DB_API_KEY, this.apiKeyValue)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOGGING_KEY, "ERROR Cannot build correctly URL: " + e.getMessage());
        }

        return url;
    }

    private URL buildReviewsURL(String id) {
        Uri builtUri = Uri
                .parse(MOVIE_DB_API_URL + id + MOVIE_DB_REVIEWS).buildUpon()
                .appendQueryParameter(MOVIE_DB_API_KEY, this.apiKeyValue)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOGGING_KEY, "ERROR Cannot build correctly URL: " + e.getMessage());
        }

        return url;
    }

    private URL buildVideosURL(String id) {
        Uri builtUri = Uri
                .parse(MOVIE_DB_API_URL + id + MOVIE_DB_VIDEOS).buildUpon()
                .appendQueryParameter(MOVIE_DB_API_KEY, this.apiKeyValue)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOGGING_KEY, "ERROR Cannot build correctly URL: " + e.getMessage());
        }

        return url;
    }

    private URL buildMovieDetailURL(String id) {
        Uri builtUri = Uri
                .parse(MOVIE_DB_API_URL + id).buildUpon()
                .appendQueryParameter(MOVIE_DB_API_KEY, this.apiKeyValue)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOGGING_KEY, "ERROR Cannot build correctly URL: " + e.getMessage());
        }

        return url;
    }

    private String resolveSortOrder(SortOrder sortOrder) {
        if(sortOrder == SortOrder.POPULAR) {
            return QUERY_SORT_ORDER_POPULAR;
        }

        return QUERY_SORT_ORDER_TOP_RATED;
    }
}
