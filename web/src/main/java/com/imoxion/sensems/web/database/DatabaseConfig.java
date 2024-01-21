package com.imoxion.sensems.web.database;

import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ucar.nc2.util.IO;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


//@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan(basePackages = "com.imoxion.sensems.web.database",
        annotationClass = MapperScan.class,
        sqlSessionTemplateRef = "sqlSession"
)
public class DatabaseConfig {

    private static Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired
    private VendorDatabaseIdProvider vendorDatabaseIdProvider;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    private SqlSessionFactoryBean sqlSessionFactoryBean() throws IOException{

        logger.info("MyBatisConfig Load..");

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        // 마이바티스가 사용한 DataSource를 등록
        factoryBean.setDataSource(dataSource);

        // 마이바티스 설정파일 위치 설정
        factoryBean.setConfigLocation(applicationContext.getResource("/WEB-INF/config/mybatis/mybatis-config.xml"));

        StringBuffer typeAlias = new StringBuffer();
        typeAlias.append("com.imoxion.sensems.web.database.domain");

        // databaseIdProvider
        factoryBean.setDatabaseIdProvider(vendorDatabaseIdProvider);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            logger.debug("META : {}", conn.getMetaData().getDriverName());
        }catch (DataAccessException de) {
            logger.error("SqlSessionFactoryBean error");
        }

        catch(Exception e){
            logger.error("SqlSessionFactoryBean error");
        }finally{
            try{ if( conn != null ) conn.close(); }catch (DataAccessException de) {} catch(Exception e){}
        }
        ArrayList<Resource> resources = new ArrayList<>();
        resources.addAll(Arrays.asList(applicationContext.getResources("classpath:com/imoxion/sensems/web/database/mapper/*.xml")));
        factoryBean.setTypeAliasesPackage(typeAlias.toString());

        Resource[] mapperLocations = resources.toArray(new Resource[resources.size()]);
        factoryBean.setMapperLocations(mapperLocations);

        return factoryBean;
    }

    @Bean(name="sqlSession",autowire = Autowire.BY_NAME)
    public SqlSessionTemplate sqlSessionTemplate() throws Exception{
        return new SqlSessionTemplate(sqlSessionFactoryBean().getObject());
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }
}
