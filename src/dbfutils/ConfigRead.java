package dbfutils;

import java.io.File;

public class ConfigRead {
	public static File readConfig(String name){
		File filePath = null;
		
		filePath = new File(ConfigRead.class.getResource("/" + name).getFile()); 
		
		return filePath;
	}
	
}
