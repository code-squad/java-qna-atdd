package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QnaService questionService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        log.debug("loginUser={}", loginUser);
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, String title, String contents) {
        Question question = new Question(title, contents);
        log.debug("loginUser={}, question={}", loginUser, question);

        questionService.create(loginUser, question);
        return "redirect:/questions";
    }

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("questions", questionService.findAll());
        return "/qna/show";
    }

    @GetMapping("/{id}")
    public String getOne(Model model, @PathVariable long id) {
        Question question = questionService.findById(id);
        model.addAttribute("questions", Collections.singletonList(question));
        model.addAttribute("answersCount", question == null ? 0 : question.getAnswersCount());
        return "/qna/show";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, Model model, @PathVariable long id) throws CannotDeleteException {
        questionService.deleteQuestion(loginUser, id);

        model.addAttribute("questions", questionService.findAll());
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, Model model, @PathVariable long id) {
        model.addAttribute("question", Collections.singletonList(questionService.findById(id)));
        model.addAttribute("id", id);
        return "/qna/updateForm";
    }

    // [NEED TO STUDY] PUT method ? why not return at test?
    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, Model model, @PathVariable long id, QuestionDto questionDto) {
        Question updatedQuestion = new Question(questionDto.getTitle(), questionDto.getContents());
        questionService.update(loginUser, id, updatedQuestion);

        model.addAttribute("questions", Collections.singletonList(questionService.findById(id)));
        return "/qna/show";
    }
}
