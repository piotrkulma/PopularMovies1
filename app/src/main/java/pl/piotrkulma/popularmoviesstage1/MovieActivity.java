package pl.piotrkulma.popularmoviesstage1;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import pl.piotrkulma.popularmoviesstage1.databinding.ActivityMovieDetailsBinding;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

/**
 * Activity for displaying single movie details.
 *
 */
public class MovieActivity extends AppCompatActivity {
    private ImageView poster;
    private ProgressBar progressBar;

    private ActivityMovieDetailsBinding movieDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        bindData();
    }

    private void initViews() {
        movieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        poster = (ImageView) findViewById(R.id.poster);
        progressBar = (ProgressBar) findViewById(R.id.movie_progressbar);
    }

    private void bindData() {
        hideAll();
        Intent intentFromParent = getIntent();

        MovieDBResponse movieData = (MovieDBResponse)intentFromParent.getSerializableExtra("movieData");
        movieDetailsBinding.setMovie(movieData);

        Picasso.with(MovieActivity.this).load(movieData.getPosterUrl()).into(poster);
        showAll();
    }

    private void fillMovieData(MovieDBResponse movieData) {
        //hideAll();
/*
        title.setText(movieData.getTitle());
        releaseDate.setText(movieData.getReleaseDateYear());
        avgVotes.setText(movieData.getVoteAgerageFull());
        synopsis.setText(movieData.getPlotSynopsis());

        Picasso.with(MovieActivity.this).load(movieData.getPosterUrl()).into(poster);*/
        //showAll();
    }

    private void showAll() {
        poster.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideAll() {
        poster.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
