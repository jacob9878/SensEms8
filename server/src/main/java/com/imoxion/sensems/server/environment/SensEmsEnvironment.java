package com.imoxion.sensems.server.environment;

import org.apache.commons.lang.StringUtils;

/**
 * Created by sungg on 2017-07-14.
 */
public class SensEmsEnvironment {

    private static String sensEmsServerHome;

    public static void init(){
        sensEmsServerHome = System.getProperty("sensems.home");
        System.setProperty("sensems.home", sensEmsServerHome);
        System.out.println("SensEmsServerHome = " + sensEmsServerHome);
    }

    /**
     * 센스메일 홈 디렉토리를 구한다.
     * @return
     */
    public static String getSensEmsServerHome(){
    	if(StringUtils.isEmpty(sensEmsServerHome)) {
            SensEmsEnvironment.init();
    	}
        return sensEmsServerHome;
    }

    /**
     * 센스메일 홈디렉토리 설정이 되어있는지 확인.
     * @return
     */
    public static boolean hasSensEmsHome(){
        if( StringUtils.isEmpty(sensEmsServerHome) ){
            System.out.println("----------------- ERROR ------------------");
            System.out.println("sensems home path setting please");
            System.out.println("system environment append or java -D option append -sensems.home={sensems home direocty}");
            System.out.println("vi /etc/profile");
            System.out.println("export SENSEMS_HOME={sensems home directory}");
            System.out.println("");
            System.out.println("OR");
            System.out.println("");
            System.out.println("JAVA OPT ADD -Dsensems.home={sensems home directory path}");
            System.out.println("");
            System.out.println("SYSTEM ERROR...");
            return false;
        }
        return true;
    }
}
