package com.oxaira.airq.machinelearning.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;

@Slf4j
@Component
public class PythonMLClient {

    private final RestTemplate restTemplate;

    @Value("${ml.api.url:https://airq-ml.onrender.com/predict}")
    private String mlApiUrl;

    public PythonMLClient() {
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
            };
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            javax.net.ssl.HostnameVerifier allHostsValid = (hostname, session) -> true;
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            
            // Adicionalmente creamos una request factory para RestTemplate
            org.springframework.http.client.SimpleClientHttpRequestFactory requestFactory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            this.restTemplate = new RestTemplate(requestFactory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PredictionResponse getPrediction(String sensorId, List<MeasurementData> recentData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PredictionRequest> request = new HttpEntity<>(new PredictionRequest(sensorId, recentData), headers);
            
            ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(
                    mlApiUrl,
                    request,
                    PredictionResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.warn("Error calling python ML service at {}: {}", mlApiUrl, e.getMessage());
            // Fallback for simulation/testing if python server is offline
            double avgCo2 = recentData.stream().mapToDouble(MeasurementData::co2).average().orElse(0.0);
            String risk = avgCo2 > 1000 ? "CRITICAL" : "LOW";
            String action = avgCo2 > 1000 ? "Sistema de ventilación activado al 70%" : "Ninguna";
            return new PredictionResponse(risk, action);
        }
    }

    public record MeasurementData(Double co2, Double pm25, Double temp, Double hum) {}
    public record PredictionRequest(String sensorId, List<MeasurementData> data) {}
    public record PredictionResponse(String riskLevel, String aiActionTaken) {}
}
