package support.aspect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;

import codesquad.QnaApplication;
import codesquad.dto.UserDto;
import codesquad.web.LoginController;
import codesquad.web.UserController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QnaApplication.class)
public class LoggingAspectTest {
    @Autowired
    private UserController userController;

    @Autowired
    private LoginController loginController;

    @Test
    public void logging() {
        UserDto user = new UserDto("aspectuser", "password", "name2", "javajigi@slipp.net2");
        userController.create(user);

        MockHttpSession session = new MockHttpSession();
        loginController.login(user.getUserId(), user.getPassword(), session);
    }
}
