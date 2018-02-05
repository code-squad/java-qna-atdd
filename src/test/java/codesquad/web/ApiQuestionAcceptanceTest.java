package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.service.DeleteHistoryService;
import codesquad.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private User otherUser;

    @Autowired
    private QnaService qnaService;

    @Autowired
    private DeleteHistoryService deleteHistoryService;

    @Before
    public void setUp() throws Exception {
        otherUser = findByUserId("sanjigi");
        super.setDefaultRequestUrl("/api/questions");
    }

    @Test
    public void create_login() throws Exception {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        QuestionDto insertedQuestionDto = getResource(location, template(), QuestionDto.class);
        assertEquals(newQuestionDto, insertedQuestionDto);
    }

    @Test
    public void create_not_login() throws Exception {
        QuestionDto newQuestionDto = createQuestionDto();
        ResponseEntity<String> response = template().postForEntity(super.getDefaultRequestUrl(), newQuestionDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void show_게스트() {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        QuestionDto insertedQuestionDto = getResource(location, template(), QuestionDto.class);
        assertEquals(newQuestionDto, insertedQuestionDto);
    }

    @Test
    public void show_작성자() {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        QuestionDto insertedQuestionDto = getResource(location, basicAuthTemplate(defaultUser()), QuestionDto.class);
        assertEquals(newQuestionDto, insertedQuestionDto);
    }

    @Test
    public void show_다른사람() {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        QuestionDto insertedQuestionDto = getResource(location, basicAuthTemplate(otherUser), QuestionDto.class);
        assertEquals(newQuestionDto, insertedQuestionDto);
    }

    @Test
    public void update_작성자() throws Exception {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        QuestionDto updateQuestion = new QuestionDto(newQuestionDto.getId(), "update title", "update contents");
        basicAuthTemplate().put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, template(), QuestionDto.class);
        assertEquals(dbQuestion, updateQuestion);
    }

    @Test
    public void update_다른사람() throws Exception {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        QuestionDto updateQuestion = new QuestionDto(newQuestionDto.getId(), "update title", "update contents");
        basicAuthTemplate(otherUser).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, template(), QuestionDto.class);
        assertEquals(dbQuestion, newQuestionDto);
    }

    @Test
    public void delete_없는_질문() {
        ResponseEntity<String> response = getResponseWithDeleteRequest(defaultUser(), "/api/questions/-30");

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_다른사람() {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        ResponseEntity<String> response = getResponseWithDeleteRequest(otherUser, location);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);

        QuestionDto dbQuestion = getResource(location, template(), QuestionDto.class);
        assertNotNull(dbQuestion);
    }

    @Test
    public void delete_작성자_답글없음() {
        QuestionDto newQuestionDto = createQuestionDto();
        String location = createResource(newQuestionDto);

        ResponseEntity<String> response = getResponseWithDeleteRequest(defaultUser(), location);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        QuestionDto dbQuestion = getResource(location, template(), QuestionDto.class);
        assertNull(dbQuestion);
    }

    @Test
    public void delete_작성자_답글도_작성자() {
        QuestionDto newQuestionDto = createQuestionDto();
        String questionLocation = createResource(newQuestionDto);

        Answer savedAnswer = qnaService.createAnswer(
                defaultUser(),
                Integer.parseInt(questionLocation.substring(15)),
                new AnswerDto("test Contents").toAnswer()
        );

        ResponseEntity<String> response = getResponseWithDeleteRequest(defaultUser(), questionLocation);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        QuestionDto dbQuestion = getResource(questionLocation, template(), QuestionDto.class);
        assertNull(dbQuestion);

        AnswerDto answerDto = getResource(savedAnswer.generateApiUrl(), basicAuthTemplate(defaultUser()), AnswerDto.class);
        assertNull(answerDto);
    }

    @Test
    public void delete_작성자_답글은_다른사람() {
        QuestionDto newQuestionDto = createQuestionDto();
        String questionLocation = createResource(newQuestionDto);

        Answer savedAnswer = qnaService.createAnswer(
                otherUser,
                Integer.parseInt(questionLocation.substring(15)),
                new AnswerDto("test Contents").toAnswer()
        );

        ResponseEntity<String> response = getResponseWithDeleteRequest(defaultUser(), questionLocation);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        QuestionDto dbQuestion = getResource(questionLocation, template(), QuestionDto.class);
        assertNotNull(dbQuestion);

        AnswerDto answerDto = getResource(savedAnswer.generateApiUrl(), basicAuthTemplate(defaultUser()), AnswerDto.class);
        assertNotNull(answerDto);
    }

    private QuestionDto createQuestionDto() {
        return new QuestionDto("test title", "test contents");
    }

    private ResponseEntity<String> getResponseWithDeleteRequest(User loginUser, String location) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .setRequestMethod("delete")
                .build();

        return basicAuthTemplate(loginUser)
                .postForEntity(
                        String.format(location),
                        request,
                        String.class
                );
    }
}
