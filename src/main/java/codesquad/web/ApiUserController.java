package codesquad.web;

import java.net.URI;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.UserService;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    private static final Logger log = LoggerFactory.getLogger(ApiUserController.class);

    @Resource(name = "userService")
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<Void> create(@Valid @RequestBody UserDto user) {
        User savedUser = userService.add(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(savedUser.generateResourceURI()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
    
    @GetMapping("{id}")
    public UserDto show(@LoginUser User loginUser, @PathVariable long id) {
        User user = userService.findById(loginUser, id);
        return user.toUserDto();
    }
    
    @PutMapping("{id}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody UserDto updatedUser) {
        log.debug("param: {}", updatedUser);
        userService.update(loginUser, id, updatedUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
