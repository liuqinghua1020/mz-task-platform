package com.shark.taskPlatform.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;

/**
 * 根据 JobDescription 创建 Quartz的
 * @author liuqinghua
 *
 */
public class JobDescription2QuartzFactory {
	
	private static final Log log = LogFactory.getLog(JobDescription2QuartzFactory.class);
	
	public static Class<? extends Job> createQuartzJob(final JobDescription jobDescription) throws NullPointerException{
		if(jobDescription == null){
			throw new NullPointerException("指定的任务为空");
		}
		
		if(jobDescription.getAppClassLoader() == null || jobDescription.getJobImage() == null){
			throw new NullPointerException("加载任务的ClassLoader和jar文件为空");
		}
		
		StubJob job = new StubJob(jobDescription);
		return job.getClass();
		
	} 
}
