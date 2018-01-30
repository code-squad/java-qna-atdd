package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PutMapping("")
    public String create(@LoginUser User loginUser, String title, String contents) {
        qnaService.create(loginUser, new Question(title, contents));
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PostMapping("")
    public String update(@LoginUser User loginUser, QuestionDto questionDto) {
        qnaService.update(loginUser, questionDto);
        return "redirect:/questions/" + questionDto.getId();
    }

    @GetMapping("/{id}")
    public String show(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException cde) {
        }
        return "redirect:/";
    }
}
