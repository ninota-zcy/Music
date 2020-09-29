package com.example.music;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AppCompatSeekBar.OnSeekBarChangeListener , MediaPlayer.OnCompletionListener{

    private TextView start;
    private TextView end;
    private Button play;
    private Button last;
    private Button next;
    private RecyclerView list;

    private MediaPlayer mediaPlayer;
    private MusicAdapter musicAdapter;//recyclerView的适配器，用于显示音乐列表
    private List<Music> musicList;
    private AppCompatSeekBar seekBar;
    private int position = -1;//定位当前播放的音乐


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, Another.class);
        startActivity(intent);
        return true;
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(new Handler.Callback() {  //在这里实现seekBar的动态更新
        @Override
        public boolean handleMessage(Message message) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            start.setText(parseDate(mediaPlayer.getCurrentPosition()));
            updateProgress();//发送更新seekBar的消息
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        start = findViewById(R.id.time_start);
        end = findViewById(R.id.time_end);
        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        play = findViewById(R.id.music_play);
        last = findViewById(R.id.last_music);
        next = findViewById(R.id.next_music);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicList.size() != 0){
                    if (mediaPlayer == null) {
                        changeMusic(0);
                        position = 0;
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            play.setBackgroundResource(R.drawable.ic_pause);

                        } else {
                            mediaPlayer.start();
                            play.setBackgroundResource(R.drawable.ic_playing);

                        }
                    }
                }else
                    Toast.makeText(MainActivity.this,"音乐列表为空",Toast.LENGTH_LONG).show();




            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicList.size() != 0) {
                    changeMusic(--position);
                }else
                    Toast.makeText(MainActivity.this,"音乐列表为空",Toast.LENGTH_LONG).show();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicList.size() != 0) {
                    changeMusic(++position);
                }else
                    Toast.makeText(MainActivity.this,"音乐列表为空",Toast.LENGTH_LONG).show();
            }
        });


        list = findViewById(R.id.music_list);
        musicList = new ArrayList<>();
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        musicAdapter = new MusicAdapter(musicList);
        list.setAdapter(musicAdapter);
        musicAdapter.setSelected(-1);
        musicAdapter.setOnItemClickListener(new MusicAdapter.OnClickListener() {
            @Override
            public void onClick(View v, int position1) {
                position = position1;
                changeMusic(position1);
            }
        });

        musicAdapter.setOnLongClickListener(new MusicAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View v, final int position) {
                new AlertDialog.Builder(MainActivity.this).setTitle("确定从列表中删除该歌曲吗？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                musicList.remove(position);
                                musicAdapter.notifyDataSetChanged();//刷新list
                            }
                        }).create().show();
            }
        });
        //增加分割线
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void changeMusic(int position1) {    //实现歌曲的切换
        if (position1 < 0) {
            position = musicList.size() - 1;
            playMusic(musicList.get(position));
        } else if (position > musicList.size() - 1) {
            position = 0;
            playMusic(musicList.get(0));
        } else {
            playMusic(musicList.get(position1));
            position = position1;
        }
        musicAdapter.setSelected(position);    //设置选中音乐

        //更新RecyclerView，有这一步的原因是我设置了两个布局，正在播放的音乐行布局变更
        musicAdapter.notifyDataSetChanged();
        play.setBackgroundResource(R.drawable.ic_playing); //更新播放、暂停键的图标
    }

    private void playMusic(Music myMusic) {
        try {
            if (mediaPlayer == null) {  //判断是否为空，避免重复创建
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(this);
            }
            mediaPlayer.reset();//播放前重置播放器，其实第一次播放时不需要做此操作，但为了这一方法复用性我选择在这里使用
            mediaPlayer.setDataSource(myMusic.getPath());//设置播放源
            mediaPlayer.prepare();//准备，这一步很关键，在新播放一首歌的时候必不可少
            mediaPlayer.start();//开始播放
            end.setText(parseDate(mediaPlayer.getDuration()));//用来显示音乐时长
            seekBar.setMax(mediaPlayer.getDuration());//设置seekBar的时长与音乐文件相同
            updateProgress();//开启seekBar的更新

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());//将音乐定位到seekBar指定的位置
        updateProgress();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
    private String parseDate(int time) {//cursor获取的时间是毫秒，这里将它转成常用的时间格式
        time = time / 1000;
        int min = time / 60;
        int second = time % 60;
        return min + ":" + second;
    }
    //每秒发送一个空的message，提示handler更新
    private void updateProgress() {
        handler.sendMessageDelayed(Message.obtain(), 1000);
    }


}
