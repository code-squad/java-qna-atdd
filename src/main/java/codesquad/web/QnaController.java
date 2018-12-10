package codesquad.web;

import codesquad.CannotDeleteException;
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
public class QnaController {
    @Resource(name = "qnaService")
    QnaService qnaService;

    @PostMapping
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser, question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @GetMapping("{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        return "/qna/form";
    }

    @PutMapping("{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updatedQuestion) {
        qnaService.update(loginUser, id, updatedQuestion);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
