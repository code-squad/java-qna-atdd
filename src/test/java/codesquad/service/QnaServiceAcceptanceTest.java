package codesquad.service;

import codesquad.QuestionNotFoundException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QnaServiceAcceptanceTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private QuestionRepository repository;

    private User user;

    @Before
    public void setUp() throws Exception {
        user = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
    }

    @Test
    @DirtiesContext
    public void create() throws Exception {
        Question question = qnaService.create(user, new Question("test", "test"));

        assertThat(question).isNotNull();
        assertThat(repository.findOne(question.getId())).isNotNull();
    }

    @Test
    public void findById() throws Exception {
        Optional<Question> question = qnaService.findById(1L);
        assertThat(question.isPresent()).isTrue();
    }

    @Test
    public void findById_ID에해당하는데이터가없는경우() throws Exception {
        Optional<Question> question = qnaService.findById(100L);
        assertThat(question.isPresent()).isFalse();
    }

    @Test
    public void findByIdAndNotDeleted_삭제된Question() throws Exception {
        Optional<Question> question = qnaService.findById(3L);
        assertThat(question.isPresent()).isFalse();
    }

    @Test
    @DirtiesContext
    public void update() throws Exception {
        Question question = qnaService.update(user, 1, new Question("update", "update content"));
        assertThat(question).isNotNull();

        Question dbQuestion = repository.findOne(question.getId());
        assertThat(dbQuestion).isNotNull();
        assertThat(dbQuestion.getTitle()).isEqualTo("update");
        assertThat(dbQuestion.getContents()).isEqualTo("update content");

    }

    @Test(expected = UnAuthorizedException.class)
    public void update_권한이없는경우() throws Exception {
        User user = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
        qnaService.update(user, 1, new Question("update", "update content"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_파라미터가NULL인경우() throws Exception {
        qnaService.update(null, 1, new Question("update", "update content"));

    }

    @Test(expected = QuestionNotFoundException.class)
    public void update_해당Question이존재하지않는경우() throws Exception {
        qnaService.update(user, 10, new Question("update", "update content"));
    }

    @Test(expected = QuestionNotFoundException.class)
    public void update_해당Question삭제된경우() throws Exception {
        qnaService.update(user, 3, new Question("update", "update content"));
    }

    @Test
    @DirtiesContext
    public void deleteQuestion() throws Exception {
        qnaService.deleteQuestion(user, 1);
        assertThat(repository.findOne(1L).isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_권한이없는경우() throws Exception {
        User user = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
        qnaService.deleteQuestion(user, 1);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_파라미터가NULL인경우() throws Exception {
        qnaService.deleteQuestion(null, 1);
    }

    @Test(expected = QuestionNotFoundException.class)
    public void deleteQuestion_이미존재하지않는경우() throws Exception {
        qnaService.deleteQuestion(user, 10);
    }

    @Test
    public void checkAuthority() throws Exception {
        assertThat(qnaService.isOwnerOfQuestion(user, 1)).isTrue();
        assertThat(qnaService.isOwnerOfQuestion(user, 2)).isFalse();
    }

    @Test
    public void checkAuthority_파라미터가NULL인경우() throws Exception {
        assertThat(qnaService.isOwnerOfQuestion(null, 1)).isFalse();
    }
}