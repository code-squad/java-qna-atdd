package codesquad.domain;

import codesquad.CannotManageException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class QuestionTest {
    private User javajigi = new User("javajigi", "test", "test", "test@gmail.com");
    private User sanjigi = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");

    @Test(expected = CannotManageException.class)
    public void 질문_삭제시_글쓴이가_아닐경우_에러를_던지는가() throws CannotManageException {
        Question question = questionTestData();
        question.deleted(sanjigi);
    }

    @Test(expected = CannotManageException.class)
    public void 질문_삭제시_답변자가_다른경우_에러를_던지는가() throws CannotManageException {
        Question question = questionTestData();
        question.addAnswer(new Answer(sanjigi, "answer"));
        question.deleted(javajigi);
    }

    @Test
    public void 질문_삭제후_삭제_히스토리_객체를_반환하는가() throws CannotManageException {
        Question question = questionTestData();
        question.addAnswer(new Answer(javajigi, "answer"));
        List<DeleteHistory> deleteHistorys = question.deleted(javajigi);

        assertNotNull(deleteHistorys);
        assertEquals(2, deleteHistorys.size());
    }

    private Question questionTestData() {
        Question question = new Question("title", "content");
        question.writeBy(javajigi);
        return question;
    }
}
