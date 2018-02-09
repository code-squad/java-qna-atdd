package codesquad.web;

import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.service.DeleteHistoryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Test
    public void createAndSelect() {
        QuestionDto newQuestion = new QuestionDto("title test", "question test");
        String location = createQuestion(newQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }

    private String createQuestion(QuestionDto newQuestion) {
        return createQuestion(newQuestion, defaultUser());
    }

    private String createQuestion(QuestionDto newQuestion, User loginUser) {
        return createResource("/api/questions", newQuestion, loginUser);
    }

    @Test
    public void update() {
        QuestionDto newQuestion = new QuestionDto("title test2", "question test2");
        String location = createQuestion(newQuestion);

        QuestionDto updatedQuestion = new QuestionDto("updated title test2", "updated question test2");
        basicAuthTemplate().put(location, updatedQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(updatedQuestion));
    }

    @Test
    public void updateFailedBecauseOfNotSameUser() {
        QuestionDto newQuestion = new QuestionDto("title test2", "question test2");
        String location = createQuestion(newQuestion);

        QuestionDto updatedQuestion = new QuestionDto("updated title test2", "updated question test2");
        basicAuthTemplate(defaultUserAsSANJIGI()).put(location, updatedQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }

    @Test
    public void deleteWithoutAnswer() {
        QuestionDto newQuestion = new QuestionDto("title test3", "question test3");
        String location = createQuestion(newQuestion);

        basicAuthTemplate().delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertNull(dbQuestion);
    }

    @Test
    public void deleteFailedWithAnotherUserAnswer() {
        QuestionDto newQuestion = new QuestionDto("title test3", "question test3");
        String questionLocation = createQuestion(newQuestion);

        AnswerDto newAnswer = new AnswerDto("answer test");
        String answerLocation = createAnswer(questionLocation, newAnswer, defaultUserAsSANJIGI());

        basicAuthTemplate().delete(questionLocation);

        QuestionDto dbQuestion = getResource(questionLocation, QuestionDto.class);
        assertNotNull(dbQuestion);

        AnswerDto dbAnswer = getResource(answerLocation, AnswerDto.class);
        assertNotNull(dbAnswer);

        Answer answer = getAnswerBy(answerLocation);
        assertFalse(answer.isDeleted());
    }

    private String createAnswer(String location, AnswerDto newAnswer, User loginUser) {
        return createResource(location + "/answers", newAnswer, loginUser);
    }

    @Test
    public void deleteWithSameUserAnswer() {
        User loginUser = defaultUserAsSANJIGI();

        QuestionDto newQuestion = new QuestionDto("title test3", "question test3");
        String questionLocation = createQuestion(newQuestion, loginUser);

        AnswerDto newAnswer = new AnswerDto("answer test");
        String answerLocation = createAnswer(questionLocation, newAnswer, loginUser);

        basicAuthTemplate(loginUser).delete(questionLocation);

        QuestionDto dbQuestion = getResource(questionLocation, QuestionDto.class, loginUser);
        assertNull(dbQuestion);

        AnswerDto dbAnswer = getResource(answerLocation, AnswerDto.class, loginUser);
        assertNull(dbAnswer);

        Answer answer = getAnswerBy(answerLocation);
        assertTrue(answer.isDeleted());

        Question question = answer.getQuestion();
        DeleteHistory questionDeleteHistory = deleteHistoryRepository.findOneByContentId(question.getId());
        assertEquals(ContentType.QUESTION, questionDeleteHistory.getContentType());
        assertEquals(Long.valueOf(question.getId()), questionDeleteHistory.getContentId());
        assertEquals(loginUser, questionDeleteHistory.getDeletedBy());

        DeleteHistory answerDeleteHistory = deleteHistoryRepository.findOneByContentId(answer.getId());
        assertEquals(ContentType.ANSWER, answerDeleteHistory.getContentType());
        assertEquals(Long.valueOf(answer.getId()), answerDeleteHistory.getContentId());
        assertEquals(loginUser, answerDeleteHistory.getDeletedBy());
    }

    private Answer getAnswerBy(String answerLocation) {
        String[] splitLocation = answerLocation.split("/");
        return answerRepository.findOne(Long.valueOf(splitLocation[6]));
    }

    @Test
    public void deleteFailedBecauseOfNotSameUser() {
        QuestionDto newQuestion = new QuestionDto("title test4", "question test4");
        String location = createQuestion(newQuestion);

        basicAuthTemplate(defaultUserAsSANJIGI()).delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }
}

