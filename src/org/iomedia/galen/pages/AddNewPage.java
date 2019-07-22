package org.iomedia.galen.pages;

import org.iomedia.common.BaseUtil;
import org.iomedia.framework.Driver.HashMapNew;
import org.iomedia.framework.Reporting;
import org.iomedia.framework.WebDriverFactory;
import org.openqa.selenium.By;

public class AddNewPage extends BaseUtil{

	public AddNewPage(WebDriverFactory driverFactory, HashMapNew Dictionary,HashMapNew Environment, Reporting Reporter,org.iomedia.framework.Assert Assert,org.iomedia.framework.SoftAssert SoftAssert) {
		super(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert);
	}
	
	private By homePage = By.xpath("//label[contains(@for,'home-page')]");
	private By contentPage = By.xpath("//label[contains(@for,'content-page')]");
	private By homePageLayout = By.xpath("//label[contains(@for,'view-modes-home')]/div[1]/img");
	private By contentLayout = By.xpath("//label[contains(@for,'view-modes-content')]/div[1]/img");
	private By next= By.xpath(".//*[@id='edit-submit']");
	
	public void clickSelectHomePage(){
		click(homePage, "Home Page");
	}
	
	public void clickSelectContentPage(){
		click(contentPage, "Content Page");
	}
	
	public void selectHomePageLayout(){
		click(homePageLayout, "HomePage Layout");
	}
	
	public void selectContentPageLayout(){
		click(contentLayout, "Content Page Layout");
	}
	
	
	public void clickNext(){
		click(next, "Next Button") ;
	}
}
