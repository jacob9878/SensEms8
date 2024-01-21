package com.imoxion.sensems.web.util;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 파일 유형이 변경되었는지 확인하는 Util Class
 * 업로드 제한되는 확장자를 우회하는 경우를 막기 위해 실제 파일 확장자와 MimeType 을 비교 하여 변경 유무를 감지하는 클래스이다.
 * 실제 MimeType 과 확장자간의 관계는 MimeType.xml 를 참조한다.
 * @author sunggyu
 * @since 2019.01.23
 */
public class MimeDetectUtil {

    private Logger logger = LoggerFactory.getLogger(MimeDetectUtil.class);

    private Map<String, Set<String>> mimeTypeMap = new HashMap<>();

    private static MimeDetectUtil mimeDetectUtil = null;

    public static MimeDetectUtil getInstance(){
        if( mimeDetectUtil == null ){
            mimeDetectUtil = new MimeDetectUtil();
        }
        return mimeDetectUtil;
    }
    private MimeDetectUtil() {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(MimeDetectUtil.class.getResourceAsStream("/MimeType.xml"));
            NodeList nodeList = doc.getElementsByTagName("MimeType");
            int nodeLength = nodeList.getLength();
            for (int i = 0; i < nodeLength; i++) {
                NamedNodeMap namedNodeMap = nodeList.item(i).getAttributes();
                String mimeType = namedNodeMap.getNamedItem("type").getNodeValue();
                String[] exts = StringUtils.split(namedNodeMap.getNamedItem("exts").getNodeValue(),",");
//                logger.debug("read - {}:{}",mimeType,exts);
                Set<String> extSet = new HashSet<>();
                for(String ext : exts){
                    extSet.add(ext);
                }
                mimeTypeMap.put(mimeType,extSet);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("MimeDetectUtil error:{}",errorId);
        } catch (Exception ee) {
            String errorId = ErrorTraceLogger.log(ee);
            logger.error("MimeDetectUtil error:{}",errorId);
        }


    }

    /**
     * 파일 확장자와 실제 MimeType 과 일치하는지 확인
     * @param filePath - 파일경로
     * @return
     */
    public boolean detectFileExtensionChange(File filePath){
        return detectFileExtensionChange( filePath , filePath.getName() );
    }

