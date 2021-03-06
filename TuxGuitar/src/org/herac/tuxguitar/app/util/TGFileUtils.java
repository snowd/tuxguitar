package org.herac.tuxguitar.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLibraryLoader;
import org.herac.tuxguitar.util.TGVersion;

public class TGFileUtils {
	
	private static final String TG_CONFIG_PATH = "tuxguitar.config.path";
	private static final String TG_SHARE_PATH = "tuxguitar.share.path";
	private static final String TG_USER_SHARE_PATH = "tuxguitar.user-share.path";
	private static final String TG_CLASS_PATH = "tuxguitar.class.path";
	private static final String TG_LIBRARY_PATH = "tuxguitar.library.path";
	private static final String TG_LIBRARY_PREFIX = "tuxguitar.library.prefix";
	private static final String TG_LIBRARY_EXTENSION = "tuxguitar.library.extension";
	
	public static final String PATH_USER_CONFIG = getUserConfigDir();
	public static final String PATH_USER_PLUGINS_CONFIG = getUserPluginsConfigDir();
	public static final String PATH_USER_SHARE_PATH = getUserSharedPath();
	//writable
	public static final String[] TG_STATIC_SHARED_PATHS = getStaticSharedPaths();
	
	public static InputStream getResourceAsStream(TGContext context, String resource) {
		try {
			if(TG_STATIC_SHARED_PATHS != null){
				for( int i = 0; i < TG_STATIC_SHARED_PATHS.length ; i ++ ){
					File file = new File(TG_STATIC_SHARED_PATHS[i] + File.separator + resource);
					if( isExistentAndReadable( file ) ){
						return new FileInputStream( file );
					}
				}
			}
			return TGResourceManager.getInstance(context).getResourceAsStream(resource);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public static URL getResourceUrl(TGContext context, String resource) {
		try {
			if(TG_STATIC_SHARED_PATHS != null){
				for( int i = 0; i < TG_STATIC_SHARED_PATHS.length ; i ++ ){
					File file = new File(TG_STATIC_SHARED_PATHS[i] + File.separator + resource);
					if( isExistentAndReadable( file ) ){
						return file.toURI().toURL();
					}
				}
			}
			return TGResourceManager.getInstance(context).getResource(resource);
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public static Enumeration<URL> getResourceUrls(TGContext context, String resource) {
		try {
			Vector<URL> vector = new Vector<URL>();
			if(TG_STATIC_SHARED_PATHS != null){
				for( int i = 0; i < TG_STATIC_SHARED_PATHS.length ; i ++ ){
					File file = new File(TG_STATIC_SHARED_PATHS[i] + File.separator + resource);
					if( isExistentAndReadable( file ) ){
						vector.addElement( file.toURI().toURL() );
					}
				}
			}
			Enumeration<URL> resources = TGResourceManager.getInstance(context).getResources(resource);
			while( resources.hasMoreElements() ){
				URL url = (URL)resources.nextElement();
				if( !vector.contains(url) ){
					vector.addElement( url );
				}
			}
			return vector.elements();
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	private static String getResourcePath(TGContext context, String resource) {
		try {
			if(TG_STATIC_SHARED_PATHS != null){
				for( int i = 0; i < TG_STATIC_SHARED_PATHS.length ; i ++ ){
					File file = new File(TG_STATIC_SHARED_PATHS[i] + File.separator + resource);
					if( isExistentAndReadable( file ) ){
						return file.getAbsolutePath() + File.separator;
					}
				}
			}
			URL url = TGResourceManager.getInstance(context).getResource(resource);
			if(url != null){
				return getUrlPath(url);
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public static void loadClasspath(TGContext context){
		try {
			Enumeration<URL> plugins = getResourceUrls(context, "plugins");
			while( plugins.hasMoreElements() ){
				URL url = (URL)plugins.nextElement();
				TGClassLoader.getInstance(context).addPaths(new File(getUrlPath(url)));
			}
			
			String custompath = System.getProperty(TG_CLASS_PATH);
			if(custompath != null){
				String[] paths = custompath.split(File.pathSeparator);
				for(int i = 0; i < paths.length; i++){
					TGClassLoader.getInstance(context).addPaths(new File(paths[i]));
				}
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	public static void loadLibraries(TGContext context){
		String libraryPath = System.getProperty(TG_LIBRARY_PATH);
		if(libraryPath != null){
			String[] libraryPaths = libraryPath.split(File.pathSeparator);
			String libraryPrefix = System.getProperty(TG_LIBRARY_PREFIX);
			String libraryExtension = System.getProperty(TG_LIBRARY_EXTENSION);
			for(int i = 0; i < libraryPaths.length; i++){
				TGLibraryLoader.getInstance(context).loadLibraries(new File(libraryPaths[i]),libraryPrefix,libraryExtension);
			}
		}
	}
	
	public static String[] getFileNames(TGContext context, String resource ){
		try {
			String path = getResourcePath(context, resource);
			if( path != null ){
				File file = new File( path );
				if( isExistentAndReadable( file ) && isDirectoryAndReadable( file )){
					return file.list();
				}
			}
			InputStream stream = getResourceAsStream(context, resource + "/list.properties" );
			if( stream != null ){
				BufferedReader reader = new BufferedReader( new InputStreamReader(stream) );
				List<String> fileNameList = new ArrayList<String>();
				String fileName = null;
				while( (fileName = reader.readLine()) != null ){
					fileNameList.add( fileName );
				}
				String[] fileNames = new String[ fileNameList.size() ];
				for (int i = 0 ; i < fileNames.length ; i ++ ){
					fileNames[ i ] = (String)fileNameList.get( i );
				}
				return fileNames;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return null;
	}
	
	public static Image loadImage(TGContext context, String name){
		return loadImage(context, TuxGuitar.getInstance().getConfig().getStringValue(TGConfigKeys.SKIN),name);
	}
	
	public static Image loadImage(TGContext context, String skin,String name){
		try{
			InputStream stream = getResourceAsStream(context, "skins/" + skin + "/" + name);
			if(stream != null){			
				return new Image(TuxGuitar.getInstance().getDisplay(),new ImageData(stream));
			}
			System.err.println(name + ": not found");
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return new Image(TuxGuitar.getInstance().getDisplay(),16,16);
	}
	
	public static boolean isLocalFile(URL url){
		try {
			if( url.getProtocol().equals( new File(url.getFile()).toURI().toURL().getProtocol() ) ){
				return true;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return false;
	}
	
	public static String getDecodedFileName(URL url) {
		try {
			return URLDecoder.decode(new File(url.getFile()).getName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getDefaultUserAppDir(){
		return ((System.getProperty("user.home") + File.separator + ".tuxguitar-" + TGVersion.CURRENT.getVersion()));
	}
	
	private static String getUserConfigDir(){
		// Look for the system property
		String configPath = System.getProperty(TG_CONFIG_PATH);
		
		// Default System User Home
		if(configPath == null){
			configPath = (getDefaultUserAppDir() + File.separator + "config");
		}
		
		// Check if the path exists
		File file = new File(configPath);
		if(!isExistentAndReadable( file )){
			tryCreateDirectory( file );
		}
		return configPath;
	}
	
	private static String getUserPluginsConfigDir(){
		String configPluginsPath = (getUserConfigDir() + File.separator + "plugins");
		
		//Check if the path exists
		File file = new File(configPluginsPath);
		if(!isExistentAndReadable( file )){
			tryCreateDirectory( file );
		}
		
		return configPluginsPath;
	}
	
	private static String getUserSharedPath(){
		// Look for the system property
		String userSharePath = System.getProperty(TG_USER_SHARE_PATH);
		
		// Use configuration path as default.
		if( userSharePath == null){
			userSharePath = (getDefaultUserAppDir() + File.separator + "cache");
		}
		
		// Check if the path exists
		File file = new File(userSharePath);
		if(!isExistentAndReadable( file )){
			tryCreateDirectory( file );
		}
		return userSharePath;
	}
	
	private static String[] getStaticSharedPaths(){
		String staticSharedPaths = new String(PATH_USER_SHARE_PATH);
		String staticSharedPathsProperty = System.getProperty(TG_SHARE_PATH);
		if( staticSharedPathsProperty != null ){
			staticSharedPaths += (File.pathSeparator + staticSharedPathsProperty);
		}
		return staticSharedPaths.split(File.pathSeparator);
	}
	
	private static String getUrlPath( URL url ) throws UnsupportedEncodingException{
		return (new File(URLDecoder.decode(url.getPath(), "UTF-8")).getAbsolutePath() + File.separator);
	}
	
	private static boolean isExistentAndReadable( File file ){
		try{
			return file.exists();
		}catch(SecurityException se){
			return false;
		}
	}
	
	private static boolean isDirectoryAndReadable( File file ){
		try{
			return file.isDirectory();
		}catch(SecurityException se){
			return false;
		}
	}
	
	private static boolean tryCreateDirectory( File file ){
		try{
			return file.mkdirs();
		}catch(SecurityException se){
			return false;
		}
	}
}
