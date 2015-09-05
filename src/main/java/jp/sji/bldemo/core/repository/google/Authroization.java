package jp.sji.bldemo.core.repository.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashSet;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.compute.ComputeCredential;
import com.google.api.client.googleapis.extensions.java6.auth.oauth2.GooglePromptReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;

import jp.sji.bldemo.core.config.CoreConfig;

@Component
public class Authroization {

    private final static Logger log = LoggerFactory.getLogger(Authroization.class);

    @Autowired
    protected CoreConfig coreConfig;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    private static Storage client;

    /** Authorizes the installed application to access user's protected data. */
    public Credential oauthAuthorize() throws Exception {
        final boolean authLocalWebserver = false;

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

        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(coreConfig.getStringValue("gcp_auth_data_dir")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, scopes).setDataStoreFactory(dataStoreFactory).build();

        // Authorize.
        VerificationCodeReceiver receiver =
                authLocalWebserver ? new LocalServerReceiver() : new GooglePromptReceiver();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Credential getCredential(Set<String> scopes) throws GeneralSecurityException, IOException {

        String p12KeyFile = coreConfig.getStringValue("gcp_auth_data_dir") + "/"
                + coreConfig.getStringValue("gcp_auth_p12_file");

        String emailAddress = coreConfig.getStringValue("gcp_auth_service_account_email");

        log.info("init google service api auth with p12 file: " + p12KeyFile);
        log.info("init google service api auth with email: " + emailAddress);


        if (httpTransport == null) {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        }

        Credential credential = null;

        if (coreConfig.isDevEnv()) {
            credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(emailAddress)
                .setServiceAccountPrivateKeyFromP12File(new File(p12KeyFile))
                .setServiceAccountScopes(scopes)
                .build();

        } else {
            // running on Google Compute Engine
            log.debug("using simple credential when running on GCE");
            credential = new ComputeCredential.Builder(httpTransport, JSON_FACTORY).build();

        }

        return credential;
    }

    public Storage getStorageClient() throws GeneralSecurityException, IOException {
        Set<String> scopes = new HashSet<String>();
        scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
        scopes.add(StorageScopes.DEVSTORAGE_READ_ONLY);
        scopes.add(StorageScopes.DEVSTORAGE_READ_WRITE);

        // Authorization.
        Credential credential = getCredential(scopes);
        String appName = coreConfig.getStringValue("gcp_oauth_app_name");

        if (client == null) {
            client = new Storage.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(appName).build();
        }

        return client;
    }

}
