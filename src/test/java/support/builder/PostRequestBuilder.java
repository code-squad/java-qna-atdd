package support.builder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

public class PostRequestBuilder {
    private HttpHeaders headers;
    private MultiValueMap<String, Object> params;

    private PostRequestBuilder(HttpHeaders headers) {
        this.headers = headers;
        this.params = params = new LinkedMultiValueMap<>();
    }

    public void addParam(String key, String value){
        params.add(key, value);
    }

    public HttpEntity<MultiValueMap<String, Object>> build(){
        return new HttpEntity<>(this.params, this.headers);
    }

    public static PostRequestBuilder urlEncodedHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new PostRequestBuilder(headers);
    }

}
