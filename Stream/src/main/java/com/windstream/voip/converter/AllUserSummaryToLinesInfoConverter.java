package com.windstream.voip.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;

import com.ssf.allusersummary.GroupUsersSummaryDTO;
import com.ssf.allusersummary.UserSummaryDTO;
import com.windstream.voip.model.common.EntGroupLines;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AllUserSummaryToLinesInfoConverter implements Converter<List<GroupUsersSummaryDTO>, List<EntGroupLines>>{

	private String enterpriseId;

	public AllUserSummaryToLinesInfoConverter(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	@Override
	public List<EntGroupLines> convert(List<GroupUsersSummaryDTO> groupUserSummaryList) {

		List<EntGroupLines> lineDetailsList = groupUserSummaryList.stream()
				.map(groupUserSummary -> 
				 new EntGroupLines(enterpriseId, groupUserSummary.getGroupId(), 
									 groupUserSummary.getUsersList()
									 	.getUsers().stream()
												.map(user-> {
													return extractPhoneNumber(user);
												})
										.filter(phone-> phone != null)
										.collect(Collectors.toList())))
				.collect(Collectors.toList());
		return lineDetailsList;
	}



	/**
	 * Check the userId first if it is a phone number then pick it else select phone number column. (This is to just handle the issue in case phone number field is blank.)
	 * @param usersSummaryDTO
	 * @return
	 */
	private String extractPhoneNumber(UserSummaryDTO usersSummaryDTO) { 
		String phoneNumber = "";
		boolean isSelectPhoneNumber = false;
		try {
			phoneNumber = String.valueOf(Long.parseLong(usersSummaryDTO.getUserID()));
		}catch (NumberFormatException e) {
			log.error("Invalid phone number:"+ usersSummaryDTO.getUserID());
			isSelectPhoneNumber = true;
		}

		if(isSelectPhoneNumber) {
			phoneNumber = usersSummaryDTO.getPhoneNumber().contains("-")?usersSummaryDTO.getPhoneNumber().split("-")[1]:usersSummaryDTO.getPhoneNumber();;
		}
		return phoneNumber;
	}


}
