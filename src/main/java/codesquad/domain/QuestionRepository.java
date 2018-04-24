package codesquad.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Iterable<Question> findByDeleted(boolean deleted);

    List<Question> findByDeleted(boolean deleted, Pageable pageable);
}
