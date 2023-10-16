package com.practice.repository;

import com.practice.entity.Contact;
import com.practice.entity.Otp;
import com.practice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
}
