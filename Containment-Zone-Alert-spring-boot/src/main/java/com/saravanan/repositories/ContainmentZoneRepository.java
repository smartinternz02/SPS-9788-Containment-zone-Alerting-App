package com.saravanan.repositories;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.saravanan.models.ContainmentAreas;

@Repository
public interface ContainmentZoneRepository extends JpaRepository<ContainmentAreas, Long> {

	String insertPointQuery = "update containment_areas SET location = ST_GEOMFROMTEXT(:location, 4326) where c_id=:c_id";

//	@Modifying
//	@Transactional
//	@Query(nativeQuery = true, value = insertPointQuery)
//	int saveLocation(@Param("location") String location,@Param("c_id") Long c_id);
	
	
	@Query(value="select zone from containment_areas where latitude=:lat and longitude=:lng",nativeQuery = true)
	public Optional<ContainmentAreas> findCZoneWithLatLng(double lat,double lng);
	
	@Query(value="select count(*) from user_location as ul join containment_areas as ca where ca.c_id=:cId and st_within(ul.location,ca.boundaries) ",nativeQuery = true )
	public int findUsersCountWithinBoundary(@Param("cId") long cId);
	
	@Query(value="select ca.address from user_location as ul join containment_areas as ca where ul.id=:uId and st_within(ul.location,ca.boundaries) limit 1",nativeQuery = true)
	public String[] findCZoneWhereUserIs(@Param("uId") long uId);
}
