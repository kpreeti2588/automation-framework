package org.iomedia.galen.tests;

import org.iomedia.framework.Driver;
import org.testng.annotations.Test;
import cucumber.api.CucumberOptions;

@CucumberOptions(plugin = "json:target/cucumber-report-feature-composite.json", format = "pretty", features = {"features/userJourneys.feature"}, glue = {"org.iomedia.galen.steps"}, monochrome = true, strict = true)	
public class UserJourneys_1 extends Driver{
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "sendFunctional", "nonstp"}, priority = 1)
	public void verifyReclaimTicketAfterEmailIdChangeInCAM() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "sendFunctional", "nonstp"}, priority = 2)
	public void verifySendTicketafterEmailChange() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "sellFunctional", "nonstp"}, priority = 3)
	public void verifySellTicketafterEmailChange() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "sellFunctional", "nonstp"}, priority = 4)
	public void verifyEditPostingSellerCredit() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "sellFunctional", "nonstp"}, priority = 5)
	public void verifyEditPostingBankAccount() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "sellFunctional", "nonstp"}, priority = 6)
	public void verifyCancelPostingafterEmailChange() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "ViewFunctional", "nonstp"}, priority = 7)
	public void verifyRenderBarcodeafterEmailChange() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
	
	@Test(groups={"smoke","regression","user_journey","criticalbusiness", "sso", "prod", "ticketsFunctional", "sendFunctional", "nonstp"}, priority = 8)
	public void verifyClaimTicketafterEmailChange() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
}
