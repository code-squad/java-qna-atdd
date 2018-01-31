package codesquad.domain;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AnswerTest {
    private Question question = new Question("title", "content");
    private User javajigi = new User("javajigi", "test", "test", "test@gmail.com");
    private User sanjigi = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");

    @Test
    public void 답변_리스트에서_다른_사용자가_존재하는가() {
        Answers answers = new Answers();
        Answer answer1 = new Answer(1L, javajigi, question, "answer1");
        Answer answer2 = new Answer(2L, sanjigi, question, "answer2");
        answers.add(answer1);
        answers.add(answer2);

        assertTrue(answers.isHasOtherWriter(javajigi));
    }

    @Test
    public void 답변_리스트의_삭제_히스토리_ID_반환이_올바른가() {
        Answers answers = new Answers();
        Answer answer1 = new Answer(1L, javajigi, question, "answer1");
        Answer answer2 = new Answer(2L, javajigi, question, "answer2");
        answers.add(answer1);
        answers.add(answer2);

        List<DeletedId> deletedIds = (List<DeletedId>) answers.convertDeletedId();
        assertNotNull(deletedIds);
        assertEquals(2, deletedIds.size());
    }
}
