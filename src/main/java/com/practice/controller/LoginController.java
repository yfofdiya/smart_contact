package com.practice.controller;

import com.practice.entity.Contact;
import com.practice.entity.Otp;
import com.practice.entity.User;
import com.practice.helper.Message;
import com.practice.repository.OtpRepository;
import com.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login - Smart Contact Management System");
        return "login";
    }

    @RequestMapping("/forgot-password")
    public String openForgotPasswordFormContainingEmailIdFieldOnly(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot_password_form";
    }

    @PostMapping("/generate-otp")
    public String generateOtp(@RequestParam("email") String email, Model model, HttpSession session) {
        // Validate Email
        boolean isValidEmail = validateEmail(email);
        if (!isValidEmail) {
            session.setAttribute("message", new Message("Email is not registered with us", "danger"));
            return "redirect:/forgot-password";
        }

        // Generate OTP
        int generatedOtp = generateNewOtp();

        // Send OTP over Email
//        String subject = "OTP for forgot password";
        String body = "Here is your otp " + generatedOtp;
//        String from = "";
//        String to = email;
//        boolean isEmailSent = emailSendingService.sendEmail(subject, body, from, to);
        session.setAttribute("message", new Message("We have sent OTP to your registered email id", "success"));
        System.out.println(body);

        // Save OTP to Database
        Otp otp = new Otp(generatedOtp, email);
        otpRepository.save(otp);

        model.addAttribute("otp", otp);
        return "verify_otp_form";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@ModelAttribute Otp otp, Model model, HttpSession session) {
        Otp otpDetails = otpRepository.findById(otp.getId()).get();
        if (otpDetails.getOtp() != otp.getOtp()) {
            session.setAttribute("message", new Message("OTP is not matched, Try again", "danger"));
            model.addAttribute("otp", new Otp());
            return "verify_otp_form";
        }
        session.setAttribute("email", otp.getEmail());
        return "reset_password_form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
        User user = userRepository.getUserByUserName((String) session.getAttribute("email"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        session.setAttribute("message", new Message("Your password is reset successfully, You can login with new passowrd", "success"));
        return "reset_password_form";
    }

    private boolean validateEmail(String email) {
        boolean isValidEmail = false;
        User user = userRepository.getUserByUserName(email);
        if (user != null) {
            isValidEmail = true;
        }
        return isValidEmail;
    }

    private int generateNewOtp() {
        int min = 100000;
        int max = 999999;
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt((max - min) + 1) + min;
    }
}
