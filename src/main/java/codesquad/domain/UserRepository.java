package codesquad.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUserId(String userId);

    User save(User user);

    User findOne(long id);

    List<User> findAll();
}
