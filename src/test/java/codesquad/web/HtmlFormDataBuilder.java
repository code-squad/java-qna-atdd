package codesquad.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class HtmlFormDataBuilder {

    private HttpHeaders header;
    private MultiValueMap<String, Object> params;

    private HtmlFormDataBuilder(HttpHeaders header) {
        this.header = header;
        this.params = new LinkedMultiValueMap<>();
    }

    public HtmlFormDataBuilder addParameter(String key, Object value) {
        this.params.add(key, value);
        return this;
    }

    public HttpEntity<MultiValueMap<String, Object>> build() {
        return new HttpEntity<MultiValueMap<String, Object>>(params, header);
    }

    public static HtmlFormDataBuilder urlEncodedForm() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        header.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        return new HtmlFormDataBuilder(header);
    }
}
