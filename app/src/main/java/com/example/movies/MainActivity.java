package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mMoviesGrid;
    private static final int spanCount = 2;
    private GridAdapter mAdapter;
    final static String POSTER_PATH = "poster_path";
    final static String TITLE = "title";
    final static String VOTE_COUNT = "vote_count";
    final static String VOTE_AVERAGE = "vote_average";
    final static String OVERVIEW = "overview";
    final static String RELEASE_DATE = "release_date";
    static String defaultOrder = "top_rated";
    private ArrayList<HashMap<String, String>> moviesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mMoviesGrid = (RecyclerView) findViewById(R.id.movies_grid);
        //Create Grid layout manager and set the layout manager of above procured recycler view
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        mMoviesGrid.setLayoutManager(layoutManager);

        //Create recycler view adapter
         mAdapter = new GridAdapter(this, (GridAdapter.GridAdapterOnClickHandler) this);

//       set adapter of recycler view to this adapter

        mMoviesGrid.setAdapter(mAdapter);
        load(defaultOrder);
    }


    public void load(String sort_type)
    {
        new MovieDbQueryTask().execute(sort_type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelectedId = item.getItemId();
        switch (itemSelectedId){
            case R.id.sort_popularity:
                Toast.makeText(this, "Now Showing: Popular Movies!", Toast.LENGTH_LONG).show();
                defaultOrder = "popular";
                load(defaultOrder);
                return super.onOptionsItemSelected(item);
            case R.id.sort_rated:
                Toast.makeText(this, "Now Showing: Top-Rated Movies!", Toast.LENGTH_LONG).show();
                defaultOrder = "top_rated";
                load(defaultOrder);
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onListItemClick(int clickedItemIndex) {
        String posterPath = moviesList.get(clickedItemIndex).get(POSTER_PATH);
        String title = moviesList.get(clickedItemIndex).get(TITLE);
        String voteCount = moviesList.get(clickedItemIndex).get(VOTE_COUNT);
        String voteAverage = moviesList.get(clickedItemIndex).get(VOTE_AVERAGE);
        String overview = moviesList.get(clickedItemIndex).get(OVERVIEW);
        String releaseDate = moviesList.get(clickedItemIndex).get(RELEASE_DATE);
        Log.v("MovieArrayData", title);


       //intent to the details activity

        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        //sending additional info using bundle
        Bundle extras = new Bundle();
        extras.putString("Poster_Path", posterPath);
        extras.putString("Title", title);
        extras.putString("Vote_Count", voteCount);
        extras.putString("Vote_Average", voteAverage);
        extras.putString("Overview", overview);
        extras.putString("Release_Date", releaseDate);
        intentToStartDetailActivity.putExtras(extras);
        startActivity(intentToStartDetailActivity);
    }


    public class MovieDbQueryTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
            String queryType = params[0];
            URL queryURL = NetworkUtil.buildUrl(queryType);
            try{
                moviesList.clear();
                JSONObject movieResponse = new JSONObject(NetworkUtil.getResponseFromHttpUrl(queryURL));
                JSONArray movieJsonArray = movieResponse.getJSONArray("results");
                for (int i = 0; i < movieJsonArray.length(); i++) {
                    JSONObject movieJSON = movieJsonArray.getJSONObject(i);
                    HashMap<String, String> movie = new HashMap<>();
                    movie.put(POSTER_PATH, movieJSON.getString(POSTER_PATH));
                    movie.put(TITLE, movieJSON.getString(TITLE));
                    movie.put(VOTE_COUNT, movieJSON.getString(VOTE_COUNT));
                    movie.put(VOTE_AVERAGE, movieJSON.getString(VOTE_AVERAGE));
                    movie.put(OVERVIEW, movieJSON.getString(OVERVIEW));
                    movie.put(RELEASE_DATE, movieJSON.getString(RELEASE_DATE));
                    moviesList.add(movie);
                }
                return moviesList;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> hashMaps) {
            if(hashMaps != null){
                mAdapter.setMovieData(hashMaps);
            }
            super.onPostExecute(hashMaps);
            mAdapter.notifyDataSetChanged();
        }
    }
}
