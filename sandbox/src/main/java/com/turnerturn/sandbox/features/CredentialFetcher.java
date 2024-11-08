package com.turnerturn.sandbox.features;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

public interface CredentialFetcher {
 
    public String fetchDatasourceUsername();
    public String fetchDatasourcePassword();
}

//We can use the @Order annotation to specify the order in which the beans should be loaded in our application context.  
//Cyberark fetcher takes precendence and we will always fetch with cyberark when it is enabled.
//@see https://www.baeldung.com/spring-order
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ConditionalOnProperty(prefix = "features.CyberarkCredentialFetcher", name = "enabled", havingValue = "true", matchIfMissing = false)
class CyberarkCredentialFetcher implements CredentialFetcher {
    @Override
    public String fetchDatasourceUsername() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fetchDatasourceUsername'");
    }

    @Override
    public String fetchDatasourcePassword() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fetchDatasourcePassword'");
    }
}


//This should always be the last resort of fetching credentials.  We will fetch from dotenv when no other credential fetcher is enabled.
//This can only be used when it is also enabled
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
@DependsOn("dotenv")
@ConditionalOnProperty(prefix = "features.DotenvCredentialFetcher", name = "enabled", havingValue = "true", matchIfMissing = false)
class DotenvCredentialFetcher implements CredentialFetcher {

    private final com.turnerturn.sandbox.features.Dotenv dotenv;

    public DotenvCredentialFetcher(com.turnerturn.sandbox.features.Dotenv dotenv) {
        if(dotenv == null) {
            throw new IllegalArgumentException("Dotenv must be enabled to use DotenvCredentialFetcher. Please enable features.Dotenv.enabled=true");
        }
        this.dotenv = dotenv;
    }
    @Override
    public String fetchDatasourceUsername() {
        return dotenv.get("datasource.username");
    }

    @Override
    public String fetchDatasourcePassword() {
        return dotenv.get("datasource.password");
    }
}

