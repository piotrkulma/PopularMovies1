package pl.piotrkulma.popularmoviesstage1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

public class MovieActivity extends AppCompatActivity {
    private TextView title;
    private TextView releaseDate;
    private ImageView poster;
    private TextView avgVotes;
    private TextView synopsis;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent intentFromParent = getIntent();

        MovieDBResponse movieData = (MovieDBResponse)intentFromParent.getSerializableExtra("movieData");

        fillMovieData(movieData);
    }

    private void fillMovieData(MovieDBResponse movieData) {
        title = (TextView) findViewById(R.id.movie_title);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        poster = (ImageView) findViewById(R.id.movie_poster);
        avgVotes = (TextView) findViewById(R.id.movie_avg_votes);
        synopsis = (TextView) findViewById(R.id.movie_synopsis);
        progressBar = (ProgressBar) findViewById(R.id.movie_progressbar);

        showAll();

        title.setText(movieData.getTitle());
        releaseDate.setText(movieData.getReleaseDate());
        avgVotes.setText(movieData.getVoteAverage());
        synopsis.setText(movieData.getPlotSynopsis());

        Picasso.with(MovieActivity.this).load(movieData.getPosterUrl()).into(poster);

        //showAll();
    }

    private void showAll() {
        title.setVisibility(View.VISIBLE);
        releaseDate.setVisibility(View.VISIBLE);
        poster.setVisibility(View.VISIBLE);
        avgVotes.setVisibility(View.VISIBLE);
        synopsis.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideAll() {
        title.setVisibility(View.INVISIBLE);
        releaseDate.setVisibility(View.INVISIBLE);
        poster.setVisibility(View.INVISIBLE);
        avgVotes.setVisibility(View.INVISIBLE);
        synopsis.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
