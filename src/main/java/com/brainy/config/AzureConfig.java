package com.brainy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.batch.BlobBatchClient;
import com.azure.storage.blob.batch.BlobBatchClientBuilder;

@Configuration
public class AzureConfig {

    @Value("${storage-account-name}")
    private String storageAccountName;

    private DefaultAzureCredential defaultAzureCredential;
    private BlobServiceClient blobServiceClient;

    @Bean
    DefaultAzureCredential defaultCredential() {
        defaultAzureCredential = new DefaultAzureCredentialBuilder().build();
        return defaultAzureCredential;
    }

    @Bean
    @DependsOn("defaultCredential")
    BlobServiceClient blobServiceClient() {
        String url = String.format("https://%s.blob.core.windows.net/", 
                storageAccountName);
        
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(url)
                .credential(defaultAzureCredential)
                .buildClient();

        return blobServiceClient;
    }

    @Bean
    @DependsOn("blobServiceClient")
    BlobBatchClient blobBatchClient() {
        return new BlobBatchClientBuilder(blobServiceClient).buildClient();
    }
}
