package org.iomedia.galen.steps;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iomedia.common.BaseUtil;
import org.iomedia.galen.common.AccessToken;
import org.iomedia.galen.common.ManageticketsAPI;
import org.json.JSONException;
import org.testng.SkipException;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class TicketsApiSteps {
	
	ManageticketsAPI api;	
	BaseUtil base;
	AccessToken accessToken;
	org.iomedia.framework.Assert Assert;
	org.iomedia.framework.SoftAssert SoftAssert;
	String driverType;
	
	boolean CAN_TRANSFER, CAN_RESALE, CAN_RENDER, CAN_RENDER_FILE, CAN_RENDER_BARCODE, CAN_RENDER_PASSBOOK, CAN_DONATE_CHARITY;
	
	public TicketsApiSteps(ManageticketsAPI api, BaseUtil base, AccessToken accessToken, org.iomedia.framework.Assert Assert, org.iomedia.framework.SoftAssert SoftAssert) {
		this.api = api;
		this.base = base;
		this.driverType = base.driverFactory.getDriverType().get();
		this.accessToken = accessToken;	
		this.Assert = Assert;
		this.SoftAssert = SoftAssert;
		CAN_TRANSFER = CAN_RESALE = CAN_RENDER = CAN_RENDER_FILE = CAN_RENDER_BARCODE = CAN_RENDER_PASSBOOK = CAN_DONATE_CHARITY = true;
	}

	@Given("^Customer details are fetched for (.+) and (.+)$")
	public void customer_details_are_fetched(String emailaddress,String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		Map<String, Object> names = api.getCustomerName(emailaddress, password);
		api.Dictionary.put("AccountName", names.get("CUST_NAME").toString().trim());
		api.Dictionary.put("StpAccountName", names.get("STP_CUST_NAME").toString().trim());
	}
	
	@Given("^Generate member response with (.+) and (.+)$")
	public void generate_member_response_with_and(String emailaddress, String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		accessToken.getMemberResponse(emailaddress, password);
		if(Integer.parseInt(base.Dictionary.get("associatedCount")) < 2){
			throw new SkipException("No Associated Accounts Available");
		}
	}
	
	@Given("^Get transfer ticket ID for (.+) and (.+)$")
	public void get_transfer_ticket_ID(String emailaddress, String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		String[] Tkt = api.getTransferDetails(emailaddress, password, true, "event", false, false, false);
		base.Dictionary.put("TransferTicketID", Tkt[0]);
		base.Dictionary.put("EventId", Tkt[0].split("\\.")[0]);
	}
	
	@Given("^Get donate ticket ID for (.+) and (.+)$")
	public void get_ticketID_for_donate(String emailaddress, String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		String[] Tkt = api.getDonateDetails(emailaddress, password, true, "event", false, false, false);
		base.Dictionary.put("DonateTicketID", Tkt[0]);
		base.Dictionary.put("EventId", Tkt[0].split("\\.")[0]);
	}
	
	@Given("^Save ticket flags for ticket Id (.+) using (.+) and (.+)$")
	public void save_ticket_flags(String ticketId, String emailaddress, String password) throws JSONException, IOException {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		ticketId = (String) base.getGDValue(ticketId);
		String[] ticket = ticketId.split("\\.");
		base.Dictionary.put("Event_Id", ticket[0]);
		Boolean[] flags = api.getTicketFlags(ticketId, emailaddress , password);
		CAN_TRANSFER = flags[0];
		CAN_RESALE = flags[1];
		CAN_RENDER = flags[2];
		CAN_RENDER_FILE = flags[3];
		CAN_RENDER_BARCODE = flags[4];
		CAN_RENDER_PASSBOOK = flags[5];
		CAN_DONATE_CHARITY = flags[6];
	}
	
	@Then("Verify ticket flags for (.+), (.+) and (.+)")
	public void verify_ticket_flags(String ticketId, String emailaddress, String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		ticketId = (String) base.getGDValue(ticketId);
		SoftAssert.assertEquals(api.getTicketFlags(ticketId, emailaddress, password), new Boolean[] {CAN_TRANSFER, CAN_RESALE, CAN_RENDER, CAN_RENDER_FILE, CAN_RENDER_BARCODE, CAN_RENDER_PASSBOOK, CAN_DONATE_CHARITY}, "Verify the ticket flags");
	}
	
	@Then("Verify False ticket flags for (.+), (.+) and (.+)")
	public void verify_ticket_flags_after_action(String ticketId, String emailaddress, String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		ticketId = (String) base.getGDValue(ticketId);
		Assert.assertEquals(api.getTicketFlags(ticketId, emailaddress, password), new Boolean[] {false, false, false, false, false, false, false}, "Verify the ticket flags after user action performed");
	}
	
	@Given("^Send Ticket using (.+)$")
	public void send_ticket(String ticketId) throws NumberFormatException, Exception {
		ticketId = (String) base.getGDValue(ticketId);
		String[] ticket = ticketId.split("\\.");
		api.sendTickets(Integer.valueOf(ticket[0]), new String[]{ticketId});
	}
	
	@Given("^User generate TransferId for (.+)$")
	public void user_generate_transferId(String ticketId) throws Exception {
		ticketId = (String) base.getGDValue(ticketId);
		String[] transferIds = api.getTransferID(new String[]{ticketId});
		base.Dictionary.put("TransferID", transferIds[0]);
	}
	
	@Given("^Get Resale details of ticket for (.+) and (.+)$")
	public void get_resale_detail(String emailaddress, String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		String[] tickets = api.getResaleDetails(emailaddress, password, true, "event", false, false);
		base.Dictionary.put("ResaleTicketID", tickets[0]);
		base.Dictionary.put("EventId", tickets[0].split("\\.")[0]);
	}
	
	@Given("^User fetch ticketId for selling$")
	public void user_fetch_ticketId() throws Exception {
		String[] tickets = api.getResaleDetails(true, "event", false, false);
		base.Dictionary.put("ResaleTicketID", tickets[0]);
	}
	
	@Given("^Save Event Id for ticket Id (.+)$")
	public void save_event_id(String ticketId) {
		ticketId = (String) base.getGDValue(ticketId);
		String[] ticket=ticketId.split("\\.");
		base.Dictionary.put("Resale_Event_Id", ticket[0]);
	}
	
	@Given("^User sells ticket using API for (.+)$")
	public void user_sell_ticket_api(String ticketId) throws Exception {
		ticketId = (String) base.getGDValue(ticketId);	
		api.selltickets(new String[] {ticketId});
	}
	
	@Given("^User fetches Render details for (.+) and (.+)$")
	public void user_fetch_render_details(String emailaddress,String password) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		String[] tickets = api.getRenderDetails(emailaddress, password, true, "event", false, false);
		base.Dictionary.put("RenderTicketID", tickets[0]);	
	}
	
	@When("^fetch Render details for (.+)$")
	public void fetch_render_detail(String EventID) throws Exception {
		EventID = (String) base.getGDValue(EventID);
		String[] tickets = api.getRenderDetails(true, "event", false, false);
		for(int i=0; i<tickets.length;i++) {
			String[] tkt = tickets[i].split("\\.");
			if(tkt[0].equalsIgnoreCase(EventID)) {
				base.Dictionary.put("RenderTicketID", tickets[i]);
				break;
			}
		}
	}
	
	@Given("^Save Render Event Id for (.+)$")
	public void save_render_event_id(String ticketId) {
		ticketId = (String) base.getGDValue(ticketId);
		String[] ticket=ticketId.split("\\.");
		base.Dictionary.put("Render_Event_Id", ticket[0]);
	}
	
	@Then("^User click on render Barcode using (.+) and (.+) for (.+)$")
	public void user_click_render_barcode(String emailaddress,String password, String ticketId) throws Exception {
		emailaddress = (String) base.getGDValue(emailaddress);
		password = (String) base.getGDValue(password);
		ticketId = (String) base.getGDValue(ticketId);
		api.renderBarcode(emailaddress,password,ticketId);
	}
	
	@When("^User fetch render ticket Ids$")
	public void fetch_eventId_with_max_tickets() throws Exception {
		HashMap<Integer, ManageticketsAPI.Event> events = api.getEventIdWith3RenderTktsDetails();
		int eventId = -1;
		if(base.Dictionary.get("RENDER_TICKET_EVENT_ID").trim().equalsIgnoreCase("")) {
			Assert.assertNotNull(events);
			if((driverType.trim().toUpperCase().contains("ANDROID") || driverType.trim().toUpperCase().contains("IOS")) && base.Environment.get("deviceType").trim().equalsIgnoreCase("phone"))
				eventId = api.getEventIdWithMaxTktsHavingRenderBarcode(events);
			else 
				eventId = api.getEventIdWithMaxTkts(events);
			
			Assert.assertNotEquals(eventId, -1);
		} else {
			eventId = Integer.valueOf(base.Dictionary.get("RENDER_TICKET_EVENT_ID").trim());
		}
		
		base.Dictionary.put("EventID", String.valueOf(eventId));
		
		List<List<String>> ticketIds = api.getTop3TicketIds(eventId, events);
		if(ticketIds.size() < 2)
			throw new SkipException("No event found with more than 1 render tickets");
		
		String tickets = "";
		for(List<String> ticket: ticketIds) {
			String sectionName = ticket.get(0).trim().replace(" ", "%20");
			String rowName = ticket.get(1).trim().replace(" ", "%20");
			String seatNumber = ticket.get(2).trim().replace(" ", "%20");
			String ticketId = String.valueOf(eventId).trim() + "." + sectionName + "." + rowName + "." + seatNumber;
			tickets += ticketId + ",";
		}
		tickets = tickets.trim().substring(0, tickets.trim().length() - 1);
		base.Dictionary.put("RenderBarcodeID", tickets);
		base.Dictionary.put("RenderBarcodeID_Count", String.valueOf(tickets.split(",").length));
	}
}
