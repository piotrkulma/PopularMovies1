package pl.piotrkulma.popularmoviesstage1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import pl.piotrkulma.popularmoviesstage1.model.MovieDBResponse;

/**
 * Created by Piotr Kulma on 2017-01-18.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private Context context;

    private MovieDBResponse movies[];

    private MovieClickHandler movieClickHandler;

    public MoviesAdapter(MovieClickHandler movieClickHandler) {
        this.movieClickHandler = movieClickHandler;
    }

    public interface MovieClickHandler {
        void onMovieClick(MovieDBResponse movie);
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_grid_item, parent, false);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        MovieDBResponse movie = movies[position];
        Picasso.with(context).load(movie.getPosterUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(movies == null || movies.length == 0) {
            return 0;
        }

        return movies.length;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView imageView;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            movieClickHandler.onMovieClick(movies[index]);
        }
    }

    public void setMovies(MovieDBResponse[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}
