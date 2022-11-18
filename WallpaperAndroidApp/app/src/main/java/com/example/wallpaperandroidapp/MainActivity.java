package com.example.wallpaperandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    List<DataHandler> dataHandlerList;
    SwipeRefreshLayout swipeRefreshLayout;
    WallpaperAdapter wallpaperAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.list);
        dataHandlerList = new ArrayList<>();
        swipeRefreshLayout = findViewById(R.id.swipe);

        loadData("First");
        swipeRefreshLayout.setOnRefreshListener(() -> loadData("Refresh"));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String title, image;
            title = dataHandlerList.get(position).getTitle();
            image = dataHandlerList.get(position).getImage();

            Intent intent = new Intent(getApplicationContext(), ViewWallpaper.class);
            intent.putExtra("title", title);
            intent.putExtra("image", image);
            startActivity(intent);
        });
    }

    private void loadData(String type) {
        swipeRefreshLayout.setRefreshing(true);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://wallpaper-apps.herokuapp.com/apis/",
                response -> {
                    swipeRefreshLayout.setRefreshing(false);
                    parseJSON(response, type);
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        queue.add(stringRequest);
    }

    private void parseJSON(String res, String type) {
        String title, thumbnail, image;

        if(type.equals("Refresh")) {
            dataHandlerList.clear();
            wallpaperAdapter.notifyDataSetChanged();
        }

        try {
            JSONArray jsonArray = new JSONArray(res);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                title = jsonObject.get("title").toString();
                thumbnail = jsonObject.get("thumbnail").toString();
                image = jsonObject.get("image").toString();

                dataHandlerList.add( new DataHandler(title, thumbnail, image));
            }
            wallpaperAdapter = new WallpaperAdapter(getApplicationContext(), R.layout.list_items, dataHandlerList);
            listView.setAdapter(wallpaperAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}