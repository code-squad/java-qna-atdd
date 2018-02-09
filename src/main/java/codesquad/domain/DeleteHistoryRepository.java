package codesquad.domain;

import org.springframework.data.repository.CrudRepository;

public interface DeleteHistoryRepository extends CrudRepository<DeleteHistory, Long> {
    DeleteHistory findOneByContentId(Long id);
}
