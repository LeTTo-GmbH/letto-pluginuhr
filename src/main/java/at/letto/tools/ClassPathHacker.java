package at.letto.tools;

import java.io.IOException;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.lang.reflect.Method;

public class ClassPathHacker {

@SuppressWarnings("rawtypes")
private static final Class[] parameters = new Class[]{URL.class};

	public static void addFile(String s) throws IOException {
	   File f = new File(s);
	   addFile(f);
	}//end method
	
	@SuppressWarnings("deprecation")
	public static void addFile(File f) throws IOException {
	   addURL(f.toURL());
	}//end method
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addURL(URL u) throws IOException {
	
	  URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	  Class sysclass = URLClassLoader.class;
	
	  try {
	     Method method = sysclass.getDeclaredMethod("addURL", parameters);
	     method.setAccessible(true);
	     method.invoke(sysloader, new Object[]{u});
	  } catch (Throwable t) {
	     throw new IOException("Error, could not add URL to system classloader");
	  }//end try catch
	
	}//end method

}//end class