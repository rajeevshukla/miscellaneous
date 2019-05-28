package com.windstream.voip.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.windstream.voip.model.animgmt.UnAssignAniRequest;
import com.windstream.voip.service.impl.ValidationService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UnAssignAniValidator implements Validator{


	@Autowired
	ValidationService validationService;

	@Override
	public boolean supports(Class<?> clazz) {
		return UnAssignAniRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		log.info("Unassing ani validator is called");
		UnAssignAniRequest request = UnAssignAniRequest.class.cast(target);
		// validate if target and sourceUserId are under same busentityId
		//TODO Checking if validation could be applied on v2 api.

	}
}
