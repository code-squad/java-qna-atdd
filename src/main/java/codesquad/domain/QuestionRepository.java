package codesquad.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByDeleted(boolean deleted, Pageable pageable);
    Optional<Question> findByTitle(String text);
}
