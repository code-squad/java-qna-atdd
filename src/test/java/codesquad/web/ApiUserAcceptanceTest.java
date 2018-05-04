package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import support.test.AcceptanceTest;

public class ApiUserAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() throws Exception {

        UserDto newUser = createUserDto("testuser1");
        UserDto dbUser = getResource(createResource("/api/users",createUserDto("testuser1")),UserDto.class,findByUserId(newUser.getUserId()));
        assertThat(dbUser, is(newUser));
    }
    
    @Test
    public void show_다른_사람() throws Exception {

        ResponseEntity<String> response = getResource(createResource("/api/users",createUserDto("testuser2")),defaultUser());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {

        UserDto newUser = createUserDto("testuser3");
        String location = createResource("/api/users",newUser);

        User loginUser = findByUserId(newUser.getUserId());
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(loginUser).put(location, updateUser);

        UserDto dbUser = getResource(location,UserDto.class,findByUserId(newUser.getUserId()));
        assertThat(dbUser, is(updateUser));
    }
    
    @Test
    public void update_다른_사람() throws Exception {

        UserDto newUser = createUserDto("testuser4");
        String location = createResource("/api/users",newUser);
        
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(defaultUser()).put(location, updateUser);
        
        UserDto dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, UserDto.class);
        assertThat(dbUser, is(newUser));
    }

    private UserDto createUserDto(String userId) {

        return new UserDto(userId, "password", "name", "javajigi@slipp.net");
    }
}
