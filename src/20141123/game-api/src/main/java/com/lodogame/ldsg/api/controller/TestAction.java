package com.lodogame.ldsg.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestAction {
	@RequestMapping("/test.do")
	public ModelAndView test() {
		return new ModelAndView("/jsp/test.jsp"); 
	}
}
