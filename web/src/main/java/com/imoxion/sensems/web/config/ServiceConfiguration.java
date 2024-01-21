package com.imoxion.sensems.web.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    

    /**
     * velocity가 spring3.2 부터 deprecated되어 아래와 같이 변경되었음.
     * @return
     */
    @Bean(name = "velocityEngine")
    public VelocityEngine velocityEngine(){
        String templatePath ="";// SensData.getPath(SensData.TEMPLATE);
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "file");
        velocityEngine.setProperty("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        velocityEngine.setProperty("file.resource.loader.path",templatePath);
        velocityEngine.setProperty("file.resource.loader.ca1023che",true);
        velocityEngine.setProperty("file.resource.loader.modificationCheckInterval","10");
        velocityEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem"); 
        velocityEngine.init();
        return velocityEngine;
    }
}
