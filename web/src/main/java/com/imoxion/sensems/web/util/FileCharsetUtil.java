package com.imoxion.sensems.web.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 파일의 Charset 관련 Util.
 */
public class FileCharsetUtil {

    private static Logger logger = LoggerFactory.getLogger(FileCharsetUtil.class);

    /**
     * 파일의 charset 을 구한다.
     * @param bytes
     * @return
     */
    public static String detectCharset(byte[] bytes) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        return detector.getDetectedCharset();
    }

    /**
     * srcEncoding 형식의 source 파일을 targetEncoding 의 target 파일로 converter 한다.
     * @param source
     * @param srcEncoding
     * @param target
     * @param targetEncoding
     * @throws IOException
     */
    private static void transform(File source, String srcEncoding, File target, String targetEncoding) throws IOException {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(source),srcEncoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), targetEncoding));
            char[] buffer = new char[16384];
            int read;
            while ((read = br.read(buffer)) != -1)
                bw.write(buffer, 0, read);
        } finally {
            try {
                if (br != null)
                    br.close();
            } finally {
                if (bw != null)
                    bw.close();
            }
        }
    }

    /**
     * source 파일을 UTF8 로 변환하여 target 으로 저장한다.
     * @param source
     * @param target
     * @throws IOException
     */
    public static void converterUTF8(File source, File target) throws IOException {
        String srcEncoding = detectCharset(FileUtils.readFileToByteArray(source));
        logger.debug("converter source charset result : {}({})",source.getAbsolutePath(),srcEncoding);
        if(StringUtils.isEmpty(srcEncoding)) srcEncoding = "8859_1";
        if( !"UTF-8".equalsIgnoreCase(srcEncoding) ) {
            transform(source, srcEncoding, target, "UTF-8");
        }else{
            // 이미 encoding 이 UTF-8 이면 복사만 한다.
            FileUtils.copyFile(source , target);
        }
    }

    @Test
    public void test() throws Exception {
        File f = new File("d:/files/address.csv");
        System.out.println(FileCharsetUtil.detectCharset(FileUtils.readFileToByteArray(f)));
    }
}