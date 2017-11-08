package codesquad.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository("userRepository")
public class MockUserRepository implements UserRepository {

    @Override
    public Optional<User> findByUserId(String userId) {
        return null;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User findOne(long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

}
