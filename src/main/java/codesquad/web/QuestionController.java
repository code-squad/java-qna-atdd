package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author sangsik.kim
 */
@Controller
@RequestMapping("/questions")
public class QuestionController {


    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).toQuestionDto());
        return "/qna/show";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, QuestionDto questionDto) {
        qnaService.create(loginUser, questionDto.toQuestion());
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findByIdForUpdate(loginUser, id);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto questionDto) {
        qnaService.update(loginUser, id, questionDto.toQuestion());
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
