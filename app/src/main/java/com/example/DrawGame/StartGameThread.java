package com.example.DrawGame;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adminlyb on 2017/12/15.
 */

public class StartGameThread implements Runnable {
    private static final String MSG = "I'm fucking ready,Please gogogo";
    private List<String> host;
    public HashMap<String, Socket> smap;
    private int port;

    StartGameThread(List<String>host,HashMap<String,Socket>map){
        this.host = host;
        this.smap = map;
    }
    @Override
    public void run() {
        for (String ip : host) {
            new SendGameStart(smap.get(ip)).run();
        }
    }

    class SendGameStart implements Runnable {
        private Socket socket;
        private int port;
        private OutputStream ou;

        public SendGameStart(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ou = socket.getOutputStream();
                ou.write(MSG.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
