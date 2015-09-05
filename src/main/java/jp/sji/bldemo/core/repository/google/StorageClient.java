package jp.sji.bldemo.core.repository.google;

import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.Storage.Objects.Insert;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;

import jp.sji.bldemo.core.config.CoreConfig;

/**
 * refer to
 * https://cloud.google.com/storage/docs/json_api/v1/json-api-java-samples
 *
 * @author wang
 *
 */
@Component
public class StorageClient {

    private final static Logger log = LoggerFactory.getLogger(StorageClient.class);

    @Autowired
    protected CoreConfig coreConfig;

    @Autowired
    protected Authroization authroization;

    public void list(String bucketName) throws Exception {
        log.info("show content of bucket: " + bucketName);

        // Set up global Storage instance.
        Storage client = authroization.getStorageClient();


        // Get metadata about the specified bucket.
        Storage.Buckets.Get getBucket = client.buckets().get(bucketName);
        getBucket.setProjection("full");
        Bucket bucket = getBucket.execute();

        log.debug("name: " + bucketName);
        log.debug("location: " + bucket.getLocation());
        log.debug("timeCreated: " + bucket.getTimeCreated());
        log.debug("owner: " + bucket.getOwner());


        // List the contents of the bucket.
        Storage.Objects.List listObjects = client.objects().list(bucketName);
        Objects objects;

        do {
            objects = listObjects.execute();
            List<StorageObject> items = objects.getItems();

            if (null == items) {
                log.debug("There were no objects in the given bucket; try adding some and re-running.");
                break;
            }

            for (StorageObject object : items) {
                log.debug(object.getName() + " (" + object.getSize() + " bytes)");
            }

            listObjects.setPageToken(objects.getNextPageToken());
        } while (null != objects.getNextPageToken());
    }

    public StorageObject upload(String localFilePath) throws Exception {
        String bucketName = coreConfig.getStringValue("gcp_storage_bucket_name");

        log.info("Upload file " + localFilePath + " to bucket: " + bucketName);

        // Set up global Storage instance.
        Storage client = authroization.getStorageClient();

        final String contentType = "application/octet-stream";

        InputStreamContent mediaContent = new InputStreamContent(contentType, new FileInputStream(localFilePath));

        Path path = FileSystems.getDefault().getPath(localFilePath);
        Insert insertObject = client.objects().insert(bucketName, null, mediaContent)
                .setName(path.getFileName().toString());
        insertObject.getMediaHttpUploader().setDisableGZipContent(true);

        return insertObject.execute();
    }

}
