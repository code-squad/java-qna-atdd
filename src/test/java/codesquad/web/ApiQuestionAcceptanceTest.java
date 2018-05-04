package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.helper.HtmlFormDataBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void show() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = getResource("/api/questions/1",loginUser);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().getLocation().getPath(),is("/api/questions/1"));

    }

    @Test
    public void show_다른사람() throws Exception {
        User diffrUser = new User("kjp2673", "password12", "name", "javajigi@slipp.net");
        ResponseEntity<String> response = getResource("/api/questions/1",diffrUser);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("title","제목입니다.")
                .addParameter("contents","내용물입니다.").build();

        ResponseEntity<String> response = createResource(loginUser,"/api/questions",request);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getHeaders().getLocation().getPath(),is("/api/questions/3"));

    }

    @Test
    public void create_다른사람() throws Exception {
        User diffrUser = new User("kjp2673", "password12", "name", "javajigi@slipp.net");
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("title","제목입니다.")
                .addParameter("contents","내용물입니다.").build();

        ResponseEntity<String> response = createResource(diffrUser,"/api/questions",request);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("title","제목입니다.")
                .addParameter("contents","내용물입니다.").build();

        basicAuthTemplate(loginUser)
                .put(String.format("/api/questions/%d", 1), request, String.class);

        Question question = getResource(String.format("/api/questions/info/%d", 1), Question.class,loginUser);

        assertThat(question.getContents(), is("내용물입니다."));
        assertThat(question.getTitle(), is("제목입니다."));
    }

    @Test
    public void update_다른사람() throws Exception {
        User loginUser = defaultUser();
        User diffrUser = new User("kjp2673", "password12", "name", "javajigi@slipp.net");
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().addParameter("title","제목입니다.")
                .addParameter("contents","내용물입니다.").build();

        basicAuthTemplate(diffrUser)
                .put(String.format("/api/questions/%d", 1), request, String.class);

        Question question = getResource(String.format("/api/questions/info/%d", 1), Question.class,loginUser);

        assertThat(question.getTitle(), is("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));
    }

    @Test
    public void delete() throws Exception {
        User loginUser = defaultUser();
        basicAuthTemplate(loginUser).delete(String.format("/api/questions/%d", 1));

        Question question = getResource(String.format("/api/questions/info/%d", 1), Question.class,loginUser);
        assertNull(question);
    }

    @Test
    public void delete_다른사람() throws Exception {
        User loginUser = defaultUser();
        User diffrUser = new User("kjp2673", "password12", "name", "javajigi@slipp.net");
        basicAuthTemplate(diffrUser).delete(String.format("/api/questions/%d", 1));

        Question question = getResource(String.format("/api/questions/info/%d", 1), Question.class,loginUser);
        assertThat(question.getTitle(), is("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));

    }
}
