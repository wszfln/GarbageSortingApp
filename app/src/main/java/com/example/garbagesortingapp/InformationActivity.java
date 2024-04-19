package com.example.garbagesortingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class InformationActivity extends AppCompatActivity {
    private Toolbar tlb5;
    private ImageButton imgBtnReturn5;
    private ImageButton imgBtnAccount5;
    private TextView newsTitle;
    private ListView newsList;
    private static final String BASE_URL = "https://newsapi.org/";
    private static final String API_KEY = "b8597fcd38d944df837eff3d842e1b99";
    private NewsService newsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        // Get component
        tlb5 = findViewById(R.id.tlb5);
        // Set the Toolbar as the app bar for the activity
        setSupportActionBar(tlb5);
        imgBtnReturn5 = findViewById(R.id.imgBtnReturn5);
        imgBtnAccount5 = findViewById(R.id.imgBtnAccount5);
        newsTitle = findViewById(R.id.newsTitle);
        newsList = findViewById(R.id.newsList);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newsService = retrofit.create(NewsService.class);
        loadNews();

        imgBtnReturn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // 结束当前活动
            }
        });

    }
    private void loadNews() {
        newsService.getNews("Ireland AND recycling AND 'waste management'", API_KEY).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final List<Article> articles = response.body().getArticles();
                    List<String> titles = articles.stream()
                            .map(article -> article.getTitle())
                            .collect(Collectors.toList());
                    // Using the custom adapter
                    NewsAdapter adapter = new NewsAdapter(InformationActivity.this, titles);
                    newsList.setAdapter(adapter);

                    // Set an item click listener to the ListView
                    newsList.setOnItemClickListener((parent, view, position, id) -> {
                        Article selectedArticle = articles.get(position);
                        String url = selectedArticle.getUrl();
                        // Start WebViewActivity with the URL
                        Intent intent = new Intent(InformationActivity.this, WebViewActivity.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    });
                } else {
                    // Log error message
                    Log.e("NewsApi", "Error: " + response.code() + " " + response.message());
                    // Display a toast or use a dialog box to notify the user
                    Toast.makeText(InformationActivity.this, "Error loading news: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                // Log exception
                Log.e("NewsApi", "Network Error: ", t);
                // Show error message to user
                Toast.makeText(InformationActivity.this, "Network error, please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // 保存状态
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 检查适配器中的数据是否可用
        if (newsList.getAdapter() != null) {
            ArrayList<String> titles = new ArrayList<>();
            for (int i = 0; i < newsList.getAdapter().getCount(); i++) {
                titles.add((String) newsList.getAdapter().getItem(i));
            }
            outState.putStringArrayList("newsTitles", titles);
        }
    }

    public class Article {
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        // Getters
        public String getTitle() {
            return title;
        }
        public String getDescription() {
            return description;
        }
        public String getUrl() {
            return url;
        }
        public String getUrlToImage() {
            return urlToImage;
        }
    }

    public class NewsResponse {
        private List<Article> articles;

        public List<Article> getArticles() {
            return articles;
        }
    }

    interface NewsService {
        @GET("v2/everything")
        Call<NewsResponse> getNews(
                @Query("q") String query,
                @Query("apiKey") String apiKey);
    }

    public class NewsAdapter extends BaseAdapter {
        private Context context;
        private List<String> newsTitles;

        public NewsAdapter(Context context, List<String> newsTitles) {
            this.context = context;
            this.newsTitles = newsTitles;
        }

        @Override
        public int getCount() {
            return newsTitles.size();
        }

        @Override
        public Object getItem(int position) {
            return newsTitles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_news, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.textViewItem);
            textView.setText(newsTitles.get(position));
            return convertView;
        }
    }
}
