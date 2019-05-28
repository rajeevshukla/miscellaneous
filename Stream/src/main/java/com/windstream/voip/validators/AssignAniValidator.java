package com.windstream.voip.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.windstream.voip.model.animgmt.AssignAniRequest;
import com.windstream.voip.service.impl.ValidationService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AssignAniValidator implements Validator{

	 @Autowired
	 ValidationService validationService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return AssignAniRequest.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		 // validate if give ani's  enterprise and groups are valid
		log.info("Assign ani validator is called");
		AssignAniRequest request = AssignAniRequest.class.cast(target);
		
		if(!validationService.checkIfAnisAreValid(request.getLoggedInUserId(), request.getAnis())) { 
			errors.rejectValue("anis", "anis.ani","Passed ANI does not belongs to given enterprise/group or user.");
		}
		
	}
}
