package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.DeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.dto.AnswerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("answerService")
@Transactional
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    public AnswerDto findById(long id) throws DeleteException {
        return answerRepository.findById(id).map(Answer::toAnswerDto)
                .orElseThrow(DeleteException::new);
    }
}
