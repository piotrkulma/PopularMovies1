package pl.piotrkulma.popularmoviesstage1.utility;

/**
 * Created by Piotr Kulma on 2017-01-18.
 */

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

/**
 * This class is a helper utility to access themoviedb.org API
 */
public final class MovieDBHelper {
    private String LOGGING_KEY = MovieDBHelper.class.getName();

    public static String QUERY_SORT_ORDER_TOP_RATED = "top_rated";
    public static String QUERY_SORT_ORDER_POPULAR = "popular";
    public static String MOVIE_DB_API_KEY = "api_key";
    public static String MOVIE_DB_API_URL = "https://api.themoviedb.org/3/movie/";

    private String apiKeyValue;

    public enum SortOrder {
        TOP_RATED, POPULAR
    }

    public MovieDBHelper(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public MovieDBResponse[] getMovieDBResponses(SortOrder sortOrder) {
        JSONObject jsonReposnse = getMovieDBApiResponse(sortOrder);
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

    private JSONObject getMovieDBApiResponse(SortOrder sortOrder) {
        URL apiRequestURL = buildMovieDBApiQueryURL(resolveSortOrder(sortOrder));

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

    private URL buildMovieDBApiQueryURL(String sortOrder) {
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

    private String resolveSortOrder(SortOrder sortOrder) {
        if(sortOrder == SortOrder.POPULAR) {
            return QUERY_SORT_ORDER_POPULAR;
        }

        return QUERY_SORT_ORDER_TOP_RATED;
    }
}
