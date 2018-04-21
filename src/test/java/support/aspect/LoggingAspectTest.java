package support.aspect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import codesquad.QnaApplication;
import codesquad.dto.UserDto;
import codesquad.web.UserController;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QnaApplication.class)
public class LoggingAspectTest {
    @Autowired
    private UserController userController;

    @Test
    public void logging() {
        UserDto user = new UserDto("aspectuser", "password", "name2", "javajigi@slipp.net2");
        userController.create(user);
    }



}
