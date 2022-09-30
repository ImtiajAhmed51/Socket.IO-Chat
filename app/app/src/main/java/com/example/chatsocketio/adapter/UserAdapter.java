package com.example.chatsocketio.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsocketio.ImageProcess;
import com.example.chatsocketio.R;
import com.example.chatsocketio.UserOnClickListener;
import com.example.chatsocketio.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> userArray;
    private Context context;
    private ImageProcess imageProcess;
    private UserOnClickListener userOnClickListener;

    public UserAdapter(ArrayList<User> userArray, Context context, UserOnClickListener userOnClickListener) {
        this.userArray = userArray;
        this.context = context;
        this.userOnClickListener=userOnClickListener;
        imageProcess = ImageProcess.getInstance();
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User userFormat=userArray.get(position);
        holder.userName.setText(userFormat.getUserName());
        if(userFormat.getUserName().equals("Imtiaj")){
            holder.adminText.setVisibility(View.VISIBLE);
            holder.userName.setTextColor(Color.parseColor("#FFC10A"));
            holder.adminBadge.setVisibility(View.VISIBLE);
        }else if(userFormat.getUserName().equals("Imtiaj Ahmed")){
            holder.adminText.setVisibility(View.VISIBLE);
            holder.userName.setTextColor(Color.parseColor("#FFC10A"));
            holder.adminBadge.setVisibility(View.VISIBLE);
        }else if(userFormat.getUserName().equals("Imtiaj Ahmed Anik")){
            holder.adminText.setVisibility(View.VISIBLE);
            holder.userName.setTextColor(Color.parseColor("#FFC10A"));
            holder.adminBadge.setVisibility(View.VISIBLE);
        }else if(userFormat.getUserName().equals("Md. Imtiaj Ahmed")){
            holder.adminText.setVisibility(View.VISIBLE);
            holder.userName.setTextColor(Color.parseColor("#FFC10A"));
            holder.adminBadge.setVisibility(View.VISIBLE);
        }else{
            holder.adminText.setVisibility(View.GONE);
            holder.userName.setTextColor(Color.parseColor("#FFFFFFFF"));
            holder.adminBadge.setVisibility(View.GONE);
        }
        if(userFormat.getOnline()){
            holder.onlineStatus.setVisibility(View.VISIBLE);
        }else{
            holder.onlineStatus.setVisibility(View.GONE);
        }
        holder.userSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userOnClickListener.userOnClick(position);
            }
        });
        Glide.with(context)
                .load(imageProcess.getBitmapFromString(userFormat.getUserImage()))
                .into(holder.profileImage);
    }
    @Override
    public int getItemCount() {
        return userArray.size();
    }
    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName,adminText;
        CircleImageView profileImage;
        ImageView onlineStatus,adminBadge;
        CardView userSelect;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.txt_name);
            profileImage=itemView.findViewById(R.id.tvProfileImage);
            onlineStatus=itemView.findViewById(R.id.tvOnlineStatus);
            userSelect=itemView.findViewById(R.id.allcard);
            adminText=itemView.findViewById(R.id.adminBadgeText);
            adminBadge=itemView.findViewById(R.id.adminBadge);
        }
    }
}
