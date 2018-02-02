package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {

    @Autowired
    private QnaService answerService;

    @PostMapping
    public String addAnswer(@LoginUser User loginUser, @PathVariable Long questionId, String contents, Model model) {
        Answer answer = answerService.addAnswer(loginUser, questionId, contents);
        model.addAttribute("answer", answer);
        return "/qna/show";
    }

    @PutMapping("/{answerId}/form")
    public String update(@LoginUser User loginUser, @PathVariable Long answerId, String contents, Model model) throws IllegalAccessException {
        Answer answer = answerService.updateAnswer(loginUser, answerId, contents);
        model.addAttribute("answer", answer);
        return "/qna/show";
    }

    @DeleteMapping("/{answerId}")
    public String delete(@LoginUser User loginUser, @PathVariable Long answerId) throws CannotDeleteException {
        answerService.deleteAnswer(loginUser, answerId);
        return "redirect:/";
    }
}
