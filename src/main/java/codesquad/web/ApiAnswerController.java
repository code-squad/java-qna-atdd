package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{id}/answers")
    @ResponseBody
    public List<AnswerDto> showAnswer(@LoginUser User loginUser, @PathVariable long id) {

        Question questions = qnaService.findById(id);
        List<AnswerDto> answerList = questions.getAnswersDtoes();

        return answerList;
    }


    @PostMapping("/{questionId}/answers")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, AnswerDto answerDto) {
        try {
            qnaService.addAnswer(loginUser,questionId,new Answer(loginUser,answerDto.getContents()));
            return new ResponseEntity<Void>(getURIHeader(questionId), HttpStatus.CREATED);
        } catch (UnAuthenticationException e) {
            return new ResponseEntity<Void>(getURIHeader(questionId), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{questionId}/answers/{answerID}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable long questionId, AnswerDto answerDto, @PathVariable long answerId) {
        try {
            qnaService.updateAnswer(loginUser,answerId,new Answer(loginUser,answerDto.getContents()));
            return new ResponseEntity<Void>(getURIHeader(questionId), HttpStatus.OK);
        } catch (UnAuthorizedException e) {
            return new ResponseEntity<Void>(getURIHeader(questionId), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{questionId}/answers/{answerID}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId) {
        try {
            qnaService.deleteAnswer(loginUser,answerId);
            return new ResponseEntity<Void>(getURIHeader(questionId,answerId), HttpStatus.OK);
        } catch (UnAuthenticationException e) {
            return new ResponseEntity<Void>(getURIHeader(questionId,answerId), HttpStatus.FORBIDDEN);
        }
    }

    private HttpHeaders getURIHeader(long questionId, long answerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + answerId));
        return headers;
    }

    private HttpHeaders getURIHeader(long questionId ) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers"));
        return headers;
    }
}
