package com.example.takethraithip.myproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class notiAdapter extends RecyclerView.Adapter<notiAdapter.MyViewHolder>{

    private final List<notiTyping> notiTypingList;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView userTyping;

        public MyViewHolder (View view){
            super(view);
            userTyping = (TextView) view.findViewById(R.id.tv_noti_data);
        }
    }

        public notiAdapter (List<notiTyping> notiTypingList){
        this.notiTypingList = notiTypingList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_user_noti, parent, false);

            return new MyViewHolder(itemView);
        }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        notiTyping notiTyping = notiTypingList.get(position);
        holder.userTyping.setText(notiTyping.getNotiTyping());
    }

    @Override
    public int getItemCount() {
        return notiTypingList.size();
    }

}
