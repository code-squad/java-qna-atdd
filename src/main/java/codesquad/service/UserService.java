package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityExistsException;
import java.util.List;

@Service("userService")
public class UserService {
    @Resource(name = "userRepository")
    private UserRepository userRepository;

    public User add(User newUser) {
        return userRepository.save(newUser);
    }

    @Transactional
    public User update(User loginUser, long id, User updatedUser) {
        return findById(id).update(loginUser, updatedUser);
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(EntityExistsException::new);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User login(String userId, String password) throws UnAuthenticationException {
        // TODO 로그인 기능 구현
        return userRepository
                .findByUserId(userId)
                .filter(x -> x.matchPassword(password))
                .orElseThrow(UnAuthenticationException::new);
    }
}
