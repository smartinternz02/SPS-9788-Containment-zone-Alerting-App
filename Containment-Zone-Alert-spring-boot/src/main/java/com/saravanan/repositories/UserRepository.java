package com.saravanan.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.saravanan.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   
	public Optional<User> findByEmail(String email);
	
	@Query(value="select id from user where email=:email",nativeQuery = true)
	public long findIdByEmail(@Param("email") String email);
}
