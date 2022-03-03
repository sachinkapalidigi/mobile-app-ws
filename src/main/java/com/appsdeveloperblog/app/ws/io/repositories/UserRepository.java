package com.appsdeveloperblog.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.appsdeveloperblog.app.ws.io.entity.UserEntity;

// extended CrudRepository before
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	// UserEntity findUserByEmail(String email);
	// if you need to implement custom function
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
}	
