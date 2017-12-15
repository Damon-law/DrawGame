package com.example.DrawGame;

import android.app.ListFragment;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adminlyb on 2017/12/15.
 */

public class InfoServer implements Runnable {

    private Socket socket;
    private ServerSocket serverSocket;
    private int pic_port;
    private int name_port;
    private HashMap<String,String>host_name;
    private HashMap<String,String>host_pic;
    private List<String> host;

    public InfoServer(int pic, int name, HashMap<String,String>namemap, HashMap<String,String>picmap, List<String>ip){
        this.pic_port =pic;
        this.name_port = name;
        this.host_name = namemap;
        this.host_pic = picmap;
        this.host = ip;
    }

    @Override
    public void run() {
        new PicSer(pic_port,host_pic,host).run();
        new NameSer(name_port).run();;
    }


    class PicSer implements Runnable{
        private final String TAG = "Doing PicSer";
        private int port;
        private HashMap<String,String>map;
        private ServerSocket mServersocket;
        private Socket socket;
        private List<String>host;
        public PicSer(int port,HashMap<String,String>map,List<String>host){
            this.port = port;
            this.map = map;
            this.host = host;
        }

        @Override
        public void run() {
            try{
                mServersocket = new ServerSocket(port);
                while (true){
                    try{
                        socket = serverSocket.accept();
                        if(!host.contains(socket.getInetAddress().getHostAddress())) {
                            host.add(socket.getInetAddress().getHostAddress());
                        }
                        new DealPic(socket.getInetAddress().getHostAddress(),socket).run();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class NameSer implements Runnable{

        private final String TAG = "NAMESER";
        int port;
        private ServerSocket mSeverSocket;
        public NameSer(int port){
            this.port = port;
        }

        @Override
        public void run() {
            try{
                mSeverSocket = new ServerSocket(port);
                while(true){
                    socket = mSeverSocket.accept();
                    if(!host.contains(socket.getInetAddress().getHostAddress())){
                        host.add(socket.getInetAddress().getHostAddress());
                    }
                    new DealName(socket,socket.getInetAddress().getHostAddress()).run();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class DealName implements Runnable{
        private final String TAG = "DEALNAME";
        private String ip;
        private Socket socket;
        private InputStream in;
        private ByteArrayOutputStream ou;

        public DealName(Socket socket,String ip){
            this.socket = socket;
            this.ip = ip;
        }

        @Override
        public void run() {
            try{
                in = socket.getInputStream();
                int i;
                Log.i(TAG,"Aceptting name...");
                while((i = in.read()) != -1){
                    ou.write(i);
                }
                String name = ou.toString();
                Log.i(TAG,"Aceptting..done"+name);
                if(!host_name.containsKey(ip)){
                    host_name.put(ip,name);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class DealPic implements Runnable{
        private final String TAG = "Deal Pic Data";
        private String ip;
        private DataInputStream in;
        private FileOutputStream ou;
        private Socket socket;
        private int length;

        public DealPic(String ip,Socket socket){
            this.socket = socket;
            this.ip = ip;
        }
        @Override
        public void run() {
            try{
                String filename = ip;
                in = new DataInputStream(socket.getInputStream());
                if(host_name.containsKey(ip)){
                    filename = host_name.get(ip);
                }
                final File f = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + "com.lyb.DrawGame" + "/userinfo-"
                                + filename + ".jpg");
                File dirs = new File(f.getParent());

                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                ou = new FileOutputStream(f);
                byte []inputbytes = new byte[1024];
                Log.i(TAG,"Aceptting img...");
                while((length = in.read(inputbytes,0,inputbytes.length)) > 0){
                    ou.write(inputbytes,0,length);
                    ou.flush();
                }
                Log.i(TAG,"Aceptted...done");
                if(!host_pic.containsKey(ip)) {
                    host_pic.put(ip, f.getAbsolutePath());
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
