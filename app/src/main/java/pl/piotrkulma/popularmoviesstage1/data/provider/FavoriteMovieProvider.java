package pl.piotrkulma.popularmoviesstage1.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract;
import pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieDbHelper;

/**
 * Created by Piotr on 2017-02-09.
 */

public final class FavoriteMovieProvider extends ContentProvider {
    public static final int MOVIES      = 100;
    public static final int MOVIE_ID   = 101;

    public static final String PROVIDER_NAME = "pl.piotrkulma.popularmoviesstage1.data.provider";
    public static final String URL = "content://" + PROVIDER_NAME + "/movies";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private FavoriteMovieDbHelper favoriteMovieDbHelper;

    static {
        sUriMatcher.addURI(PROVIDER_NAME, "movies", MOVIES);
        sUriMatcher.addURI(PROVIDER_NAME, "movies/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        favoriteMovieDbHelper = new FavoriteMovieDbHelper(ctx);

        SQLiteDatabase db = favoriteMovieDbHelper.getWritableDatabase();
        return (db != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = favoriteMovieDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);

        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch(match) {
            case MOVIES:
                break;
            case MOVIE_ID:
                qb.appendWhere(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_IDENTIFIER +
                        " = " + uri.getLastPathSegment());
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor = qb.query(
                db,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri result = null;
        if(MOVIES == sUriMatcher.match(uri)) {
            SQLiteDatabase db = favoriteMovieDbHelper.getWritableDatabase();
            long id = db.insert(
                    FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                    null,
                    values);

            if(id > 0) {
                result = ContentUris.withAppendedId(CONTENT_URI, id);
                getContext().getContentResolver().notifyChange(result, null);
            }
        } else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = favoriteMovieDbHelper.getWritableDatabase();
        int count;

        if(MOVIE_ID  == sUriMatcher.match(uri)) {
            String id = uri.getLastPathSegment();
            count = db.delete(
                    FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                    FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_IDENTIFIER + " = " + id,
                    selectionArgs);
        } else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = favoriteMovieDbHelper.getWritableDatabase();

        if(MOVIE_ID  == sUriMatcher.match(uri)) {
            String id = uri.getLastPathSegment();
            count = db.update(
                    FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                    values,
                    FavoriteMovieContract.FavoriteMovieEntry._ID + "=" + id,
                    selectionArgs);
        }else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
}
