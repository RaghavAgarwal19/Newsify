package com.example.raghav.newsify;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mNewsList;
    private String url = "https://content.guardianapis.com/search?api-key=76868353-6248-4365-be81-bf40228b6b64&page-size=50&";
    private EditText mSearchEditText;

    public List<News> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);

        mNewsList = (ListView) findViewById(R.id.news_list);
        new FetchNews().execute(url);
        mSearchEditText = (EditText) findViewById(R.id.search);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String text = mSearchEditText.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String q = "q="+text;
                    String qUrl = url + q;
                    new FetchNews().execute(qUrl);
                    mSearchEditText.clearFocus();
                    InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                }
                return true;
            }
        });

    }

    private class FetchNews extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            URL newsUrl = null;
            String jsonResponse = null;
            try {
                newsUrl = new URL(urls[0]);
            } catch (MalformedURLException e) {
                Log.e("creating url", "createUrl: Problem building URL", e);
            }
            try {
                if (newsUrl == null) {
                    return jsonResponse;
                }
                HttpURLConnection urlConnection = null;
                InputStream inputStream = null;
//        Create the connection
                try {
                    urlConnection = (HttpURLConnection) newsUrl.openConnection();
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        inputStream = urlConnection.getInputStream();
                        StringBuilder output = new StringBuilder();
                        if (inputStream != null) {
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            String line = bufferedReader.readLine();
                            while (line != null) {
                                output.append(line);
                                line = bufferedReader.readLine();
                            }
                        }
                        jsonResponse = output.toString();
                    } else {
                        Log.v("making request", "makeHttpRequest: Error code : " + urlConnection.getResponseCode());
                    }
                } catch (IOException ioe) {
                    Log.v("making request", "makeHttpRequest: Could not retrieve JSON", ioe);
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } catch (IOException ioe) {
                Log.v("fetching data", "fetchNewsData:Problem makinh HTTPRequest " + ioe);
            }
            String title, author, date, urlSource;
//          Check for json
            if (TextUtils.isEmpty(jsonResponse)) {
                return null;
            }
            List<News> news = new ArrayList<>();
            try {
                JSONObject baseJSONResponse = new JSONObject(jsonResponse);

                JSONObject baseJSONResponseResult = baseJSONResponse.getJSONObject("response");

                JSONArray currentNewsArticles = baseJSONResponseResult.getJSONArray("results");

//            make items
                for (int n = 0; n < currentNewsArticles.length(); n++) {
                    JSONObject currentArticle = currentNewsArticles.getJSONObject(n);
                    title = currentArticle.getString("webTitle");
                    urlSource = currentArticle.getString("webUrl");
                    date = currentArticle.getString("webPublicationDate");
                    News news1 = new News(title, date, url);
                    news.add(news1);
                }
            } catch (JSONException e) {
                Log.v("extracting from json", "extractNewsFromJson: Problem parsing result", e);
            }
            newsList = news;
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equalsIgnoreCase("executed"))
                Log.v("postExecute", "fetched");
            if (newsList.size() == 0)
                Toast.makeText(getApplicationContext(), "Could not find any results for your search", Toast.LENGTH_LONG).show();
            else {
                NewsAdapter adapter = new NewsAdapter(getApplicationContext(), newsList);
                mNewsList.setAdapter(adapter);
            }
        }
    }

}
