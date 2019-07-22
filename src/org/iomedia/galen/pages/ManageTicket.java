package org.iomedia.galen.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import org.iomedia.common.BaseUtil;
import org.iomedia.framework.Driver.HashMapNew;
import org.iomedia.framework.Driver.TestDevice;
import org.iomedia.framework.Reporting;
import org.iomedia.framework.WebDriverFactory;
import org.iomedia.galen.common.ManageticketsAPI;
import org.iomedia.galen.common.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.SkipException;

public class ManageTicket extends BaseUtil {

	private String driverType;
	
	public ManageTicket(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment, Reporting Reporter, org.iomedia.framework.Assert Assert, org.iomedia.framework.SoftAssert SoftAssert, ThreadLocal<HashMapNew> sTestDetails) {
		super(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
		driverType = driverFactory.getDriverType().get();
	}
	
	private By bundleParking = By.cssSelector("div[class*='manage-tickets-sendDonateButtons'] button,[class*='manage-tickets-formSubmitButtons'] button:nth-child(1)");
	private By popupButton = By.cssSelector("div[class*='manage-tickets-sendDonateButtons'] button,[class*='manage-tickets-formSubmitButtons'] button[class*='theme-neutral']");
	private By claimButton = By.cssSelector("div[class*='manage-tickets-sendDonateButtons'] button,[class*='manage-tickets-formSubmitButtons'] button");
	private By confirm = By.xpath("//section[contains(@class,'theme-body')]//button[text()='CONFIRM']");
	private By reclaimDone= By.xpath("//section[contains(@class,'theme-body')]//button[text()='GO TO TICKETS']");
	private By successText= By.xpath("//section[contains(@class,'theme-body')]//h5");
	private By popUpEventName = By.xpath("//section[contains(@class,'theme-body')]//div[contains(@class,'ticketInfo')]//h5");
	private By mobilepopUpEventName = By.cssSelector("div[class*='ticket-team']");
	private By popUpSeatDetail = By.xpath(".//section[contains(@class,'theme-body')]//div[contains(@class,'ticketDetails')] | //section[contains(@class,'theme-body')]//div[contains(@class,'ticketInfo')]//p");
	private By seatExpiryDate = By.xpath("//section[contains(@class,'theme-body')]//div[contains(@class,'charityInfo')]//span");
	private By claimLink = By.xpath("//*[contains(@class,'claimLinkContainer')]/div[2]");
	private By eventClaimLink = By.xpath("//div[text()='Event Link']/../..//*[contains(@class,'claimLinkContainer')]/div[2]/span");
	private By parkingClaimLink = By.xpath("//div[text()='Parking Link']/../..//*[contains(@class,'claimLinkContainer')]/div[2]/span");
	private By reclaim = By.xpath("//section[contains(@class,'theme-body')]//form//button[text()='RECLAIM']");
	private By sendUser= By.xpath("//section[contains(@class,'theme-body')]//div[contains(@class,'charityInfo')]//h5");
	private By charitySelect = By.xpath("//section[contains(@class,'theme-body')]//div[contains(@class,'theme-dropdown')]");
	private By firstCharity = By.xpath(".//section[contains(@class,'theme-body')]//div[contains(@class,'theme-dropdown')]/ul/li");
	private By earningPrice = By.xpath("//section[contains(@class,'theme-body')]//input[contains(@name,'seatprice')]");
	private By depositAccount= By.xpath("//*[contains(@class,'theme-dropdown')]//input");
	private By bankAccount = By.xpath("//*[contains(@class,'theme-dropdown')]//ul/li[1]");
	private By sellerCredit = By.xpath("//*[contains(@class,'theme-dropdown')]//ul/li[2]");
	private By editBankAccount = By.xpath("//*[contains(@class,'sell-endingIn')]//button");
	private By editSellerProfile = By.xpath("//*[contains(@class,'sell-sellerAddress')]//button");
	private By sellerFirstName = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//input[@name='first_name']");
	private By sellerLastName= By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//input[@name='last_name']");
	private By sellerAddress1 = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//input[@name='address[line_1]']");
	private By sellerAddress2 = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//input[@name='address[line_2]']");
	private By sellerCountry = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//*[@name='address[country]']");
	private By sellerCity = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//*[@name='address[city]']");
	private By sellerState = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//*[@id='addressState']");
	private By sellerZip = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//*[@name='address[postal_code]']");
	private By sellerMobile = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//*[@name='address[mobile_phone]']");
	private By sellerHomePhone = By.xpath("//*[contains(@class,'sell-sellTicketsProfileEdit')]//*[@name='address[home_phone]']");
	private By sellerAddress = By.xpath("//*[contains(@class,'sell-sellerAddress')]//p");
	private By bankDetails = By.xpath("//*[contains(@class,'manage-tickets-endingIn')]//p | //*[contains(@class,'sell-endingIn')]//p");
	private By bankAccountType = By.xpath("//*[contains(@class,'sell-bankEditProfile')]//input[contains(@name,'account_type')]");
//	private By checkingBankAccout = By.xpath("//*[contains(@class,'theme-dropdown')]/ul/li[text()='Checking']");
//	private By savingBankAccount = By.xpath("//*[contains(@class,'theme-dropdown')]/ul/li[text()='Saving']");
	private By bankRoutingNumber = By.xpath("//*[contains(@class,'sell-bankEditProfile')]//input[contains(@name,'routing_number')]");
	private By bankAccountNumber = By.xpath("//*[contains(@class,'sell-bankEditProfile')]//input[contains(@name,'account_number')]");
	private By bankConfirmAccount = By.xpath("//*[contains(@class,'sell-bankEditProfile')]//input[contains(@name,'confirm_account_number')]");
	
	private By manageTicketsList = By.cssSelector(".react-root-event ul[class*='events-eventList'] li[class*='events-event'] a");
	private By ticketsListing = By.cssSelector(".react-root-event  ul[class*='ticketList'] li[class*='ticket-event'] div[class*='ticket-eventInner']");
	private By ticketListingMobile = By.cssSelector(".react-root-event  div[class*='ticket-eventListContainerMobile'] section > div[class*='slick']");
	private By downloadTickets = By.xpath(".//button[contains(.,'Print')]");
	private By downloadTextPopUp = By.xpath(".//h6[contains(text(),'Print Tickets')]");
	private By sendTicket = By.xpath("//*[contains(@class,'ticket-subHeader')]//button[contains(.,'Send')]");
	private By donateTicket = By.xpath(".//ul[contains(@class, 'theme-menuInner')]//li/span[text()='Donate']");
	private By moreButton = By.xpath(".//button[contains(.,'more_horiz')]");
	private By moreButtonMobile = By.xpath(".//div[contains(@class, 'ticketButtonRowMobile')]//button[contains(.,'more_horiz')]");
	private By sendTicketMobile = By.xpath(".//div[contains(@class, 'ticketButtonRowMobile')]//button[contains(., 'Send')]");
	private By donateTicketMobile = By.xpath(".//div[contains(@class, 'ticketButtonRowMobile')]//ul[contains(@class, 'theme-menuInner')]//li/span[text()='Donate']");
	private By sendTextPopUp = By.xpath(".//h6[contains(text(),'Send Tickets')]");
	private By donateTextPopUp = By.xpath(".//h6[contains(text(),'Donate Tickets')]");
	private By sellTicket = By.xpath(".//button[contains(.,'Sell')]");
	private By sellTicketMobile = By.xpath(".//div[contains(@class, 'ticketButtonRowMobile')]//button[contains(., 'Sell')]");
	private By sellTicketPopUp = By.xpath(".//h6[contains(text(),'Sell Tickets')]");
	private By firstManageTickets = By.cssSelector(".react-root-event ul[class*='events-eventList'] li[class*='first odd']");
	
	private By ticketsDetail = By.xpath(".//div[contains(@class, 'react-root-event')]//ul//li[contains(@class, 'list-item')]//div[contains(@class, 'ticket-ticketDetails')]");
	private By mobiletickets = By.xpath(".//div[contains(@class, 'ticket-eventListContainerMobile')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide') and contains(@class, 'slick-active')] | //div[contains(@class, 'ticket-eventListContainerMobile')]/section/div[contains(@class, 'ticket-event') and not(contains(@class, 'ticket-eventTicketPagger'))]");
	private By noofmobiletickets = By.xpath(".//div[contains(@class, 'ticket-eventListContainerMobile')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide')] | //div[contains(@class, 'ticket-eventListContainerMobile')]/section/div[contains(@class, 'ticket-event') and not(contains(@class, 'ticket-eventTicketPagger'))]");
	private By bundleparkingcheckbox = By.cssSelector("div[class*='bundleParkingCheck'] div");
//	private By parkingslotsCheckbox = By.cssSelector("ul[class*='manage-tickets-parkingSlots'] li label div");
	private By events = By.xpath(".//div[contains(@class, 'react-root-event')]//ul[contains(@class, 'events-eventList')]//li[contains(@class, 'events-event')]//a");
	private By eventNames = By.xpath(".//div[contains(@class, 'react-root-event')]//ul[contains(@class, 'events-eventList')]//li[contains(@class, 'events-event')]//a//h3");
	private By totalTicketsCountText = By.xpath(".//div[contains(@class, 'ticket-totalTicketCounts')]/span[1]");
	private By sentTicketsCountText = By.xpath(".//div[contains(@class, 'ticket-totalTicketCounts')]/span[contains(., 'Sent')]");
	private By listedTicketsCountText = By.xpath(".//div[contains(@class, 'ticket-totalTicketCounts')]/span[contains(., 'Listed')]");
	private By claimedTicketsCountText = By.xpath(".//div[contains(@class, 'ticket-totalTicketCounts')]/span[contains(., 'Claimed')]");
	private By donatedTicketsCountText = By.xpath(".//div[contains(@class, 'ticket-totalTicketCounts')]/span[contains(., 'Donated')]");
	private By sentTickets = By.xpath(".//div[contains(@class, 'react-root-event')]//ul//li[contains(@class, 'list-item')]//div[contains(@class, 'ticket-ticketDetails')]/..//div[contains(@class, 'ticket-ticketImage')]//div[contains(@class, 'ticket-listedOn') and text()='Sent'][1]");
	private By listedTickets = By.xpath(".//div[contains(@class, 'react-root-event')]//ul//li[contains(@class, 'list-item')]//div[contains(@class, 'ticket-ticketDetails')]/..//div[contains(@class, 'ticket-ticketImage')]//div[contains(@class, 'ticket-listedOn') and text()='Listed'][1]");
	private By claimedTickets = By.xpath(".//div[contains(@class, 'react-root-event')]//ul//li[contains(@class, 'list-item')]//div[contains(@class, 'ticket-ticketDetails')]/..//div[contains(@class, 'ticket-ticketImage')]//div[contains(@class, 'ticket-listedOn') and text()='Claimed'][1]");
	private By donatedTickets = By.xpath(".//div[contains(@class, 'react-root-event')]//ul//li[contains(@class, 'list-item')]//div[contains(@class, 'ticket-ticketDetails')]/..//div[contains(@class, 'ticket-ticketImage')]//div[contains(@class, 'ticket-listedOn') and text()='Donated'][1]");
	private By ticketsdetails = By.xpath("//button[contains(@class,'ticket-detailTicket') and text()='TICKET DETAILS']");
	private By terms_condition = By.xpath("//div[contains(@class,'ticket-termsText')]/p");
	private By ticketDetails = By.cssSelector("div[class*='ticket-detail'] h5");
	
	private By scanBarcode = By.cssSelector("div[class*='ticket-barCodeInner'] svg,div[class*='ticket-barcode'] svg");
	private By scanCode = By.xpath("//div[contains(@class,'ticket-barCodeInner')]");
	private By mobileDownloadSectionName = By.xpath("(.//div[contains(@class, 'ticket-barDetails')]//span)[1]/../strong");
	private By mobileDownloadRowName = By.xpath("(.//div[contains(@class, 'ticket-barDetails')]//span)[2]/../strong");
	private By mobileDownloadSeatName = By.xpath("(.//div[contains(@class, 'ticket-barDetails')]//span)[3]/../strong");
	private By barIcon = By.xpath("//div[contains(@class, 'ticket-qrIcon')]//img");
	private By addToAppleWalletIcon = By.cssSelector("div[class*='ticket-addApple'] a");
	private By gateNumber = By.cssSelector("div[class*='ticket-entryFrom']");
	private By BuyTickets = By.xpath("//div[@class='container']//div[2]//button");
	
	private Homepage homepage = new Homepage(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
	private By cancelPosting= By.cssSelector("div[class*='sell-buttonStack'] :nth-child(2) div button");
	private By editPosting = By.cssSelector("div[class*='sell-buttonStack'] :nth-child(1) div button");
	private By search = By.cssSelector("[class*='eventlistSearch'] div div");
	private By searchValue = By.cssSelector("[class*='eventlistSearch'] input");
	private By eventsList = By.cssSelector("ul[class*='events-eventList'] li");
	private By firstEventName = By.cssSelector("ul[class*='events-eventList'] li:nth-child(1) h3");
	private By clickticketlistview = By.cssSelector(".listView #Page-1");
	private By transferTag= By.xpath("//input[@name='transferTag']");
	private By done = By.cssSelector("div[class*='manage-tickets-sendDonateButtons'] button,[class*='manage-tickets-formSubmitButtons'] button:nth-child(2)");
	private By noTicketDetails = By.cssSelector("span[class*='ticket-noDetails']");
	private By lostTicket = By.xpath(".//div[contains(@class,'download-charityInfo')]//label");
	
	public void clickBundleParking(){
		click(bundleParking, "BundleParking");
	}
	
	public void clickTransferDone(){
		click(done, "DONE");
	}
	
	public void typeTransferTag(String textToType) throws Exception {
		type(transferTag,"Transfer Name", textToType, true, By.xpath("//XCUIElementTypeTextField[1]"));
	}
	
	public String getNoTicketDetailsText() {
		return getText(noTicketDetails).trim();
	}
	
	public void clickEventName(String eventName) {
		click(By.xpath(".//h3[contains(@class, 'events-eventName') and text()='" + eventName.trim() + "']"), eventName.trim());
	}
	
	public boolean verifySearchValues(String value){
		boolean has= true;
		List<WebElement> events = getWebElementsList(eventsList);
		for(WebElement event: events){
			if(!event.getText().trim().toLowerCase().contains(value.trim().toLowerCase())){
				has = false;
			}
		}
		return has;
	}
	
	public boolean verifySearchableValue(String value){
		boolean has= false;
		Assert.assertTrue(isManageTicketsListDisplayed() , "Verify manage tickets list is displayed");
		List<WebElement> events = getWebElementsList(eventsList);
		for(WebElement event: events){
			if(event.getText().trim().toLowerCase().contains(value.trim().toLowerCase())){
				has = true;
				break;
			}
		}
		return has;
	}
	
	public String getFirstEventName(){
		return getText(firstEventName);
	}
	
	public void clickSearch(){
		click(search, "Search");
	}
	
	public void typeSearchValue(String searchText) throws Exception{
		type(searchValue, "SearchBox", searchText, true, By.xpath("//XCUIElementTypeTextField[1]"));
	}
	
	public void clickticketlistview(){
		getElementWhenVisible(clickticketlistview).click();
	}
	
	public boolean isViewButtonVisible(){
		return checkIfElementPresent(downloadTickets, 1);
	}
	
	public boolean isScanBarcodeEnabled() {
		boolean enabled = true;
		try{ 
			if(getAttribute(By.cssSelector("div[class*='ticket-barCodeContainer'] > div div:nth-child(1)"), "class").contains("grayOut"))
				enabled= false;
			else
				enabled = true;
		}
		catch (NoSuchElementException e){
			enabled = true;
		}
		return enabled;
	}
		
	
	public boolean isSellButtonVisible(TestDevice device){
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone"))) {
			return checkIfElementPresent(sellTicketMobile, 1);
		} else
			return checkIfElementPresent(sellTicket, 1);
	}
	
	public boolean isSendButtonVisible(TestDevice device){
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone")))
			return checkIfElementPresent(sendTicketMobile, 1);
		else
			return checkIfElementPresent(sendTicket, 1);
	}
	
	public boolean isDonateButtonVisible(TestDevice device){
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone"))) {
			scrollingToBottomofAPage();
			By moreButton = By.xpath(".//android.widget.Button[contains(@content-desc, 'more_horiz')]");
			click(moreButtonMobile, "More", moreButton, null, "More");
			return checkIfElementPresent(donateTicketMobile, 1);
		} else {
			click(moreButton, "More");
			return checkIfElementPresent(donateTicket, 1);
		}
	}
	
