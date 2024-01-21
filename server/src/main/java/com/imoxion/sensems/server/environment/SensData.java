package com.imoxion.sensems.server.environment;

import com.imoxion.sensems.server.config.ImEmsConfig;

public class SensData {

    public final static int BASE = 0;

    public final static int FILEDB = 1;

    public final static int TEMPLATE = 2;

    public final static int GEOIP = 3;

    public final static int USER_DATA = 4;

    public final static int SYSMON = 5;

    public final static int DKIM = 6;

    public final static int WATCH = 7;

    public static String getPath(int type){
        String sensdataPath = ImEmsConfig.getInstance().getSensdataPath();
        if( type == FILEDB ){
            return sensdataPath + "/user_data/user_dir";
        }else if( type == TEMPLATE ){
            return sensdataPath + "/template";
        }else if( type == GEOIP ){
            return sensdataPath + "/geoip";
        }else if( type == USER_DATA ){
            return sensdataPath + "/user_data";
        }else if( type == SYSMON ) {
            return sensdataPath + "/sysmon";
        }else if( type == DKIM ) {
            return sensdataPath + "/dkim";
        }else if( type == WATCH ) {
            return sensdataPath + "/watch";
        }
        return sensdataPath;
    }
}