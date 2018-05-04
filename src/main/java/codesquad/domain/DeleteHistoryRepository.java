package codesquad.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DeleteHistoryRepository extends CrudRepository<DeleteHistory, Long> {
    Optional<Object> findByContentIdAndContentType(long contentId, ContentType contentType);
}
