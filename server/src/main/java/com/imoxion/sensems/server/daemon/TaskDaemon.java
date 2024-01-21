package com.imoxion.sensems.server.daemon;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import com.imoxion.sensems.server.logger.LoggerLoader;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2015-01-29.
 */
public class TaskDaemon {

    private static Logger logger = LoggerFactory.getLogger("DAEMON");

    public static boolean bIsAlive = true;

    private static Scheduler scheduler;

    public static Scheduler getScheduler(){
        return scheduler;
    }

    public static void main(String[] args) {
    	try {
			SensEmsEnvironment.init();
			//SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
System.out.println("TaskDaemon.main start");

	    	//System.out.println("Scheduler conf: " + SensProxyEnvironment.getSensProxyServerHome()); 

			LoggerLoader.initLog("emslog.xml");

			logger.info( "TaskDaemon Start...000");
			ImDatabaseConnectionEx.init("sensems.home", "mybatis-config.xml");

			ImEmsConfig emsConfig = ImEmsConfig.getInstance();
	
	        String daemonConfigFile = SensEmsEnvironment.getSensEmsServerHome() + File.separator + "conf" + File.separator + "sensems-daemon.xml";
			logger.info("Scheduler conf: " + daemonConfigFile);
			 
			//quartz 를 이용한 스케쥴러 데몬
			Properties quartzProperties = new Properties();
			quartzProperties.put("org.quartz.scheduler.skipUpdateCheck","true");
			quartzProperties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			quartzProperties.put("org.quartz.threadPool.threadCount", "3");
			quartzProperties.put("org.quartz.plugin.jobInitializer.class", "org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin");
			quartzProperties.put("org.quartz.plugin.jobInitializer.fileNames", daemonConfigFile);
			quartzProperties.put("org.quartz.plugin.jobInitializer.failOnFileNotFound", true);
			quartzProperties.put("org.quartz.plugin.jobInitializer.scanInterval", "0");
			
			
			StdSchedulerFactory factory;
//			Scheduler scheduler = null;
			try {
			    factory = new StdSchedulerFactory(quartzProperties);
			    scheduler = factory.getScheduler();
			    scheduler.start();
			    
			    for (String groupName : scheduler.getJobGroupNames()) {
			        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
			            String jobName = jobKey.getName();
			            String jobGroup = jobKey.getGroup();
			
			            //get job's trigger
			            List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
			            Date nextFireTime = triggers.get(0).getNextFireTime();
			
			            logger.info("[Scheduler JobName]: " + jobName + ", [groupName]: " + jobGroup + " - " + nextFireTime);
						//System.out.println("[Scheduler JobName]: " + jobName + ", [groupName]: " + jobGroup + " - " + nextFireTime);
			        }
			    }
			    logger.info("Scheduler Start.");
				System.out.println("Scheduler Start.");
			} catch (Exception e) {
				logger.error("Scheduler Error: "+ e);
			}
	        
    	} catch(Exception ee) {
    		ee.printStackTrace();
    	}
    }

}
