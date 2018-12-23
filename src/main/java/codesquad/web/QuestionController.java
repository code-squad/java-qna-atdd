package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String questionForm(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException, UnAuthenticationException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession httpSession) {
        model.addAttribute("question",
                qnaService.findById(id).orElse(null).applyOwner(HttpSessionUtils.getUserFromSession(httpSession)));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) throws UnAuthenticationException {
        model.addAttribute("question", qnaService.findById(id).orElse(null));
        return "/qna/updateForm";
    }

    @PutMapping("")
    public String update(@LoginUser User loginUser, Long id, Question updatedQuestion) throws UnAuthenticationException {
        Question question = qnaService.update(loginUser, id, updatedQuestion);
        return "redirect:/questions/" + Long.valueOf(question.getId());
    }

}
