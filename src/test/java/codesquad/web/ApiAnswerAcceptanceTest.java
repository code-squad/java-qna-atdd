package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.helper.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    public static final User FOBIDDEN_USER = new User(5L, "kunkun", "hehehe", "name", "sanjigi@slipp.net");

    @Test
    public void show() throws Exception {

        User loginUser = defaultUser();
        List<AnswerDto> question = getResource("/api/questions/1/answers", List.class,loginUser);
        assertThat(question.size(), is(2));
    }

    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("contents","내용물입니다.").build();

        ResponseEntity<String> response = createResource(loginUser,"/api/questions/1/answers",request);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getHeaders().getLocation().getPath(),is("/api/questions/1/answers"));
    }

    @Test
    public void create_다른사람() throws Exception {
        User loginUser = FOBIDDEN_USER;
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("contents","내용물입니다.").build();

        ResponseEntity<String> response = createResource(loginUser,"/api/questions/1/answers",request);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {

        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("contents","내용물입니다.").build();

        basicAuthTemplate(loginUser)
                .put("/api/questions/1/answers/1", request, String.class); //answerId 가 없어서 현재 500error가남
    }

    @Test
    public void update_다른사람() throws Exception {

        User loginUser = FOBIDDEN_USER;
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("contents","내용물입니다.").build();

        basicAuthTemplate(loginUser)
                .put("/api/questions/1/answers/1", request, String.class); //403
    }

    @Test
    public void delete() throws Exception {

        User loginUser = defaultUser();
        basicAuthTemplate(loginUser).delete("/api/questions/1/answers/1");  //answerId가 없으므로 500

    }

    @Test
    public void delete_다른사람() throws Exception {

        User loginUser = FOBIDDEN_USER;
        basicAuthTemplate(loginUser).delete("/api/questions/1/answers/1"); // 403
    }
}
