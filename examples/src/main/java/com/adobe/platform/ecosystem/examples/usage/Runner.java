package com.adobe.platform.ecosystem.examples.usage;

import com.adobe.platform.ecosystem.examples.catalog.api.CatalogService;
import com.adobe.platform.ecosystem.examples.catalog.impl.CatalogAPIStrategy;
import com.adobe.platform.ecosystem.examples.catalog.impl.CatalogServiceImpl;
import com.adobe.platform.ecosystem.examples.catalog.model.Batch;
import com.adobe.platform.ecosystem.examples.constants.SDKConstants;
import com.adobe.platform.ecosystem.examples.data.access.api.DataAccessService;
import com.adobe.platform.ecosystem.examples.data.access.impl.DataAccessServiceImpl;
import com.adobe.platform.ecosystem.examples.data.access.model.DataAccessFileEntity;
import com.adobe.platform.ecosystem.examples.data.access.model.DataSetFileProcessingEntity;
import com.adobe.platform.ecosystem.examples.util.ConnectorSDKException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runner {
    /**
     * Main method to demonstrate
     * Data Access API.
     */
    public static void main(String[] args) throws ConnectorSDKException {
        doMain(args);
    }

    private static void doMain(String[] args) throws ConnectorSDKException {
        // Parse command line args
        final Configuration configuration = ConfigHelper.getConfig(args);

        // Create Http Client.
        final HttpClient httpClient = HttpClientBuilder
            .create()
            .setMaxConnTotal(50)
            .build();

        // Retrieve Access Token.
        final String accessToken = TokenHelper.generateAccessToken(
            configuration.toMap(),
            TokenHelper.generateJWTToken(configuration.toMap()),
            configuration.getClientId(),
            configuration.getClientSecretKey(),
            httpClient
        );

        // Retrieve batches
        final CatalogService catalogService = new CatalogServiceImpl(configuration.getCatalogEndpoint(), httpClient);
        final List<Batch> batchList = catalogService.getBatches(
            configuration.getImsOrg(),
            accessToken,
            getQueryMapForBatchApi(configuration),
            CatalogAPIStrategy.ONCE
        );

        // Download file
        final DataAccessService dataAccessService = new DataAccessServiceImpl(configuration.getDataAccessEndpoint(), httpClient);
        try {
            for (Batch batch : batchList) {
                // Each batch has multiple DataSetFiles registered under it.
                // One DSF per partition.
                final List<DataAccessFileEntity> dataSetFilesInBatch = dataAccessService.getDataSetFilesFromBatchId(
                    configuration.getImsOrg(),
                    accessToken,
                    batch.getId()
                );

                for (DataAccessFileEntity file : dataSetFilesInBatch) {
                    // Each DataSetFile can have multiple parts depending upon
                    // Size of each file.
                    List<DataSetFileProcessingEntity> partsOfFile = dataAccessService.getDataSetFileEntries(
                        configuration.getImsOrg(),
                        accessToken,
                        file.getDataSetFileId()
                    );

                    for (DataSetFileProcessingEntity part : partsOfFile) {
                        // Consumption of individual file part.
                        consume(accessToken, configuration, httpClient, part);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error while downloading file: " + e);
            System.exit(1);
        }

        System.out.println("Exiting Successfully!!");
    }

    private static void consume(String accessToken, Configuration configuration, HttpClient httpClient, DataSetFileProcessingEntity part) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(part.getHref());
        HttpGet request = new HttpGet(builder.build());

        // We can optionally set the byte range
        // after inspecting the length from `part` entity.
        //request.setHeader("Range", "bytes=0-100");

        request.setHeader("Authorization", "Bearer " + accessToken);
        request.setHeader(SDKConstants.CONNECTION_HEADER_IMS_ORG_KEY, configuration.getImsOrg());
        request.setHeader(SDKConstants.CONNECTION_HEADER_X_API_KEY, configuration.getClientId());

        HttpResponse response = httpClient.execute(request);
        createFile(
            configuration.getOutputFilePath(),
            part.getName(),
            response.getEntity().getContent()
        );
    }

    private static void createFile(String outputFilePath, String filePartName, InputStream content) {
        try {
            File targetFile = new File(outputFilePath + "/" + filePartName);

            java.nio.file.Files.copy(
                content,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

            IOUtils.closeQuietly(content);

        } catch (IOException e) {
            System.out.println("Exception Occurred: " + e);
            System.exit(1);
        }
    }

    private static Map<String, String> getQueryMapForBatchApi(Configuration configuration) {
        Map<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(configuration.getLimit()));
        params.put("dataSet", configuration.getDataSetId());
        params.put("sort", "desc:created");
        params.put("status", "success");

        // Optionally we can set offset.
        //params.put("offset", "500");
        
        return params;
    }
}
