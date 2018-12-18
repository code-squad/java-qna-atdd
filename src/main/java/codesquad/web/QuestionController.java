package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question, Model model) {
        question = qnaService.create(loginUser, question);
        model.addAttribute("question", question);
        return "redirect:" + question.generateUrl();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Question question = qnaService.findQuestionById(id);
        model.addAttribute("question", question);
        return "qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        Question question = qnaService.findQuestionById(id);
        if(!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        model.addAttribute("question", question);
        return "qna/updateForm";
    }

    @PutMapping("/{id}")
    public String udpate(@LoginUser User loginUser, @PathVariable Long id, Question updatedQuestion, Model model) {
        Question question = qnaService.updateQuestion(loginUser, id,updatedQuestion);
        model.addAttribute("question", question);
        return "qna/show";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            throw new UnAuthorizedException();
        }
        return "redirect:/";
    }

}
