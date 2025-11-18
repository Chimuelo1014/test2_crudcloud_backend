package com.crudzaso.crudcloud_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiscordNotificationService {

    private static final Logger log = LoggerFactory.getLogger(DiscordNotificationService.class);

    @Value("${discord.webhook.url:}")
    private String webhookUrl;

    @Value("${discord.notifications.enabled:false}")
    private boolean notificationsEnabled;

    private final RestTemplate restTemplate;

    public DiscordNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envía una notificación a Discord cuando un usuario se registra.
     */
    public void sendUserRegistrationNotification(String email, String fullName, String role) {
        if (!notificationsEnabled || webhookUrl == null || webhookUrl.isBlank()) {
            log.debug("Discord notifications are disabled or webhook URL not configured");
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            
            Map<String, Object> embed = new HashMap<>();
            embed.put("title", "🎉 Nuevo Usuario Registrado");
            embed.put("color", 3066993); // Verde
            embed.put("timestamp", LocalDateTime.now().toString());
            
            Map<String, Object>[] fields = new Map[]{
                createField("👤 Nombre", fullName, true),
                createField("📧 Email", email, true),
                createField("🔑 Rol", role, true),
                createField("⏰ Fecha", timestamp, false)
            };
            embed.put("fields", fields);
            
            Map<String, Object> footer = new HashMap<>();
            footer.put("text", "CrudCloud Backend");
            embed.put("footer", footer);

            Map<String, Object> payload = new HashMap<>();
            payload.put("embeds", List.of(embed));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
            
            log.info("Discord notification sent for new user: {}", email);
        } catch (Exception e) {
            log.error("Failed to send Discord notification: {}", e.getMessage());
            // No lanzamos la excepción para no romper el flujo de registro
        }
    }

    /**
     * Envía una notificación de pago aprobado.
     */
    public void sendPaymentApprovedNotification(String userEmail, String planName, String amount) {
        if (!notificationsEnabled || webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            
            Map<String, Object> embed = new HashMap<>();
            embed.put("title", "💳 Pago Aprobado");
            embed.put("color", 5763719); // Azul
            embed.put("timestamp", LocalDateTime.now().toString());
            
            Map<String, Object>[] fields = new Map[]{
                createField("👤 Usuario", userEmail, true),
                createField("📦 Plan", planName, true),
                createField("💰 Monto", "$" + amount, true),
                createField("⏰ Fecha", timestamp, false)
            };
            embed.put("fields", fields);
            
            Map<String, Object> footer = new HashMap<>();
            footer.put("text", "CrudCloud Payments");
            embed.put("footer", footer);

            Map<String, Object> payload = new HashMap<>();
            payload.put("embeds", List.of(embed));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
            
            log.info("Discord payment notification sent for user: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send Discord payment notification: {}", e.getMessage());
        }
    }

    /**
     * Envía una notificación de nueva instancia creada.
     */
    public void sendInstanceCreatedNotification(String userEmail, String engineName, String dbName) {
        if (!notificationsEnabled || webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            
            Map<String, Object> embed = new HashMap<>();
            embed.put("title", "🗄️ Nueva Instancia Creada");
            embed.put("color", 15844367); // Naranja
            embed.put("timestamp", LocalDateTime.now().toString());
            
            Map<String, Object>[] fields = new Map[]{
                createField("👤 Usuario", userEmail, true),
                createField("⚙️ Motor", engineName, true),
                createField("💾 Base de Datos", dbName, true),
                createField("⏰ Fecha", timestamp, false)
            };
            embed.put("fields", fields);
            
            Map<String, Object> footer = new HashMap<>();
            footer.put("text", "CrudCloud Instances");
            embed.put("footer", footer);

            Map<String, Object> payload = new HashMap<>();
            payload.put("embeds", List.of(embed));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
            
            log.info("Discord instance notification sent for user: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send Discord instance notification: {}", e.getMessage());
        }
    }

    private Map<String, Object> createField(String name, String value, boolean inline) {
        Map<String, Object> field = new HashMap<>();
        field.put("name", name);
        field.put("value", value);
        field.put("inline", inline);
        return field;
    }
}