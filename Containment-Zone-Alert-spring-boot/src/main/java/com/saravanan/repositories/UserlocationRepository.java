package com.saravanan.repositories;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.saravanan.models.UserLocation;

@Repository
public interface UserlocationRepository extends JpaRepository<UserLocation,Long> {

	String insertPointQuery = "update user_location SET location = ST_GEOMFROMTEXT(:point, 4326) where id=:id";

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = insertPointQuery)
	int updateLocation(@Param("point") String point,@Param("id") Long id);
}
