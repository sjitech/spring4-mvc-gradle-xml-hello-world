package jp.sji.bldemo.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:/core.properties")
public class CoreConfig {

    @Autowired
    Environment env;

    public String getUploadSavePath() {
        return env.getProperty("upload_save_path");
    }

    public String getStringValue(String key, String defaultValue) {
    	return env.getProperty(key, defaultValue);
    }

    public String getStringValue(String key) {
    	return env.getProperty(key);
    }

    public int getIntegerValue(String key, int defaultValue) {
    	String str = env.getProperty(key);

    	if (str == null || "".equals(str.trim())) {
    		if (defaultValue != Integer.MIN_VALUE) {
    			return defaultValue;
    		}

    		return Integer.MIN_VALUE;
    	}

    	return Integer.parseInt(str);
    }

    public int getIntegerValue(String key) {
    	return getIntegerValue(key, Integer.MIN_VALUE);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
    	String str = env.getProperty(key);

    	if (str == null || "".equals(str.trim())) {
    		return defaultValue;
    	}

    	if ("true".equals(str) || "on".equals(str) || "yes".equals(str)) {
    		return true;
	    } else {
	    	return false;
	    }
    }

    public boolean getBooleanValue(String key) {
    	return getBooleanValue(key, false);
    }

}
