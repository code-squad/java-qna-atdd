package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.UserDto;
import codesquad.web.QuestionController;

@Service("userService")
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@Resource(name = "userRepository")
	private UserRepository userRepository;

	public User add(UserDto userDto) {
		return userRepository.save(userDto.toUser());
	}

	public User update(User loginUser, long id, UserDto updatedUser) {
		User original = userRepository.findOne(id);
		original.update(loginUser, updatedUser.toUser());
		return userRepository.save(original);
	}

	public User findById(User loginUser, long id) {
		User user = userRepository.findOne(id);
		if (!user.equals(loginUser)) {
			throw new UnAuthorizedException();
		}
		return user;
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User login(String userId, String password) throws UnAuthenticationException {
		log.debug("============ access ID : " + userId + " | access PASSWORD : " + password + " ============");
		User user = userRepository.findByUserId(userId).orElseThrow(UnAuthenticationException::new);
		
		if (!user.matchPassword(password)) {
			throw new UnAuthenticationException();
		}
		
		System.out.println("========== Login Success!! User is " + user + " =============");
		return user;
	}
}
