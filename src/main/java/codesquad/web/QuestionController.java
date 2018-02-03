package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
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
    public String questionDetail(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findByQuestionId(loginUser, id));
        return "/qna/updateForm";
    }

    @PostMapping("/")
    public String create(@LoginUser User loginUser, QuestionDto questionDto) {
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        Question created = qnaService.create(loginUser, question);
        return "redirect:/questions/" + created.getId();
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id,  QuestionDto question) {
        qnaService.update(loginUser, id, question);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            return "redirect:/";
        }catch (CannotDeleteException e) {
            return "redirect:/questions/" + id;
        }
    }
}
