package codesquad.web;

import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest{

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create_no_login(){
        QuestionDto questionDto = createDto();
        ResponseEntity<String> response = template().postForEntity("/api/questions", questionDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create_login(){
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(newQuestion, is(dbQuestion));
    }

    @Test
    public void update_login(){
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        QuestionDto updateQuestion = new QuestionDto("titleTest2", "contentsTest2");
        basicAuthTemplate().put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
        assertThat(updateQuestion, is(dbQuestion));
    }

    @Test
    public void update_다른_사용자(){
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        QuestionDto updateQuestion = new QuestionDto("titleTest2", "contentsTest2");
        basicAuthTemplate(findByUserId("riverway")).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId("riverway"));
        assertThat(newQuestion, is(dbQuestion));
    }

    @Test
    public void delete_login(){
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        basicAuthTemplate().delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
        log.debug("dbQuestion : {}", dbQuestion);
        assertNull(dbQuestion);
    }

    @Test
    public void delete_다른_사용자(){
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        basicAuthTemplate(findByUserId("riverway")).delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId("riverway"));
        assertThat(dbQuestion, is(newQuestion));
    }

    private QuestionDto createDto(){
        return new QuestionDto("titleTest", "contentsTest");
    }
}
