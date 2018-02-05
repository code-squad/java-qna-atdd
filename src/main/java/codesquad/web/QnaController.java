package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QnaController {
    private static final Logger logger = LoggerFactory.getLogger(QnaController.class);

    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String createForm(@LoginUser User user) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User user, String title, String contents) {
        qnaService.createQuestion(user, new Question(title, contents));
        logger.debug("Question created.");
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Question question = qnaService.findQuestionById(id);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable long id, @LoginUser User user, String title, String contents) {
        Question question = new Question(title, contents);

        qnaService.update(user, id, question);

        return String.format("redirect:/questions/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable long id, @LoginUser User user) throws CannotDeleteException {
        qnaService.deleteQuestion(user, id);
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable long id, @LoginUser User user, Model model) {
        Question question = qnaService.findQuestionById(id);
        if (!question.isOwner(user)) {
            throw new UnAuthorizedException("자신의 글만 수정할 수 있습니다.");
        }

        model.addAttribute("question", question);
        return "/qna/updateForm";
    }
}
