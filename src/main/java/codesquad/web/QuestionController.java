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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QnaService questionService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, QuestionDto questionDto, Model model) {
        Question question = new Question(questionDto.getTitle(), questionDto.getContents());
        model.addAttribute("question", questionService.create(loginUser, question));
        return "/qna/show";
    }

    @GetMapping("/{questionId}/form")
    public String updateForm(@PathVariable Long questionId) {
        return "/qna/updateForm";
    }

    @PutMapping("/{questionId}")
    public String update(@LoginUser User loginUser, @PathVariable Long questionId, QuestionDto questionDto, Model model) throws IllegalAccessException {
        Question question = new Question(questionDto.getTitle(), questionDto.getContents());
        model.addAttribute("question", questionService.update(loginUser, questionId, question));
        return "/qna/show";
    }

    @DeleteMapping("/{questionId}")
    public String update(@LoginUser User loginUser, @PathVariable Long questionId) throws CannotDeleteException, IllegalAccessException {
        questionService.deleteQuestion(loginUser, questionId);
        return "redirect:/";
    }

}
