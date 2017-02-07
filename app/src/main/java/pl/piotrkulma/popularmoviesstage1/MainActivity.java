package pl.piotrkulma.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
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

import pl.piotrkulma.popularmoviesstage1.utility.MovieDBHelper;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

/**
 * Main activity showing movies grid
 *
 */

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickHandler {
    private static int COLUMNS_IN_GRID = 2;
    private String LOGGING_KEY = MainActivity.class.getName();

    private MovieDBHelper movieDBHelper;

    private MoviesAdapter moviesAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private TextView errorView;

    private Menu menu;

    private MovieDBHelper.SortOrder actualSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMovieDBApi();

        actualSortOrder = MovieDBHelper.SortOrder.POPULAR;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, COLUMNS_IN_GRID);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        errorView = (TextView) findViewById(R.id.grid_error);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMovies(actualSortOrder);
            }
        });

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
        if(item.isChecked() && errorView.getVisibility() == View.INVISIBLE) {
            return false;
        }

        if(item.getItemId() == R.id.item_most_popular) {
            menu.getItem(1).setChecked(false);
            loadMovies(MovieDBHelper.SortOrder.POPULAR);
            actualSortOrder = MovieDBHelper.SortOrder.POPULAR;
        } else if(item.getItemId() == R.id.item_top_rated) {
            menu.getItem(0).setChecked(false);
            loadMovies(MovieDBHelper.SortOrder.TOP_RATED);
            actualSortOrder = MovieDBHelper.SortOrder.TOP_RATED;
        }

        item.setChecked(true);
        return true;
    }

    /**
     * Fetching movie posters form moviedb rest service and putting them into movies grid.
     *
     */
    public class FetchMoviesTask extends AsyncTask<MovieDBHelper.SortOrder, Void, MovieDBResponse[]>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieDBResponse[] doInBackground(MovieDBHelper.SortOrder... params) {
            MovieDBResponse response[] = movieDBHelper.getMovies(params[0]);
            Log.d(LOGGING_KEY, "Response from moviedbapi: " + (response==null?"null":response.length));

            return response;
        }

        @Override
        protected void onPostExecute(MovieDBResponse[] movieDBResponses) {
            progressBar.setVisibility(View.INVISIBLE);
            moviesAdapter.setMovies(movieDBResponses);

            if(movieDBResponses.length == 0) {
                errorView.setVisibility(View.VISIBLE);
            } else {
                errorView.setVisibility(View.INVISIBLE);
            }

            progressBar.setVisibility(View.INVISIBLE);
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
