package jp.sji.bldemo.core.repository.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.storage.Storage;

import jp.sji.bldemo.core.config.CoreConfig;

@Component
public class StorageClient {

    private final Logger log = LoggerFactory.getLogger(StorageClient.class);

    @Autowired
    protected CoreConfig coreConfig;

    public Storage init() {
    	log.info("Connect to google storage bucket: ");

    	return null;
    }

}
