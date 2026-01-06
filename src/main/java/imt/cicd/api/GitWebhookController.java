package imt.cicd.api;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import imt.cicd.data.FullPipeline;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
public class GitWebhookController {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/push")
    public void handleGitHubEvent(
            @RequestBody String rawPayload,
            @RequestHeader(value = "X-GitHub-Event", defaultValue = "unknown") String eventType,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {

        String secret = System.getenv("GITHUB_WEBHOOK_SECRET");
        
        if (secret != null && !secret.isEmpty()) {
            if (signature == null) {
                log.warn("Signature manquante");
                return;
            }
            if (!verifySignature(rawPayload, signature, secret)) {
                log.warn("Signature invalide");
                return;
            }
        } else {
            log.warn("ATTENTION : Aucun secret configuré, le webhook n'est pas sécurisé");
        }

        try {
            JsonNode payload = objectMapper.readTree(rawPayload);

            if ("ping".equals(eventType)) {
                log.info("Ping GitHub reçu et validé");
                return;
            }

            if ("push".equals(eventType) && payload.has("ref")) {
                String ref = payload.path("ref").asText();
                if ("refs/heads/main".equals(ref)) {
                    String repoUrl = payload.path("repository").path("clone_url").asText();
                    log.info("Push authenticated on main. Running the pipeline for {}", repoUrl);

                    FullPipeline.run(repoUrl);
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement du JSON", e);
        }
    }

    private boolean verifySignature(String payload, String signature, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder result = new StringBuilder("sha256=");
            for (byte b : hmacBytes) {
                result.append(String.format("%02x", b));
            }
            
            return java.security.MessageDigest.isEqual(
                result.toString().getBytes(StandardCharsets.UTF_8), 
                signature.getBytes(StandardCharsets.UTF_8)
            );

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Erreur lors de la vérification de signature", e);
            return false;
        }
    }
}