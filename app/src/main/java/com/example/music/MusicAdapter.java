package com.example.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
//RecyclerView 一个在大小有限的窗口内展示大量数据集的view
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements View.OnClickListener{

    int selected;
    List<Music> musicList;
    OnClickListener onItemClickListener;
    OnLongClickListener onItemLongClickListener;
    Context context;

    public interface OnLongClickListener{
        void onLongClick(View v,int position);
    }
    public interface OnClickListener{
        void onClick(View v,int position);
    }

    public void setOnLongClickListener(OnLongClickListener listener){
        this.onItemLongClickListener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_music,parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onItemLongClickListener != null)
                    onItemLongClickListener.onLongClick(v,(Integer)v.getTag());
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.content.removeAllViews();
        if(position == selected){
            holder.content.addView(addFocusView());
            TextView textView = holder.content.findViewById(R.id.music_playing);
            textView.setText("正在播放："+ music.getMusicName());
        }else{
            holder.content.addView(addNormalView());
            TextView name = holder.content.findViewById(R.id.music_name);
            TextView singer = holder.content.findViewById(R.id.music_singer);
            name.setText(music.getMusicName());
            singer.setText(music.getSinger());
        }

        holder.itemView.setTag(position);

    }
//View中的setTag(Onbect)表示给View添加一个格外的数据，以后可以用getTag()将这个数据取出来。
    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public void onClick(View v) {
        if(onItemClickListener != null)
            onItemClickListener.onClick(v , (Integer)v.getTag());
    }

    private View addFocusView(){
        return LayoutInflater.from(context).inflate(R.layout.item_playing,null,false);
    }
    private View addNormalView(){
        return LayoutInflater.from(context).inflate(R.layout.item_normal,null,false);
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }
    public MusicAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout content;

        ViewHolder(View itemView) {
            super(itemView);
            content=itemView.findViewById(R.id.content);
        }
    }
}
