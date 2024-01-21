package com.imoxion.sensems.server.service;

import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.util.ImEmsUtil;
import org.junit.Test;

public class ImbDatabaseServiceTest {

    @Test
    public void decryptTest(){
        String secret_key = "!ems!imoxion00000000000000000000";
        String enc = "I7wEPWb4uniiAcRPFSprLV/tH7i8f26+2faXu+RcC6MdTq8K2hI5Hg7PYriXuEQgtuSjLQtC9skLv+nGx7C63smNUMirnNejOwI3wSUvzzbh32zdmR1SY3vhpPRPiBa0";
        try {
            String dec = ImSecurityLib.decryptAES256(secret_key, enc);
            System.out.println("dec = " + dec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void decryptStringTest(){
        System.setProperty("sensems.home", "D:\\eclipse\\workspace4\\sensems\\8.0\\trunk\\dist");

        String test = "sF5xXCr5Yy8ILWvAvNpFEQ==";
        try {
            String ttt = ImEmsUtil.getDecryptString(test);
            System.out.println("ttt = " + ttt);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}