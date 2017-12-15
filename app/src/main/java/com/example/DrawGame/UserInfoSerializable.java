package com.example.DrawGame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adminlyb on 2017/12/15.
 */

public class UserInfoSerializable implements Serializable {
    private HashMap<String,String>hostToName;
    private List<String>host;
    private HashMap<String,String>hostToPic;

    public UserInfoSerializable(HashMap<String,String>namemap,HashMap<String,String>picmap,List<String>host){
        this.hostToPic = picmap;
        this.hostToName = namemap;
        this.host = host;
    }

    public HashMap<String,String> getHostToName(){
        return hostToName;
    }
    public HashMap<String,String> getHostToPic(){
        return hostToPic;
    }

    public List<String> getHost(){
        return host;
    }

    public void setHostToName(HashMap<String,String> map){
        this.hostToName = map;
    }
    public void setHostToPic(HashMap<String,String>map){
        this.hostToPic = map;
    }

    public void setHost(List<String>host){
        this.host = host;
    }
}
