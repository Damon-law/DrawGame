package com.example.DrawGame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipSession;

/**
 * Created by adminlyb on 2017/12/15.
 */

public class MessageBrocastReceiver extends BroadcastReceiver {
    public static final String START_GET_PICTURE = "GET_PICTURE_FROM_SOCKET";
    public static final String START_GET_USER_NAME = "GET_USER_NAME_FROM_SOCKET";
    public static final String END_GET_PICTURE = "END_OF_GETTING_A_PICTURE";
    public static final String END_GET_USER_NAME = "END_OF_GETTING_A_USER_NAME";
    public static final String START_GAME = "GAME_START";
    private StartListener listener;
    boolean isRecevingPicture;
    boolean isRecevingName;

    public MessageBrocastReceiver(StartListener listener){

        this.listener = listener;
        isRecevingPicture = false;
        isRecevingName = false;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(START_GAME)){
            if(!isRecevingName && !isRecevingPicture){
                listener.ListenningChange();
            }
        }
        else if(action.equals(START_GET_PICTURE)){
            isRecevingPicture = true;
        }
        else if(action.equals(START_GET_USER_NAME)){
            isRecevingName = true;
        }
        else if(action.equals(END_GET_PICTURE)){
            isRecevingPicture = false;
        }
        else if(action.equals(END_GET_USER_NAME)){
            isRecevingName = false;
        }
    }
}
