package com.practice.controller;

import com.practice.entity.User;
import com.practice.helper.CommonMethods;
import com.practice.helper.Message;
import com.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class SignUpController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart Contact Management System");
        model.addAttribute("user", new User());
        return "signup";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               @RequestParam("profile") MultipartFile file,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model,
                               HttpSession session) {
        user.setEnabled(true);
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            if (!agreement) {
                System.out.println("You have not agreed T&C");
                throw new Exception("You have not agreed T&C");
            }
            if (result.hasErrors()) {
                System.out.println("Errors " + result.toString());
                model.addAttribute("user", user);
                return "signup";
            }
            if (file.isEmpty()) {
                System.out.println("Uploaded File is Empty");
                user.setImageUrl("default.png");
            } else {
                System.out.println("File Original Name " + file.getOriginalFilename() + "_" + CommonMethods.getConvertedDate());
                String imageName = CommonMethods.getImageName(file);
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + imageName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                user.setImageUrl(imageName);
                System.out.println("File is uploaded");
            }
            userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully registered", "alert-success"));
            return "redirect:/signup";
        } catch (Exception ex) {
            System.out.println("Something went wrong!");
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong! " + ex.getMessage(), "alert-danger"));
            return "signup";
        }

    }
}
