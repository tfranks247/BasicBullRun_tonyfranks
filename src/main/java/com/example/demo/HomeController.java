package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;


    @RequestMapping("/")
    public String listMessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }
    @PostMapping("/searchlist")
    public String search(Model model, @RequestParam("search") String search) {
        model.addAttribute("messages", messageRepository.findByContentContainingIgnoreCaseOrDateContainingIgnoreCaseOrSentByContainingIgnoreCase(search, search, search));
        return "searchlist";
    }

    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm( @ModelAttribute Message message1, @Valid  Message message, BindingResult result,
                              @RequestParam("file") MultipartFile file
                              ) {
        if (result.hasErrors()) {
            return "messageform";
        }
        if (file.isEmpty() && message1.getImage() == null) {
            return "redirect:/add";
        }
        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                message.setImage(uploadResult.get("url").toString());
                messageRepository.save(message);

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/add";
            }
        }
        else
        messageRepository.save(message1);
            return "redirect:/";
        }

    @RequestMapping("/list/{id}")
    public String showMessage(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("message", messageRepository.findById(id).get());
                return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String delmessage(@PathVariable("id") long id) {
        messageRepository.deleteById(id);
        return "redirect:/";
    }
}
