package com.turnerturn.sandbox.features;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * RestartApplicationOnPasswordRotation is a feature that restarts the application when a password rotation is detected.  Password is encoded with random uuid value and cached into local class variable.  This class will schedule a task to periodically compare our cached encoded value with encoded value of our latest fetched credential password.
 * If the encoded values are different, this component will use spring's actuator library and spring-cloud-starter library to restart the application.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "features.RestartApplicationOnDatasourcePasswordRotation", name = "enabled", havingValue = "true", matchIfMissing = false)
public class RestartApplicationOnDatasourcePasswordRotation {

    private final List<CredentialFetcher> credentialFetchers;
	private final RestartEndpoint restartEndpoint;
	private String salt= UUID.randomUUID().toString(); 
	private String cachedEncodedPassword;


    public RestartApplicationOnDatasourcePasswordRotation(final List<CredentialFetcher> credentialFetchers,final RestartEndpoint restartEndpoint) {
        if(credentialFetchers == null || credentialFetchers.isEmpty()) {
            throw new IllegalArgumentException("Exactly one CredentialFetcher feature must be enabled. Please enable either CyberarkCredentialFetcher or DotenvCredentialFetcher. (features.CyberarkCredentialFetcher.enabled=true or features.DotenvCredentialFetcher.enabled=true)");
        }
        this.credentialFetchers = credentialFetchers;
        this.restartEndpoint = restartEndpoint;
    }
    
    @PostConstruct
    protected void init() {
        //always fetch with fetcher of the highest ordering precedence. (i.e. credentialFetcher.get(0))
        cacheEncodedPassword(fetchDatasourcePassword());
    }
    protected void cacheEncodedPassword(String password) {
        this.cachedEncodedPassword = encode(password);
    }

    protected String fetchDatasourcePassword() {
        return credentialFetchers.get(0).fetchDatasourcePassword();
    }
    @Scheduled(fixedRate = 10000)
    protected void checkForPasswordRotation() {
        String currentEncodedPassword = encode(fetchDatasourcePassword());
        if (!currentEncodedPassword.equals(cachedEncodedPassword)) {
            log.info("Detected password rotation. Restarting application...");
            restart();
        }
    }
    protected String encode(String password) {
        try {
            // Combine password with secret salt
            String saltedPassword = password + salt;

            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add salted password bytes to digest
            md.update(saltedPassword.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest();

            // Convert it to a Base64 encoded string
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected void restart() {
        restartEndpoint.restart();
    }

}
