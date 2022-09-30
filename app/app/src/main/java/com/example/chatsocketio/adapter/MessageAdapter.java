package com.example.chatsocketio.adapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsocketio.AllUserActivity;
import com.example.chatsocketio.model.MessageFormat;
import com.example.chatsocketio.OnImageClickListener;
import com.example.chatsocketio.ImageProcess;
import com.example.chatsocketio.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
public class MessageAdapter extends RecyclerView.Adapter{
    private ArrayList<MessageFormat> messagesArray;
    private Context context;
    private MessageFormat message;
    private ImageProcess imageProcess;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private static double time=0.0;
    private OnImageClickListener onImageClickListener;
    private int SENDER_VIEW_TYPE=1,RECEIVER_VIEW_TYPE=2,SENDER_VIEW_IMAGE=4,RECEIVER_VIEW_IMAGE=5,SENDER_MESSAGE_IMAGE=6,RECEIVED_MESSAGE_IMAGE=7;
    public MessageAdapter(ArrayList<MessageFormat> messagesArray, Context context,OnImageClickListener onImageClickListener) {
        this.messagesArray = messagesArray;
        this.context = context;
        this.onImageClickListener=onImageClickListener;
        imageProcess = ImageProcess.getInstance();
        myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_message,parent,false);
            return new SenderViewHolder(view);
        }else if(viewType==SENDER_VIEW_IMAGE){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_image,parent,false);
            return new SenderViewHolderImage(view);
        }
        else if(viewType==RECEIVER_VIEW_IMAGE){
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_image,parent,false);
            return new ReceiverViewHolderImage(view);
        }
        else if(viewType==RECEIVER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_message,parent,false);
            return new ReceiverViewHolder(view);
        }else if(viewType==SENDER_MESSAGE_IMAGE){
            View view= LayoutInflater.from(context).inflate(R.layout.sender_message_image,parent,false);
            return new SenderViewHolderMessageImage(view);

        }else if(viewType==RECEIVED_MESSAGE_IMAGE){
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_message_image,parent,false);
            return new ReceiverViewHolderMessageImage(view);
        }
        else
            return null;
    }
    @Override
    public int getItemViewType(int position) {
        if(messagesArray.get(position).getUniqueId().equals(AllUserActivity.senderId)){
            if(!messagesArray.get(position).getMessage().equals("")&&!messagesArray.get(position).getImage().equals(""))
                return SENDER_MESSAGE_IMAGE;
            else if(messagesArray.get(position).getMessage().equals("")&&!messagesArray.get(position).getImage().equals(""))
                return SENDER_VIEW_IMAGE;
            else
                return SENDER_VIEW_TYPE;
        }else{
            if(!messagesArray.get(position).getMessage().equals("")&&!messagesArray.get(position).getImage().equals(""))
                return RECEIVED_MESSAGE_IMAGE;
            else if(messagesArray.get(position).getMessage().equals("")&&!messagesArray.get(position).getImage().equals(""))
                return RECEIVER_VIEW_IMAGE;
            else
                return RECEIVER_VIEW_TYPE;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageFormat messageFormat=messagesArray.get(position);
        if(holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(messageFormat.getMessage());
            ((SenderViewHolder)holder).senderTime.setText(messageFormat.getTime());
            if(0==position)
                ((SenderViewHolder)holder).container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));
        }
        else if(holder.getClass()==SenderViewHolderImage.class){
            Bitmap bitmap = imageProcess.getBitmapFromString(messageFormat.getImage());
            Glide.with(context)
                    .load(bitmap)
                    .into(((SenderViewHolderImage)holder).senderImage);
            ((SenderViewHolderImage)holder).senderImageTime.setText(messageFormat.getTime());
            if(0==position)
                ((SenderViewHolderImage)holder).container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));
        }
        else if(holder.getClass()==SenderViewHolderMessageImage.class){
            Bitmap bitmap = imageProcess.getBitmapFromString(messageFormat.getImage());
            Glide.with(context)
                    .load(bitmap)
                    .into(((SenderViewHolderMessageImage)holder).senderImage);
            ((SenderViewHolderMessageImage)holder).senderImageTime.setText(messageFormat.getTime());
            ((SenderViewHolderMessageImage)holder).senderImageMessage.setText(messageFormat.getMessage());
            if(0==position)
                ((SenderViewHolderMessageImage)holder).container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));
        }
        else if(holder.getClass()==ReceiverViewHolder.class){
            Bitmap bitmap = imageProcess.getBitmapFromString(messageFormat.getUserPicture());
            ((ReceiverViewHolder)holder).receiverName.setText(messageFormat.getUsername());
            ((ReceiverViewHolder)holder).receiverMsg.setText(messageFormat.getMessage());
            ((ReceiverViewHolder)holder).receiverTime.setText(messageFormat.getTime());
            Glide.with(context)
                    .load(bitmap)
                    .into(((ReceiverViewHolder)holder).userReceiverPicture);
            if(0==position){
                ((ReceiverViewHolder)holder).container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));
                ((ReceiverViewHolder)holder).userReceiverPicture.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
            }
        }else if(holder.getClass()==ReceiverViewHolderImage.class){
            Bitmap bitmap = imageProcess.getBitmapFromString(messageFormat.getImage());
            Bitmap bitmap1 = imageProcess.getBitmapFromString(messageFormat.getUserPicture());
            ((ReceiverViewHolderImage)holder).receiverImageName.setText(messageFormat.getUsername());
            Glide.with(context)
                    .load(bitmap)
                    .into(((ReceiverViewHolderImage)holder).receiverImage);
            ((ReceiverViewHolderImage)holder).receiveImageTime.setText(messageFormat.getTime());
            Glide.with(context)
                    .load(bitmap1)
                    .into(((ReceiverViewHolderImage)holder).userReceiverPictureImage);
            if(0==position){
                ((ReceiverViewHolderImage)holder).userReceiverPictureImage.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
                ((ReceiverViewHolderImage)holder).container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));
            }
        }else if(holder.getClass()==ReceiverViewHolderMessageImage.class){
            Bitmap bitmap = imageProcess.getBitmapFromString(messageFormat.getImage());
            Bitmap bitmap1 = imageProcess.getBitmapFromString(messageFormat.getUserPicture());
            ((ReceiverViewHolderMessageImage)holder).receiverImageName.setText(messageFormat.getUsername());
            Glide.with(context)
                    .load(bitmap)
                    .into(((ReceiverViewHolderMessageImage)holder).receiverImage);
            ((ReceiverViewHolderMessageImage)holder).receiveImageTime.setText(messageFormat.getTime());
            Glide.with(context)
                    .load(bitmap1)
                    .into(((ReceiverViewHolderMessageImage)holder).userReceiverPictureImage);
            ((ReceiverViewHolderMessageImage)holder).receiverImageMessage.setText(messageFormat.getMessage());
           if(0==position){
               ((ReceiverViewHolderMessageImage)holder).container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));
               ((ReceiverViewHolderMessageImage)holder).userReceiverPictureImage.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
           }
        }
    }
    @Override
    public int getItemCount() {
      return messagesArray.size();
    }

    public void removeItem(int position) {
        messagesArray.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(MessageFormat item, int position) {
        messagesArray.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<MessageFormat> getData() {
        return messagesArray;
    }
    public class ReceiverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private TextView receiverMsg,receiverTime,receiverName;
        private CircleImageView userReceiverPicture;
        private RelativeLayout container;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.message_body);
            receiverTime=itemView.findViewById(R.id.nameTextView);
            receiverName=itemView.findViewById(R.id.name);
            userReceiverPicture=itemView.findViewById(R.id.receiverUserPictureMessage);
            container=itemView.findViewById(R.id.receiverMessageRelativeLayout);
            //userReceiverPicture.setOnClickListener(this);
            receiverMsg.setOnClickListener(this);
            receiverMsg.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.message_body:
                    if(receiverTime.getVisibility()==View.GONE)
                        receiverTime.setVisibility(View.VISIBLE);
                    else
                        receiverTime.setVisibility(View.GONE);
                    break;
//                case R.id.receiverUserPictureMessage:
//                    onImageClickListener.showDialog(getAdapterPosition(),1);
//                    break;
            }
        }
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.message_body:
                    message=messagesArray.get(getAdapterPosition());
                    copyText(message.getMessage());
                    return true;
            }
            return false;
        }
    }
    public class ReceiverViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener {
        private TextView receiverImageName;
        private  ImageView receiverImage;
        private TextView receiveImageTime;
        private CircleImageView userReceiverPictureImage;
        private RelativeLayout container;
        public ReceiverViewHolderImage(@NonNull View itemView) {
            super(itemView);
            receiverImageName=itemView.findViewById(R.id.nameTxt);
            receiverImage=itemView.findViewById(R.id.imageView);
            receiveImageTime=itemView.findViewById(R.id.txt_date_image_recive);
            userReceiverPictureImage=itemView.findViewById(R.id.receiverUserPictureImage);
            container=itemView.findViewById(R.id.receiverImageContainer);
           // userReceiverPictureImage.setOnClickListener(this);
            receiverImage.setOnClickListener(this);
            receiverImage.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imageView:
                    onImageClickListener.showDialog(getAdapterPosition(),2);
                    break;
//                case R.id.receiverUserPictureImage:
//                    onImageClickListener.showDialog(getAdapterPosition(),1);
//                    break;
            }
        }
        @Override
        public boolean onLongClick(View view) {
            if(receiveImageTime.getVisibility()==View.GONE)
                receiveImageTime.setVisibility(View.VISIBLE);
            else
                receiveImageTime.setVisibility(View.GONE);
            return true;
        }
    }
    public class ReceiverViewHolderMessageImage extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private TextView receiverImageName,receiverImageMessage;
        private ImageView receiverImage;
        private  TextView receiveImageTime;
        private CircleImageView userReceiverPictureImage;
        private RelativeLayout container;
        public ReceiverViewHolderMessageImage(@NonNull View itemView) {
            super(itemView);
            receiverImageName=itemView.findViewById(R.id.receiverMessageImageUserName);
            receiverImage=itemView.findViewById(R.id.receivedMessageImage);
            receiveImageTime=itemView.findViewById(R.id.receiverMessageImageTextTime);
            userReceiverPictureImage=itemView.findViewById(R.id.receiverMessageImageUserPic);
            receiverImageMessage=itemView.findViewById(R.id.receiverMessageImageText);
            container=itemView.findViewById(R.id.receiverMessageImageContainer);
            receiverImageMessage.setOnClickListener(this);
            //userReceiverPictureImage.setOnClickListener(this);
            receiverImage.setOnClickListener(this);
            receiverImageMessage.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.receiverMessageImageText:
                    if(receiveImageTime.getVisibility()==View.GONE)
                        receiveImageTime.setVisibility(View.VISIBLE);
                    else
                        receiveImageTime.setVisibility(View.GONE);
                    break;
//                case R.id.receiverMessageImageUserPic:
//                    onImageClickListener.showDialog(getAdapterPosition(),1);
//                    break;
                case R.id.receivedMessageImage:
                    onImageClickListener.showDialog(getAdapterPosition(),2);
                    break;
            }
        }
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.receiverMessageImageText:
                    message=messagesArray.get(getAdapterPosition());
                    copyText(message.getMessage());
                    return true;
            }
            return false;
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private TextView senderMsg,senderTime;
        private RelativeLayout container;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.message_body);
            senderTime=itemView.findViewById(R.id.nameTextView);
            container=itemView.findViewById(R.id.senderMessageRelativeLayout);
            senderMsg.setOnClickListener(this);
            senderMsg.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.message_body:
                    if(senderTime.getVisibility()==View.GONE)
                        senderTime.setVisibility(View.VISIBLE);
                    else
                        senderTime.setVisibility(View.GONE);
            }
        }
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.message_body:
                    message=messagesArray.get(getAdapterPosition());
                    copyText(message.getMessage());
                    return true;
            }
            return false;
        }
    }
    public class SenderViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView senderImage;
        private TextView senderImageTime;
        private ConstraintLayout container;
        public SenderViewHolderImage(@NonNull View itemView) {
            super(itemView);
            senderImage=itemView.findViewById(R.id.imageView1);
            senderImageTime=itemView.findViewById(R.id.txt_date_image);
            container=itemView.findViewById(R.id.sendImageConstraintLayout);
            senderImage.setOnClickListener(this);
            senderImage.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imageView1:
                    onImageClickListener.showDialog(getAdapterPosition(),2);
                    break;
            }
        }
        @Override
        public boolean onLongClick(View view) {
            if(senderImageTime.getVisibility()==View.GONE)
                senderImageTime.setVisibility(View.VISIBLE);
            else
                senderImageTime.setVisibility(View.GONE);
            return true;
        }
    }
    public class SenderViewHolderMessageImage extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnLongClickListener{
        private ImageView senderImage;
        private TextView senderImageTime,senderImageMessage;
        RelativeLayout container;
        public SenderViewHolderMessageImage(@NonNull View itemView) {
            super(itemView);
            senderImage=itemView.findViewById(R.id.sendMessageImage);
            senderImageTime=itemView.findViewById(R.id.sendMessageImageTime);
            senderImageMessage=itemView.findViewById(R.id.sendMessageImageText);
            container=itemView.findViewById(R.id.senderMessageImageRelativeLayout);
            senderImageMessage.setOnClickListener(this);
            senderImage.setOnClickListener(this);
            senderImageMessage.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sendMessageImageText:
                    if(senderImageTime.getVisibility()==View.GONE)
                        senderImageTime.setVisibility(View.VISIBLE);
                    else
                        senderImageTime.setVisibility(View.GONE);
                    break;
                case R.id.sendMessageImage:
                    onImageClickListener.showDialog(getAdapterPosition(),2);
                    break;
            }
        }
        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.sendMessageImageText:
                    message=messagesArray.get(getAdapterPosition());
                    copyText(message.getMessage());
                    return true;
            }
            return false;
        }
    }
    private void copyText(String text){
        myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(context.getApplicationContext(), "Text Copied",Toast.LENGTH_SHORT).show();
    }
    private String getTimerText(){
        int rounded=(int)Math.round(time);
        int seconds=((rounded%86400)%3600)%60;
        int minutes=((rounded%86400)%3600)/60;
        int hours=((rounded%86400)/3600);
        return formatTime(seconds,minutes,hours);
    }
    private String formatTime(int seconds,int minutes,int hours){
        return String.format("%02d",hours)+" : "+String.format("%02d",minutes)+" : "+String.format("%02d",seconds);
    }
}

