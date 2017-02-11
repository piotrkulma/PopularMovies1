package pl.piotrkulma.popularmoviesstage1.data;

import android.provider.BaseColumns;

/**
 * Created by Piotr Kulma on 2017-02-09.
 */

public final class FavoriteMovieContract {
    private FavoriteMovieContract() {
    }

    public static class FavoriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME                   = "favorite_movie";

        public static final String COLUMN_NAME_IDENTIFIER       = "identifier";
        public static final String COLUMN_NAME_TITLE            = "title";
        public static final String COLUMN_NAME_RELEASE_DATE     = "release_date";
        public static final String COLUMN_NAME_POSTER_PATH      = "posterPath";
        public static final String COLUMN_NAME_VOTE_AVERAGE     = "vote_average";
        public static final String COLUMN_NAME_PLOT_SYNOPSIS    = "plot_synopsis";
        public static final String COLUMN_NAME_RUNTIME          = "runtime";
        public static final String COLUMN_NAME_POSTER_PHOTO     = "poster_photo";
    }
}
