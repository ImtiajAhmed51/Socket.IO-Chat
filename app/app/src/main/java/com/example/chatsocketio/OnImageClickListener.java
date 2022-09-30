package com.example.chatsocketio;
public interface OnImageClickListener {
    void onImageLongClick(int imageData,int type);
    void onImageClick(int imageData);
    void showDialog(int position,int type);
}
