
# Release Testing Assertions

### Feature components can be enabled via application properties.  
**Testing Result:**. PASS or FAIL

```text
GIVEN 
   spring application is started
WHEN  
   RestartApplicationOnDatasourcePasswordRotation.enabled=true
THEN 
    RestartApplicationOnDatasourcePasswordRotation is enabled
    Logs indicate the component was included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   Dotenv.enabled=true
THEN 
    Dotenv is enabled
    Logs indicate the component was included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   DotenvCredentialFetcher.enabled=true
THEN 
    DotenvCredentialFetcher is enabled
    Logs indicate the component was included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   CyberarkCredentialFetcher.enabled=true
THEN 
    CyberarkCredentialFetcher is enabled
    Logs indicate the component was included in the scope of our Spring application.
```

### Feature components can be explicitly disabled via application properties.
**Testing Result:**. PASS or FAIL
```text
GIVEN
   spring application is started
WHEN
   RestartApplicationOnDatasourcePasswordRotation.enabled=false
THEN 
    RestartApplicationOnDatasourcePasswordRotation is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   Dotenv.enabled=false
THEN 
    Dotenv is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   DotenvCredentialFetcher.enabled=false
THEN 
    DotenvCredentialFetcher is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   CyberarkCredentialFetcher.enabled=false
THEN 
    CyberarkCredentialFetcher is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.
```

### Feature components are implicitly disabled when feature is not specified in the application properties.
**Testing Result:**. PASS or FAIL
```text
GIVEN 
   spring application is started
WHEN  
   environment properties do not contain property for 'features.RestartApplicationOnDatasourcePasswordRotation.enabled'
THEN 
    RestartApplicationOnDatasourcePasswordRotation is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   environment properties do not contain property for 'features.Dotenv.enabled'
THEN 
    Dotenv is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.
```

```text
GIVEN 
   spring application is started
WHEN  
   environment properties do not contain property for 'features.DotenvCredentialFetcher.enabled'
THEN 
    DotenvCredentialFetcher is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.

NOTE: Application will fail to start when DotenvCredentialFetcher is enabled and Dotenv feature is disabled. Logs should still indicate if application failed to start because of this scenario. If you find this scenario in the logs then this indicates that DotenvCredentialFetcher was enabled appropriately. You can also enable both features and confirm a positive test.
```

```text
GIVEN 
   spring application is started
WHEN  
   environment properties do not contain property for 'features.CyberarkCredentialFetcher.enabled'
THEN 
    CyberarkCredentialFetcher is disabled
    Logs indicate the component was NOT included in the scope of our Spring application.
```

### Application fails to start when we don't have at least one credential fetcher feature enabled.
**Testing Result:**. PASS or FAIL
### Application fails to start when DotenvCredentialFetcher feature is enabled and Dotenv feature is disabled.
**Testing Result:**. PASS or FAIL
### CyberarkCredentialFetcher is used as our primary fetching implementation when more than one credential fetcher is enabled.
**Testing Result:**. PASS or FAIL
### Logs indicate the application is restarted when RestartApplicationOnDatasourcePasswordRotation is enabled and CyberarkCredentialFetcher is disabled and DotenvCredentialFetcher is enabled, and the value for 'datasource.password' is changed in the .env file while application is started.
**Testing Result:**. PASS or FAIL
### Logs indicate the application is restarted when RestartApplicationOnDatasourcePasswordRotation is enabled, CyberarkCredentialFetcher is enabled, and the Cyberark vault password is changed while the application is running.
**Testing Result:**. PASS or FAIL