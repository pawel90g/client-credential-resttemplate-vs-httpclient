package dev.garbacik.webclientclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class WebClientClientApplication
        implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(WebClientClientApplication.class);

    @Value("${host.url}")
    private String hostUrl;

    @Autowired
    private WebClient apiWebClient;

    public static void main(String[] args) {
        SpringApplication.run(WebClientClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        UriSpec<RequestBodySpec> uriSpec = apiWebClient.method(HttpMethod.GET);

        RequestBodySpec bodySpec = uriSpec.uri(
                uriBuilder -> uriBuilder.pathSegment("values").build());

        Mono<String[]> response = bodySpec.retrieve()
                .bodyToMono(String[].class);

        response
                .doOnError(err -> log.error("Unable to get data from {}. {}", hostUrl, err))
                .doOnNext(result -> log.info(String.join(", ", result)))
                .subscribe();
    }
}
