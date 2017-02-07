package pl.piotrkulma.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.piotrkulma.popularmoviesstage1.databinding.ActivityMovieDetailsBinding;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBReviewResponse;
import pl.piotrkulma.popularmoviesstage1.utility.MovieDBHelper;

/**
 * Activity for displaying single movie details.
 *
 */
public class MovieActivity extends AppCompatActivity {
    private ImageView poster;
    private LinearLayout trailersLayout;
    private LinearLayout reviewsLayout;
    //private ProgressBar progressBar;

    private ActivityMovieDetailsBinding movieDetailsBinding;
    private MovieDBHelper movieDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMovieDBApi();
        initViews();
        bindData();
    }

    private void initViews() {
        movieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        trailersLayout = (LinearLayout)findViewById(R.id.movie_trailers);
        reviewsLayout = (LinearLayout)findViewById(R.id.movie_reviews);

        poster = (ImageView) findViewById(R.id.poster);
        //progressBar = (ProgressBar) findViewById(R.id.movie_progressbar);
    }

    private void bindData() {
        hideAll();
        Intent intentFromParent = getIntent();

        MovieDBResponse movieData = (MovieDBResponse)intentFromParent.getSerializableExtra("movieData");
        movieDetailsBinding.setMovie(movieData);

        Picasso.with(MovieActivity.this).load(movieData.getPosterUrl()).into(poster);
        showAll();

        MovieDetailsTask detailsTask = new MovieDetailsTask();
        detailsTask.execute(movieData.getId());
    }

    private void showAll() {
        poster.setVisibility(View.VISIBLE);
        //progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideAll() {
        poster.setVisibility(View.INVISIBLE);
        //progressBar.setVisibility(View.VISIBLE);
    }

    private void initMovieDBApi() {
        Context ctx = getApplicationContext();
        movieDBHelper = new MovieDBHelper(ctx.getString(R.string.api_key_value));
    }

    private class MovieDetailsTask extends AsyncTask<String, Void, MovieDBResponse> {

        @Override
        protected MovieDBResponse doInBackground(String... params) {
            MovieDBResponse response = movieDBHelper.getMovieWithAllDetails(params[0]);
            return response;
        }

        @Override
        protected void onPostExecute(MovieDBResponse movieDBResponse) {
            super.onPostExecute(movieDBResponse);

            showReviews(movieDBResponse);

            movieDetailsBinding.setMovie(movieDBResponse);
        }

        private void showReviews(MovieDBResponse movieDBResponse) {
            if(movieDBResponse.getReviews() != null && movieDBResponse.getReviews().length > 0) {
                for(int i=0; i<movieDBResponse.getReviews().length; i++) {
                    MovieDBReviewResponse review = movieDBResponse.getReviews()[i];
                    reviewsLayout.addView(newTextView(review), i);
                }
            }
        }

        private TextView newTextView(MovieDBReviewResponse review) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText(review.getAuthor());

            return textView;
        }
    }
}
