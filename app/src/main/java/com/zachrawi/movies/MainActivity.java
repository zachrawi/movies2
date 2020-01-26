package com.zachrawi.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    GridView mGridView;
    ProgressBar mProgressBar;
    ArrayList<Movie> mMovies;
    CustomAdapter mCustomAdapter;

    private int mPage = 1;
    private boolean isLoading = false;
    private int mTotalPages = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) ambil gridview & progressbar
        mGridView = findViewById(R.id.gridView);
        mProgressBar = findViewById(R.id.progressBar);

        // 2) inisial movies (kosong)
        mMovies = new ArrayList<>();

        // 3) setup custom adapter
        mCustomAdapter = new CustomAdapter(this, R.layout.item_movie, mMovies);

        // 4) set adapter dari gridview
        mGridView.setAdapter(mCustomAdapter);

        // set on item click
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovies.get(position);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                intent.putExtra("id", movie.getId());
                intent.putExtra("title", movie.getTitle());
                intent.putExtra("poster", movie.getPoster());
                intent.putExtra("backdrop", movie.getBackdrop());
                intent.putExtra("overview", movie.getOverview());

                startActivity(intent);
            }
        });

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("###", "onScroll: " + firstVisibleItem + " - " + visibleItemCount + " - " + totalItemCount);

                int threshold = totalItemCount - visibleItemCount;

                if (firstVisibleItem >= threshold && totalItemCount > 0 && !isLoading && mPage <= mTotalPages) {
                    // load halaman berikutnya
                    Log.d("###", "onScroll: load halaman berikutnya");
                    mPage = mPage + 1;
                    loadPage(mPage);
                }
            }
        });

        loadPage(mPage);
    }

    private void loadPage(int page) {
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=490bc3f8bd238721511d3c3c21b9e925&language=en-US&region=ID&page=" + page;

        // sedang loading
        isLoading = true;

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("###", "onFailure: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(json);

                        mTotalPages = jsonObject.getInt("total_pages");
                        JSONArray results = jsonObject.getJSONArray("results");
                        for (int index = 0; index < results.length(); index++) {
                            JSONObject result = results.getJSONObject(index);
                            int id = result.getInt("id");
                            String title = result.getString("title");
                            String overview = result.getString("overview");
                            String poster = "https://image.tmdb.org/t/p/w500" + result.getString("poster_path");
                            String backdrop = "https://image.tmdb.org/t/p/original" + result.getString("backdrop_path");

                            Movie movie = new Movie(id, title, overview, poster, backdrop);

                            mMovies.add(movie);

                            // beri tau custom adapter
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCustomAdapter.notifyDataSetChanged();

                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    mGridView.setVisibility(View.VISIBLE);

                                    // selesai loading
                                    isLoading = false;
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
