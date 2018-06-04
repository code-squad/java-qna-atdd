package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import codesquad.domain.Question;
import codesquad.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;

@Service("deleteHistoryService")
public class DeleteHistoryService {

    private final Logger log = LoggerFactory.getLogger(DeleteHistoryService.class);

    @Resource(name = "deleteHistoryRepository")
    private DeleteHistoryRepository deleteHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAll(List<DeleteHistory> deleteHistories) {
        for (DeleteHistory deleteHistory : deleteHistories) {
            log.debug("delete history : {}", deleteHistory);
            deleteHistoryRepository.save(deleteHistory);
        }
    }

    public void registerHistory(User loginUser, Question original) {
        saveAll(original.toDeleteHistories(loginUser));
    }
}
