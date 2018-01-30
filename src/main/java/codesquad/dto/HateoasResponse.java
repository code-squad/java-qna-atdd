package codesquad.dto;

import codesquad.domain.QuestionList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Link;

import java.util.HashMap;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class HateoasResponse<T> {
    @JsonProperty("_embedded")
    private T embedded;
    @JsonProperty("_links")
    private HashMap<String, Link> links;
    private pageResponse page;

    @Getter
    @NoArgsConstructor
    @EqualsAndHashCode
    public class pageResponse {
        private Integer size;
        private Integer totalElements;
        private Integer totalPages;
        private Integer number;
    }
}

