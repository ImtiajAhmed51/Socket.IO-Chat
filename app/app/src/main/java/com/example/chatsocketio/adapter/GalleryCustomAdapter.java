package com.example.chatsocketio.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chatsocketio.OnImageClickListener;
import com.example.chatsocketio.R;
import java.io.File;
import java.util.List;

public class GalleryCustomAdapter extends RecyclerView.Adapter<GalleryCustomAdapter.MyViewHolder> {
    private List<String> mList;
    private Context context;
    private OnImageClickListener onImageClickListener;
    public GalleryCustomAdapter(List<String> mList, Context context, OnImageClickListener onImageClickListener) {
        this.mList = mList;
        this.context=context;
        this.onImageClickListener = onImageClickListener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.layout_image,parent,false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        File imgFile = new File(mList.get(position));
        if (imgFile.exists()) {
            Glide.with(context)
                    .load(imgFile.getPath())
                    .centerCrop()
                    .into(holder.imageView);

            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onImageClickListener.onImageLongClick(position,3);
                    return true;
                }
            });
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onImageClickListener.onImageClick(position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageViewID);
        }
    }
}
