package codesquad.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnswerTest {
	private static final Logger log = LoggerFactory.getLogger(AnswerTest.class);

	public static User newUser(String userId, String password) {
		return new User(1L, userId, password, "name", "javajigi@slipp.net");
	}

	public static User loginUser() {
		return new User("ksm0814", "k5696", "link", "kksm0814@naver.com");
	}

	@Test
	public void 답변만다른사용자_삭제() throws Exception {
		Question question = new Question("제목1", "내용1");
		question.writeBy(loginUser());
		Answer answer = new Answer(1L, newUser("guestUser", "none"), question, "답변 내용1");
		question.addAnswer(answer);
		assertThat(answer.isDeleted(), is(false));
		
		try {
			answer.delete(loginUser());
		} catch (Exception e) {
		}
		assertThat(answer.isDeleted(), is(false));
	}

	@Test
	public void 다같은사용자_삭제() throws Exception {
		Question question = new Question("제목2", "내용2");
		question.writeBy(loginUser());
		Answer answer = new Answer(1L, loginUser(), question, "답변 내용2");
		question.addAnswer(answer);
		assertThat(answer.isDeleted(), is(false));
		
		answer.delete(loginUser());
		assertThat(answer.isDeleted(), is(true));
	}

}
