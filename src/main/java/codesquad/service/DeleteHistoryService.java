package codesquad.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;

@Transactional
@Service("deleteHistoryService")
public class DeleteHistoryService {

    @Resource(name = "deleteHistoryRepository")
    private DeleteHistoryRepository deleteHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Iterable<DeleteHistory> saveAll(List<DeleteHistory> deleteHistories) {
        return deleteHistoryRepository.save(deleteHistories);
    }

    public DeleteHistory save(DeleteHistory deleteHistory) {
        return deleteHistoryRepository.save(deleteHistory);
    }
}
