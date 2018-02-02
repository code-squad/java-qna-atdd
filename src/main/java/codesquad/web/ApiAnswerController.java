package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.etc.CannotDeleteException;
import codesquad.etc.UnAuthorizedException;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> createAnswer(@LoginUser User loginUser,
                                             @PathVariable long questionId,
                                             @Valid @RequestBody AnswerDto answerDto) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerDto.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(answer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public AnswerDto show(@PathVariable long id) {
        Answer answer = qnaService.findOneAnswer(id);

        return Optional.ofNullable(answer)
                .map(Answer::toAnswerDto)
                .orElse(null);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id,
                       @LoginUser User loginUser,
                       @Valid @RequestBody AnswerDto answerDto) {
        try {
            qnaService.updateAnswer(loginUser, id, new Answer()
                    .setContents(answerDto.getContents()));
        } catch (UnAuthorizedException e) {
            log.debug(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id,
                       @LoginUser User loginUser) {
        try {
            qnaService.deleteAnswer(loginUser, id);
            log.debug("delete success");
        } catch (CannotDeleteException e) {
            log.debug(e.getMessage());
            log.debug("delete failed");
        }
    }
}
