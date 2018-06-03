package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
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

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Resource
    private QnaService qnaService;

    @GetMapping("/form")
    public String questionForm(@LoginUser User user) {
        logger.debug("Showing question form...");
        return "/qna/form";
    }

    @PutMapping("/create")
    public String create(@LoginUser User user, QuestionDto questionDto) {
        logger.debug("Submitting question...");
        qnaService.create(user, questionDto);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Question question = questionRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        logger.debug("Directing to show...");
        return "/qna/show";
    }

    @GetMapping("/{id}/update")
    public String updateForm(@LoginUser User user, @PathVariable long id, Model model) {
        logger.debug("Directing to update form...");
        Question question = questionRepository.findById(id)
                .orElseThrow(UnAuthenticationException::new);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User user, @PathVariable long id, QuestionDto questionDto) {
        logger.debug("Updating question...");
        qnaService.update(user, id, questionDto);
        return "redirect:/questions/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable long id) {
        logger.debug("Deleting question...");
        qnaService.deleteQuestion(user, id);
        return "redirect:/";
    }
}
