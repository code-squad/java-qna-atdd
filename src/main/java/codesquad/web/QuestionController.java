package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    public String create(@LoginUser User loginUser, QuestionDto target) throws UnAuthenticationException {
        if(loginUser.isGuestUser()) {
            throw new UnAuthenticationException();
        }

        qnaService.create(loginUser, new Question(target.getTitle(), target.getContents()));
        return "redirect:/questions";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id);

        model.addAttribute("question", question);

        log.debug("question : {}", question);
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String update(@LoginUser User loginUser, @PathVariable long id, Model model) throws UnAuthenticationException {
        Question question = qnaService.findById(id);

        if ( ! question.isOwner(loginUser)) {
            throw new UnAuthenticationException();
        }

        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto question) throws UnAuthenticationException {
        qnaService.update(loginUser, id, new Question(question));
        return "redirect:/questions";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions";
    }
}
