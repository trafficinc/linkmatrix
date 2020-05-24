package linkmatrx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class ConfigManager {
	
	private static ArrayList<String> ignorelist;
	private static Boolean showImages = false;
	
	ConfigManager() {}
	
	public static void load() {
		String configFile = "./config.properties";
		File tempFile = new File(configFile);
		boolean configFileExists = tempFile.exists();
		if (configFileExists) {
			getConfigs(configFile);
		} else {
			//System.out.println("There is no file!");
			File newConfigFile = new File(configFile);
			//Create the file
			try {
				if (newConfigFile.createNewFile())
				{
				    System.out.println("Config File is created!");
				} else {
				    //System.out.println("Config File already exists.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			//Write Content
			FileWriter writer;
			try {
				writer = new FileWriter(newConfigFile);
//				writer.write("#db.password=password\n");
//				writer.write("#db.url=localhost\n");
				writer.write("ignore.ext=pdf,jpg,gif,png\n");
				writer.write("ignore.image=false\n");
				writer.close();
				getConfigs(configFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private static void getConfigs(String configFile) {
		try (InputStream input = new FileInputStream(configFile)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
//            System.out.println(prop.getProperty("db.url"));
//            System.out.println(prop.getProperty("db.user"));
//            System.out.println(prop.getProperty("db.password"));
            //System.out.println(prop.getProperty("ignore.ext"));
            ConfigManager.loadExt(prop.getProperty("ignore.ext"));
            ConfigManager.loadExtshowImg(prop.getProperty("ignore.image"));
           // ConfigManager.loadExt(prop.getProperty("ignore.image"));
            //System.out.println(getIgnorelist());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	private static void loadExtshowImg(String property) {
		boolean showimg = Boolean.parseBoolean(property);
	    setshowImages(showimg);
    }

	private static void loadExt(String property) {
		
        String[] values = property.split(",");
        setIgnorelist(new ArrayList<String>(Arrays.asList(values)));
		
	}

	public static ArrayList<String> getIgnorelist() {
		return ignorelist;
	}

	public static void setIgnorelist(ArrayList<String> ignorelist) {
		ConfigManager.ignorelist = ignorelist;
	}

	public static void setshowImages(Boolean property) {
		ConfigManager.showImages = property;
	}
	
	public static Boolean getshowImages() {
		return ConfigManager.showImages;
	}
	
}
