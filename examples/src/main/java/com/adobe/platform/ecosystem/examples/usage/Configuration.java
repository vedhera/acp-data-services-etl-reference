package com.adobe.platform.ecosystem.examples.usage;

import com.adobe.platform.ecosystem.examples.constants.SDKConstants;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private String imsEndpoint;

    private String dataAccessEndpoint;

    private String catalogEndpoint;

    private String imsOrg;

    private String clientId;

    private String clientSecretKey;

    private String privateKeyPath;

    private String technicalAccountId;

    private String dataSetId;

    private int limit;

    private String outputFilePath;

    public String getImsEndpoint() {
        return imsEndpoint;
    }

    public Configuration setImsEndpoint(String imsEndpoint) {
        this.imsEndpoint = imsEndpoint;
        return this;
    }

    public String getDataAccessEndpoint() {
        return dataAccessEndpoint;
    }

    public Configuration setDataAccessEndpoint(String dataAccessEndpoint) {
        this.dataAccessEndpoint = dataAccessEndpoint;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public Configuration setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecretKey() {
        return clientSecretKey;
    }

    public Configuration setClientSecretKey(String clientSecretKey) {
        this.clientSecretKey = clientSecretKey;
        return this;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public Configuration setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Configuration setLimit(int limit) {
        this.limit = limit > 500 ? 500 : limit;
        return this;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public Configuration setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        return this;
    }

    public String getImsOrg() {
        return imsOrg;
    }

    public void setImsOrg(String imsOrg) {
        this.imsOrg = imsOrg;
    }

    public String getTechnicalAccountId() {
        return technicalAccountId;
    }

    public void setTechnicalAccountId(String technicalAccountId) {
        this.technicalAccountId = technicalAccountId;
    }

    public String getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(String dataSetId) {
        this.dataSetId = dataSetId;
    }

    public String getCatalogEndpoint() {
        return catalogEndpoint;
    }

    public Configuration setCatalogEndpoint(String catalogEndpoint) {
        this.catalogEndpoint = catalogEndpoint;
        return this;
    }

    public Map<String, String> toMap() {
        Map<String, String> connectionAttributes = new HashMap<>();
        connectionAttributes.put("imsEndpoint", imsEndpoint);
        connectionAttributes.put(SDKConstants.CREDENTIAL_IMS_ORG_KEY, imsOrg);
        connectionAttributes.put(SDKConstants.CREDENTIAL_CLIENT_KEY, clientId);
        connectionAttributes.put(SDKConstants.CREDENTIAL_SECRET_KEY, clientSecretKey);
        connectionAttributes.put(SDKConstants.CREDENTIAL_PRIVATE_KEY_PATH, privateKeyPath);
        connectionAttributes.put(SDKConstants.CREDENTIAL_TECHNICAL_ACCOUNT_KEY, technicalAccountId);
        connectionAttributes.put(SDKConstants.CREDENTIAL_META_SCOPE_KEY, "ent_dataservices_sdk");
        return connectionAttributes;
    }
}
