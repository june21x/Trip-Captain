package com.june.tripcaptain;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.june.tripcaptain.DataClass.News;
import com.june.tripcaptain.Helper.GlideApp;

public class NewsActivity extends AppCompatActivity {
    private TextView tvDate;
    private TextView tvAuthor;
    private ImageView ivImage;
    private TextView tvTitle;
    private TextView tvContent;
    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setTitle("News");


        tvDate = findViewById(R.id.tvDate);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvTitle = findViewById(R.id.tvTitle);
        ivImage = findViewById(R.id.ivImage);
        tvContent = findViewById(R.id.tvContent);

        Intent intent = getIntent();
        news = intent.getParcelableExtra("News");

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy h:mm a");
        tvDate.setText(dateFormat.format(news.getDate()));
        tvAuthor.setText("by " + news.getAuthor());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(news.getImageUriStr());

        try {
            GlideApp.with(this).load(storageRef).into(ivImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvTitle.setText(news.getTitle());
        tvContent.setText(news.getContent());
    }
}
