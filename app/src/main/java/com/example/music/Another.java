package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class Another extends AppCompatActivity {
    private AddAdapter adapter;
    private List<Music> myMusics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        RecyclerView recyclerView = findViewById(R.id.check_music_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        myMusics = new ArrayList<>();
        adapter = new AddAdapter(myMusics);

        Log.v("test","进来添加");
        recyclerView.setAdapter(adapter);

        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            Log.v("test","cursor不为空");
            while (cursor.moveToNext()) {
                //从属性名很容易看出所代表的音乐文件属性，所以一下属性不做讲解了
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                int time = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Log.v("test",id+"   "+ title + "   " + time);

                System.out.println("test:"+id+"   "+ title + "   " + time);
                //myMusic是我自己定义的一个javaBean，用来存放音乐文件
                Music myMusic = new Music(id, title , time);
                myMusics.add(myMusic);
            }
            adapter.notifyDataSetChanged();
        }
        else{
            Log.v("test","cursor为空");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        addMusic();
        finish();
        return true;
    }
    private void addMusic() {
        for (Music m : myMusics) {
            if (m.getIsCheck()) {
                m.save();
            }
        }
    }


}
