package com.SJTU7.Tiktok;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.VideoViewHolder>{

    private List<VideoItem> videoItemList;
    public void setData(List<VideoItem> videoList){
        videoItemList = videoList;
        notifyDataSetChanged();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        private SimpleDraweeView coverSD;
        private TextView fromTV;
        private TextView toTV;
        private TextView contentTV;
        private View videoView;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView;
            fromTV = itemView.findViewById(R.id.tv_from);
            toTV = itemView.findViewById(R.id.tv_to);
            contentTV = itemView.findViewById(R.id.tv_content);
            coverSD = itemView.findViewById(R.id.sd_cover);
        }

    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed,parent,false);
        VideoViewHolder holder = new VideoViewHolder(view);


        holder.videoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                VideoItem video = videoItemList.get(position);
                Intent intent = new Intent(v.getContext(), videoPlay.class);
                intent.putExtra("videoUrl", video.getVideoUrl());
                v.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem videoItem = videoItemList.get(position);
        holder.coverSD.setImageURI(videoItem.getImageUrl());
        holder.fromTV.setText("@"+videoItem.getUserName());
        holder.contentTV.setText(videoItem.getContent());
        holder.toTV.setText("Published at: "+videoItem.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return videoItemList==null?0:videoItemList.size();
    }

}