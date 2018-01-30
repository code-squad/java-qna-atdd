package codesquad.web;

import codesquad.CannotManageException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String questionForm() {
        return "qna/form";
    }

    @GetMapping("/{id}")
    public String questionShow(@PathVariable Long id, Model model) throws CannotManageException {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateQuestionForm(@PathVariable Long id, Model model) throws CannotManageException {
        model.addAttribute("question", qnaService.findById(id));
        return "qna/updateForm";
    }

    @PostMapping
    public String questionSave(@LoginUser User loginUser, String title, String contents) {
        qnaService.create(loginUser, new Question(title, contents));
        return "redirect:/";
    }

    @PutMapping("/{id}")
    public String questionUpdate(@PathVariable Long id, @LoginUser User loginUser, String title, String contents, Model model) {
        try {
            qnaService.update(loginUser, id, new Question(title, contents));
        } catch (CannotManageException e) {
            model.addAttribute("message", e.getMessage());
            return "/qna/qna_error";
        }
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String questionDelete(@PathVariable Long id, @LoginUser User loginUser, Model model) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotManageException e) {
            model.addAttribute("message", e.getMessage());
            return "/qna/qna_error";
        }
        return "redirect:/";
    }
}
