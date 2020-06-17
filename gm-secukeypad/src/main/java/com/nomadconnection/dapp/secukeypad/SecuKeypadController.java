package com.nomadconnection.dapp.secukeypad;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecuKeypadController {

	@RequestMapping("/index")
	public String home() {
		return "pluginfree/examples/index";
	}

	@RequestMapping("/pluginfree/examples")
	public String pluginfree() {
		return "pluginfree/examples/index";
	}

	@RequestMapping("/pluginfree/decrypt")
	public String decrypt() {
		return "pluginfree/examples/decrypt";
	}
}