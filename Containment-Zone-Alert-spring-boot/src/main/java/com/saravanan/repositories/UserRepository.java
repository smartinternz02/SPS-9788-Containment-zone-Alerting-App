package com.saravanan.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saravanan.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   
	public Optional<User> findByEmail(String email);
}
