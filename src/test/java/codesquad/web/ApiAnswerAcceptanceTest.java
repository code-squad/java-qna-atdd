package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.AnswerDto;
import codesquad.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Autowired
    private QnaService qnaService;

    private Question question;
    private User otherUser;

    @Before
    public void setUp() throws Exception {
        long questionId = 1;

        super.setDefaultRequestUrl(String.format("/api/questions/%d/answers", questionId));
        question = qnaService.findQuestionById(questionId);
        otherUser = findByUserId("sanjigi");
    }

    @Test
    public void create_있는질문_로그인된상황() throws Exception {
        AnswerDto newAnswer = createAnswerDto();
        String location = createResource(newAnswer);

        AnswerDto insertedAnswerDto = getResource(location, template(), AnswerDto.class);
        assertEquals(newAnswer, insertedAnswerDto);
    }

    @Test
    public void create_있는질문_로그인_안_되어있는상황() {
        AnswerDto newAnswerDto = createAnswerDto();
        ResponseEntity<String> response = template().postForEntity(super.getDefaultRequestUrl(), newAnswerDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create_없는질문() {
        AnswerDto newAnswerDto = createAnswerDto();
        ResponseEntity<String> response = template().postForEntity("/api/questions/-10/answers", newAnswerDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void show_게스트() {
        AnswerDto newAnswerDto = createAnswerDto();
        logger.debug(newAnswerDto.toString());
        String location = createResource(newAnswerDto);
        logger.debug(location);
        AnswerDto insertedAnswerDto = getResource(location, template(), AnswerDto.class);
        logger.debug(insertedAnswerDto.toString());
        assertEquals(newAnswerDto, insertedAnswerDto);
    }

    @Test
    public void show_작성자() {
        AnswerDto newQuestionDto = createAnswerDto();
        String location = createResource(newQuestionDto);

        AnswerDto insertedQuestionDto = getResource(location, basicAuthTemplate(defaultUser()), AnswerDto.class);
        assertEquals(newQuestionDto, insertedQuestionDto);
    }

    @Test
    public void show_다른사람() {
        AnswerDto newQuestionDto = createAnswerDto();
        String location = createResource(newQuestionDto);

        AnswerDto insertedQuestionDto = getResource(location, basicAuthTemplate(otherUser), AnswerDto.class);
        assertEquals(newQuestionDto, insertedQuestionDto);
    }

    @Test
    public void delete_작성자() {
        AnswerDto newAnswerDto = createAnswerDto();
        String location = createResource(newAnswerDto);

        basicAuthTemplate().delete(location);

        AnswerDto dbQuestion = getResource(location, template(), AnswerDto.class);
        assertNull(dbQuestion);
    }

    @Test
    public void delete_다른사람() {
        AnswerDto newQuestionDto = createAnswerDto();
        String location = createResource(newQuestionDto);

        basicAuthTemplate(otherUser).delete(location);

        AnswerDto dbQuestion = getResource(location, template(), AnswerDto.class);
        assertNotNull(dbQuestion);
    }

    @Test
    public void delete_게스트() {
        AnswerDto newQuestionDto = createAnswerDto();
        String location = createResource(newQuestionDto);

        template().delete(location);
        AnswerDto dbQuestion = getResource(location, template(), AnswerDto.class);
        assertNotNull(dbQuestion);
    }

    private AnswerDto createAnswerDto() {
        return new AnswerDto("test contents");
    }
}
