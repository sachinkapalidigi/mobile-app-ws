package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.security.SecurityConstants;
import com.appsdeveloperblog.app.ws.ui.model.response.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
	
	@Autowired
	UserService userService;

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserRest> getUsers(@RequestParam(value="page", defaultValue = "1") int page, @RequestParam(value = "limit", defaultValue = "25") int limit)
	{
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		for(UserDto userDto: users){
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		return returnValue;
	}

	// @GetMapping(path="/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }) // for both
	// @GetMapping(path="/{id}", produces = MediaType.APPLICATION_XML_VALUE) // default xml format
	// /users/:id
	@GetMapping(path="/{id}")
	public UserRest getUser(@PathVariable String id)
	{
		UserRest returnValue = new UserRest();

		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);

		return returnValue;
	}

	//	@PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) - this accepts request in xml and json format
	@PostMapping
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception
	{
		UserRest returnValue = new UserRest();

		if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, returnValue);
		
		return returnValue;
	}
	
	@PutMapping(path="/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public UserRest updateUser(@RequestHeader(SecurityConstants.HEADER_STRING) String authHeader, @PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
	{

		String userName = SecurityConstants.getUserIdFromToken(authHeader);
		String userId = userService.getUser(userName).getUserId();
		// Change message later - along with status code
		if(userId != id) throw new UserServiceException(ErrorMessages.AUTHENTICATION_FAILED.name());

		UserRest returnValue = new UserRest();

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	}
	
	@DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel deleteUser(@RequestHeader(SecurityConstants.HEADER_STRING) String authHeader, @PathVariable String id)
	{
		String userName = SecurityConstants.getUserIdFromToken(authHeader);
		String userId = userService.getUser(userName).getUserId();
		// Change message later - along with status code
		if(userId != id) throw new UserServiceException(ErrorMessages.AUTHENTICATION_FAILED.name());

		OperationStatusModel returnValue = new OperationStatusModel();
		userService.deleteUser(id);
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}
}
