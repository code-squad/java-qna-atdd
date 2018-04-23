package support.html;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

/**
 * @author sangsik.kim
 */
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

    public HtmlFormDataBuilder put() {
        return addParameter("_method", "put");
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
}