    /**
     * 파일경로의 파일이름이 실제 파일이름과 다른 경우 실제 파일이름을 받아 확인
     * @param filePath - 파일 경로
     * @param fileName - 파일이름
     * @return
     */
    public boolean detectFileExtensionChange(File filePath, String fileName){
        /*String ext = FilenameUtils.getExtension( fileName );
        String mimeType = getMimeType(filePath);
        logger.debug("mime detect - fileName : {} -- mimeType : {}",fileName,mimeType);
        return mimeTypeCheck(mimeType,ext);*/
        boolean bResult = false;
        try {
            String ext = FilenameUtils.getExtension(fileName);
            if(StringUtils.isEmpty(ext)){
                ext = "txt";
            }
            String mimeType = new Tika().detect(filePath);
            if(StringUtils.isNotEmpty(mimeType)) {
                mimeType = ImStringUtil.getStringBefore(mimeType, ";", false);
            }
            logger.debug("mime detectFileExtensionChange fileName: {}, mimeType: {}", fileName, mimeType);
            MediaType mdType = MediaType.parse(mimeType);
            String mediaType = mdType.toString();
            String subType = mdType.getSubtype();

            List listMime = getExtensions(mimeType);

            if(listMime !=null) {
                logger.debug("mime detect0 - fileName : {}, mimeType : {}, mediaType : {}", fileName, mimeType, mediaType);

                if (listMime.contains("." + ext.toLowerCase())) {
                    bResult = true;
                }
            }
            /*String mimeType = getMimeType(is);
            logger.debug("mime detect - fileName : {} -- mimeType : {}",fileName,mimeType);
            return mimeTypeCheck(mimeType,ext);*/
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            logger.error("getMimeType error:{}",errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("getMimeType error:{}",errorId);
        }

        return bResult;
    }

    public boolean detectFileExtensionChange(InputStream is,String fileName){
        boolean bResult = false;
        try {
            String ext = FilenameUtils.getExtension(fileName);

            if(StringUtils.isEmpty(ext)){
                ext = "txt";
            }
            logger.debug("mime detectFileExtensionChange fileName: {}", fileName);
            String mimeType = new Tika().detect(is, fileName);
            if(StringUtils.isNotEmpty(mimeType)) {
                mimeType = ImStringUtil.getStringBefore(mimeType, ";", false);
            } else {
                return false;
            }
            logger.debug("mime detectFileExtensionChange fileName: {}, mimeType: {}", fileName, mimeType);
            MediaType mdType = MediaType.parse(mimeType);
            String mediaType = mdType.toString();
            String subType = mdType.getSubtype();

//            List<String> listExts = new ArrayList<>();
            List listMime = getExtensions(mimeType);
//            List listMedia = getExtensions(mediaType);
//            listExts.addAll(listMime);
//            listExts.addAll(listMedia);

            if(listMime != null) {
            logger.debug("mime detect1 - fileName : {}, mimeType : {}, mediaType : {}, listMime size: {}",fileName, mimeType, mediaType, listMime.size());

                if (listMime.contains("." + ext.toLowerCase())) {
                    bResult = true;
                }
            }
            /*String mimeType = getMimeType(is);
            logger.debug("mime detect - fileName : {} -- mimeType : {}",fileName,mimeType);
            return mimeTypeCheck(mimeType,ext);*/
        }catch (NullPointerException ne) {
            String errorId = ErrorTraceLogger.log(ne);
            logger.error("getMimeType error:{}",errorId);
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("getMimeType error:{}",errorId);
        }

        return bResult;
    }

    public List<String> getExtensions(final String mimeType) {
        if (mimeType == null || mimeType.isEmpty())
            return null;

        final MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();

        try {
            MimeType tikaMimeType = allTypes.forName(mimeType);
            return tikaMimeType.getExtensions();
        } catch (MimeTypeException e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("getMimeType getExtensions error:{}", errorId);
            return null;
        } catch (Exception ee) {
            String errorId = ErrorTraceLogger.log(ee);
            logger.error("getMimeType getExtensions error:{}", errorId);
            return null;
        }


    }

    private boolean mimeTypeCheck(String mimeType, String ext){
        List<String> listExts = getExtensions(mimeType);
        if(listExts != null){
            if(listExts.contains("."+ext.toLowerCase())){
                logger.debug(" mimeTypeMap.get(mimeType).contains(ext.toLowerCase()) : {} - {}", mimeType, ext);
                return true;
            }
        }
        return false;
        /*if( mimeTypeMap.containsKey(mimeType) ){
            logger.debug("mimeType contains : {} - {}", mimeType, ext);
            if( mimeTypeMap.get(mimeType).contains(ext.toLowerCase()) ){
                logger.debug(" mimeTypeMap.get(mimeType).contains(ext.toLowerCase()) : {} - {}", mimeType, ext);

                return true;
            }
        }
        return false;*/
    }

    public String getMimeType(File f){
        String mimeType = "unknown";
        try {
            mimeType = new Tika().detect(f);
            if(StringUtils.isNotEmpty(mimeType)) {
                mimeType = ImStringUtil.getStringBefore(mimeType, ";", false);
            }
        } catch (IOException e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - getMimeType error:{}",errorId);
        } catch (Exception ee) {
            String errorId = ErrorTraceLogger.log(ee);
            logger.error("getMimeType error:{}",errorId);
        }
        /*InputStream is = null;
        try {
            TikaConfig tika = new TikaConfig();
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, f.toString());
            is = new FileInputStream(f);
            MediaType mimetype = tika.getDetector().detect(TikaInputStream.get(is), new Metadata());
            return mimetype.toString();
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - getMimeType error:{}",errorId);
            return "unknown";
        }finally{
            try{ if( is != null ) is.close(); }catch (Exception e){}
        }*/
        return mimeType;
    }

    public String getMimeType(InputStream is, String fileName){
        try {
//            TikaConfig tika = new TikaConfig();
//            MediaType mimetype = tika.getDetector().detect(TikaInputStream.get(is), new Metadata());
            String mimeType = new Tika().detect(is, fileName);
            if(StringUtils.isNotEmpty(mimeType)) {
                mimeType = ImStringUtil.getStringBefore(mimeType, ";", false);
            }
            return mimeType;
        }catch (IOException ee) {
            String errorId = ErrorTraceLogger.log(ee);
            logger.error("getMimeType error:{}",errorId);
            return "unknown";
        }
        catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("getMimeType error:{}",errorId);
            return "unknown";
        }
    }

