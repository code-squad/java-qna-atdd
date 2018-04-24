package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.CannotDeleteException;
import codesquad.domain.CannotUpdateException;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URI;

@RequestMapping("/api/questions/{questionId}/answers")
@RestController
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity create(@LoginUser User loginUser,
                                         @PathVariable long questionId,
                                         @RequestBody String content) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, content);
        return ResponseEntity.created(URI.create(answer.resourceUrl())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@LoginUser User loginUser,
                                 @PathVariable long questionId,
                                 @PathVariable long id,
                                 @RequestBody String content) throws CannotUpdateException {
        qnaService.updateAnswer(loginUser, id, content);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@LoginUser User loginUser,
                                         @PathVariable long questionId,
                                         @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, id);
        return ResponseEntity.noContent().build();
    }
}
