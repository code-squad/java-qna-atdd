package support.test;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class HtmlFormDataBuilderTemplate {
    public ResponseEntity<String> executePostForEntity(TestRestTemplate template, String targetUrl, Method method
            , HtmlFormDataBuilderMapper htmlFormDataBuilderMapper) throws Exception {

        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter(Method.METHOD.getType(), method.getType());
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilderMapper.setParams(htmlFormDataBuilder);

        return template.postForEntity(targetUrl, request, String.class);
    }

}
