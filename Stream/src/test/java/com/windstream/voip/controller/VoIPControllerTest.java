package com.windstream.voip.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.windstream.voip.config.VoIPProperties;
import com.windstream.voip.service.VoIPService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = VoIPController.class)
public class VoIPControllerTest {

	@Autowired
	MockMvc mockMvc;
	
    @MockBean
    VoIPService voipService;
    
    @MockBean
    VoIPProperties properties;
    
    
    
   /* @Before
    public void setup(WebApplicationContext wac) { 
    	this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }*/
    

	@Test
	public void testSsoUrl() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/voip/ssoUrl/{userId}","n9952335")
										.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder)
				.andExpect(status().isOk()).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

}
