package org.iomedia.galen.tests;

import org.iomedia.framework.Driver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import cucumber.api.CucumberOptions;

import org.iomedia.galen.pages.*;

@CucumberOptions(plugin = "json:target/cucumber-report-feature-composite.json", format = "pretty", features = "features/prodsanity.feature", glue = {"org.iomedia.galen.steps"}, monochrome = true, strict = true)
public class Homescreen extends Driver{
	
	private Homepage homepage; 
	
	@BeforeMethod(alwaysRun=true)
	public void init(){
		homepage = new Homepage(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
	}

	@Test(groups={"smoke", "miscUi","regression","prod"}, dataProvider="devices", priority = 1)
	public void verifyHomePage(TestDevice device) throws Exception{
		load("/");
		Assert.assertTrue(homepage.isHomepageDisplayed(device), "Verify homepage is displayed");
		checkLayout(Dictionary.get("SPEC_FILE_NAME"), device.getTags());
	}
	
	@Test(groups={"smoke","regression", "prod"}, priority = 2)
	public void verifyPrivacyLink() throws Throwable {
		runScenario(Dictionary.get("SCENARIO"));
	}
}