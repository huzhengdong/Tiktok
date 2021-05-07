package com.SJTU7.Tiktok;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>{

    private List<String> friendList;
    public void setData(List<String> l){
        friendList = l;
        notifyDataSetChanged();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        private TextView friend_id;
        private Button del;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friend_id = itemView.findViewById(R.id.friend_id);
            del = itemView.findViewById(R.id.delete);
        }

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,parent,false);
        FriendViewHolder holder = new FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        String id = friendList.get(position);
        holder.friend_id.setText(id);
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendList.remove(id);
                Constants.friend_id = friendList;
                setData(friendList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList==null?0:friendList.size();
    }

}