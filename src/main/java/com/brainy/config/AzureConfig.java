package com.brainy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.batch.BlobBatchClient;
import com.azure.storage.blob.batch.BlobBatchClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

@Configuration
public class AzureConfig {

	@Value("${storage-account-name}")
	private String storageAccountName;

	@Value("${storage-account-key}")
	private String storageAccountKey;

	private BlobServiceClient blobServiceClient;
	private StorageSharedKeyCredential storageSharedKeyCredential;

	@Bean
	StorageSharedKeyCredential defaultStorageSharedKeyCredential() {
		storageSharedKeyCredential =
				new StorageSharedKeyCredential(storageAccountName, storageAccountKey);
		return storageSharedKeyCredential;
	}

	@Bean
	@DependsOn("defaultStorageSharedKeyCredential")
	BlobServiceClient blobServiceClient() {
		String url = String.format("https://%s.blob.core.windows.net/", storageAccountName);

		blobServiceClient = new BlobServiceClientBuilder().endpoint(url)
				.credential(storageSharedKeyCredential).buildClient();

		return blobServiceClient;
	}

	@Bean
	@DependsOn("blobServiceClient")
	BlobBatchClient blobBatchClient() {
		return new BlobBatchClientBuilder(blobServiceClient).buildClient();
	}
}
