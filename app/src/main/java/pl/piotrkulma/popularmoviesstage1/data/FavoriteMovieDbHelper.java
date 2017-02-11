package pl.piotrkulma.popularmoviesstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_IDENTIFIER;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_PLOT_SYNOPSIS;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PATH;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_POSTER_PHOTO;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_RELEASE_DATE;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_RUNTIME;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_TITLE;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE;
import static pl.piotrkulma.popularmoviesstage1.data.FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME;

/**
 * Created by Piotr Kulma on 2017-02-09.
 */

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "favorite_movie.db";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    FavoriteMovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_IDENTIFIER + " TEXT," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_RELEASE_DATE + " TEXT," +
                    COLUMN_NAME_POSTER_PATH + " TEXT," +
                    COLUMN_NAME_VOTE_AVERAGE + " TEXT," +
                    COLUMN_NAME_PLOT_SYNOPSIS + " TEXT," +
                    COLUMN_NAME_RUNTIME + " TEXT," +
                    COLUMN_NAME_POSTER_PHOTO + " BLOB" +
                    ")";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public FavoriteMovieDbHelper(Context ctx) {
        super(ctx,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);

    }
}
