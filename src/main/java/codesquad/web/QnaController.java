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

@Controller
@RequestMapping("/questions")
public class QnaController {
    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String createQnaForm() {
        return "qna/form";
    }

    @GetMapping("/{id}")
    public String showQna(@PathVariable long id, Model model) {
        model.addAttribute("question",qnaService.findById(id));
        return "qna/show";
    }

    @PostMapping("")
    public String createQna(@LoginUser User loginUser, QuestionDto questionDto) {
        qnaService.create(loginUser, questionDto.toQuestion());
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        if (!question.isOwner(loginUser)) {
            return "qna/update_failed";
        }
        model.addAttribute("question", qnaService.findById(id));
        return "qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto questionDto) {
        qnaService.update(loginUser, id, questionDto.toQuestion());
        return "redirect:/questions/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            return "redirect:/";
        } catch (CannotDeleteException e) {
            return "qna/update_failed";
        }
    }
}
