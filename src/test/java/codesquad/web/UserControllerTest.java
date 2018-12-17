package codesquad.web;

import codesquad.domain.User;
import codesquad.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class UserControllerTest {
    @Mock
    private UserService userService;

    private UserController controller;

    @Before
    public void setUp() throws Exception {
        controller = new UserController();
    }

//    @Test
//    public void create() {
//        User user = new User(1,"javajigi", "password", "name", "ccc@naver.com");
//        controller.create(user);
//    }
}