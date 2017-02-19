package com.shark.taskPlatform.pojo;

import java.util.Map;

/**
 * 用户上传上来的任务镜像
 * @author liuqinghua
 *
 */
public class JobImage{
	
	private String jobName;
	private String jarFileName;
	private String mainClass;
	private Map<String,String> envParamters;
	private String[]  paramters;
	//调度时间安排
	private String cron;
	
	public String getJarFileName() {
		return jarFileName;
	}
	public void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}
	public String getMainClass() {
		return mainClass;
	}
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	 
	public String[] getParamters() {
		return paramters;
	}
	public void setParamters(String[] paramters) {
		this.paramters = paramters;
	}
	public Map<String, String> getEnvParamters() {
		return envParamters;
	}
	public void setEnvParamters(Map<String, String> envParamters) {
		this.envParamters = envParamters;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
}
