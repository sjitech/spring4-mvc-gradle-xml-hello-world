package jp.sji.bldemo.core.repository.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.java6.auth.oauth2.GooglePromptReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.Storage.Objects.Insert;
import com.google.api.services.storage.StorageScopes;
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

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final boolean AUTH_LOCAL_WEBSERVER = false;

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to
     * make it a single globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    private static Storage client;

    /** Authorizes the installed application to access user's protected data. */
    public Credential authorize() throws Exception {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = null;

        String clientSecretsFile = coreConfig.getStringValue("gcp_auth_data_dir") + "/"
                + coreConfig.getStringValue("gcp_client_secret_filename");

        log.debug("load google client secret file: " + clientSecretsFile);

        try {

            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(new FileInputStream(clientSecretsFile)));
            if (clientSecrets.getDetails().getClientId() == null
                    || clientSecrets.getDetails().getClientSecret() == null) {
                throw new Exception("client_secrets not well formed.");
            }
        } catch (Exception e) {
            log.error("error on loading client secret file: " + clientSecretsFile, e);
            throw e;
        }

        // Set up authorization code flow.
        // Ask for only the permissions you need. Asking for more permissions
        // will
        // reduce the number of users who finish the process for giving you
        // access
        // to their accounts. It will also increase the amount of effort you
        // will
        // have to spend explaining to users what you are doing with their data.
        // Here we are listing all of the available scopes. You should remove
        // scopes
        // that you are not actually using.
        Set<String> scopes = new HashSet<String>();
        scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
        scopes.add(StorageScopes.DEVSTORAGE_READ_ONLY);
        scopes.add(StorageScopes.DEVSTORAGE_READ_WRITE);

        dataStoreFactory = new FileDataStoreFactory(new File(coreConfig.getStringValue("gcp_auth_data_dir")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, scopes).setDataStoreFactory(dataStoreFactory).build();

        // Authorize.
        VerificationCodeReceiver receiver =
                AUTH_LOCAL_WEBSERVER ? new LocalServerReceiver() : new GooglePromptReceiver();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void list(String bucketName) throws Exception {
        log.info("show content of bucket: " + bucketName);

        dataStoreFactory = new FileDataStoreFactory(new File(coreConfig.getStringValue("gcp_auth_data_dir")));
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Authorization.
        Credential credential = authorize();

        String appName = coreConfig.getStringValue("gcp_project_name");

        // Set up global Storage instance.
        client = new Storage.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(appName).build();


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
        String appName = coreConfig.getStringValue("gcp_project_name");
        String bucketName = coreConfig.getStringValue("gcp_storage_bucket_name");

        log.info("Upload file " + localFilePath + " to bucket: " + bucketName);

        dataStoreFactory = new FileDataStoreFactory(new File(coreConfig.getStringValue("gcp_auth_data_dir")));
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Authorization.
        Credential credential = authorize();

        // Set up global Storage instance.
        client = new Storage.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(appName).build();

        final String contentType = "application/octet-stream";

        InputStreamContent mediaContent = new InputStreamContent(contentType, new FileInputStream(localFilePath));

        Path path = FileSystems.getDefault().getPath(localFilePath);
        Insert insertObject = client.objects().insert(bucketName, null, mediaContent)
                .setName(path.getFileName().toString());
        insertObject.getMediaHttpUploader().setDisableGZipContent(true);

        return insertObject.execute();
    }

}
