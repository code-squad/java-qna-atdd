package codesquad.web;

import codesquad.exception.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/questions";
    }

    @GetMapping("")
    public String list(Model model, Pageable pageable) {
        //TODO : Pageable 학습
        List<Question> questions = qnaService.findAll(pageable);
        log.debug("qna size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String read(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        log.debug("qna number {}", id);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        //TODO : 최선인가?
        Question question = qnaService.findById(id);
        if(!question.matchUserId(loginUser.getUserId())) {
            return "/user/relogin";
        }
        log.debug("qna updateform");
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updatedQuestion) {
        log.debug("qna update {}", id);
        qnaService.update(loginUser, id, updatedQuestion);
        return "redirect:/questions/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException{
        log.debug("qna delete {}", id);
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}