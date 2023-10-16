package com.practice.controller;

import com.practice.entity.Contact;
import com.practice.entity.User;
import com.practice.helper.CommonMethods;
import com.practice.helper.Message;
import com.practice.repository.ContactRepository;
import com.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    public static final int SIZE = 3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Common Method runs before every @RequestMapping method call
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
//        System.out.println("PRINCIPLE " + principal);
//        System.out.println("Name " + principal.getName());
        User user = getUserData(principal.getName());
//        System.out.println("USER " + user);
        model.addAttribute("user", user);
    }

    // User Dashboard View
    @RequestMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // Open Add Contact Form
    @RequestMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // Add Contact
    @RequestMapping(value = "/process-contact", method = RequestMethod.POST)
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profile") MultipartFile file,
                                 Principal principal,
                                 HttpSession session) {
//        System.out.println("Contact " + contact);
//        System.out.println("PRINCIPLE " + principal);
        try {
            if (file.isEmpty()) {
                System.out.println("Uploaded File is Empty");
                contact.setImage("contact.png");
            } else {
                System.out.println("File Original Name " + file.getOriginalFilename() + "_" + CommonMethods.getConvertedDate());
                String imageName = CommonMethods.getImageName(file);
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + imageName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(imageName);
                System.out.println("File is uploaded");
                session.setAttribute("message", new Message("Successfully added", "success"));
            }
            User user = getUserData(principal.getName());
            contact.setUser(user);
            user.getContacts().add(contact);
//        System.out.println(user);
//        System.out.println(contact);
            userRepository.save(user);
        } catch (Exception ex) {
            ex.getMessage();
            session.setAttribute("message", new Message("Something went wrong", "danger"));
        }
        return "normal/add_contact_form";
    }

    // View All Contacts by User
    @RequestMapping("/view-contacts/{pageNumber}")
    public String viewContacts(@PathVariable int pageNumber,
                               Model model,
                               Principal principal) {
        model.addAttribute("title", "View All Contacts");
        User user = getUserData(principal.getName());
        Pageable pageable = PageRequest.of(pageNumber, SIZE);
        Page<Contact> pagedContacts = contactRepository.findContactsByUser(user.getId(), pageable);
        List<Contact> contacts = pagedContacts.getContent();
        if (contacts.size() != 0) {
            model.addAttribute("contacts", contacts);
            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", pagedContacts.getTotalPages());
        }
        return "normal/view_contacts";
    }

    // Delete Contact by ID
    @GetMapping("/delete-contact/{cId}")
    public String deleteContactById(@PathVariable("cId") int cId,
                                    Model model,
                                    Principal principal,
                                    HttpSession session) {
        Contact contact = contactRepository.findById(cId).get();
        User user = getUserData(principal.getName());
        if (user.getId() == contact.getUser().getId()) {
            contact.setUser(null);
            // Delete file from the folder
            try {
                File f = new ClassPathResource("static/img").getFile();
                File deleteFile = new File(f, contact.getImage());
                deleteFile.delete();
                System.out.println("File is deleted");
            } catch (Exception ex) {
                ex.getMessage();
                session.setAttribute("message", new Message("Something went wrong", "danger"));
            }
            contactRepository.delete(contact);
            session.setAttribute("message", new Message("Deleted successfully", "success"));
        }
        return "redirect:/user/view-contacts/0";
    }

    // Open update contact form
    @PostMapping("/update-contact/{cId}")
    public String openUpdateContactForm(@PathVariable("cId") int cId, Model model) {
        model.addAttribute("title", "Update Contact");
        Contact contact = contactRepository.findById(cId).get();
        model.addAttribute("contact", contact);
        return "normal/update_contact_form";
    }

    // Update Contact Details
    @RequestMapping(value = "/process-update-contact", method = RequestMethod.POST)
    public String processUpdateContact(@ModelAttribute Contact contact,
                                       @RequestParam("profile") MultipartFile file,
                                       Model model,
                                       Principal principal,
                                       HttpSession session) {
        try {
            Contact oldContact = contactRepository.findById(contact.getCId()).get();
            User user = getUserData(principal.getName());
            if (file.isEmpty()) {
                contact.setImage(oldContact.getImage());
            } else {
                File f = new ClassPathResource("static/img").getFile();
                // Update Photo
                String imageName = CommonMethods.getImageName(file);
                Path path = Paths.get(f.getAbsolutePath() + File.separator + imageName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(imageName);
                System.out.println("File is updated");
                // Delete Old Photo
                File deleteFile = new File(f, oldContact.getImage());
                deleteFile.delete();
                System.out.println("File is deleted");
            }
            contact.setUser(user);
            contactRepository.save(contact);
            session.setAttribute("message", new Message("Updated successfully", "success"));
        } catch (Exception ex) {
            ex.getMessage();
            session.setAttribute("message", new Message("Something went wrong while updating contact", "danger"));
        }
        return "redirect:/user/view-contacts/0";
    }

    // View Particular Contact Details
    @RequestMapping("/{cId}/contact")
    public String viewContactById(@PathVariable("cId") int cId, Model model, Principal principal) {
        model.addAttribute("title", "View Contact");
        Contact contact = contactRepository.findById(cId).get();
        User user = getUserData(principal.getName());
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        }
        return "normal/view_contact_by_id";
    }

    // Profile
    @GetMapping("/profile")
    public String myProfile(Model model) {
        model.addAttribute("title", "Profile");
        return "normal/profile";
    }

    // Settings Tab
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("title", "Settings");
        return "normal/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 HttpSession session) {
        User user = getUserData(principal.getName());
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            session.setAttribute("message", new Message("You entered wrong old password", "danger"));
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            session.setAttribute("message", new Message("Password changes", "success"));
        }
        return "redirect:/user/settings";
    }

    private User getUserData(String name) {
        return userRepository.getUserByUserName(name);
    }

}
