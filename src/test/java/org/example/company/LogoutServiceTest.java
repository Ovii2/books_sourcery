package org.example.company;

import static org.junit.jupiter.api.Assertions.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


@SpringBootTest
@RequiredArgsConstructor
public class LogoutServiceTest {

    private RestTemplate restTemplate;

    @Test
    public void testLogout() {
        String url = "http://localhost:8080/api/auth/logout";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer your_jwt_token");

        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class, headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
