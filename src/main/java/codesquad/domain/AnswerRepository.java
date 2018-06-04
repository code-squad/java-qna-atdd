package codesquad.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Transactional
    void deleteByWriterId(long writerId);
}
