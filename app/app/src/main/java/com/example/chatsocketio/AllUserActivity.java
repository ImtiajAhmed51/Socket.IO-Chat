package com.example.chatsocketio;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.chatsocketio.adapter.UserAdapter;
import com.example.chatsocketio.model.User;
import com.example.chatsocketio.socket.SocketManager;
import com.github.nkzawa.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
public class AllUserActivity extends AppCompatActivity implements UserOnClickListener ,View.OnClickListener {
    private SocketManager socketManager;
    private RecyclerView userRecycler;
    private ArrayList<User> userArrayList;
    private UserAdapter userAdapter;
    private TextView serverConnected;
    private byte[] senderImageByte;
    public static String senderId;
    private String senderName;
    private TextView ownName;
    private CircleImageView ownPicture;
    private Bitmap senderBitmap;
    private TextView waitingText;
    private LottieAnimationView searchingPerson;
    private ConstraintLayout updateProfileClick;
    private ImageView ownActiveStatus;
    private Handler handler;
    private boolean CheckActivity=true;
    private boolean handlerCheck=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.darkGreen));
        window.setNavigationBarColor(this.getResources().getColor(R.color.darkGreen));
        setContentView(R.layout.activity_all_user);
        initData();
        joinUser();
        onActive();
    }
    private void initData(){
        socketManager= SocketManager.getInstance();
        userRecycler=findViewById(R.id.userListView1);
        updateProfileClick=findViewById(R.id.userProfileUpdateClick);
        ownName=findViewById(R.id.ownName);
        searchingPerson=findViewById(R.id.allAnimationView);
        ownPicture=findViewById(R.id.ownPicture);
        ownActiveStatus=findViewById(R.id.ownOnlineStatus);
        waitingText=findViewById(R.id.waitingId);
        serverConnected=findViewById(R.id.serverConnectionId);
        userArrayList=new ArrayList<>();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        socketManager.getEmitterListener("AllUsers",AllUser);
        userAdapter = new UserAdapter(userArrayList,this,this::userOnClick);
        userRecycler.setAdapter(userAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        userRecycler.setLayoutManager(layoutManager);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            senderName=extras.getString("username");
            senderImageByte = extras.getByteArray("userpicture");
            senderId=extras.getString("userId");

            senderId=preferences.getDataId(AllUserActivity.this);
            senderName=preferences.getDataName(AllUserActivity.this);
            senderImageByte=Base64.decode(preferences.getDataImage(AllUserActivity.this), Base64.DEFAULT);
            ownName.setText(senderName);
            senderBitmap = BitmapFactory.decodeByteArray(senderImageByte, 0, senderImageByte.length);
            Glide.with(this)
                    .load(senderBitmap)
                    .into(ownPicture);
        }
        updateProfileClick.setOnClickListener(this::onClick);
    }
    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    private String capitalize(String s) {
        if (s == null || s.length() == 0)
            return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first))
            return s;
        else
            return Character.toUpperCase(first) + s.substring(1);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.userProfileUpdateClick:
                CheckActivity=false;
                startActivity(new Intent(AllUserActivity.this,UpdateProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
        }
    }
    private Emitter.Listener AllUser=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0] instanceof JSONArray) {
                        JSONArray data = (JSONArray) args[0];
                        userArrayList.clear();
                        try {
                            int len = data.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject data1= (JSONObject) data.get(i);
                                if(!data1.getString("id").equals(senderId)){
                                    User u = new User();
                                    u.setId(data1.getString("id"));
                                    u.setUserName(data1.getString("username"));
                                    u.setUserImage(data1.getString("image"));
                                    u.setOnline(data1.getBoolean("isOnline"));
                                    userArrayList.add(u);
                                }else{
                                    serverConnected.setText("Server Connected");
                                    ownPicture.setBorderColor(Color.parseColor("#2167F3"));
                                    serverConnected.setTextColor(Color.parseColor("#2167F3"));
                                    ownActiveStatus.setVisibility(View.VISIBLE);
                                }
                                userAdapter.notifyItemRangeChanged(0,userArrayList.size());
                            }
                            if(userArrayList.isEmpty()){
                                waitingText.setVisibility(View.VISIBLE);
                                waitingText.setVisibility(View.VISIBLE);
                                waitingText.setText("Users Not Available.");
                                searchingPerson.setVisibility(View.VISIBLE);
                            }else{
                                searchingPerson.setVisibility(View.GONE);
                                waitingText.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            // return;
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(handlerCheck){
            onActive();
            handlerCheck=false;
        }
        CheckActivity=true;
        senderName=preferences.getDataName(AllUserActivity.this);
        senderImageByte=Base64.decode(preferences.getDataImage(AllUserActivity.this), Base64.DEFAULT);
        ownName.setText(senderName);
        senderBitmap = BitmapFactory.decodeByteArray(senderImageByte, 0, senderImageByte.length);
        Glide.with(this)
                .load(senderBitmap)
                .into(ownPicture);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                //onDelete();
                Toast.makeText(AllUserActivity.this,"Please wait for new update.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.aboutIt:
                showDialog();
                break;
        }
        return true;
    }

    public void showDialog() {
        final Dialog dialog=new Dialog(this);
        View view= LayoutInflater.from(this).inflate(R.layout.about_us,null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //ImageView imageView=dialog.findViewById(R.id.photoView);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()){
            onOffline();
            socketManager.getSocket().off("AllUsers", AllUser);
            socketManager.getSocket().disconnect();
            userArrayList.clear();
        }
    }
    private void onActive(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id", senderId);
            jsonObject.put("isOnline",true);
            socketManager.getSocket().emit("updateStatus",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onOffline(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id", senderId);
            jsonObject.put("isOnline",false);
            socketManager.getSocket().emit("updateStatus",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onDelete(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id", senderId);
            socketManager.getSocket().emit("delete",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(AllUserActivity.this, R.style.AlertDialogCustom);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle( Html.fromHtml("<font color='#CFCFCF'>Closing Activity</font>"));
        alertDialog.setMessage( Html.fromHtml("<font color='#CFCFCF'>Are you sure you want to close this activity?</font>"));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, Html.fromHtml("<font color='#B71C1C'>Yes</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                        //AllUserActivity.this.finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,  Html.fromHtml("<font color='#2061FF'>No</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
        @Override
    protected void onPause() {
        super.onPause();
        if(CheckActivity){
            handler=  new Handler();
            Runnable myRunnable = new Runnable() {
                public void run() {
                    handlerCheck=true;
                    onOffline();
                }
            };
            handler.postDelayed(myRunnable,10000);
        }
    }
    private void joinUser(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id", senderId);
            jsonObject.put("username",senderName);
            jsonObject.put("image", Base64.encodeToString(senderImageByte, Base64.DEFAULT));
            jsonObject.put("isOnline",true);
            socketManager.getSocket().emit("join",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void userOnClick(int position) {
        Intent intent=new Intent(AllUserActivity.this,ChatUserActivity.class);
        User userId=userArrayList.get(position);
        String userid=userId.getId();
        String username=userId.getUserName();
        CheckActivity=false;
        byte[] userimageByte=Base64.decode(userId.getUserImage(), Base64.DEFAULT);
        intent.putExtra("userId", userid);
        intent.putExtra("username", username);
        intent.putExtra("userpicture", userimageByte);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}