package codesquad.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findByIdAndDeleted(long id, boolean deleted);

    Iterable<Question> findByDeleted(boolean deleted);
}
