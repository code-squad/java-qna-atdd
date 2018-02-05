package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static codesquad.domain.UserTest.newUser;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuestionTest {

    private Question question;
    private User writer;
    private User testUser;

    @Before
    public void setup() {
        writer = newUser("sanjigi");
        testUser = newUser("testUser");

        question = new Question("제목", "내용");
        question.writeBy(writer);
    }

    @Test
    public void 수정테스트() {
        question.update(writer, new Question("업데이트", "업데이트"));
        assertThat(question.getTitle()).isEqualTo("업데이트");
    }

     @Test(expected = UnAuthorizedException.class)
     public void 다른사람이_수정테스트() {
        question.update(testUser, new Question("업데이트", "업데이트"));
     }

     @Test
     public void 삭제테스트() throws CannotDeleteException {
        question.delete(writer);
        assertTrue(question.isDeleted());
     }

     @Test(expected = CannotDeleteException.class)
     public void 다른사람이_삭제테스트() throws CannotDeleteException {
        question.delete(testUser);
     }
}
