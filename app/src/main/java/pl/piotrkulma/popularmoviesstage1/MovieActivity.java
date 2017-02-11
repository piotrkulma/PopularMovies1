package pl.piotrkulma.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import pl.piotrkulma.popularmoviesstage1.data.provider.FavoriteProviderHelper;
import pl.piotrkulma.popularmoviesstage1.databinding.ActivityMovieDetailsBinding;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBReviewResponse;
import pl.piotrkulma.popularmoviesstage1.model.MovieDBTrailersResponse;
import pl.piotrkulma.popularmoviesstage1.utility.MovieDBHelper;

/**
 * Activity for displaying single movie details.
 *
 */
public class MovieActivity extends AppCompatActivity {
    public static final String FAVORITE_TAG_VALUE_ON = "ON";
    public static final String FAVORITE_TAG_VALUE_OFF = "OFF";
    private String LOGGING_KEY = MovieActivity.class.getName();

    private ImageView poster;
    private LinearLayout trailersLayout;
    private LinearLayout reviewsLayout;

    private ImageView favorite;

    private MovieDBResponse movieData;
    //private ProgressBar progressBar;

    private ActivityMovieDetailsBinding movieDetailsBinding;
    private MovieDBHelper movieDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMovieDBApi();
        initViews();
        bindData();

        favorite = (ImageView) findViewById(R.id.favorite);

        refreshFavoriteImage();

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FAVORITE_TAG_VALUE_OFF.equals(v.getTag(R.string.favorite_tag_key))) {
                    Uri uri = FavoriteProviderHelper.addNewFavorite(getContentResolver(), movieData);

                    Toast.makeText(getApplicationContext(), "Added to favorite list", Toast.LENGTH_SHORT).show();
                    Log.d(LOGGING_KEY, "SELECTING AS FAVORITE " + uri.toString());
                } else {
                    int count = FavoriteProviderHelper.removeFavorite(getContentResolver(), movieData.getId());

                    Toast.makeText(getApplicationContext(), "Removed from favorite list", Toast.LENGTH_SHORT).show();
                    Log.d(LOGGING_KEY, "UNSELECTING AS FAVORITE " + count);
                }

                refreshFavoriteImage();
            }
        });
    }

    private void refreshFavoriteImage() {
        Cursor cursor = FavoriteProviderHelper.selectFavoritesCursorById(getContentResolver(), movieData.getId());

        if(cursor.getCount() > 0) {
            favorite.setImageResource(android.R.drawable.star_big_on);
            favorite.setTag(R.string.favorite_tag_key, FAVORITE_TAG_VALUE_ON);
        } else {
            favorite.setImageResource(android.R.drawable.star_big_off);
            favorite.setTag(R.string.favorite_tag_key, FAVORITE_TAG_VALUE_OFF);
        }
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

        movieData = (MovieDBResponse)intentFromParent.getSerializableExtra("movieData");
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

            showTrailers(movieDBResponse);
            showReviews(movieDBResponse);

            movieDetailsBinding.setMovie(movieDBResponse);
        }

        private void showReviews(MovieDBResponse movieDBResponse) {
            if(movieDBResponse.getReviews() != null && movieDBResponse.getReviews().length > 0) {
                for(int i=0; i<movieDBResponse.getReviews().length; i++) {
                    MovieDBReviewResponse review = movieDBResponse.getReviews()[i];
                    reviewsLayout.addView(newRewiewItem(review), i);
                }
            }
        }

        private void showTrailers(MovieDBResponse movieDBResponse) {
            if(movieDBResponse.getVideos() != null && movieDBResponse.getVideos().length > 0) {
                for(int i=0; i<movieDBResponse.getVideos().length; i++) {
                    MovieDBTrailersResponse trailer = movieDBResponse.getVideos()[i];
                    trailersLayout.addView(newTrailerItem(trailer), i);
                }
            }
        }

        private String getReviewShort(String fullReview) {
            if(fullReview != null && fullReview.length() > 30) {
                return fullReview.substring(0, 29) + "...(show more)";
            }

            return fullReview;
        }

        private ConstraintLayout newTrailerItem(final MovieDBTrailersResponse trailer) {
            final Context ctx = getApplicationContext();

            ConstraintLayout constraintLayout = (ConstraintLayout)getLayoutInflater().inflate(R.layout.trailer_item, null);

            TextView textView = (TextView) constraintLayout.findViewById(R.id.movie_trailer_name);
            textView.setText(trailer.getName() + " (" + trailer.getSite() + " " + trailer.getSize()  + ")");

            ImageButton shareButton = (ImageButton) constraintLayout.findViewById(R.id.movie_trailers_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ShareCompat.IntentBuilder
                                .from(MovieActivity.this)
                                .setChooserTitle("Share " + trailer.getSite() + " trailer")
                                .setType("text/html")
                                .setText(trailer.getSiteUrl())
                                .startChooser();
                    } catch(Exception e) {
                        Log.e(LOGGING_KEY, "Cannot start sharing: " + e.getMessage());
                        Toast.makeText(ctx, "xxx", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ImageButton playButton = (ImageButton) constraintLayout.findViewById(R.id.movie_trailers_play_button);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getSiteUrl()));
                        startActivity(myIntent);
                    } catch(Exception e) {
                        Log.e(LOGGING_KEY, "Cannot start activity: " + e.getMessage());
                        Toast.makeText(ctx, "xxx", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return constraintLayout;
        }

        private LinearLayout newRewiewItem(final MovieDBReviewResponse review) {
            LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.review_item, null);

            TextView userName = (TextView)linearLayout.findViewById(R.id.movie_reviews_user_name);
            userName.setText("Review by: " + review.getAuthor());

            final TextView shortReview = (TextView)linearLayout.findViewById(R.id.movie_reviews_user_review_short);
            shortReview.setText(getReviewShort(review.getContent()));

            final TextView fullReview = (TextView)linearLayout.findViewById(R.id.movie_reviews_user_review_full);
            fullReview.setText(review.getContent() + " (show less)");

            shortReview.setVisibility(View.VISIBLE);
            fullReview.setVisibility(View.GONE);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(shortReview.getVisibility() == View.GONE) {
                        shortReview.setVisibility(View.VISIBLE);
                        fullReview.setVisibility(View.GONE);
                    } else {
                        shortReview.setVisibility(View.GONE);
                        fullReview.setVisibility(View.VISIBLE);
                    }
                }
            });

            return linearLayout;
        }
    }
}
