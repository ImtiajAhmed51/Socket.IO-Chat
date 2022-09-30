package com.example.chatsocketio;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.chatsocketio.socket.SocketManager;
import com.yalantis.ucrop.UCrop;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;
public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {
    private Button setNickName,clearPicture;
    private EditText userNickName;
    private String Name;
    private String uniqueId;
    private boolean isPermissionGranted=false;
    private CircleImageView addProfilePicture;
    private String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
    private int REQUEST_CODE=12345;
    private byte[] imageByte=null;
    private Bitmap bitmap1;
    private static final int PICK_FROM_FILE = 3;
    private  String SAMPLE_CROPPER_IMG_NAME="SampleCropImg";
    private SocketManager socketManager;
    private String userId,userName,userPicture;
    private ImageProcess imageProcess;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setEnterTransition(null);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.darkGreen));
            window.setNavigationBarColor(this.getResources().getColor(R.color.darkGreen));
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initView();
        initData();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addProfilePicture:
                setPicture();
                break;
            case R.id.clearPicture:
                imageByte=null;
                clearPicture.setVisibility(View.GONE);
                addProfilePicture.setImageResource(R.drawable.user_dp);
                break;
            case R.id.setNickName:
                anotherActivity();
                break;
        }
    }
    private void anotherActivity(){
        //boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(imageByte!=null&&userNickName.getText().toString().length()>0){
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                //connected = true;
                Intent intent = new Intent(AddUserActivity.this, AllUserActivity.class);
                //Pair[] pairs= new Pair[2];
                uniqueId=UUID.randomUUID().toString();
                userId=uniqueId;
                userName=userNickName.getText().toString();
                bitmap= imageProcess.getResizedBitmap(BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length),300);
                userPicture= imageProcess.getStringFromBitmap(bitmap,"low");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                //bitmap.recycle();
                joinUser();
                intent.putExtra("userId", userId);
                intent.putExtra("username", userName);
                intent.putExtra("userpicture", byteArray);
                preferences.setDataLogin(AddUserActivity.this,true);
                preferences.setDataAs(AddUserActivity.this,uniqueId,userNickName.getText().toString(), imageProcess.getStringFromBitmap(bitmap,"low"));
                //ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation( AddUserActivity.this,pairs);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                //getWindow().setExitTransition(null);
                AddUserActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            } else {
                // connected = false;
                Toast.makeText(AddUserActivity.this, "Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        }if(imageByte==null){
            Toast.makeText(AddUserActivity.this, "Please Upload Your Picture.", Toast.LENGTH_SHORT).show();
        }if(userNickName.getText().toString().length()==0){
            Toast.makeText(AddUserActivity.this, "Please Enter Your Name.", Toast.LENGTH_SHORT).show();
        }
    }
    private void setPicture(){
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.gallery_picture_dialog);
        Button gallery=dialog.findViewById(R.id.selectFromGallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                checkPermission();

            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
    private void initView(){
        clearPicture=findViewById(R.id.clearPicture);
        addProfilePicture=findViewById(R.id.addProfilePicture);
        userNickName = findViewById(R.id.userNickName);
        setNickName = findViewById(R.id.setNickName);
        socketManager= SocketManager.getInstance();
        imageProcess = ImageProcess.getInstance();
    }
    private void initData(){
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            clearPicture.setVisibility(View.VISIBLE);
            setNickName.setEnabled(true);
            imageByte = extras.getByteArray("userPicture");
            Name=getIntent().getStringExtra("name");
            userNickName.setText(Name);
            bitmap1 = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            Glide.with(this)
                    .load(bitmap1)
                    .into(addProfilePicture);
        }
        clearPicture.setOnClickListener(this);
        addProfilePicture.setOnClickListener(this);
        setNickName.setOnClickListener(this);
    }
    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            isPermissionGranted=true;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
        }
        else{
            ActivityCompat.requestPermissions(AddUserActivity.this,permissions,REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            Toast.makeText(AddUserActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            checkPermission();
        } else {
            Toast.makeText(AddUserActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_FROM_FILE&&resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            if(imageUri!=null){
                startCrop(imageUri);
            }
        }
        else if(requestCode== UCrop.REQUEST_CROP&&resultCode==RESULT_OK){
            Uri imageUriResultCrop=UCrop.getOutput(data);
            if(userNickName.length()!=0){
                setNickName.setEnabled(true);
            }
            if(imageUriResultCrop!=null){
                clearPicture.setVisibility(View.VISIBLE);
                Bitmap image = null ;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUriResultCrop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(AddUserActivity.this)
                        .load(image)
                        .into(addProfilePicture);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG,20,outputStream);
                imageByte=outputStream.toByteArray();
            }
        }

    }

    private void startCrop(Uri uri){
        String destinationFileName=SAMPLE_CROPPER_IMG_NAME;
        destinationFileName+=".jpg";
        UCrop uCrop=UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
        uCrop.withAspectRatio(1,1);

//        uCrop.withAspectRatio(3,4);
//        uCrop.useSourceImageAspectRatio();
//        uCrop.withAspectRatio(2,3);
//        uCrop.withAspectRatio(16,9);
        uCrop.withMaxResultSize(8000,8000);
        uCrop.withOptions(getCropOptions());
        uCrop.start(AddUserActivity.this);

    }
    private UCrop.Options getCropOptions(){
        UCrop.Options options=new UCrop.Options();
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setStatusBarColor(getResources().getColor(R.color.darkGreen));
        options.setToolbarColor(getResources().getColor(R.color.darkGreen));
        options.setToolbarWidgetColor(getResources().getColor(R.color.darkGreen2));
        options.setCropFrameColor(getResources().getColor(R.color.darkGreen2));
        options.setActiveControlsWidgetColor(getResources().getColor(R.color.darkGreen2));
       // options.bott
        options.setToolbarTitle("Crop Image");
        return options;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()){
          //
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        socketManager.getSocket().disconnect();
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        AddUserActivity.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(preferences.getDataLogin(this)){
            userId=preferences.getDataId(AddUserActivity.this);
            userName=preferences.getDataName(AddUserActivity.this);
            userPicture=preferences.getDataImage(AddUserActivity.this);
            joinUser();
            Intent intent=new Intent(AddUserActivity.this, AllUserActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("username", userName);
            intent.putExtra("userpicture", Base64.decode(userPicture, Base64.DEFAULT));
            startActivity(intent);
            //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            AddUserActivity.this.finish();
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

        }
    }
    private void joinUser(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",userId);
            jsonObject.put("username",userName);
            jsonObject.put("image",userPicture);
            jsonObject.put("isOnline",true);
            socketManager.getSocket().emit("join",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
