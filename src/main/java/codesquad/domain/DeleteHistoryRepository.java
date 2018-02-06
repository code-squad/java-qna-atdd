package codesquad.domain;

import org.springframework.data.repository.CrudRepository;

public interface DeleteHistoryRepository extends CrudRepository<DeleteHistory, Long> {
    DeleteHistory findByContentIdAndContentType(Long contentId, ContentType contentType);
}