	public boolean isScanBarcodeVisible() {
		return checkIfElementPresent(scanBarcode, 1);
	}
		
	public boolean isViewButtonEnable(){
		String value = getAttribute(downloadTickets, "disabled");
		return value == null ? true : value.trim().equalsIgnoreCase("true") ? false : true;
	}
	
	public boolean isSendButtonEnable(TestDevice device) {
		String value;
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone")))
			value = getAttribute(sendTicketMobile, "disabled");
		else
			value = getAttribute(sendTicket, "disabled");
		return value == null ? true : value.trim().equalsIgnoreCase("true") ? false : true;
	}
	
	public boolean isSellButtonEnable(TestDevice device) {
		String value;
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone")))
			value = getAttribute(sellTicketMobile, "disabled");
		else
			value = getAttribute(sellTicket, "disabled");
		return value == null ? true : value.trim().equalsIgnoreCase("true") ? false : true;
	}
	
	public boolean isDonateButtonEnable(TestDevice device) {
		boolean value;
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone"))) {
			scrollingToBottomofAPage();
			By moreButton = By.xpath(".//android.widget.Button[contains(@content-desc, 'more_horiz')]");
			click(moreButtonMobile, "More", moreButton, null, "More");
			String xpath = getXpath(donateTicketMobile, "Donate", "", -1) + "/..";
			value = getAttribute(By.xpath(xpath), "class").contains("disabled");
		} else {
			click(moreButton, "More");
			String xpath = getXpath(donateTicket, "Donate", "", -1) + "/..";
			value = getAttribute(By.xpath(xpath), "class").contains("disabled");
		}
		return !value;
	}
	
	public void clickEditPosting(){
		click(editPosting, "Edit Posting");
	}
	
	public String getEarningPrice(){
		return getAttribute(earningPrice,"value");
	}
	
	public void clickCancelPosting(){
		click(cancelPosting, "Cancel Posting");
	}
	
	public String getBankDetails(String text){
		sync(1000L);
		utils.getElementWhenEditable(bankDetails, "innerHTML", text);
		return getText(bankDetails);
	}
	
	public boolean isBarcodeDisplayed() {
		return isElementDisplayed(barIcon, "Bar Icon", true);
	}
	
	public boolean isBarcodeDisplayed(String Section, String Row, String Seat) {
		String xpath = ".//div[contains(@class, 'ticket-barCodeDialog')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide')]//div[contains(@class, 'ticket-barCodeScan')]//div[contains(@class, 'ticket-barDetails') and descendant::strong[text()='" + Section + "'] and descendant::strong[text()='" + Row + "'] and descendant::strong[text()='" + Seat + "']]/..";
		return isElementDisplayed(By.xpath(xpath + getXpath(barIcon, "Bar icon", "", -1)), "Bar Icon", true);
	}
	
	public String getBarcodeImageUrl(String Section, String Row, String Seat) {
		String xpath = ".//div[contains(@class, 'ticket-barCodeDialog')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide')]//div[contains(@class, 'ticket-barCodeScan')]//div[contains(@class, 'ticket-barDetails') and descendant::strong[text()='" + Section + "'] and descendant::strong[text()='" + Row + "'] and descendant::strong[text()='" + Seat + "']]/..";
		return getAttribute(By.xpath(xpath + getXpath(barIcon, "Bar icon", "", -1)),"src");
	}
	
	public boolean isAddToAppletWalletDisplayed() {
		return isElementDisplayed(addToAppleWalletIcon, "Add to apple wallet", true);
	}
	
	public String getMobileScanGateNumber() {
		return getText(gateNumber).trim();
	}
	
	public String getTotalTicketsCountText() {
		String text = getText(totalTicketsCountText);
		return text.substring(text.indexOf(".") + 1).trim();
	}
	
	public String getSentTicketsCountText() {
		String text = getText(sentTicketsCountText);
		return text.substring(text.indexOf(".") + 1).trim();
	}
	
	public String getListedTicketsCountText() {
		String text = getText(listedTicketsCountText);
		return text.substring(text.indexOf(".") + 1).trim();
	}
	
	public String getClaimedTicketsCountText() {
		String text = getText(claimedTicketsCountText);
		return text.substring(text.indexOf(".") + 1).trim();
	}
	
	public String getDonatedTicketsCountText() {
		String text = getText(donatedTicketsCountText);
		return text.substring(text.indexOf(".") + 1).trim();
	}
	
	public int getTotalTicketsCount() {
		return getWebElementsList(ticketsListing).size();
	}
	
	public int getSentTicketsCount() {
		return getWebElementsList(sentTickets).size();
	}
	
	public int getListedTicketsCount() {
		return getWebElementsList(listedTickets).size();
	}
	
	public int getClaimedTicketsCount() {
		return getWebElementsList(claimedTickets).size();
	}
	
	public int getDonatedTicketsCount() {
		return getWebElementsList(donatedTickets).size();
	}
	
	public String getSellerAddress(String text){
		sync(1000L);
		utils.getElementWhenEditable(sellerAddress, "innerHTML", text);
		if(driverType.trim().toUpperCase().contains("FIREFOX")) {
			return getText(sellerAddress);
		} else
			return getAttribute(sellerAddress, "innerHTML");
	}
	
	public HashMap<Integer, String> getListOfEvents() {
		List<WebElement> levents = getWebElementsList(events);
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		for(int i = 0; i < levents.size(); i++) {
			int eventId = Integer.valueOf(levents.get(i).getAttribute("href").split("/")[levents.get(i).getAttribute("href").split("/").length - 1]);
			String xpath = getXpath(events, "Event", "", -1);
			String ticketCount = "0 Ticket";
			if(checkIfElementPresent(By.xpath("(" + xpath + ")[" + (i+1) + "]" + "//div[contains(@class, 'events-eventTicketCount')]//span[2]"), 1))
				ticketCount = getText(By.xpath("(" + xpath + ")[" + (i+1) + "]" + "//div[contains(@class, 'events-eventTicketCount')]//span[2]"));
			map.put(eventId, ticketCount);
		}
		return map;
	}
	
	public List<String> getListOfEventNames() {
		List<WebElement> levents = getWebElementsList(eventNames);
		List<String> map = new ArrayList<String>();
		for(int i = 0; i < levents.size(); i++) {
			String eventName = levents.get(i).getText();
			map.add(eventName);
		}
		return map;
	}
	
	public boolean isContinueEnabled(){
		boolean enable= true;
		try{
			String value = getAttribute(popupButton, "disabled");
			System.out.println(value);
			if(value.equals("true"))
				enable = false;
		} catch (Exception e){
			enable = true;
		}
		return enable;
	}
	
	public void clickBundleParkingCheckBox() {
		click(bundleparkingcheckbox, "Bundle parking checkbox");
	}

	public void clickBuyMoreTickets(){
		click(BuyTickets,"BUY MORE TICKETS");
	}

	public void clickTicketDetails(String eventId, String Section, String Row, String Seat) {
		String xpath = scrollToTicket(eventId, Section, Row, Seat);
		click(By.xpath(xpath + getXpath(ticketsdetails, "Ticket Details", "", -1)), "Click ticket details");
	}
	
	public boolean isTicketDisplayed(String eventId, String Section, String Row, String Seat) {
		String xpath = scrollToTicket(eventId, Section, Row, Seat);
		getElementWhenVisible(By.xpath(xpath));
		return true;
	}
	
	public String getTermsCondition() {
		return getText(terms_condition).trim();
	}
	
