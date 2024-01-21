import org.junit.Test;

import java.util.ArrayList;

public class QeuryColSplitTest {

    @Test
    public void test(){
        /**
         * 수신그룹 관리
         * 필드를 추출 테스트
         *
         * String selector = select ~ from 절을 추출한 내용
         *
         * 체크케이스
         * 1. concat 만 사용한 경우
         * 2. concat as 를 사용한 경우
         * 3. as만 사용한 경우
         * 4. 콤마로만 구분된 경우
         * 5. 구분자가 없는 경우
         */
        //String qry = "select col2,col1,col0,col3,col4 from imb_test";
      //  String qry = "select email,col1,col2,col3,col4,col5 from import_20070625184605";
        String qry = "select concat(userid,'@hanjin.sensmail.com') as email ,name,pwd from import_20090413170759";
        String selector;
        String col [];

        ArrayList<String> colList = new ArrayList<String>();

        //소문자로 전환
        qry = qry.toLowerCase();

        //공백문자 제거
        qry = qry.replaceAll("\\p{Z}", "");

        //소문자로 형변환 ? mysql에 lower_case table_names 가 되어있으면상관없다.
        int start_char = qry.indexOf("select") + 6;
        int last_char = qry.indexOf("from");

        // select ~ from 까지 문자를 구한다.
        selector = qry.substring(start_char, last_char);

        // 구한문자에서 , 콤마를 통한 구분자로 구분하여 구분자가 존재하는 경우 컬럼이 여러항목이므로 스플릿한다.
        if(selector.indexOf(",") != -1){
            col = selector.split(",");
            System.out.println("구분자가 있습니다.");

            // concat - as가 존재하는 경우가 있으므로 예외 처리한다.
//            if(selector.indexOf("concat") != -1 && selector.indexOf("as") != -1){ // concat과 앨리어스가 동시에 선언된 경우
//
//            }else if(selector.indexOf("as") != -1) { // as만 선언된 경우
//
//            }else if(selector.indexOf("concat") != -1){ //concat만 선언된 경우
//
//            }else{ // 예외가 존재하지 않고 일반 구분으로만 구성된 되어있는 경우
                for(String selcol  : col){
                    colList.add(selcol);
                    System.out.println(selcol);
                }
//            }
        }else{ // 구분자가 없을 경우 하나만 추출하므로 selector 만 반환
            colList.add(selector);
            System.out.println("구분자가 없습니다.");
            System.out.println(selector);
        }
    }
}


