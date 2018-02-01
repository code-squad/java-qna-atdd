package support.test;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HtmlFormDataBuilder(headers);
    }

    public static HttpEntity<MultiValueMap<String, Object>> updateRequest() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "title test1")
                .addParameter("contents", "contents test1")
                .build();
    }

    public static HttpEntity<MultiValueMap<String, Object>> deleteRequest() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();
    }

    public static HttpEntity<MultiValueMap<String, Object>> createRequest() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title test")
                .addParameter("contents", "contents test").build();
    }
}
