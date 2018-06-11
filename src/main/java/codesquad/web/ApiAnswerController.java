package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import support.domain.RestResponseEntityMaker;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Resource
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable Long questionId, @Valid @RequestBody AnswerDto answerDto) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerDto);
        return RestResponseEntityMaker.of(answer.generateApiUrl(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Answer> show(@PathVariable Long id) {
        Answer answer = qnaService.findAnswerById(id);
        return RestResponseEntityMaker.of(answer, answer.generateApiUrl(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> update(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody AnswerDto answerDto) {
        Answer updatedAnswer = qnaService.updateAnswer(loginUser, id, answerDto).toAnswer();
        return RestResponseEntityMaker.of(updatedAnswer, updatedAnswer.questionPath(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        String questionPath = qnaService.deleteAnswer(loginUser, id);
        return RestResponseEntityMaker.of(questionPath, HttpStatus.OK);
    }
}