    /**
     * mimeType 이 이미지인지 아닌지 확인
     * @param file
     * @return
     */
    public boolean isImage(File file){
        String mimeType = getMimeType(file);
        logger.debug("isImage - filePath:{} , mimeType:{}",file.getAbsolutePath() , mimeType );
        if(!mimeType.toLowerCase().startsWith("image/")){
            return false;
        }
        return true;
    }

    /**
     * File 을 이용하여 실제 파일의 확장자를 구한다.
     * MimeType 에 대한 확장자가 여러개인경우 또는 알수 없는 MimeType 인경우 null 을 반환한다.
     * @param file
     * @return
     */
    public String getFileExtForMimeType(File file){
        String mimeType = getMimeType(file);
        return getFileExtForMimeType(mimeType);
    }

    /**
     * InputStream 을 이용하여 실제 파일의 확장자를 구한다.
     * MimeType 에 대한 확장자가 여러개인경우 또는 알수 없는 MimeType 인경우 null 을 반환한다.
     * @param is
     * @return
     */
    public String getFileExtForMimeType(InputStream is, String fileName){
        String mimeType = getMimeType(is, fileName);
        return getFileExtForMimeType(mimeType);
    }

    /**
     * MimeType 을 이용하여 실제 파일의 확장자를 구한다.
     * MimeType 에 대한 확장자가 여러개인경우 에는 정의된 확장자 첫번째께 나옴(MimeType.xml)
     * 알수 없는 MimeType 인경우 null 을 반환한다.
     * @param mimeType
     * @return
     */
    private String getFileExtForMimeType(String mimeType){
        String ext = null;

        TikaConfig config = TikaConfig.getDefaultConfig();
        MimeTypes allTypes = config.getMimeRepository();

        MimeType mt;
        try {
            mt = allTypes.forName(mimeType);
            ext = mt.getExtension();
        } catch (MimeTypeException e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("getFileExtForMimeType error:{}",errorId);
        } catch (Exception ee) {
            String errorId = ErrorTraceLogger.log(ee);
            logger.error("getFileExtForMimeType error:{}",errorId);
        }

        if(StringUtils.isEmpty(ext)) {
            if (mimeTypeMap.containsKey(mimeType)) {
                Set<String> exts = mimeTypeMap.get(mimeType);
                Iterator<String> extIt = exts.iterator();
                // 화
                if (extIt.hasNext()) {
                    ext = extIt.next();
                }
            }
        }
        logger.debug("getFileExtForMimeType - mimeType:{}, ext:{}",mimeType,ext);
        return ext;
    }

    /*public static void main(String[] args){
        try {
            TikaConfig config = TikaConfig.getDefaultConfig();
            MimeTypes allTypes = config.getMimeRepository();

            //MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();

            File file = new File("C:\\eclipse\\jar_out\\imcommon-1.2.1.jar");
            //File file = new File("E:\\사용자\\Downloads\\NYK.zip");

            String mimeType = null;
            FileInputStream fis = null;

            try{
                fis = new FileInputStream(file);
                mimeType = new Tika().detect(fis, file.getName());
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(fis != null) fis.close();
            }

            MediaType mediaType = MediaType.parse(mimeType);
            String subType = mediaType.getSubtype();


            System.out.println("파일명: " + file.getName());
            System.out.println("마임타입: " + mimeType + " // mediatype: " + mediaType + " / subtype - " + subType);

            System.out.println("확장자1: " + FilenameUtils.getExtension( file.getName() ));
            MimeType mt;
            try {
                mt = allTypes.forName(mimeType);
                System.out.println("확장자: " + mt.getExtension());
            } catch (MimeTypeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/
}