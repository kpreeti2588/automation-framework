package org.iomedia.galen.tests;

import org.iomedia.framework.Driver;
import org.testng.annotations.Test;
import cucumber.api.CucumberOptions;
	
@CucumberOptions(plugin = "json:target/cucumber-report-feature-composite.json", format = "pretty", features = "features/userJourneys.feature", glue = {"org.iomedia.galen.steps"}, monochrome = true, strict = true)	
public class TempPassword extends Driver {

	@Test(groups={"smoke","regression","user_journey","criticalbusiness","resetpassword"}, priority = 1)
	public void verifyResetPassword() throws Throwable {
	   runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness","resetpassword"}, priority = 2)
	public void verifyUserLoginafterResetPasswordRequest() throws Throwable {
	   runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness","resetpassword"}, priority = 3)
	public void verifyResetPasswordExpireafterChangingPassword() throws Throwable {
	   runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness","resetpassword"}, priority = 4)
	public void claimTicketafterResetingPassword() throws Throwable {
	   runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "takescreenshot"}, priority = 5)
	public void takeScreenshotbeforeDeployment() throws Throwable {
	   runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness"}, priority = 6)
	public void compareScreenshotafterDeployment() throws Throwable {
	   runScenario(Dictionary.get("SCENARIO"));
	}
}
