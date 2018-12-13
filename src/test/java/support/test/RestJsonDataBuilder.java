package support.test;

import codesquad.domain.User;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class RestJsonDataBuilder {
    private String location;

    public RestJsonDataBuilder(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public <T> ResponseEntity<T> createEntity(TestRestTemplate template, Object request, Class<T> responseType) {
        ResponseEntity<T> responseEntity = template.postForEntity(this.location, request, responseType);
        this.location = responseEntity.getHeaders().getLocation().getPath();
        return responseEntity;
    }

    public <T> ResponseEntity<T> updateEntity(TestRestTemplate template, Object body, Class<T> responseType) {
        return (ResponseEntity<T>) template.exchange(this.location, HttpMethod.PUT, createHttpEntity(body), responseType);
    }

    public <T> ResponseEntity<T> deleteEntity(TestRestTemplate template, Class<T> responseType) {
        return template.exchange(this.location, HttpMethod.DELETE, HttpEntity.EMPTY, responseType);
    }

    public <T> T getResource(TestRestTemplate template, Class<T> responseType) {
        return template.getForObject(this.location, responseType);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
