package com.example.chatsocketio.socket;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;
public class SocketManager{
    private static Socket mSocket;
    private static SocketManager socketManager=null;
    private SocketManager(){
        try {
            mSocket = IO.socket("https://nameless-falls-01747.herokuapp.com/");
            mSocket.connect();
        } catch (URISyntaxException e) {}
    }
    public Socket getSocket(){
        return mSocket;
    }
    public static SocketManager getInstance(){
        if(socketManager==null){
            socketManager=new SocketManager();
        }
        return socketManager;
    }
    public Emitter getEmitterListener(String event, Emitter.Listener listener){
        return getSocket().on(event,listener);
    }

}
