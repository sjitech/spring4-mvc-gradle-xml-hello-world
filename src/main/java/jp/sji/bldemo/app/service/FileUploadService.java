package jp.sji.bldemo.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.sji.bldemo.core.repository.google.StorageClient;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    protected StorageClient storageClient;

    public void saveUploadedFileToCloud(String localFilePath) {
    	try {
        	storageClient.upload(localFilePath);
    	} catch(Exception e) {
    		log.error("failed to upload file " + localFilePath + " to cloud storage.", e);
    	}
    }

}
