package org.iomedia.galen.steps;

import org.iomedia.common.BaseUtil;
import cucumber.api.java.en.Given;

public class UtilSteps {
	BaseUtil base;
	
	public UtilSteps(BaseUtil base) {
		this.base = base;
	}
	
	@Given("^Save (.+) into (.+)$")
	public void save_into(String value, String key) {
		value = (String) base.getGDValue(value);
		base.Dictionary.put(key, value);	
	}
}
