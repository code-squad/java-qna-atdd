package codesquad.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Iterable<Question> findByDeleted(boolean deleted);
    Iterable<Question> findByWriterId(long writerId);
}