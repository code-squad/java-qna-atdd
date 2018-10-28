package support.test;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

public interface HtmlFormDataBuilderMapper {
    HttpEntity<MultiValueMap<String, Object>> setParams(HtmlFormDataBuilder htmlFormDataBuilder);
}