//	public void selectParkingSlots(int noofparkingslots) {
//		List<WebElement> parkingslot = getWebElementsList(parkingslotsCheckbox);
//		for(int i = 0; i < parkingslot.size() && (i < noofparkingslots || noofparkingslots == -1); i++) {
//			parkingslot.get(i).click();
//		}
//	}
	
	public void selectParkingSlots(int numOfSlots){
		for(int i=1;i<=numOfSlots;i++){
			click(By.cssSelector("[class*='send-parkingSlots'] li:nth-child("+i+") div"), "ParkingSlot");
		}
	}
	
	public List<List<String>> getTicketsDetail() {
		List<List<String>> lticketsDetail = new ArrayList<List<String>>();
		List<WebElement> tickets = getWebElementsList(ticketsDetail);
		for(int i = 0; i < tickets.size(); i++) {
			By section = By.xpath("(" + getXpath(ticketsDetail, "Tickets Details", "", -1) + ")[" + (i+1) +"]" + "/div[1]//span/../strong");
			By row = By.xpath("(" + getXpath(ticketsDetail, "Tickets Details", "", -1) + ")[" + (i+1) +"]" + "/div[2]//span/../strong");
			By seat = By.xpath("(" + getXpath(ticketsDetail, "Tickets Details", "", -1) + ")[" + (i+1) +"]" + "/div[3]//span/../strong");
			
			String sectionText = getText(section);
			String rowText = getText(row);
			String seatText = getText(seat);
			
			List<String> lTicketDetail = new ArrayList<String>();
			lTicketDetail.add(sectionText);
			lTicketDetail.add(rowText);
			lTicketDetail.add(seatText);
			lticketsDetail.add(lTicketDetail);
		}
		return lticketsDetail;
	}
	
	public List<List<String>> getMobileTicketsDetail() {
		List<List<String>> lticketsDetail = new ArrayList<List<String>>();
		List<WebElement> tickets = getWebElementsList(noofmobiletickets);
		int i = 0;
		Object[] coords = null;
		do{
			getElementWhenVisible(By.xpath(".//div[contains(@class, 'react-root-event')]//div[contains(@class, 'ticket-eventListContainerMobile')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide') and @data-index='" + i + "']"));
			By section = By.xpath("(" + getXpath(noofmobiletickets, "Mobile Ticket", "", -1) + ")[" + (i+1) +"]" + "//div[contains(@class, 'ticket-ticketDetails')]/div[1]//span/../strong");
			By row = By.xpath("(" + getXpath(noofmobiletickets, "Mobile Ticket", "", -1) + ")[" + (i+1) +"]" + "//div[contains(@class, 'ticket-ticketDetails')]/div[2]//span/../strong");
			By seat = By.xpath("(" + getXpath(noofmobiletickets, "Mobile Ticket", "", -1) + ")[" + (i+1) +"]" + "//div[contains(@class, 'ticket-ticketDetails')]/div[3]//span/../strong");
			String sectionText = getText(section);
			String rowText = getText(row);
			String seatText = getText(seat);
			List<String> lTicketDetail = new ArrayList<String>();
			lTicketDetail.add(sectionText);
			lTicketDetail.add(rowText);
			lTicketDetail.add(seatText);
			lticketsDetail.add(lTicketDetail);
			if(i < tickets.size() - 1) {
				if(driverType.trim().toUpperCase().contains("ANDROID") && i == 0) {
					coords = getCoordinates(By.xpath(".//android.view.View[@resource-id='block-componentblockformanageticket']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[not(@content-desc='') and @index='" + i + "'] | .//android.view.View[@resource-id='block-componentblockformanageticket']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[@index='" + i + "']"));
				}
				swipe(By.xpath(".//div[contains(@class, 'react-root-event')]//div[contains(@class, 'ticket-eventListContainerMobile')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide') and @data-index='" + i + "']"), "Left", coords);
			}
			i++;
		} while(i < tickets.size());
		return lticketsDetail;
	}
	
	public void inputSellerProfile(String FirstName, String LastName, String Add1, String Add2, String Country, String City, String State, String ZipCode, String MobileNum, String PhoneNum ) throws Exception{
		ManageticketsAPI manageticketsapi = new ManageticketsAPI(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
		String firstName = manageticketsapi.getPostingProfileFirstName();
		getElementWhenRefreshed(sellerFirstName, "value", firstName.trim());
		type(sellerFirstName, "First Name", FirstName.trim(), false, By.xpath("//XCUIElementTypeTextField[1]"));
		type(sellerLastName, "Last Name", LastName.trim(), false, By.xpath("//XCUIElementTypeTextField[2]"));
		type(sellerAddress1,"Address1", Add1.trim(), false, By.xpath("//XCUIElementTypeTextField[3]"));
		type(sellerAddress2, "Address2", Add2.trim(), false, By.xpath("//XCUIElementTypeTextField[4]"));
		type(sellerCountry, "Country", Country.trim(), false, By.xpath("//XCUIElementTypeTextField[5]"));
		type(sellerCity, "City", City.trim(), false, By.xpath("//XCUIElementTypeTextField[6]"));
		if(driverType.trim().toUpperCase().contains("IOS")) {
			type(sellerState, "State", State.trim(), false, By.xpath("//XCUIElementTypeTextField[7]"));
		}
		type(sellerZip, "Zip", ZipCode.trim(), false, By.xpath("//XCUIElementTypeTextField[8]"));
		type(sellerMobile, "Mobile", MobileNum.trim(), false, By.xpath("//XCUIElementTypeTextField[9]"));
		type(sellerHomePhone, "Phone", PhoneNum.trim(), false, By.xpath("//XCUIElementTypeTextField[10]"));
		if(!driverType.trim().toUpperCase().contains("IOS")) {
			type(sellerState, "State", State.trim(), false, By.xpath("//XCUIElementTypeTextField[7]"));
			WebElement we = getElementWhenVisible(sellerState, 1);
			we.sendKeys(Keys.TAB);
		}
	}
	
	public void inputSellerProfile(String emailaddress, String password, String FirstName, String LastName, String Add1, String Add2, String Country, String City, String State, String ZipCode, String MobileNum, String PhoneNum ) throws Exception{
		ManageticketsAPI manageticketsapi = new ManageticketsAPI(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
		String firstName = manageticketsapi.getPostingProfileFirstName(emailaddress,password);
		getElementWhenRefreshed(sellerFirstName, "value", firstName.trim());
		type(sellerFirstName, "First Name", FirstName.trim(), false, By.xpath("//XCUIElementTypeTextField[1]"));
		type(sellerLastName, "Last Name", LastName.trim(), false, By.xpath("//XCUIElementTypeTextField[2]"));
		type(sellerAddress1,"Address1", Add1.trim(), false, By.xpath("//XCUIElementTypeTextField[3]"));
		type(sellerAddress2, "Address2", Add2.trim(), false, By.xpath("//XCUIElementTypeTextField[4]"));
		type(sellerCountry, "Country", Country.trim(), false, By.xpath("//XCUIElementTypeTextField[5]"));
		type(sellerCity, "City", City.trim(), false, By.xpath("//XCUIElementTypeTextField[6]"));
		if(driverType.trim().toUpperCase().contains("IOS")) {
			type(sellerState, "State", State.trim(), false, By.xpath("//XCUIElementTypeTextField[7]"));
		}
		type(sellerZip, "Zip", ZipCode.trim(), false, By.xpath("//XCUIElementTypeTextField[8]"));
		type(sellerMobile, "Mobile", MobileNum.trim(), false, By.xpath("//XCUIElementTypeTextField[9]"));
		type(sellerHomePhone, "Phone", PhoneNum.trim(), false, By.xpath("//XCUIElementTypeTextField[10]"));
		if(!driverType.trim().toUpperCase().contains("IOS")) {
			type(sellerState, "State", State.trim(), false, By.xpath("//XCUIElementTypeTextField[7]"));
			WebElement we = getElementWhenVisible(sellerState, 1);
			we.sendKeys(Keys.TAB);
		}
	}
	
	public String SellerfirstName(String firstName){
		getElementWhenRefreshed(sellerFirstName, "value", firstName.trim());
		String str= getAttribute(sellerFirstName, "value");
		return str;
	}
	
	public String SellerLastName(){
		String str= getAttribute(sellerLastName, "value");
		return str;
	}
	
	public String sellerZip(){
		String str= getAttribute(sellerZip, "value");
		return str;
	}
	
	public String sellerAddress2(){
		String str= getAttribute(sellerAddress2, "value");
		return str;
	}
	
	public String sellerCountry(){
		String str= getAttribute(sellerCountry, "value");
		return str;
	}
	
	public String sellerCity(){
		String str= getAttribute(sellerCity, "value");
		return str;
	}
	
	public String sellerState(){
		String str= getAttribute(sellerState, "value");
		return str;
	}
	
	public String sellerAddress1(){
		String str= getAttribute(sellerAddress1, "value");
		return str;
	}
	
	public String sellerMobile(){
		String str= getAttribute(sellerMobile, "value");
		return str;
	}
	
	public String sellerHomePhone(){
		String str= getAttribute(sellerHomePhone, "value");
		return str;
	}
	
	public void inputBankDetails(String AccType, String RoutingNum, String AccountNum, String ConfirmAcc) throws Exception{
		type(bankRoutingNumber, "Routing Number",  RoutingNum, false, By.xpath("//XCUIElementTypeOther[starts-with(@name, 'Routing Number')]/../XCUIElementTypeTextField"));
		click(bankAccountType, "AccountType");
		getElementWhenClickable(By.xpath("//*[contains(@class,'theme-dropdown')]/ul/li[text()='"+AccType+"']")).click();
		type(bankAccountNumber, "Account Number", AccountNum, false, By.xpath("//XCUIElementTypeSecureTextField[1]"));
		type(bankConfirmAccount, "Confirm Account", ConfirmAcc, false, By.xpath("//XCUIElementTypeSecureTextField[2]"));
	}
	
	public void clickEditBankDetail(){
		click(editBankAccount, "Bank Account Edit");
	}
	
	public void clickEditSellerProfile(){
		click(editSellerProfile, "Bank Account Edit");
	}
	
	public void selectBankAccount(){
		click(depositAccount,"Deposit Account");
		click(bankAccount, "Bank Account");
	}
	
	public void selectSellerCredit(){
		click(depositAccount,"Deposit Account");
		click(sellerCredit, "Seller Credit");
	}
	
	public String getDepositAccountValue() {
		return getAttribute(depositAccount, "value");
	}
	
	public String getSuccess(){
		WebElement we = getElementWhenRefreshed(successText, "innerHTML", "Success!");
		return we.getText();
	}
	
	public void typeEarningPrice(String[] prices) throws Exception{
		if(driverType.trim().toUpperCase().contains("SAFARI")) {
			throw new SkipException("Skipped");
		}
		List<WebElement> earningPrices;
		if(driverType.trim().toUpperCase().contains("IOS")) {
			earningPrices = getIosMobileElementsList(By.xpath("//XCUIElementTypeTextField"));
		} else
			earningPrices = getWebElementsList(earningPrice);
		for(int i = 0; i < earningPrices.size(); i++) {
			type(earningPrices.get(i), "Earning price " + (i+1), prices.length > i ? prices[i] : prices[0], false, By.xpath("//XCUIElementTypeTextField[" + (i+1) + "]"));
		}
	}
	
	public String scrollToTicket(String eventId, String Section, String Row, String Seat) {
		if((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone")) {
			utils.navigateTo("/tickets");
			Assert.assertTrue(isManageTicketsListDisplayed() , "Verify manage tickets list is displayed");
			utils.navigateTo("/tickets#/" + eventId);
		}
		Section = Section.replaceAll("%20", " ");
		Row = Row.replaceAll("%20", " ");
		By xpath = By.xpath(".//div[contains(@class, 'react-root-event')]//ul//li[contains(@class, 'list-item')]//div[contains(@class, 'ticket-ticketDetails') and descendant::strong[text()='" + Section + "'] and descendant::strong[text()='" + Row + "'] and descendant::strong[text()='" + Seat + "']]/..");
		if((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone")) {
			getElementWhenVisible(mobiletickets);
			xpath = By.xpath("(" + getXpath(noofmobiletickets, "Mobile ticket", "", -1) + ")" + "//div[contains(@class, 'ticket-ticketDetails') and descendant::strong[text()='" + Section + "'] and descendant::strong[text()='" + Row + "'] and descendant::strong[text()='" + Seat + "']]/..");
			List<WebElement> tickets = getWebElementsList(noofmobiletickets);
			int i = 0;
			Object[] coords = null;
			while(i < tickets.size() && !checkIfElementPresent(xpath, 1)) {
				if(i < tickets.size() - 1) {
					if(driverType.trim().toUpperCase().contains("ANDROID") && i == 0) {
						coords = getCoordinates(By.xpath(".//android.view.View[@resource-id='block-componentblockformanageticket']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[not(@content-desc='') and @index='" + i + "'] | .//android.view.View[@resource-id='block-componentblockformanageticket']/android.view.View/android.view.View/android.view.View/android.view.View/android.view.View[@index='" + i + "']"));
					}
					swipe(By.xpath(".//div[contains(@class, 'react-root-event')]//div[contains(@class, 'ticket-eventListContainerMobile')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide') and @data-index='" + i + "']"), "Left", coords);
				}
				i++;
			}
		}
		getElementWhenVisible(xpath);
		return getXpath(xpath, "Ticket", "", -1);
	}
	
	public void swipe(String direction, String index) {
		swipe(By.xpath(".//div[contains(@class, 'ticket-barCodeDialog')]//div[contains(@class, 'slick-list')]//div[contains(@class, 'slick-slide') and @data-index='" + index.trim() + "']//div[contains(@class, 'ticket-qrIcon')]/div"), direction.trim(), null);
	}
	
	public void clickEditCancelTicket(String eventId, String Section, String Row, String Seat){
		String xpath = scrollToTicket(eventId, Section, Row, Seat);
		click(getElementWhenVisible(By.xpath(xpath + "/..//div[contains(@class, 'ticket-ticketImage')]//span[contains(@class, 'ticket-soldBtn')]"), 3),"manage posting");
	}
	
	public void clickManageTickets(String eventId, String Section, String Row, String Seat){
		String xpath = scrollToTicket(eventId, Section, Row, Seat);
		click(By.xpath(xpath + "/../..//span//small/.."), "Manage Ticket", 3);
	}
	
	public void clickScanBarcode(String eventId, String Section, String Row, String Seat) {
		String xpath = scrollToTicket(eventId, Section, Row, Seat);
		String scanBarcode = getXpath(scanCode, "Scan barcode", "", -1);
		click(By.xpath(xpath + scanBarcode), "Scan Barcode");
	}
	
	public boolean verifyScanBarcodeState(String eventId, String Section, String Row, String Seat) {
		scrollToTicket(eventId, Section, Row, Seat);
		return isScanBarcodeEnabled();
	}
	
	public void selectCharity(){
		getElementWhenVisible(charitySelect).click();
		javascriptClick(getElementWhenVisible(firstCharity), "First Charity");
	}
	
	public String getSendUser(){
		return getText(sendUser);
	}
	
	public String getClaimLink(){
		String text = getText(claimLink);
		String claimUrl = text.trim().split("\\n")[0];
//		///(?:(?:https?|ftp|file):\/\/|www\.|ftp\.)(?:\([-A-Z0-9+&@#\/%=~_|$?!:,.]*\)|[-A-Z0-9+&@#\/%=~_|$?!:,.])*(?:\([-A-Z0-9+&@#\/%=~_|$?!:,.]*\)|[A-Z0-9+&@#\/%=~_|$])/igm
//		Pattern r = Pattern.compile("(?:(?:https?|ftp|file):\\/\\/|www\\.|ftp\\.)(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:,.]*\\)|[-A-Z0-9+&@#\\/%=~_|$?!:,.])*(?:\\([-A-Z0-9+&@#\\/%=~_|$?!:,.]*\\)|[A-Z0-9+&@#\\/%=~_|$])");
//        Matcher m = r.matcher(text);
//        String claimUrl = "";
//        if (m.find( )) {
//        	claimUrl = m.group(0);
//            System.out.println("Claim Url : " + claimUrl);
//        }
		return claimUrl; 
	}
	
	public String getEventClaimLink(){
		return getText(eventClaimLink);
	}
	
	public String getParkingClaimLink(){
		return getText(parkingClaimLink);
	}
	
	public String[] getEventClaimLinks(){
		List<WebElement> wEventClaimLinks = getWebElementsList(eventClaimLink);
		String[] eventClaimLinks = new String[wEventClaimLinks.size()];
		for(int i = 0; i < wEventClaimLinks.size(); i++) {
			eventClaimLinks[i] = wEventClaimLinks.get(i).getText();
		}
		return eventClaimLinks;
	}
	
	public String[] getParkingClaimLinks(){
		List<WebElement> wParkingClaimLinks = getWebElementsList(parkingClaimLink);
		String[] parkingClaimLinks = new String[wParkingClaimLinks.size()];
		for(int i = 0; i < wParkingClaimLinks.size(); i++) {
			parkingClaimLinks[i] = wParkingClaimLinks.get(i).getText();
		}
		return parkingClaimLinks;
	}
	
	public void clickConfirm() {
		sync(1000L);
		WebElement we = getElementWhenVisible(confirm);
		if(driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) {
			we.click();
		} else {
			Actions action = new Actions(getDriver());
			action.moveToElement(we).click(we).perform();
		}
	}
	
	public void clickDone(){
		click(reclaimDone, "Done");
	}
	
	public void clickViewTickets(){
		click(downloadTickets, "Print Tickets");
	}
	
	public void clickContinue(){
		click(popupButton, "Continue");
	}
	
	public void clickClaim(){
		click(claimButton, "Claim");
	}
	
	public String getPopUpEventDetails(){
		return getText(popUpEventName);
	}
	
	public String getMobileScanEventDetails() {
		return getText(mobilepopUpEventName).trim().replace("clear", "").trim();
	}
	
	public String getMobileScanSectionName() {
		return getText(mobileDownloadSectionName).trim();
	}
	
	public String getMobileScanRowName() {
		return getText(mobileDownloadRowName).trim();
	}
	
	public String getMobileScanSeatName() {
		return getText(mobileDownloadSeatName).trim();
	}
	
	public String getPopUpSeatDetails(){
		return getText(popUpSeatDetail);
	}
	
	public String getSection(){
		return getPopUpSeatDetails().split("\\|")[0].trim();
	}
	
//	public String getRow(){
//		return getPopUpSeatDetails().split("\\|")[1].trim();
//	}
//	
//	public String getSeat(){
//		return getPopUpSeatDetails().split("\\|")[2].trim();
//	}
	
	public String getMultipleSeat(){
		return getPopUpSeatDetails().split("\\|")[4].split(" ")[2].trim().replace(",", "");
	}
	
	public String getExpiryDate(){
		return getText(seatExpiryDate);
	}
	
	public void clickReclaim(){
		getElementWhenVisible(reclaim).click();
	}
	
	public void clickLostTickets() {
		click(lostTicket,"Lost or stolen tickets");
	}
	
	public void selectSeatInPopUp(String ticket_id, String Section, String Row, String SeatNum) {
		getElementWhenVisible(By.xpath(".//*[contains(@class,'tickets-selectionInner')] | //*[contains(@class,'seat-selection-selectionInner')]"));
		Section = Section.replaceAll("%20", " ");
		Row = Row.replaceAll("%20", " ");
		
		String sectionName = Dictionary.get("FORMATTED_SECTION_NAME_" + ticket_id);
		String rowName = Dictionary.get("FORMATTED_ROW_NAME_" + ticket_id);
		String seatName = Dictionary.get("FORMATTED_SEAT_NAME_" + ticket_id);
		String sectionLabel = Dictionary.get("FORMATTED_SECTION_LABEL_" + ticket_id);
		String seatlabel = Dictionary.get("FORMATTED_SEAT_LABEL_" + ticket_id).trim().equalsIgnoreCase("") ? "Seat" : Dictionary.get("FORMATTED_SEAT_LABEL_" + ticket_id);
		String row = Dictionary.get("ROW_NAME_" + ticket_id);
		
		if(seatName.trim().split(" ").length == 1) {
			seatName = " " + seatName;
		}
		
		if(sectionName.trim().split(" ").length == 1) {
			sectionName = " " + sectionName;
		}
		
		if(rowName.trim().split(" ").length == 1) {
			rowName = " " + rowName;
		}
		
		if(!rowName.trim().contains(row.trim())) {
			rowName = rowName.trim() + " " + row.trim();
		} else if(rowName.trim().equalsIgnoreCase(row.trim())) {
			rowName = "Row" + " " + row.trim();
		}
		
		if(seatName.trim().equalsIgnoreCase("") || seatName.trim().equalsIgnoreCase(SeatNum.trim())) {
			seatName = seatlabel.trim() + " " + SeatNum.trim();
		}
		
		if(sectionName.trim().equalsIgnoreCase("") || rowName.trim().equalsIgnoreCase("")) {
			click(By.xpath("//*[contains(@class,'tickets-selectionInner')]//*[contains(@class,'ticketsBlock')]/strong[contains(text(),'" + sectionLabel + " " + Section + " | " + rowName + "')]/..//li//strong[contains(text(), '" + SeatNum + "')]/../label | //*[contains(@class,'seat-selection-selectionInner')]//*[contains(@class,'ticketsBlock')]/strong[contains(text(),'" + sectionLabel + " " + Section + " | " + rowName + "')]/..//li//strong[contains(text(), '" + SeatNum + "')]/../label"), "Select seat");
		} else {
			click(By.xpath("//*[contains(@class,'tickets-selectionInner')]//*[contains(@class,'ticketsBlock')]/strong[contains(text(),'" + sectionName + " | " + rowName + "')]/..//li//strong[text()='" + seatName + "']/../label | //*[contains(@class,'seat-selection-selectionInner')]//*[contains(@class,'ticketsBlock')]/strong[contains(text(),'" + sectionName + " | " + rowName + "')]/..//li//strong[text()='" + seatName + "']/../label"), "Select seat");
		}
	}
	
	public void logoutNLogin(String emailaddress, String password) throws Exception {
		String currentUrl = getDriver().getCurrentUrl();
		String uri = currentUrl.trim().replace(Environment.get("APP_URL").trim(), "");
		utils.navigateTo("/user/logout");
		utils.navigateTo(uri);
		homepage.login(emailaddress, password, null, true);
	}
	
	public String getTicketStatus(String eventId, String Section, String Row, String Seat){
		String xpath = scrollToTicket(eventId, Section, Row, Seat);
		try {
			return getText(By.xpath(xpath + "//div[contains(@class, 'ticket-ticketImage')]//div[contains(@class, 'ticket-listedOn')][1]"), 1);
		} catch(Exception ex) {
			return "No Status";
		}
	}
	
	Utils utils = new Utils(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
	
	public void clickFirstManageTickets() {
		try{
			sync(5000L);
			getElementWhenVisible(manageTicketsList);
		} catch(Exception ex){
			getDriver().navigate().refresh();
			sync(5000L);
			getElementWhenVisible(manageTicketsList);
		}
		WebElement we = getElementWhenRendered(manageTicketsList, 150, 150);
		String[] href = getAttribute(we, "href").split("\\#/");
		Dictionary.put("eventId", href[1].replaceAll("/.*", ""));
		sync(2000L);
		utils.navigateTo("/tickets#/" + Dictionary.get("eventId").trim());
	}
	
	public boolean isManageTicketsListDisplayed(){
		try{
			getElementWhenVisible(manageTicketsList);
		} catch(Exception ex){
			utils.navigateTo("/tickets");
			getElementWhenVisible(manageTicketsList);
		}
		getElementWhenRendered(manageTicketsList, 150, 150);
		sync(2000L);
		return true;
	}
	
	public boolean isTicketsListDisplayed(TestDevice device){
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone")))
			getElementWhenVisible(ticketListingMobile, 40);
		else
			getElementWhenVisible(ticketsListing, 40);
		return true;
	}
	
	public boolean clickDownloadTickets(){
		Assert.assertTrue(isTicketsListDisplayed(null) , "Verify tickets listing page is displayed");
		if(isViewButtonEnable()) {
			click(downloadTickets, "Download Tickets", By.xpath("//*[contains(@class,'tickets-selectionInner')] | //*[contains(@class,'seat-selection-selectionInner')]"), 1);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isDownloadTicketsTextVisible(){
		getElementWhenVisible(downloadTextPopUp, 40);
		return true;
	}
	
	public void clickSendTickets(TestDevice device){
		Assert.assertTrue(isTicketsListDisplayed(device) , "Verify tickets listing page is displayed");
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone"))) {
			scrollingToBottomofAPage();
			By sendButton = By.xpath(".//android.widget.Button[contains(@content-desc, 'SEND')]");
			click(sendTicketMobile, "Send Tickets", sendButton, null, "Send Tickets");
		} else
			click(sendTicket, "Send Tickets", By.xpath("//*[contains(@class,'tickets-selectionInner')] | //*[contains(@class,'seat-selection-selectionInner')]"), 1);
	}
	
	public boolean isSendTicketsTextVisible(){
		getElementWhenVisible(sendTextPopUp, 40);
		return true;
	}
	
	public void clickSellTickets(TestDevice device){
		Assert.assertTrue(isTicketsListDisplayed(device) , "Verify tickets listing page is displayed");
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone"))){
			scrollingToBottomofAPage();
			By sellButton = By.xpath(".//android.widget.Button[contains(@content-desc, 'SELL')]");
			click(sellTicketMobile, "Sell", sellButton, null, "Sell");
		} else
			click(sellTicket, "Sell", By.xpath("//*[contains(@class,'tickets-selectionInner')] | //*[contains(@class,'seat-selection-selectionInner')]"), 1, 40);
	}
	
	public void clickDonateTickets(TestDevice device){
		Assert.assertTrue(isTicketsListDisplayed(device) , "Verify tickets listing page is displayed");
		if((device != null && device.getName().trim().equalsIgnoreCase("mobile")) || ((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && Environment.get("deviceType").trim().equalsIgnoreCase("phone"))) {
			scrollingToBottomofAPage();
			By moreButton = By.xpath(".//android.widget.Button[contains(@content-desc, 'more_horiz')]");
			click(moreButtonMobile, "More", moreButton, null, "More");
			click(donateTicketMobile, "Donate");
		} else {
			click(moreButton, "More");
			click(donateTicket, "Donate", By.xpath("//*[contains(@class,'tickets-selectionInner')] | //*[contains(@class,'seat-selection-selectionInner')]"), 1);
		}
	}
	
	public boolean isSellTicketsTextVisible(){
		getElementWhenVisible(sellTicketPopUp, 40);
		return true;
	}
	
	public boolean isDonateTicketsTextVisible(){
		getElementWhenInVisible(donateTextPopUp, 40);
		return true;
	}
	
	public void clickFirstOddManageTickets(){
		click(firstManageTickets,"Click First Manage Tickets");
	}
	
	public String[] getTicketDetails() {
		List<String> ticketDetails = new ArrayList<String>();
		List<WebElement> h5 = getWebElementsList(this.ticketDetails);
		if(h5.size() > 0) {
			for(int i = 0; i < h5.size(); i++) {
				ticketDetails.add(h5.get(i).getText().trim());
			}
			ticketDetails.add(getTermsCondition());
			return ticketDetails.toArray(new String[ticketDetails.size()]);
		} else {
			return new String[]{getNoTicketDetailsText()};
		}
	}
	
	public void verifyRenderBarcode(String email, String pass, String ticketId) throws Exception	{
		if(!utils.getManageTicketConfiguration("mobile_enabled"))
			  throw new SkipException("Mobile_Enabled is not enabled in CMS");
		if(Environment.get("deviceType").trim().equalsIgnoreCase("tablet")) {
			throw new SkipException("Skipped");
		}
		ManageticketsAPI manageticketsapi= new ManageticketsAPI(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
		String[] ticket=ticketId.split("\\.");
		utils.navigateTo("/tickets#/"+ticket[0]);
		clickScanBarcode(ticket[0], ticket[1].replaceAll("%20", " "), ticket[2], ticket[3]);
		Assert.assertEquals(getMobileScanEventDetails(), Dictionary.get("eventName"));
		Assert.assertEquals(getMobileScanSectionName(), ticket[1].replaceAll("%20", " "));
		Assert.assertEquals(getMobileScanRowName(),ticket[2]);
		Assert.assertEquals(getMobileScanSeatName(), ticket[3]);
		Assert.assertEquals(getMobileScanGateNumber(), "Enter Gate: " + Dictionary.get("entryGate").trim());
		manageticketsapi.renderBarcode(email, pass, ticketId);
		Assert.assertTrue(isBarcodeDisplayed(), "Verify bar code is displayed");
	}
	
	public void verifyPrintTicket(String email, String pass, String ticketId) throws Exception	{
		ManageticketsAPI manageticketsapi= new ManageticketsAPI(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
		String[] ticket = ticketId.split("\\.");
		utils.navigateTo("/tickets#/" + ticket[0]);
		boolean success = clickDownloadTickets();
		if(success) {
			selectSeatInPopUp(ticketId, ticket[1].replaceAll("%20", " "), ticket[2], ticket[3]);
			clickContinue();
			Assert.assertEquals(getPopUpEventDetails(), Dictionary.get("eventName"));
			Assert.assertTrue(getSection().contains(ticket[1].replaceAll("%20", " ")));
			clickContinue();
			Assert.assertTrue(isTicketsListDisplayed(null), "Verify tickets listing page is displayed");
			manageticketsapi.renderFile(email, pass, ticketId);
		}
	}
}