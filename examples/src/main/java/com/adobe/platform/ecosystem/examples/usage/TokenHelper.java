package com.adobe.platform.ecosystem.examples.usage;

import com.adobe.platform.ecosystem.examples.constants.SDKConstants;
import com.adobe.platform.ecosystem.examples.util.ConnectorSDKException;
import com.adobe.platform.ecosystem.examples.util.HttpClientUtil;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.RS256;
import static java.lang.Boolean.TRUE;

public class TokenHelper {
    private static final String HTTP_CLIENT_ID_KEY = "client_id";
    private static final String HTTP_CLIENT_SECRET_KEY = "client_secret";
    private static final String HTTP_JWT_TOKEN_KEY = "jwt_token";
    private static final Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

    public static String generateJWTToken(Map<String, String> credentials) throws ConnectorSDKException {

        // Metascopes associated to key
        String metascopes[] = new String[]{credentials.get(SDKConstants.CREDENTIAL_META_SCOPE_KEY)};
        String imsEndPoint = credentials.get("imsEndpoint");

        String filePath = credentials.get(SDKConstants.CREDENTIAL_PRIVATE_KEY_PATH);
        File file = new File(filePath);
        if (file.exists()) {
            try {
                Path path = Paths.get(filePath);
                long size = Files.size(path);
                //Files.readAllBytes throws out of memory exception if the file size exceeds 2GB,
                //We want to ensure that size of private file does not exceeds the expected 1MB.
                if (size > 1024 * 1024) {
                    throw new ConnectorSDKException("Size of private file is greater than 1 MB, file path : " + filePath);
                }
                // Secret key as byte array. Secret key file should be in DER encoded format.

                byte[] privateKeyFileContent = Files.readAllBytes(path);

                // Create the private key
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                KeySpec ks = new PKCS8EncodedKeySpec(privateKeyFileContent);
                RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(ks);
                long jwtExpiryDuration = Long.parseLong("259200");

                // Create JWT payload
                Map jwtClaims = new HashMap<>();
                jwtClaims.put("iss", credentials.get(SDKConstants.CREDENTIAL_IMS_ORG_KEY));
                jwtClaims.put("sub", credentials.get(SDKConstants.CREDENTIAL_TECHNICAL_ACCOUNT_KEY));
                jwtClaims.put("exp", (new Date().getTime() / 1000) + jwtExpiryDuration);
                jwtClaims.put("aud", imsEndPoint + "/c/" + credentials.get(SDKConstants.CREDENTIAL_CLIENT_KEY));
                for (String metascope : metascopes) {
                    jwtClaims.put(imsEndPoint + "/s/" + metascope, TRUE);
                }

                // Create the final JWT token
                String jwtToken = Jwts.builder().setClaims(jwtClaims).signWith(RS256, privateKey).compact();
                return jwtToken;
            } catch (Exception ex) {
                if (ex instanceof ConnectorSDKException) {
                    throw (ConnectorSDKException) ex;
                }
                throw new ConnectorSDKException(ex.getMessage(), ex);
            }
        } else {
            throw new ConnectorSDKException("File does not exist at location : " + filePath);
        }
    }

    public static String generateAccessToken(Map<String, String> credentials,
                                             String jwtToken,
                                             String clientId,
                                             String secretKey,
                                             HttpClient httpClient) throws ConnectorSDKException {
        String imsEndPoint = credentials.get("imsEndpoint");
        HttpClientUtil httpClientUtil = new HttpClientUtil(httpClient);

        if (StringUtils.isEmpty(jwtToken) || StringUtils.isEmpty(clientId) || StringUtils.isEmpty(secretKey)) {
            throw new ConnectorSDKException("Token, clientId and secret are mandatory, when initializing for JWT");
        }
        try {
            logger.info("Generating access token");
            URIBuilder builder = new URIBuilder(imsEndPoint);
            builder.setPath(SDKConstants.JWT_EXCHANGE_IMS_URI);
            HttpPost request = new HttpPost(builder.build());
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair(HTTP_CLIENT_ID_KEY, clientId));
            nvps.add(new BasicNameValuePair(HTTP_CLIENT_SECRET_KEY, secretKey));
            nvps.add(new BasicNameValuePair(HTTP_JWT_TOKEN_KEY, jwtToken));

            request.setEntity(new UrlEncodedFormEntity(nvps));
            String response = httpClientUtil.execute(request);
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            return (String) jsonResponse.get("access_token");

        } catch (Exception e) {
            logger.error("Error in generating access token :" + e.getMessage());
            throw new ConnectorSDKException(e.getMessage(), e.getCause());
        }
    }
}
