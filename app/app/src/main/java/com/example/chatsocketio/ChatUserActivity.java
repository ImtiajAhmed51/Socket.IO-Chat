package com.example.chatsocketio;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static com.example.chatsocketio.notification.App.CHANNEL_ID;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.chatsocketio.adapter.MessageAdapter;
import com.example.chatsocketio.model.MessageFormat;
import com.example.chatsocketio.socket.SocketManager;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;
public class ChatUserActivity extends AppCompatActivity implements View.OnClickListener, OnImageClickListener  {
    private RelativeLayout root, container;
    private EditText messageBox;
    private ImageView sendButton;
    private ImageView imageView;
    private TextView userNameTextView;
    private ImageView backPress;
    private int time = 2;
    // ViewGroup root;
    private CircleImageView circleImageView,typingUsrPic;
    private RelativeLayout imageLayout;
    private Bitmap sendImageStore,receiverBitmap;
    // RecyclerView chatShow;
    private static final String TAG  = "ChatUserActivity";

    private String receiverUniqueId;
    private String receiverName;
    private byte[] receiverImageByte;
    private String senderUniqueId;
    private String senderName;
    private byte[] senderImageByte;
    private LottieAnimationView lottieAnimationView;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private Thread thread2;
    private boolean startTyping = false;
    private final ArrayList<MessageFormat> messageArray = new ArrayList<>();
    private SocketManager socketManager;
    private ImageProcess imageProcess;
    // gallery
    private ArrayList<String> pictureList;
    private boolean isPermissionGranted=false;
    private final String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
    private int REQUEST_CODE=12345;
    private PhotoBottomSheetFragment photoBottomSheetFragment;
    //notification
    private NotificationManagerCompat notificationManagerCompat;
    private boolean returnActivity=true;
    private Bitmap  senderBitmap;
    //data encrypt And decrypt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);
        initView();
        initData();
        Window window = this.getWindow();
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.darkGreen));
        window.setNavigationBarColor(this.getResources().getColor(R.color.darkGreen));
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        socketManager=SocketManager.getInstance();
        socketManager.getEmitterListener("chat message", onNewMessage);
        socketManager.getEmitterListener("on typing", onTyping);
        returnActivity=true;

    }
    private void initView(){
        //Toast.makeText(MainActivity.this,getDeviceName(),Toast.LENGTH_SHORT).show();
        imageProcess = ImageProcess.getInstance();
        root=findViewById(R.id.root);
        circleImageView=findViewById(R.id.chatProfilePic);
        imageLayout=findViewById(R.id.imageRelativeLayout);
        ImageView imageClose = findViewById(R.id.imageViewClose);
        imageView=findViewById(R.id.imageSendView);
        userNameTextView=findViewById(R.id.chatDetailName);
        messageBox = findViewById(R.id.textField);
        sendButton = findViewById(R.id.sendButton);
        ImageView imageSend = findViewById(R.id.sendImage);
        container=findViewById(R.id.typingUserRelativeLayout);
        typingUsrPic=findViewById(R.id.typingUserPicture);
        messageRecyclerView = findViewById(R.id.messageListView);
        lottieAnimationView=findViewById(R.id.animationView);
        backPress=findViewById(R.id.backPressId);
        pictureList=new ArrayList<>();
        prepareRecyclerView();
        notificationManagerCompat=NotificationManagerCompat.from(this);
        messageAdapter = new MessageAdapter(messageArray,this,this);
        messageRecyclerView.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setNestedScrollingEnabled(false);
        onTypeButtonEnable();
        enableSwipeToDeleteAndUndo();
        backPress.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
        imageClose.setOnClickListener(this);
        imageSend.setOnClickListener(this);
    }
     private void initData(){
        Bundle extras = getIntent().getExtras();
        senderUniqueId=AllUserActivity.senderId;
        senderName=preferences.getDataName(ChatUserActivity.this);
        senderImageByte= Base64.decode(preferences.getDataImage(ChatUserActivity.this), Base64.DEFAULT);
        senderBitmap = imageProcess.getResizedBitmap(BitmapFactory.decodeByteArray(senderImageByte, 0, senderImageByte.length),200);
        if(extras!=null){
            receiverName=extras.getString("username");
            receiverImageByte = extras.getByteArray("userpicture");
            receiverUniqueId=extras.getString("userId");
            receiverBitmap =BitmapFactory.decodeByteArray(receiverImageByte, 0, receiverImageByte.length);
            Glide.with(this)
                    .load(receiverBitmap)
                    .into(circleImageView);
            userNameTextView.setText(receiverName);
        }
    }


     private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final MessageFormat item = messageAdapter.getData().get(position);
                messageAdapter.removeItem(position);
                Snackbar snackbar = Snackbar.make(root, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(Color.parseColor("#175877"));
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        messageAdapter.restoreItem(item, position);
                        messageRecyclerView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(messageRecyclerView);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageViewClose:
                imageLayout.setAnimation(AnimationUtils.loadAnimation(ChatUserActivity.this, R.anim.typing_down_fade_animation));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageLayout.setVisibility(View.GONE);
                    }
                }, 300);
                sendImageStore=null;
                if(messageBox.getText().toString().trim().equals("")){
                    sendButton.setEnabled(false);
                    sendButton.setVisibility(View.GONE);
                }
                break;
            case R.id.sendImage:
                checkPermission();
                break;
            case R.id.backPressId:
                ChatUserActivity.this.finish();
                overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_left);
                break;
            case R.id.chatProfilePic:
                showDialog(1,1);
                break;
        }
    }
    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            isPermissionGranted=true;
            getPicturePaths();
            if(pictureList.isEmpty()){
                if(!photoBottomSheetFragment.isVisible()){
                    photoBottomSheetFragment.show(getSupportFragmentManager(), photoBottomSheetFragment.getTag());

                }
            }else{
                if(!photoBottomSheetFragment.isVisible()){
                    photoBottomSheetFragment.show(getSupportFragmentManager(), photoBottomSheetFragment.getTag());
                }
            }
        }
        else{
            ActivityCompat.requestPermissions(ChatUserActivity.this,permissions,REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            checkPermission();
            Toast.makeText(ChatUserActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            if(!photoBottomSheetFragment.isVisible()){
                photoBottomSheetFragment.show(getSupportFragmentManager(), photoBottomSheetFragment.getTag());
            }
            Toast.makeText(ChatUserActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void prepareRecyclerView() {
        photoBottomSheetFragment= new PhotoBottomSheetFragment(pictureList,this,this);

    }
    private void getPicturePaths(){
        // if the sd card is present we are creating a new list in
        // which we are getting our images data with their ids.
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        // on below line we are creating a new
        // string to order our images by string.
        final String orderBy = MediaStore.Images.Media._ID;
        // this method will stores all the images
        // from the gallery in Cursor
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        // below line is to get total number of images

        // on below line we are running a loop to add
        // the image file path in our array list.
        pictureList.clear();
        while (cursor.moveToNext()) {
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            // after that we are getting the image file path
            // and adding that path in our array list.
            pictureList.add(0,cursor.getString(dataColumnIndex));
        }
        cursor.close();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(time == 0){
                userNameTextView.setText(receiverName);
                container.setAnimation(AnimationUtils.loadAnimation(ChatUserActivity.this,R.anim.typing_down_fade_animation));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lottieAnimationView.setVisibility(View.GONE);
                        typingUsrPic.setVisibility(View.GONE);
                    }
                }, 300);
                startTyping = false;
                time = 2;
            }
        }
    };
    private void onTypeButtonEnable(){
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    if(sendImageStore==null){
                        sendButton.setEnabled(false);
                        sendButton.setVisibility(View.GONE);
                    }
                }else{
                    sendButton.setEnabled(true);
                    sendButton.setVisibility(View.VISIBLE);
                    JSONObject onTyping = new JSONObject();
                    try {
                        onTyping.put("typing", true);
                        onTyping.put("username", senderName);
                        onTyping.put("senderId", senderUniqueId);
                        onTyping.put("receiverId", receiverUniqueId);
                        onTyping.put("userPicture", imageProcess.getStringFromBitmap(senderBitmap,"low"));
                        socketManager.getSocket().emit("on typing", onTyping);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //String username,message,id,recId,image,userConnectPicture,time;
                    try {
                        JSONObject data = (JSONObject) args[0];
                        if(data.getString("senderId").equals(receiverUniqueId)&&data.getString("receiverId").equals(senderUniqueId)){
                            MessageFormat format = null;
                            if(data.getString("imageSend").equals(""))
                                format = new MessageFormat(data.getString("senderId"), data.getString("username"),data.getString("message"),"",data.getString("userConnectPicture"),data.getString("time"));
                            else if(data.getString("message").equals(""))
                                format = new MessageFormat(data.getString("senderId"), data.getString("username"), "",data.getString("imageSend"),data.getString("userConnectPicture"),data.getString("time"));
                            else if(!data.getString("imageSend").equals("")&&!data.getString("message").equals(""))
                                format = new MessageFormat(data.getString("senderId"), data.getString("username"), data.getString("message"),data.getString("imageSend"),data.getString("userConnectPicture"),data.getString("time"));
                            messageArray.add(0,format);
                            messageRecyclerView.scrollToPosition(0);
                            messageAdapter.notifyItemInserted(0);
                            if(!returnActivity)
                                sendOnChannel(data.getString("username"),data.getString("message"));
                        }

                    } catch (Exception e) {
                        return;
                    }

                }
            });
        }
    };
    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if(data.getString("senderId").equals(receiverUniqueId)&&data.getString("receiverId").equals(senderUniqueId)){
                            boolean typingOrNot = data.getBoolean("typing");
                            String userName = data.getString("username") + " is Typing";
                            String senderId = data.getString("senderId");
                            String receiverId = data.getString("receiverId");
                            String typingUserPicture=data.getString("userPicture");
                            if(senderId.equals(senderUniqueId))
                                typingOrNot = false;
                            else {
                                userNameTextView.setText(userName);
                                lottieAnimationView.setVisibility(View.VISIBLE);
                                typingUsrPic.setVisibility(View.VISIBLE);
                                if(!startTyping) {
                                    Glide.with(ChatUserActivity.this)
                                            .load(imageProcess.getBitmapFromString(typingUserPicture))
                                            .into(typingUsrPic);
                                    container.setAnimation(AnimationUtils.loadAnimation(ChatUserActivity.this, R.anim.typing_up_fade_animation));
                                }
                            }
                            if(typingOrNot){
                                if(!startTyping){
                                    startTyping = true;
                                    thread2=new Thread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    while(time > 0) {
                                                        synchronized (this){
                                                            try {
                                                                wait(500);
                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                            time--;
                                                        }
                                                        handler2.sendEmptyMessage(0);
                                                    }
                                                }
                                            }
                                    );
                                    thread2.start();
                                }else {
                                    time = 2;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    public void sendMessage(View view){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            String message = messageBox.getText().toString().trim();
            if(TextUtils.isEmpty(message)&&sendImageStore==null){
                return;
            }
            JSONObject jsonObject = new JSONObject();
            SimpleDateFormat formatTime = new SimpleDateFormat("hh.mm aa");
            try {
                MessageFormat format = null;
                if(sendImageStore==null){
                    format = new MessageFormat(senderUniqueId, senderName, message,"", imageProcess.getStringFromBitmap(senderBitmap,"low"),formatTime.format(new Date().getTime()));
                }else if(message.equals("")){
                    format = new MessageFormat(senderUniqueId, senderName, "", imageProcess.getStringFromBitmap(sendImageStore,"high"), imageProcess.getStringFromBitmap(senderBitmap,"low"),formatTime.format(new Date().getTime()));
                }else {
                    format = new MessageFormat(senderUniqueId, senderName, message, imageProcess.getStringFromBitmap(sendImageStore,"high"), imageProcess.getStringFromBitmap(senderBitmap,"low"),formatTime.format(new Date().getTime()));
                }
                messageArray.add(0,format);
                messageRecyclerView.scrollToPosition(0);
                messageAdapter.notifyItemInserted(0);
                messageBox.setText("");
                jsonObject.put("username", senderName);
                jsonObject.put("receiverId", receiverUniqueId);
                jsonObject.put("senderId", senderUniqueId);
                jsonObject.put("userConnectPicture", imageProcess.getStringFromBitmap(senderBitmap,"low"));
                jsonObject.put("time",formatTime.format(new Date().getTime()));
                if(!message.equals("")&&sendImageStore!=null){
                    jsonObject.put("message", message);
                    jsonObject.put("imageSend", imageProcess.getStringFromBitmap(sendImageStore,"high"));
                }else if(!message.equals("")){
                    jsonObject.put("message", message);
                    jsonObject.put("imageSend", "");
                }else if(sendImageStore!=null){
                    jsonObject.put("imageSend", imageProcess.getStringFromBitmap(sendImageStore,"high"));
                    jsonObject.put("message", "");
                }
                socketManager.getSocket().emit("chat message", jsonObject);
                if(sendImageStore!=null){
                    imageLayout.setVisibility(View.GONE);
                    sendImageStore=null;
                }
                sendButton.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(ChatUserActivity.this,"Please Check Your Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }
    private void sendOnChannel(String name,String message){
        String text=message;
        if(message.equals("")){
            text="Send Picture";
        }
        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.admin)
                .setContentTitle(name)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManagerCompat.notify(1,notification);
    }
    @Override
    public void onBackPressed() {
        ChatUserActivity.this.finish();
        overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_left);
    }
    @Override
    public void onImageLongClick(int position,int type) {
        showDialog(position,type);
    }
    @Override
    public void onImageClick(int position) {
        photoBottomSheetFragment.dismiss();
        File imgFile = new  File(pictureList.get(position));
        if(imgFile.exists()){
            try {
                sendImageStore= ImageProcess.modifyOrientation(BitmapFactory.decodeFile(imgFile.getAbsolutePath()),imgFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //sendImageStore = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageLayout.setVisibility(View.VISIBLE);
            imageLayout.setAnimation(AnimationUtils.loadAnimation(ChatUserActivity.this, R.anim.typing_up_fade_animation));
            Glide.with(this)
                    .load(sendImageStore)
                    .into(imageView);
            sendButton.setEnabled(true);
            sendButton.setVisibility(View.VISIBLE);
        }
        else {
            imageLayout.setAnimation(AnimationUtils.loadAnimation(ChatUserActivity.this, R.anim.typing_down_fade_animation));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageLayout.setVisibility(View.GONE);
                }
            }, 300);
        }
    }
    @Override
    public void showDialog(int position,int type) {
        final Dialog dialog=new Dialog(this);
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        Bitmap viewPhoto=null;
        View view= LayoutInflater.from(this).inflate(R.layout.imagefullviewdialog,null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //ImageView imageView=dialog.findViewById(R.id.photoView);
        ImageView imageClose=dialog.findViewById(R.id.closeImage);
        ImageView imageDownload=dialog.findViewById(R.id.downloadImage);
        RelativeLayout relativeLayout=dialog.findViewById(R.id.headerBar);
        if(type==1){
            viewPhoto=receiverBitmap;
        }
        else if(type==2){
            imageDownload.setVisibility(View.VISIBLE);
            MessageFormat message=messageArray.get(position);
            viewPhoto= imageProcess.getBitmapFromString(message.getImage());
        }
        else if(type==3){
            imageDownload.setVisibility(View.GONE);
            File imgFile = new  File(pictureList.get(position));
            if(imgFile.exists()){
                try {
                    viewPhoto = ImageProcess.modifyOrientation(BitmapFactory.decodeFile(imgFile.getAbsolutePath()),imgFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        TouchImageView touchImageView=dialog.findViewById(R.id.photoView);
        touchImageView.setImageBitmap(viewPhoto);
        touchImageView.setMinZoom(1);
        touchImageView.setMaxZoom(3);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Bitmap finalViewPhoto = viewPhoto;
        imageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveImageToExternalStorage(UUID.randomUUID().toString(), finalViewPhoto)){
                    Toast.makeText(ChatUserActivity.this,"Image Saved Successfully.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean saveImageToExternalStorage(String imageName,Bitmap bmp){
        Uri ImageCollection=null;
        ContentResolver resolver=this.getContentResolver();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            ImageCollection= MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }else{
            ImageCollection=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,imageName+".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        Uri imageUri=resolver.insert(ImageCollection,contentValues);
        try {
            OutputStream outputStream=resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bmp.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            Objects.requireNonNull(outputStream);
            return true;

        }catch (Exception e){
            Toast.makeText(this,"Image not saved: \n"+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        returnActivity=false;

    }
    @Override
    protected void onStart() {
        super.onStart();
        returnActivity = true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isFinishing()){
            socketManager.getSocket().off("chat message", onNewMessage);
            socketManager.getSocket().off("on typing", onTyping);
            messageArray.clear();
        }else {
        }
    }
}