package support.domain;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class RestResponseEntityMaker {

    public static ResponseEntity<Void> of(String location, HttpStatus status) {
        return new ResponseEntity<>(makeHeaders(location), status);
    }

    public static <T> ResponseEntity<T> of(T bodyPayload, String location, HttpStatus status) {
        return new ResponseEntity<>(bodyPayload, makeHeaders(location), status);
    }

    private static HttpHeaders makeHeaders(String location) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(location));
        return headers;
    }
}
