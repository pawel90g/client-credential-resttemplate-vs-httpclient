package dev.garbacik.resttemplateclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RestTemplateClientApplication
        implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateClientApplication.class);

    @Value("${host.url}")
    private String hostUrl;

    public static void main(String[] args) {
        SpringApplication.run(RestTemplateClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        final ResponseEntity<String[]> response = restTemplate.getForEntity(hostUrl + "values", String[].class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info(String.join(", ", response.getBody()));
        } else {
            log.error("Unable to get data from {}. {}", hostUrl, response.getStatusCode().toString());
        }
    }
}
