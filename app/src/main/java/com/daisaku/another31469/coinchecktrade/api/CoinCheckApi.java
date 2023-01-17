package com.daisaku.another31469.coinchecktrade.api;

import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class CoinCheckApi {
    private final String apiKey;
    private final String apiSecret;

    public CoinCheckApi(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getTicker() throws JSONException {
        String url = "https://coincheck.com/api/accounts/ticker";
        String jsonString = requestByUrlWithHeader(url, createHeader(url));
        JSONObject object = new JSONObject(jsonString);
        return object.getString("last");
    }

    public String orderBuyOrSell() throws JSONException {
        String url = "https://coincheck.com/api/exchange/orders";
        String jsonString = responseByUrlWithHeader(url, createHeader(url));
        JSONObject object = new JSONObject(jsonString);
        return object.getString("order_type");
    }

    private Map<String, String> createHeader(String url) {
        Map<String, String> map = new HashMap<>();
        String nonce = createNonce();
        map.put("ACCESS-KEY", apiKey);
        map.put("ACCESS-NONCE", nonce);
        map.put("ACCESS-SIGNATURE", createSignature(apiSecret, url, nonce));
        return map;
    }

    private String createSignature(String apiSecret, String url, String nonce) {
        String message = nonce + url;
        return HMAC_SHA256Encode(apiSecret, message);
    }

    private String createNonce() {
        long currentUnixTime = System.currentTimeMillis() / 1000L;
        return String.valueOf(currentUnixTime);
    }

    private String requestByUrlWithHeader(String url, final Map<String, String> headers){
        ApacheHttpTransport transport = new ApacheHttpTransport();
        HttpRequestFactory factory = transport.createRequestFactory(request -> {
            request.setConnectTimeout(0);
            request.setReadTimeout(0);
            request.setParser(new JacksonFactory().createJsonObjectParser());
            final HttpHeaders httpHeaders = new HttpHeaders();
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpHeaders.set(e.getKey(), e.getValue());
            }
            request.setHeaders(httpHeaders);
        });
        String jsonString;
        try {
            HttpRequest request = factory.buildGetRequest(new GenericUrl(url));
            HttpResponse response = request.execute();
            jsonString = response.parseAsString();
        } catch (IOException e) {
            e.printStackTrace();
            jsonString = null;
        }
        return jsonString;
    }

    private String responseByUrlWithHeader(String url, final Map<String, String> headers) {
        ApacheHttpTransport transport = new ApacheHttpTransport();
        HttpRequestFactory factory = transport.createRequestFactory(response -> {
            response.setConnectTimeout(0);
            response.setReadTimeout(0);
            response.setParser(new JacksonFactory().createJsonObjectParser());
            final HttpHeaders httpHeaders = new HttpHeaders();
            for (Map.Entry<String, String> e : headers.entrySet()) {
                httpHeaders.set(e.getKey(), e.getValue());
            }
            response.setHeaders(httpHeaders);
        });
        String jsonString;
        try {
            HttpRequest request = factory.buildPostRequest(new GenericUrl(url), new UrlEncodedContent(headers));
            HttpResponse response = request.execute();
            jsonString = response.parseAsString();
        } catch (IOException e) {
            e.printStackTrace();
            jsonString = null;
        }
        return jsonString;
    }


    public static String HMAC_SHA256Encode(String secretKey, String message) {

        SecretKeySpec keySpec = new SecretKeySpec(
                secretKey.getBytes(),
                "hmacSHA256");

        Mac mac = null;
        try {
            mac = Mac.getInstance("hmacSHA256");
            mac.init(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // can't recover
            throw new RuntimeException(e);
        }
        byte[] rawHmac = mac.doFinal(message.getBytes());
        return Hex.encodeHexString(rawHmac);
    }
}
