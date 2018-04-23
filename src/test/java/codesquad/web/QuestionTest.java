package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Test;

public class QuestionTest {

    @Test
    public void 질문목록() {
        Question question = new Question("title",  "contents");
        question.writeBy(new User("serverwizard", "test", "홍종완", "serverwizrd@naver.com"));
        System.out.println(question.toString());
    }
}
