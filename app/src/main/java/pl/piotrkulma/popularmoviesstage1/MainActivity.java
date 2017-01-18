package pl.piotrkulma.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import pl.piotrkulma.popularmoviesstage1.utility.MovieDBHelper;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickHandler {
    private static int COLUMNS_IN_GRID = 2;
    private String LOGGING_KEY = MainActivity.class.getName();

    private MovieDBHelper movieDBHelper;

    private MoviesAdapter moviesAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMovieDBApi();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, COLUMNS_IN_GRID);

        progressBar = (ProgressBar) findViewById(R.id.grid_loading_indicator);
        moviesAdapter = new MoviesAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(moviesAdapter);

        loadMovies();
    }

    @Override
    public void onMovieClick(MovieDBResponse movie) {
        Intent movieIntent = new Intent(MainActivity.this, MovieActivity.class);
        movieIntent.putExtra("movieData", movie);

        startActivity(movieIntent);
    }

    public class FetchMoviesTask extends AsyncTask<MovieDBHelper.SortOrder, Void, MovieDBResponse[]>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieDBResponse[] doInBackground(MovieDBHelper.SortOrder... params) {
            MovieDBResponse response[] = movieDBHelper.getMovieDBResponses(params[0]);
            Log.d(LOGGING_KEY, "Response from moviedbapi: " + (response==null?"null":response.length));

            return response;
        }

        @Override
        protected void onPostExecute(MovieDBResponse[] movieDBResponses) {
            progressBar.setVisibility(View.INVISIBLE);
            moviesAdapter.setMovies(movieDBResponses);
        }
    }

    private void loadMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute(MovieDBHelper.SortOrder.POPULAR);
    }

    private void initMovieDBApi() {
        Context ctx = MainActivity.this;
        movieDBHelper = new MovieDBHelper(ctx.getString(R.string.api_key_value));
    }
}
