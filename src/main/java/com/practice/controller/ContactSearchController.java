package com.practice.controller;

import com.practice.entity.Contact;
import com.practice.entity.User;
import com.practice.helper.CommonMethods;
import com.practice.repository.ContactRepository;
import com.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class ContactSearchController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/search/{query}")
    public ResponseEntity<?> searchContacts(@PathVariable("query") String query, Principal principal) {
        User user = getUserData(principal.getName());
        List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }

    private User getUserData(String name) {
        return userRepository.getUserByUserName(name);
    }
}
