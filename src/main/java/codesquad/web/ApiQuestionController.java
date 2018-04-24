package codesquad.web;

import codesquad.domain.CannotDeleteException;
import codesquad.domain.CannotUpdateException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping
    public Iterable<Question> list(@PageableDefault(
                                    size = 15,
                                    sort = "id",
                                    direction = Sort.Direction.DESC) Pageable pageable) {
        return qnaService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Question get(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PostMapping
    public ResponseEntity create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto question) {
        Question created = qnaService.create(loginUser, question);
        URI resourceLocation = URI.create(String.format("/api/questions/%s", created.getId()));

        return ResponseEntity.created(resourceLocation).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@LoginUser User loginUser,
                                 @PathVariable long id, @Valid @RequestBody QuestionDto question)
            throws CannotUpdateException {
        qnaService.update(loginUser, id, question);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@LoginUser User loginUser,
                                         @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return ResponseEntity.noContent().build();
    }
}
