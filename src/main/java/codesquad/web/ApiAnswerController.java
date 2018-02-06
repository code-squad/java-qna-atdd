package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.util.UriCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@PathVariable long questionId, @LoginUser User loginUser, @Valid @RequestBody AnswerDto answerDto) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerDto.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(UriCreator.createUri(answer));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{answerId}")
    public Answer get(@PathVariable long questionId, @PathVariable long answerId) {
        Question question = getQuestionById(questionId);
        return  question.getAnswer(answerId);
    }

    @PutMapping("{answerId}")
    public void update(@PathVariable long questionId, @LoginUser User loginUser, @PathVariable long answerId, @Valid @RequestBody AnswerDto update) {
        qnaService.updateAnswer(getQuestionById(questionId), loginUser, answerId, update);

    }

    private Question getQuestionById(@PathVariable long questionId) {
        Question question = qnaService.findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException(questionId +"가 없습니다.");
        }
        return question;
    }

    @DeleteMapping("{answerId}")
    public void delete(@PathVariable long questionId, @LoginUser User loginUser, @PathVariable long answerId) {
        Question question = getQuestionById(questionId);
        qnaService.deleteAnswer(loginUser, answerId);
    }

}