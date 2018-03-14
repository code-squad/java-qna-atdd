package codesquad.service;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.mockito.exceptions.misusing.NullInsteadOfMockException;
import org.springframework.stereotype.Service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.UserDto;

@Service("userService")
public class UserService {
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
		System.out.println("============ access ID : " + userId + " | access PASSWORD : " + password + " ============");
		User user = userRepository.findByUserId(userId).orElseThrow(UnAuthenticationException::new);
		
		if (!user.matchPassword(password)) {
			throw new UnAuthenticationException();
		}
		
		System.out.println("========== Login Success!! User is " + user + " =============");
		return user;
	}
}
