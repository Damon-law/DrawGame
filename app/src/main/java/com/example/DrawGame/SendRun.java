package com.example.DrawGame;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by adminlyb on 2017/12/15.
 */

public class SendRun implements Runnable {
    private String name;
    private String path;
    private String host;
    private int pic_port;
    private int name_port;

    SendRun(String host,String name,String path,int pic_port,int name_port){
        this.host = host;
        this.name = name;
        this.path = path;
        this.pic_port = pic_port;
        this.name_port = name_port;
    }

    @Override
    public void run() {
        new sendname().run();

    }
    class sendname implements Runnable{
        private Socket socket;
        private OutputStream ou;
        @Override
        public void run() {
            try{
                socket = new Socket();
                socket.connect(new InetSocketAddress(host,name_port));
                ou = socket.getOutputStream();
                ou.write(name.getBytes());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class sendpic implements Runnable{
        private Socket socket;
        private FileInputStream fis;
        private DataOutputStream ou;


        @Override
        public void run() {
            File file = new File(path);
            try{
                socket = new Socket();
                socket.connect(new InetSocketAddress(host,pic_port));
                fis = new FileInputStream(file);
                ou = new DataOutputStream(socket.getOutputStream());
                byte [] ouputbytes = new byte[1024];
                int length = 0;
                while((length = fis.read(ouputbytes,0,ouputbytes.length)) > 0){
                    ou.write(ouputbytes,0,length);
                    ou.flush();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}