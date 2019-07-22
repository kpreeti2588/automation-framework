package org.iomedia.galen.pages;

import org.iomedia.common.BaseUtil;
import org.iomedia.framework.Driver.HashMapNew;
import org.iomedia.framework.Driver.TestDevice;
import org.iomedia.framework.Reporting;
import org.iomedia.framework.WebDriverFactory;
import org.openqa.selenium.By;

public class Hamburger extends BaseUtil {
	
	private String driverType;
	
	public Hamburger(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment, Reporting Reporter, org.iomedia.framework.Assert Assert, org.iomedia.framework.SoftAssert SoftAssert, ThreadLocal<HashMapNew> sTestDetails) {
		super(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
		driverType = driverFactory.getDriverType().get();
	}

//	private By invoiceLink = By.cssSelector("li.expanded a[href*='/invoice']");
//	private By dashboardLink = By.cssSelector("li.expanded a[href*='/dashboard']");
	private By signoutLink = By.cssSelector("div[class='navbar-left'] a[href*='/user/logout']");
	private By mobileHamburger = By.xpath(".//button[starts-with(@class, 'navbar-toggle')]//i[1]");
	private By userFullName = By.cssSelector(".user-fullname a");
//	private By ManageTickets = By.cssSelector("li.expanded a[href*='/tickets']");
	
	org.iomedia.galen.common.Utils utils = new org.iomedia.galen.common.Utils(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);  
	
	public void clickInvoice(){
		utils.navigateTo("/invoice");
	}
	
	public void clickDashboard(){
		utils.navigateTo("/dashboard");
	}
		
	public void clickMobileHamburger() throws InterruptedException{
		click(mobileHamburger, "Mobile hamburger");
		sync(500L);
	}
	
	public boolean waitforLoggedInPage(TestDevice device) {
		if((device != null && (device.getName().trim().equalsIgnoreCase("mini-tablet") || device.getName().trim().equalsIgnoreCase("mobile"))) || (driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS"))) {
			getElementWhenVisible(By.cssSelector(".navbar-brand-logo"), 40);
			utils.getElementWhenPresent(signoutLink, 40);
		}
		else {
			getElementWhenPresent(userFullName, 40);
		}
		
		return true;
	}
	
	public void clickManageTickets(){
		utils.navigateTo("/tickets");
	}
}
