package pl.piotrkulma.popularmoviesstage1.data.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import pl.piotrkulma.popularmoviesstage1.data.DbBitmapUtility;
import pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

/**
 * Created by Piotr Kulma on 2017-02-10.
 */

public final class FavoriteProviderHelper {
    private FavoriteProviderHelper() {
    }

    public static Uri getMovieIdUri(String id) {
        return Uri.parse(FavoriteMovieProvider.CONTENT_URI.toString() + "/" + id);
    }

    public static Cursor selectAllFavoritesCursor(ContentResolver resolver) {
        return selectFavoritesCursorById(resolver, null);
    }

    public static Cursor selectFavoritesCursorById(ContentResolver resolver, String id) {
        String[] projection = new String[]{
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_IDENTIFIER,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE_DATE,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PATH,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_PLOT_SYNOPSIS,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_RUNTIME,
                FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PHOTO
        };

        Cursor query = resolver.query(
                id==null?FavoriteMovieProvider.CONTENT_URI:getMovieIdUri(id),
                projection,
                null,
                null,
                null);

        return query;
    }

    public static Uri addNewFavorite(ContentResolver resolver, MovieDBResponse movieData, Bitmap bitmap) {
        ContentValues cv = new ContentValues();
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_IDENTIFIER, movieData.getId());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE, movieData.getTitle());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE_DATE, movieData.getReleaseDate());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PATH, movieData.getPosterPath());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE, movieData.getVoteAverage());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_PLOT_SYNOPSIS, movieData.getPlotSynopsis());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_RUNTIME, movieData.getRuntime());
        cv.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PHOTO, DbBitmapUtility.getBytes(bitmap));

        Uri uri = resolver.insert(FavoriteMovieProvider.CONTENT_URI, cv);

        return uri;
    }

    public static int removeFavorite(ContentResolver resolver, String id) {
         return resolver.delete(
                getMovieIdUri(id),
                null,
                null);
    }
}
