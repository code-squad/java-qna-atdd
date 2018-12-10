package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).orElseThrow(UnAuthorizedException::new));
        return "qna/show";
    }

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser) {
        return "qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, String title, String contents) {
        qnaService.create(loginUser, new Question(title, contents));
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        try {
            model.addAttribute("question", qnaService.findById(id)
                    .filter(q -> q.isOwner(loginUser))
                    .orElseThrow(UnAuthenticationException::new));
            return "qna/updateForm";
        } catch (UnAuthenticationException e) {
            return String.format("redirect:/questions/%d", id);
        }
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion) {
        qnaService.update(loginUser, id, updateQuestion);
        return String.format("redirect:/questions/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            return "redirect:/";
        } catch (CannotDeleteException e) {
            return String.format("redirect:/questions/%d", id);
        }
    }

}
