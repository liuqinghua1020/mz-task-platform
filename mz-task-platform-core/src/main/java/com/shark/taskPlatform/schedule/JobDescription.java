package com.shark.taskPlatform.schedule;

import com.shark.taskPlatform.classloader.ApplicationClassLoader;
import com.shark.taskPlatform.pojo.JobImage;
import com.shark.taskPlatform.utils.JarFileHelper;
import com.shark.taskPlatform.utils.ThreadLocalProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.jar.JarFile;

/**
 * 任务描述，一个置于任务调度器里面的任务描述信息
 * @author liuqinghua
 *
 */
public class JobDescription {
	
	public JobImage jobImage;
	
	public ApplicationClassLoader appClassLoader;

	public JobImage getJobImage() {
		return jobImage;
	}

	public void setJobImage(JobImage jobImage) {
		this.jobImage = jobImage;
	}

	public ApplicationClassLoader getAppClassLoader() {
		return appClassLoader;
	}

	public void setAppClassLoader(ApplicationClassLoader appClassLoader) {
		this.appClassLoader = appClassLoader;
	}

	public static class StubJob implements Job {

        private static final Log log = LogFactory.getLog(StubJob.class);

        private JobDescription jobDescription;

        public StubJob(JobDescription jobDescription) {
            this.jobDescription = jobDescription;
        }

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
                for(Map.Entry<String, String> entry:envs.entrySet()){
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

    }
}
