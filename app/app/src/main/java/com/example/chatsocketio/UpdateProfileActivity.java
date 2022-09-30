package com.example.chatsocketio;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatsocketio.socket.SocketManager;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static String senderUniqueId;
    private String senderName;
    private byte[] senderImageByte;
    private Bitmap senderBitmap;
    private CircleImageView userUpdatePicture;
    private EditText userName;

    private ImageView editBtn,cancelBtn,confirmBtn;
    private ImageView picChangeBtn;
    private Bitmap bitmap;
    private ImageProcess imageProcess;
    private String userPicture;
    private SocketManager socketManager;
    private ImageView backPress;


    private boolean isPermissionGranted=false;
    private String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
    private int REQUEST_CODE=12345;
    private static final int PICK_FROM_FILE = 3;
    private  String SAMPLE_CROPPER_IMG_NAME="SampleCropImg";
    private byte[] imageByte=null;
    private byte[] byteArray;
    private boolean checkUpdate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Window window = this.getWindow();
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.darkGreen));
        window.setNavigationBarColor(this.getResources().getColor(R.color.darkGreen));
        initView();
        initData();
        editBtn.setOnClickListener(this::onClick);
        cancelBtn.setOnClickListener(this::onClick);
        confirmBtn.setOnClickListener(this::onClick);
        picChangeBtn.setOnClickListener(this::onClick);
        backPress.setOnClickListener(this::onClick);
    }
    private void initView(){
        userUpdatePicture=findViewById(R.id.userUpdatePicture);
        userName=findViewById(R.id.userUpdateName);
        imageProcess = ImageProcess.getInstance();
        socketManager=SocketManager.getInstance();
        backPress=findViewById(R.id.updateProfileBackPressId);




        editBtn=findViewById(R.id.profileEditBtn);
        cancelBtn=findViewById(R.id.profileCancelBtn);
        confirmBtn=findViewById(R.id.profileConfirmBtn);
        picChangeBtn=findViewById(R.id.pictureChange);
    }

    private void initData(){
        senderUniqueId=AllUserActivity.senderId;
        senderName=preferences.getDataName(UpdateProfileActivity.this);
        senderImageByte= Base64.decode(preferences.getDataImage(UpdateProfileActivity.this), Base64.DEFAULT);
        senderBitmap = BitmapFactory.decodeByteArray(senderImageByte, 0, senderImageByte.length);
        userName.setText(senderName);
        Glide.with(this)
                .load(senderBitmap)
                .into(userUpdatePicture);
    }


    @Override
    public void onBackPressed() {
        if(checkUpdate){
            setCancel();
        }else{
            UpdateProfileActivity.this.finish();
            overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_left);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profileEditBtn:
                editBtn.setVisibility(View.GONE);
                userName.setEnabled(true);
                cancelBtn.setVisibility(View.VISIBLE);
                confirmBtn.setVisibility(View.VISIBLE);
                picChangeBtn.setVisibility(View.VISIBLE);
                checkUpdate=true;
                break;
            case R.id.profileCancelBtn:
                setCancel();
                break;
            case R.id.profileConfirmBtn:
                updateProfile();
                break;
            case R.id.pictureChange:
                setPicture();
                break;
            case R.id.updateProfileBackPressId:
                if(checkUpdate){
                    setCancel();
                }else{
                    UpdateProfileActivity.this.finish();
                    overridePendingTransition(R.anim.slide_out_right,R.anim.slide_in_left);
                }
                break;
        }
    }
    private void setCancel(){
        userName.setText(senderName);
        Glide.with(this)
                .load(senderBitmap)
                .into(userUpdatePicture);

        imageByte=null;
        checkUpdate=false;
        editBtn.setVisibility(View.VISIBLE);
        userName.setEnabled(false);
        cancelBtn.setVisibility(View.GONE);
        confirmBtn.setVisibility(View.GONE);
        picChangeBtn.setVisibility(View.GONE);
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


    public void checkPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            isPermissionGranted=true;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
        }
        else{
            ActivityCompat.requestPermissions(UpdateProfileActivity.this,permissions,REQUEST_CODE);
        }
    }
    private void updateProfile(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(userName.getText().toString().length()>0){
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                //connected = true;
                preferences.setDataLogin(UpdateProfileActivity.this,true);
                if(imageByte==null){
                    userPicture= imageProcess.getStringFromBitmap(senderBitmap,"high");
                    preferences.setDataAs(UpdateProfileActivity.this,senderUniqueId,userName.getText().toString(), preferences.getDataImage(UpdateProfileActivity.this));
                }else{
                    senderBitmap=bitmap= imageProcess.getResizedBitmap(BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length),300);
                    userPicture= imageProcess.getStringFromBitmap(bitmap,"low");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    preferences.setDataAs(UpdateProfileActivity.this,senderUniqueId,userName.getText().toString(), imageProcess.getStringFromBitmap(bitmap,"low"));
                }
                senderName=userName.getText().toString();

                //bitmap.recycle();
                updateUser();
                checkUpdate=false;

                imageByte=null;
                cancelBtn.setVisibility(View.GONE);
                editBtn.setVisibility(View.VISIBLE);
                userName.setEnabled(false);
                confirmBtn.setVisibility(View.GONE);
                picChangeBtn.setVisibility(View.GONE);
            } else {
                // connected = false;
                Toast.makeText(UpdateProfileActivity.this, "Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(UpdateProfileActivity.this, "Please upload your picture.", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUser(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",senderUniqueId);
            jsonObject.put("username",userName.getText().toString());
            jsonObject.put("image",userPicture);
            socketManager.getSocket().emit("UpdateProfile",jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
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
            if(imageUriResultCrop!=null){
                Bitmap image = null ;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUriResultCrop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(UpdateProfileActivity.this)
                        .load(image)
                        .into(userUpdatePicture);
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
        uCrop.start(UpdateProfileActivity.this);

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
}