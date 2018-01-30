package codesquad.web;

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
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping("")
    public String writeQuestion(@LoginUser User loginUser, QuestionDto questionDto) {
        log.debug("questionDto : {}", questionDto);
        qnaService.create(loginUser, questionDto.toQuestion());
        return "redirect:/";
    }

    @GetMapping("{id}")
    public String questionDetailView(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @DeleteMapping("{id}")
    public String deleteQuestion(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

    @GetMapping("{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/updateForm";
    }

    @PutMapping("{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable long id, QuestionDto questionDto) {
        log.debug("questionDto : {}", questionDto);
        qnaService.update(loginUser, id, questionDto);
        return "redirect:/";
    }
}
