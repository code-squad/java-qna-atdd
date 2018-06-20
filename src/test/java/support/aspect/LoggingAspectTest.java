package support.aspect;

import codesquad.QnaApplication;
import codesquad.domain.User;
import codesquad.web.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QnaApplication.class)
public class LoggingAspectTest {
    @Autowired
    private UserController userController;

    @Test
    public void logging() {
        User user = new User("aspectuser", "password", "name2", "javajigi@slipp.net2");
        userController.create(user);
    }
}
