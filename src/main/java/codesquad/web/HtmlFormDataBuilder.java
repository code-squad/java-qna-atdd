package codesquad.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class HtmlFormDataBuilder {
    private HttpHeaders headers;
    private MultiValueMap<String, Object> params;

    private HtmlFormDataBuilder(HttpHeaders headers) {
        this.headers = headers;
        this.params = new LinkedMultiValueMap<>();
    }

    public HtmlFormDataBuilder addParameter(String key, Object value) {
        this.params.add(key, value);
        return this;
    }

    public HttpEntity<MultiValueMap<String, Object>> build() {
        return new HttpEntity<MultiValueMap<String, Object>>(params, headers);
    }

    public static HtmlFormDataBuilder urlEncodedForm() {
        return new HtmlFormDataBuilder(getHeadersBy(MediaType.APPLICATION_FORM_URLENCODED));
    }

    public static HtmlFormDataBuilder jsonDataBuilder() {
        return new HtmlFormDataBuilder(getHeadersBy(MediaType.APPLICATION_JSON));
    }

    public static HttpHeaders defaultHeaders() {
        return getHeadersBy(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public static HttpHeaders getHeadersBy(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML, MediaType.APPLICATION_JSON));
        headers.setContentType(mediaType);
        return headers;
    }
}
