package codesquad.web;

import org.springframework.http.HttpHeaders;
import support.domain.UrlGeneratable;

import java.net.URI;

public class ApiHeaderGenerator {
    public static HttpHeaders generateApiHeader(UrlGeneratable urlGeneratable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/" + urlGeneratable.generateUrl()));
        return headers;
    }
}
