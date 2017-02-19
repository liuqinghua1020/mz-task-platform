package com.shark.taskPlatform.schedule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.jar.JarFile;

import com.shark.taskPlatform.classloader.ApplicationClassLoader;
import com.shark.taskPlatform.pojo.JobImage;
import com.shark.taskPlatform.utils.JarFileHelper;
import com.shark.taskPlatform.utils.ThreadLocalProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;


/**
 * @Title:Schedule管理类
 */
public class ScheduleManager {
	
	public static final Log log = LogFactory.getLog(ScheduleManager.class);
	
	protected Scheduler scheduler;
	
	/**
	 * 初始化任务调度管理类
	 * @param properties
	 * @throws SchedulerException
	 */
	public void initScheduler(Properties properties) throws SchedulerException{
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        schedulerFactory.initialize(properties);
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }
	
	/**
	 * 调度任务
	 * @throws SchedulerException
	 */
	public void schedule(final JobDescription jobDescription) throws SchedulerException{
		if(scheduler == null || !scheduler.isStarted()){
			throw new SchedulerException("ScheduleManager 没有初始化成功");
		}
		
		try {
			
			Job myJob = new Job() {
				
				@Override
				public void execute(JobExecutionContext context) throws JobExecutionException {
					ApplicationClassLoader applicationClassLoader = jobDescription.getAppClassLoader();
					JobImage image = jobDescription.getJobImage();

					String jarFileName = image.getJarFileName();
					if (StringUtils.isEmpty(jarFileName)) {
						log.error("jar包文件名为空");
						throw new JobExecutionException("jar包文件名为空");
					}

					JarFile jarFile = null;
					String mainClass = null;
					try {
						jarFile = JarFileHelper.readJarFile(jarFileName);
						mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
					} catch (IOException e1) {
						log.error(e1.getMessage(), e1);
					}

					if (jarFile == null || mainClass == null || "".equals(mainClass.trim())) {
						return;
					}
					
					
					Thread.currentThread().setContextClassLoader(applicationClassLoader);
					
					System.setProperties(new ThreadLocalProperties(System.getProperties()));
					
					Map<String, String> envs = image.getEnvParamters();
					
					if(envs != null){
						for(Entry<String, String> entry:envs.entrySet()){
							System.setProperty(entry.getKey(),  entry.getValue());
						}
					}
					
					String[] params = image.getParamters();
					
					applicationClassLoader.addJarFiles(new String[]{jarFileName});
					 
					Class clazz = null;
					try {
						clazz = applicationClassLoader.loadClass(mainClass);
						Method method = clazz.getMethod("main", String[].class); 
						if(params != null && params.length > 0){
							method.invoke(null, params);
						}else{
							method.invoke(null, new String[1]);
						}
					} catch (ClassNotFoundException e) {
						log.error(e.getMessage(), e);
					} catch (IllegalAccessException e) {
						log.error(e.getMessage(), e);
					} catch (IllegalArgumentException e) {
						log.error(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						log.error(e.getMessage(), e);
					} catch (NoSuchMethodException e) {
						log.error(e.getMessage(), e);
					} catch (SecurityException e) {
						log.error(e.getMessage(), e);
					}
				}
			};
			
			
			
			JobDetail job = JobBuilder.newJob(myJob.getClass())
					        .withIdentity(jobDescription.getJobImage().getJobName())
					        .build();
            scheduler.addJob(job, true);
        } catch (SchedulerException e) {
        	log.error(e.getMessage(), e);
        	throw new SchedulerException("任务调度出现异常");
        }
		
		
	}
}
