package com.example.azure.blob;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.azure.storage.common.StorageSharedKeyCredential;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Example of using Azure Blob Storage from Java, using Azure's Java SDK.
 *
 */
public enum Btp {
	;

	private static final String CONNECTION_STRING_ENV_KEY = "AZURE_STORAGE_CONNECTION_STRING";
	private static final String ACCOUNT_NAME_ENV_KEY = "AZURE_STORAGE_ACCOUNT_NAME";
	private static final String ACCOUNT_KEY_ENV_KEY = "AZURE_STORAGE_ACCOUNT_KEY";
	private static final String ACCOUNT_SAS_ENV_KEY = "AZURE_STORAGE_SAS";

	static final String PROG = Btp.class.getSimpleName();
	static String authMode = "connection-string";

	public static void main(String[] args) {
		StorageSharedKeyCredential credential = null;
		Consumer<BlobServiceClient> command = CommandLineParser.parseArgs(args);
		String endpoint = String.format(Locale.ROOT,
				"https://%s.blob.core.windows.net",
				System.getenv(ACCOUNT_NAME_ENV_KEY));		
		BlobServiceClientBuilder builder = new BlobServiceClientBuilder()
				.endpoint(endpoint);		
		authorizeBlobServiceClient(credential, builder);
		BlobServiceClient blobServiceClient = builder.buildClient();
		command.accept(blobServiceClient);
	}

	// Add authorization to the blob service client builder, depending on
	// choice of auth mode. 
	private static void authorizeBlobServiceClient(
			StorageSharedKeyCredential credential,
			BlobServiceClientBuilder builder) {
		if (authMode.equals("connection-string")) {
			credential = StorageSharedKeyCredential.fromConnectionString(
					System.getenv(CONNECTION_STRING_ENV_KEY));
		} else if (authMode.equals("account-key")) {
			credential = new StorageSharedKeyCredential(
					System.getenv(ACCOUNT_NAME_ENV_KEY),
					System.getenv(ACCOUNT_KEY_ENV_KEY));
		}
		if (authMode.equals("sas-token")) {
			builder.sasToken(System.getenv(ACCOUNT_SAS_ENV_KEY));
		} else if (authMode.equals("login")) {
			DefaultAzureCredential adCred = new DefaultAzureCredentialBuilder().build();	
			builder.credential(adCred);
		} else {
			builder.credential(credential);
		}
	}


	static BlobContainerClient createContainer(
			BlobServiceClient blobServiceClient, String containerName) {
		BlobContainerClient containerClient = blobServiceClient
				.getBlobContainerClient(containerName);
		containerClient.create();
		System.out.printf("Container %s created.%n", containerName);
		return containerClient;
	}

	static BlobContainerClient deleteContainer(
			BlobServiceClient blobServiceClient, String containerName) {
		BlobContainerClient containerClient = blobServiceClient
				.getBlobContainerClient(containerName);
		containerClient.delete();
		System.out.printf("Container %s deleted.%n", containerName);
		return containerClient;
	}

	static void uploadFile(BlobServiceClient blobServiceClient,
			String container, String file) {
		BlobContainerClient containerClient = blobServiceClient
				.getBlobContainerClient(container);
		BlobClient blobClient = containerClient.getBlobClient(file);
		blobClient.uploadFromFile(file);
	}

	static void downloadBlob(BlobServiceClient blobServiceClient,
			String container, String blob, String destination) {
		BlobContainerClient containerClient = blobServiceClient
				.getBlobContainerClient(container);
		BlobClient blobClient = containerClient.getBlobClient(blob);
		if (destination == null) {
			destination = blobClient.getBlobName();
		}
		BlobProperties props = blobClient.downloadToFile(destination, false);
		System.out.println(props);
	}

	static void listBlobs(BlobServiceClient blobServiceClient,
			String container) {
		BlobContainerClient containerClient = blobServiceClient
				.getBlobContainerClient(container);
		for (BlobItem blob : containerClient.listBlobs()) {
			System.out.printf("%s\t%s%n", blob.getName(), blob.getProperties());
		}
	}

}
