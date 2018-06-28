package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser, Model model) {
        model.addAttribute("user", loginUser);
        return "/qna/form";
    }

    @PostMapping()
    public String create(@LoginUser User loginUser, String writer, QuestionDto questionDto) {
        // TODO question title, contens가 비었을 때 처리 로직
        qnaService.create(loginUser, questionDto);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        qnaService.findById(id).ifPresent(question ->
                model.addAttribute("question", question)
        );
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginedUser, @PathVariable Long id, Model model) {
        qnaService.findById(id).ifPresent(question -> model.addAttribute("question", question));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginedUser, @PathVariable Long id, QuestionDto questionDto) {
        qnaService.update(loginedUser, id, questionDto);
        return String.format("redirect:/questions/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginedUser, @PathVariable Long id) {
        try {
            qnaService.delete(loginedUser, id);
        } catch (CannotDeleteException e) {
            e.printStackTrace();
        }
        return "redirect:/questions";
    }
}
