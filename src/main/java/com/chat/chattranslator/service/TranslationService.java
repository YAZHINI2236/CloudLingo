package com.chat.chattranslator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TranslationService {

    // 🔐 Inject from application.properties
    @Value("${azure.translation.key}")
    private String azureKey;

    @Value("${azure.translation.region}")
    private String azureRegion;

    @Value("${azure.translation.endpoint}")
    private String azureEndpoint;

    private final HttpClient httpClient;

    // Cache for performance
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public TranslationService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public String translate(String text, String sourceLang, String targetLang) {

        String cleanText = normalize(text);
        String cacheKey = sourceLang + "->" + targetLang + ":" + cleanText;

        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        try {
            String translated = callAzureTranslator(cleanText, sourceLang, targetLang);

            if (translated != null && !translated.isBlank()) {
                cache.put(cacheKey, translated);
                return translated;
            }

        } catch (Exception e) {
            System.out.println("Azure translation failed: " + e.getMessage());
        }

        return cleanText;
    }

    // ---------------- AZURE API CALL ----------------

    private String callAzureTranslator(String text, String source, String target) throws Exception {

        String url = azureEndpoint
                + "/translate?api-version=3.0"
                + "&from=" + source
                + "&to=" + target;

        String jsonBody = """
                [
                  { "Text": "%s" }
                ]
                """.formatted(escapeJson(text));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Ocp-Apim-Subscription-Key", azureKey)
                .header("Ocp-Apim-Subscription-Region", azureRegion)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Azure HTTP error: " + response.statusCode());
        }

        return extractTranslatedText(response.body());
    }

    // ---------------- HELPERS ----------------

    private String extractTranslatedText(String json) {
        String key = "\"text\":\"";
        int start = json.indexOf(key);
        if (start == -1) return null;

        start += key.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;

        return json.substring(start, end);
    }

    private String normalize(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("\\s+", " ");
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
