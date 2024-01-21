package com.imoxion.sensems.server.define;

import java.util.HashMap;
import java.util.Map;

public class ImJdbcDriver {
    public static final String DRIVER_CLASS__MYSQL = "com.mysql.jdbc.Driver";

    public static final String DRIVER_CLASS__MARIADB = "org.mariadb.jdbc.Driver";

    public static final String DRIVER_CLASS__ORACLE = "oracle.jdbc.driver.OracleDriver";

    public static final String DRIVER_CLASS__MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static final String DRIVER_CLASS__TIBERO = "com.tmax.tibero.jdbc.TbDriver";

    public static final String DRIVER_CLASS__POSTGRESQL = "org.postgresql.Driver";

    public static final String DRIVER_CLASS__DB2 = "COM.ibm.db2.jdbc.net.DB2Driver";

    public static final String DRIVER_CLASS__ALTIBASE = "Altibase.jdbc.driver.AltibaseDriver";

    public static final String DRIVER_CLASS__INFORMIX = "com.informix.jdbc.IfxDriver";

    public static final String DRIVER_CLASS__CUBRID = "cubrid.jdbc.driver.CUBRIDDriver";

    private static final Map<String, String> jdbcMap = new HashMap<String, String>() {
        {
            put("mysql", DRIVER_CLASS__MYSQL);
            put("mariadb", DRIVER_CLASS__MARIADB);
            put("oracle",  DRIVER_CLASS__ORACLE);
            put("mssql", DRIVER_CLASS__MSSQL);
            put("tibero", DRIVER_CLASS__TIBERO);
            put("postgresql", DRIVER_CLASS__POSTGRESQL);
            put("db2", DRIVER_CLASS__DB2);
            put("altibase", DRIVER_CLASS__ALTIBASE);
            put("informix", DRIVER_CLASS__INFORMIX);
            put("cubrid", DRIVER_CLASS__CUBRID);
        }
    };

    /**
     * db 종류에 따른 jdbc driver 획득
     */
    public static String getDriver(String dbType){
        return jdbcMap.get(dbType.toLowerCase());
    }

}
