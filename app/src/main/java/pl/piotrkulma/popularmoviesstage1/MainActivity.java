package pl.piotrkulma.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import pl.piotrkulma.popularmoviesstage1.data.provider.FavoriteProviderHelper;
    import pl.piotrkulma.popularmoviesstage1.utility.MovieDBHelper;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

/**
 * Main activity showing movies grid
 *
 */

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickHandler {
    private final static String SORT_ORDER_SAVE_KEY         = "SORT_ORDER_SAVE_KEY";

    private String LOGGING_KEY = MainActivity.class.getName() + "Logger";

    private int columnsInGrid;

    private MovieDBHelper movieDBHelper;

    private MoviesAdapter moviesAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private TextView errorView;
    private TextView favoriteErrorView;

    private Menu menu;

    private MovieDBHelper.SortOrder actualSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOGGING_KEY, "onCreate");

        restoreState(savedInstanceState);

        setContentView(R.layout.activity_main);

        initMovieDBApi();

        if(Configuration.ORIENTATION_LANDSCAPE == getScreenOrientation()) {
            columnsInGrid = 4;
        } else {
            columnsInGrid = 2;
        }


        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, columnsInGrid);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        errorView = (TextView) findViewById(R.id.grid_error);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMovies(actualSortOrder);
            }
        });

        favoriteErrorView = (TextView) findViewById(R.id.favorite_error);

        progressBar = (ProgressBar) findViewById(R.id.grid_loading_indicator);
        moviesAdapter = new MoviesAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(moviesAdapter);

        loadMovies(actualSortOrder);
    }

    @Override
    public void onMovieClick(MovieDBResponse movie) {
        Intent movieIntent = new Intent(MainActivity.this, MovieActivity.class);
        movieIntent.putExtra("movieData", movie);

        startActivity(movieIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_order_menu, menu);
        this.menu = menu;

        menu.getItem(MovieDBHelper.SortOrder.FAVORITE.ordinal()).setChecked(false);
        menu.getItem(MovieDBHelper.SortOrder.TOP_RATED.ordinal()).setChecked(false);
        menu.getItem(MovieDBHelper.SortOrder.POPULAR.ordinal()).setChecked(false);
        menu.getItem(actualSortOrder.ordinal()).setChecked(true);
        return true;
    }

    /**
     * Only one menu item can be selected at one time,
     * so when eg. menu item from index 0 is checked then
     * item from index 1 should be unchecked.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.isChecked() && errorView.getVisibility() == View.GONE) {
            return false;
        }

        if(item.getItemId() == R.id.item_most_popular) {
            menu.getItem(MovieDBHelper.SortOrder.TOP_RATED.ordinal()).setChecked(false);
            menu.getItem(MovieDBHelper.SortOrder.FAVORITE.ordinal()).setChecked(false);
            loadMovies(MovieDBHelper.SortOrder.POPULAR);
            actualSortOrder = MovieDBHelper.SortOrder.POPULAR;
        } else if(item.getItemId() == R.id.item_top_rated) {
            menu.getItem(MovieDBHelper.SortOrder.POPULAR.ordinal()).setChecked(false);
            menu.getItem(MovieDBHelper.SortOrder.FAVORITE.ordinal()).setChecked(false);
            loadMovies(MovieDBHelper.SortOrder.TOP_RATED);
            actualSortOrder = MovieDBHelper.SortOrder.TOP_RATED;
        } else if(item.getItemId() == R.id.item_favorite) {
            menu.getItem(MovieDBHelper.SortOrder.POPULAR.ordinal()).setChecked(false);
            menu.getItem(MovieDBHelper.SortOrder.TOP_RATED.ordinal()).setChecked(false);
            loadMovies(MovieDBHelper.SortOrder.FAVORITE);
            actualSortOrder = MovieDBHelper.SortOrder.FAVORITE;
        }

        item.setChecked(true);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState(outState);
    }

    @Override
    protected void onStart() {
        Log.d(LOGGING_KEY, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(LOGGING_KEY, "onResume");
        super.onResume();

        if(actualSortOrder == MovieDBHelper.SortOrder.FAVORITE) {
            loadMovies(actualSortOrder);
        }

    }

    @Override
    protected void onPause() {
        Log.d(LOGGING_KEY, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(LOGGING_KEY, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOGGING_KEY, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d(LOGGING_KEY, "onRestart");
        super.onRestart();
    }

    private int getScreenOrientation() {
        return this.getResources().getConfiguration().orientation;
    }

    /**
     * Fetching movie posters form moviedb rest service or content provider (if favorite)
     * and putting them into movies grid.
     *
     */
    public class FetchMoviesTask extends AsyncTask<MovieDBHelper.SortOrder, Void, MovieDBResponse[]>{
        private MovieDBHelper.SortOrder actualOrder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieDBResponse[] doInBackground(MovieDBHelper.SortOrder... params) {
            MovieDBResponse response[];

            actualOrder = params[0];

            if(params[0] == MovieDBHelper.SortOrder.FAVORITE) {
                Cursor query = FavoriteProviderHelper.selectAllFavoritesCursor(getContentResolver());
                response = movieDBHelper.getMovies(query);
                Log.d(LOGGING_KEY, "ContentProvider query size: " + Integer.toString(query.getCount()));
            } else {
                response = movieDBHelper.getMovies(params[0]);
            }
            Log.d(LOGGING_KEY, "Response from moviedbapi: " + (response==null?"null":response.length));

            return response;
        }

        @Override
        protected void onPostExecute(MovieDBResponse[] movieDBResponses) {
            progressBar.setVisibility(View.INVISIBLE);
            moviesAdapter.setMovies(movieDBResponses);

            if(movieDBResponses.length == 0) {
                if(actualOrder == MovieDBHelper.SortOrder.FAVORITE) {
                    favoriteErrorView.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                } else {
                    errorView.setVisibility(View.VISIBLE);
                    favoriteErrorView.setVisibility(View.GONE);
                }
            } else {
                errorView.setVisibility(View.GONE);
                favoriteErrorView.setVisibility(View.GONE);
            }

            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void saveState(Bundle outState) {
        outState.putString(SORT_ORDER_SAVE_KEY, actualSortOrder.name());
    }

    private void restoreState(Bundle state) {
        if(state == null) {
            actualSortOrder = MovieDBHelper.SortOrder.POPULAR;
        } else if(state.get(SORT_ORDER_SAVE_KEY) != null) {
            actualSortOrder = MovieDBHelper.SortOrder.valueOf((String)state.get(SORT_ORDER_SAVE_KEY));
        }
    }

    private void loadMovies(MovieDBHelper.SortOrder sortOrder) {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(sortOrder);
    }

    private void initMovieDBApi() {
        Context ctx = MainActivity.this;
        movieDBHelper = new MovieDBHelper(ctx.getString(R.string.api_key_value));
    }
}
