package codesquad.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import codesquad.domain.ContentType;
import codesquad.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;

@Service("deleteHistoryService")
public class DeleteHistoryService {
    @Resource(name = "deleteHistoryRepository")
    private DeleteHistoryRepository deleteHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAll(List<DeleteHistory> deleteHistories) {
        for (DeleteHistory deleteHistory : deleteHistories) {
            deleteHistoryRepository.save(deleteHistory);
        }
    }

    public DeleteHistory saveDeleteHistoryOfAnswer(DeleteHistory deleteHistory){
        return deleteHistoryRepository.save(deleteHistory);
    }
}
