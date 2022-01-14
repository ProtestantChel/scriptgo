package ru.tehworks.scriptgo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tehworks.scriptgo.DAO.TehDAO;
import ru.tehworks.scriptgo.model.DataBaseLite;

/**
 * @author Marat Sadretdinov
 */
@Controller
@RequestMapping("/")
public class MainController {
    private final TehDAO tehDAO;

    public MainController(TehDAO tehDAO) {
        this.tehDAO = tehDAO;
    }

    @GetMapping()
    public String index(@ModelAttribute("dataBaseLite") DataBaseLite dataBaseLite, Model model){
        model.addAttribute("rows", tehDAO.index());
        model.addAttribute("errors", tehDAO.errorList());
        return "index";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") String id){
        tehDAO.delete(id);
        return "redirect:/";
    }

    @PostMapping()
    public String create(@ModelAttribute("dataBaseLite") DataBaseLite dataBaseLite){
        tehDAO.save(dataBaseLite);
        return "redirect:/";
    }

}
