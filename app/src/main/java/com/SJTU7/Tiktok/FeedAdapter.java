package com.SJTU7.Tiktok;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
        private TextView tv_friend;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView;
            fromTV = itemView.findViewById(R.id.tv_from);
            toTV = itemView.findViewById(R.id.tv_to);
            contentTV = itemView.findViewById(R.id.tv_content);
            coverSD = itemView.findViewById(R.id.sd_cover);
            tv_friend = itemView.findViewById(R.id.tv_friend);
        }

    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed,parent,false);
        VideoViewHolder holder = new VideoViewHolder(view);


        holder.coverSD.setOnClickListener(new View.OnClickListener()
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
        holder.tv_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                VideoItem video = videoItemList.get(position);
                if(holder.tv_friend.getText().equals("关注"))
                {
                    holder.tv_friend.setText("已关注");
                    //holder.tv_friend.setBackgroundColor(Color.GRAY);
                    holder.tv_friend.setBackgroundResource(R.drawable.textview_grey);
                    Constants.friend_id.add(video.getStudentId());
                }else{
                    holder.tv_friend.setText("关注");
                    //holder.tv_friend.setBackgroundColor(0xFFFF8080);
                    Constants.friend_id.remove(video.getStudentId());
                    holder.tv_friend.setBackgroundResource(R.drawable.textview);
                }

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem videoItem = videoItemList.get(position);
        //holder.coverSD.setImageURI(videoItem.getImageUrl());
        Glide.with(holder.videoView.getContext())
                .load(videoItem.getImageUrl())
                .into(holder.coverSD);
        holder.fromTV.setText("@"+videoItem.getUserName());
        holder.contentTV.setText(videoItem.getContent());
        holder.toTV.setText("Published at: "+videoItem.getCreatedAt());
        if(videoItem.getStudentId().equals(Constants.USER_ID))
        {
            holder.tv_friend.setVisibility(View.GONE);
        }
        if(Constants.friend_id.contains(videoItem.getStudentId()))
        {
            holder.tv_friend.setText("已关注");
            //holder.tv_friend.setBackgroundColor(Color.GRAY);
            holder.tv_friend.setBackgroundResource(R.drawable.textview_grey);
        }
        else {
            holder.tv_friend.setText("关注");
            //holder.tv_friend.setBackgroundColor(0xFFFF8080);
            holder.tv_friend.setBackgroundResource(R.drawable.textview);
            }
    }

    @Override
    public int getItemCount() {
        return videoItemList==null?0:videoItemList.size();
    }

}