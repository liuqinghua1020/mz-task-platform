import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import com.shark.taskPlatform.classloader.ApplicationClassLoader;
import com.shark.taskPlatform.utils.JarFileHelper;
import com.shark.taskPlatform.utils.ThreadLocalProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class Bootstrap {
	
	public static final Log log = LogFactory.getLog(Bootstrap.class);

	public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String jarFileName1 = "C:\\Users\\liuqinghua\\Desktop\\jardemo1-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
		final String jarFileName2 = "C:\\Users\\liuqinghua\\Desktop\\jardemo2-0.0.1-SNAPSHOT-jar-with-dependencies.jar";
		
		
		
		final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		
		//System.setProperties(new ThreadLocalProperties(System.getProperties()));
		
		Thread thread1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				JarFile jarFile = null;
				String mainClass = null;
				try {
					jarFile = JarFileHelper.readJarFile(jarFileName1);
					mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
				} catch (IOException e1) {
					log.error(e1.getMessage(), e1);
				}
				
				if(jarFile == null || mainClass == null || "".equals(mainClass.trim())){
					return;
				}
				
				ApplicationClassLoader applicationClassLoader1 = new ApplicationClassLoader(classLoader);
				Thread.currentThread().setContextClassLoader(applicationClassLoader1);
				
				System.setProperties(new ThreadLocalProperties(System.getProperties()));
				System.setProperty("CONFIG", "aaa");
				applicationClassLoader1.addJarFiles(new String[]{jarFileName1});
				 
					Class clazz = null;
					try {
						clazz = applicationClassLoader1.loadClass(mainClass);
						Method method = clazz.getMethod("main", String[].class);  
						method.invoke(null, new String[1]);
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
		});
		
		Thread thread2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				JarFile jarFile = null;
				String mainClass = null;
				try {
					jarFile = JarFileHelper.readJarFile(jarFileName2);
					mainClass = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
				} catch (IOException e1) {
					log.error(e1.getMessage(), e1);
				}
				
				if(jarFile == null || mainClass == null || "".equals(mainClass.trim())){
					return;
				}
				
				ApplicationClassLoader applicationClassLoader1 = new ApplicationClassLoader(classLoader);
				Thread.currentThread().setContextClassLoader(applicationClassLoader1);
				
				System.setProperties(new ThreadLocalProperties(System.getProperties()));
				System.setProperty("CONFIG", "bbb");
				applicationClassLoader1.addJarFiles(new String[]{jarFileName2});
				 
					Class clazz = null;
					try {
						clazz = applicationClassLoader1.loadClass(mainClass);
						Method method = clazz.getMethod("main", String[].class);  
						method.invoke(null, new String[1]);
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
		});
		
		
		//构造一个线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(thread1);
        threadPool.execute(thread2);
        
	}

}