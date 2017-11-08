package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.security.UnAuthenticationException;

@Service("userService")
public class UserService {
	@Resource(name = "userRepository")
	private UserRepository userRepository;

	public User login(String userId, String password) throws UnAuthenticationException {
		return null;
	}

	public User add(User user) {
		return userRepository.save(user);
	}

	public User update(User loginUser, long id, User updatedUser) {
		return null;
	}

	public User findById(long id) {
		return userRepository.findOne(id);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}
}
