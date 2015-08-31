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


}
