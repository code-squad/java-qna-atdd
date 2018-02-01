package codesquad.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ApiResponse {
    public static ResponseEntity<Void> OK() {
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public static ResponseEntity<Void> CREATED(String uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(uri));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    public static ResponseEntity<Void> UNAUTHORIZED() {
        return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
    }
}
