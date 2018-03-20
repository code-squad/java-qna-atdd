package codesquad.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionTest.class);

	public static User newUser(String userId, String password) {
		return new User(1L, userId, password, "name", "javajigi@slipp.net");
	}

	public static User loginUser() {
		return new User("ksm0814", "k5696", "link", "kksm0814@naver.com");
	}

	@Test
	public void 같은사용자글_삭제() throws Exception {
		Question question = new Question("제목1", "내용1");
		question.writeBy(loginUser());
		log.debug("question : {}", question);
		assertThat(question.isDeleted(), is(false));

		question.delete(loginUser());
		assertThat(question.isDeleted(), is(true));
	}

	@Test
	public void 다른사용자글_삭제() throws Exception {
		Question question = new Question("제목2", "내용2");
		question.writeBy(newUser("guestUser", "none"));
		assertThat(question.isDeleted(), is(false));
		
		try {
			question.delete(loginUser());
		} catch (Exception e) {
		}
		assertThat(question.isDeleted(), is(false));
	}
	

}
