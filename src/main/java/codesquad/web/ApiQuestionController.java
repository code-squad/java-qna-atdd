package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.helper.ApiResponse;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto question) {
        Question savedQuestion = qnaService.create(loginUser, question.toQuestion());

        return ApiResponse.CREATED(savedQuestion.generateApiUrl());
    }

    @GetMapping("/{questionNo}")
    public QuestionDto show(@PathVariable long questionNo) {
        Question question = qnaService.findById(questionNo);

        return question.toQuestionDto();
    }

    @PutMapping("/{questionNo}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable long questionNo
            , @Valid @RequestBody QuestionDto updatedQuestion) {
        qnaService.update(loginUser, questionNo, updatedQuestion.toQuestion());

        return ApiResponse.OK();
    }

    @DeleteMapping("/{questionNo}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long questionNo) {
        try {
            qnaService.deleteQuestion(loginUser, questionNo);
        } catch (CannotDeleteException e) {
            return ApiResponse.UNAUTHORIZED();
        }
        return ApiResponse.OK();
    }
}
