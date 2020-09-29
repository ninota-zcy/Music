package com.example.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> implements View.OnClickListener{
    private List<Music> musicList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int posotion);
    }
    public AddAdapter(List<Music> list){
        this.musicList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_item,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAdapter.ViewHolder holder, int position) {
        final Music myMusic = musicList.get(position);
        holder.name.setText(myMusic.getMusicName());

        holder.itemView.setTag(position);

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMusic.getIsCheck()){
                    myMusic.setIsCheck(false);
                }else {
                    myMusic.setIsCheck(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox box;
        TextView name;

        ViewHolder(View item){
            super(item);
            box = item.findViewById(R.id.check);
            name = item.findViewById(R.id.name);

        }
    }
}
