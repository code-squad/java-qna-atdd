package codesquad.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByDeleted(boolean deleted);

    Optional<Question> findById(long questionId);
}
