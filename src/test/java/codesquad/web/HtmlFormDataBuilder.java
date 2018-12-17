package codesquad.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class HtmlFormDataBuilder {
    private HttpHeaders httpHeaders;
    private MultiValueMap<String, Object> params;

    public HtmlFormDataBuilder() {
        this.params = new LinkedMultiValueMap<>();

        urlEncodedForm();
    }

    private void urlEncodedForm() {
        this.httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public static HtmlFormDataBuilder createHtmlFormDataBuilder() {
        return new HtmlFormDataBuilder();
    }

    public HtmlFormDataBuilder addParameter(String key, Object value) {
        this.params.add(key, value);
        return this;
    }

    public HttpEntity<MultiValueMap<String, Object>> build() {
        return new HttpEntity<MultiValueMap<String, Object>>(params, httpHeaders);
    }

    public HtmlFormDataBuilder putParameter() {
        this.params.add("_method", "PUT");
        return this;
    }

    public HtmlFormDataBuilder deleteParameter() {
        this.params.add("_method", "DELETE");
        return this;
    }
}