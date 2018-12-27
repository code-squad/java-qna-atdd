package codesquad.web;

import codesquad.domain.*;
import codesquad.security.LoginUser;
import codesquad.service.DeleteHistoryService;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Autowired
    private DeleteHistoryService deleteHistoryService;

    @PostMapping("")
    public ResponseEntity<Question> create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody String contents) {
        qnaService.addReply(loginUser, questionId, contents);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Question>(qnaService.findByQuestionId(questionId), headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{answerId}")
    public Answer delete(@LoginUser User loginUser, @PathVariable long answerId) {
        deleteHistoryService.save(new DeleteHistory(ContentType.ANSWER, answerId,loginUser));
        return qnaService.deleteAnswer(loginUser, answerId);
    }


}
