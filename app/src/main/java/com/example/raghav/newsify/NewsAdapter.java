package com.example.raghav.newsify;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    Context context;

    public NewsAdapter(@NonNull Context context, List<News> newsList) {
        super(context, 0, newsList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.news_list_item, parent, false);
        }

//        Bind Views
        TextView articleTitle, articleAuthor, articleDate;
        articleTitle = (TextView) convertView.findViewById(R.id.article_title);
        articleDate = (TextView) convertView.findViewById(R.id.article_date);

//        Get current article details
        News currentNews = getItem(position);

//        Get and said the current String values
        articleTitle.setText(currentNews.getArticleTitle());
        articleDate.setText(currentNews.getArticleDate());

        return convertView;
    }
}
