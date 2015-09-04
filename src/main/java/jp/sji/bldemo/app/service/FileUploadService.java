package jp.sji.bldemo.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.sji.bldemo.core.config.CoreConfig;
import jp.sji.bldemo.core.repository.google.StorageClient;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    protected CoreConfig coreConfig;

    @Autowired
    protected StorageClient storageClient;

    public void saveUploadedFileToCloud(String localFilePath) {

    	String bucketName = coreConfig.getStringValue("gcp_storage_bucket_name");

    	log.debug("access bucket: " + bucketName);

    	try {
        	storageClient.upload(localFilePath);

    		storageClient.list(bucketName);

    	} catch(Exception e) {
    		log.error("failed to upload file " + localFilePath + " to cloud storage.", e);
    	}
    }

}
