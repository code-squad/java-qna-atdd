package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionTest {

    private long QUESTION_ID = 10L;
    private String TITLE = "질문제목";
    private String CONTENTS = "질문본문";
    private Question question;

    private long USER_ID = 10;
    private String USER_USERID = "유저아이디";
    private String USER_PW = "암호";
    private String USER_NAME = "이름";
    private String USER_MAIL = "메일";
    private User user;

    @Before
    public void setUp() throws Exception {
        question = new Question(QUESTION_ID, TITLE, CONTENTS);
        user = new User(USER_ID, USER_USERID, USER_PW, USER_NAME, USER_MAIL);
        question.writeBy(user);
    }

    @Test
    public void 제목조회() {
        final String title = question.getTitle();
        assertThat(title).isEqualTo(TITLE);
    }

    @Test
    public void 내용조회() {
        String contents = question.getContents();
        assertThat(contents).isEqualTo(CONTENTS);
    }

    @Test
    public void 작성자() {
        final User writer = question.getWriter();
        assertThat(user).isEqualTo(writer);
    }

    @Test
    public void 소유자체크() {
        final boolean isOwner = question.isOwner(user);
        assertThat(isOwner).isTrue();
    }

    @Test
    public void 소유자아님() {
        User otherUser = new User(1000, "aa", "bb", "cc", "dd");
        final boolean isOwner = question.isOwner(otherUser);
        assertThat(isOwner).isFalse();
    }

    @Test
    public void update_by_owner() {
        String updateTitle = "수정된질문제목";
        String updateContent = "수정된질문내용";
        Question target = new Question(updateTitle, updateContent);

        question.update(user, target);

        assertThat(question.getTitle()).isEqualTo(updateTitle);
        assertThat(question.getContents()).isEqualTo(updateContent);
        assertThat(question.getWriter()).isEqualTo(user);
    }

    @Test
    public void update_return() {
        String updateTitle = "수정된질문제목";
        String updateContent = "수정된질문내용";
        Question target = new Question(updateTitle, updateContent);

        final Question update = question.update(user, target);

        assertThat(question).isEqualTo(update);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_by_not_owner() {
        User otherUser = new User(100, "other123", "pw11", "otherName", "email");

        String updateTitle = "수정된질문제목";
        String updateContent = "수정된질문내용";
        Question target = new Question(updateTitle, updateContent);

        question.update(otherUser, target);
    }

    @Test
    public void delete_by_owner() {
        question.delete(user);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_by_not_owner() {
        User otherUser = new User(100, "other123", "pw11", "otherName", "email");
        question.delete(otherUser);
    }
}
