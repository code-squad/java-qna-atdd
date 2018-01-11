package codesquad.web;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("")
    public QuestionsDto list(@RequestParam(defaultValue = DEFAULT_PAGE + "") int page,
            @RequestParam(defaultValue = DEFAULT_SIZE + "") int size) {
        List<Question> questions = qnaService.findAll(new PageRequest(page, size));
        return new QuestionsDto(questions.stream().map(q -> q._toConvertQuestionDto()).collect(Collectors.toList()));
    }

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto question) {
        Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedQuestion.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public QuestionDto show(@PathVariable long id) {
        return qnaService.findById(id)._toConvertQuestionDto();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CannotDeleteException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
