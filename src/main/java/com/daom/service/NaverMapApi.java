package com.daom.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class NaverMapApi {


    @Value("${api.maps.id}")
    private String NAVER_API_KEY_ID;

    @Value("${api.maps.key}")
    private String NAVER_API_KEY;

    public double[] findShopXYApi(String locDesc) throws IOException {

        HttpURLConnection conn = null;
        ObjectMapper mapper = new ObjectMapper();
        String responseData = "";

        double[] coordinateArr = new double[2];

        String addr = URLEncoder.encode(locDesc, StandardCharsets.UTF_8);
        // 네이버 Geocode API https://api.ncloud-docs.com/docs/ai-naver-mapsgeocoding-geocode
        URL url = new URL("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr);
        conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_API_KEY_ID);
        conn.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_API_KEY);

        // 전달
        int responseCode = conn.getResponseCode();

        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            responseData = sb.toString();
            JsonNode node = mapper.readTree(responseData);

            int totalCount = node.get("meta").get("totalCount").asInt();
            System.out.println(totalCount);
            if (totalCount >= 1) {
                coordinateArr[0] = node.get("addresses").get(0).get("x").asDouble();
                coordinateArr[1] = node.get("addresses").get(0).get("y").asDouble();
            }

            conn.disconnect();
            br.close();
        }


        return coordinateArr;


    }

}
