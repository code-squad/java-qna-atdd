package support.domain;

import org.springframework.http.HttpHeaders;

import java.net.URI;

public interface ApiUrlGeneratable {
    URI generateApiUri();

    default HttpHeaders makeHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(generateApiUri());

        return headers;
    }
}
