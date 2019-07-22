package org.iomedia.galen.pages;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.iomedia.common.BaseUtil;
import org.iomedia.framework.Driver.HashMapNew;
import org.iomedia.framework.Reporting;
import org.iomedia.framework.WebDriverFactory;
import org.iomedia.galen.common.AccessToken;
import org.iomedia.galen.common.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.SkipException;

public class AdminLogin extends BaseUtil {
	
	public AdminLogin(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment, Reporting Reporter, org.iomedia.framework.Assert Assert, org.iomedia.framework.SoftAssert SoftAssert, ThreadLocal<HashMapNew> sTestDetails) {
		super(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
	}
	
	private By username= By.id("txtUserName");
	private By password = By.id("txtPassword");
	private By login = By.id("btnLogin");
	private By venue = By.cssSelector("div.loginbox ul li:first-child input");
//	private By testServices = By.id("lnkServiceTest");
//	private By TestServiceBtn = By.xpath("//*[@id='contentmain']/table/tbody/tr[6]/td[1]/input");
	private By url = By.id("txtURL");
//	private By header = By.id("txtHeader");
//	private By response = By.id("txtResponse");
	String path = System.getProperty("user.dir");
	private String invoiceRequest = path + "/APIRequest/invoiceListRequest.json";
	private String invoiceDetails = path + "/APIRequest/invoiceDetails.json";
	private String paymentPlans = path + "/APIRequest/getPaymentPlans.json";
	private String paymentPlansSchedule = path + "/APIRequest/getPaymentSchedule.json";
	private String paymentsPlanDetails = path + "/APIRequest/paymentPlanDetails.json";
	private String ccQuery = path + "/APIRequest/cc_Query.json";
	private String getVenueAvailability = path + "/APIRequest/getVenueAvailability.json";
	
	private AccessToken accessTokens = new AccessToken(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
	private Utils utils = new Utils(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
	
	public JSONObject parseJsonFile(String filename) throws Exception{
		String jsonText = readAll(new FileReader(filename));
		JSONObject obj = new JSONObject(jsonText);
		if(!Environment.get("TM_ARCHTICS_VERSION").trim().equalsIgnoreCase(""))
			obj = update(obj, "archtics_version", Environment.get("TM_ARCHTICS_VERSION").trim());
		if(!Environment.get("TM_UID").trim().equalsIgnoreCase(""))
			obj = update(obj, "uid", Environment.get("TM_UID").trim());
        return obj;
	}
	
	public JSONObject update(org.json.JSONObject obj, String keyMain, Object newValue) throws Exception {
	    Iterator<String> iterator = obj.keys();
	    String key = null;
	    while (iterator.hasNext()) {
	        key = (String) iterator.next();
            if ((key.equals(keyMain))) {
                obj.put(key, newValue);
                return obj;
            }
	        if (obj.optJSONObject(key) != null) {
	        	update(obj.getJSONObject(key), keyMain, newValue);
	        }

	        // if it's jsonarray
	        if (obj.optJSONArray(key) != null) {
	            JSONArray jArray = obj.getJSONArray(key);
	            int flag = 0;
	            for (int i = 0; i < jArray.length(); i++) {
	            	if(jArray.get(i) instanceof JSONObject){
	            		flag = 1;
            			update(jArray.getJSONObject(i), keyMain, newValue);
	            	}
	            }
	            if(flag == 0){
		            if(String.valueOf(newValue).trim().contains("&&")){
		            	if ((key.equals(keyMain))) {
			                // put new value
		            		List<String> newValues = new ArrayList<String>(Arrays.asList(String.valueOf(newValue).trim().split("&&")));
		            		obj.put(key, newValues);
			                return obj;
			            }
		            }
	            }
	        }
	    }
	    return obj;
	}
	
	public void typeUsername(String username){
		WebElement we = getElementWhenVisible(this.username);
		we.sendKeys(username, Keys.TAB);
	}
	
	public void typePassword(String password){
		WebElement we = getElementWhenVisible(this.password);
		we.sendKeys(password, Keys.TAB);
	}
	public void clickLogin(){
		click(login, "login");
	}
	
	public void enterVenue() throws Exception{
		type(venue, "Venue", "DRW-001-All (149)");
	}
	
//	public void clickTestServices(){
//		click(testServices, "test Services");
//	}
	
	public void typeUrl(String url){
		WebElement we = getElementWhenVisible(this.url);
		we.clear();
		we.sendKeys(url, Keys.TAB);
	}
	
//	public void adminLogin() throws InterruptedException {
//		enterVenue();
//		typeUsername("admin");
//		typePassword("Iopa5s#01");
//		clickLogin();
//		clickTestServices();
//		typeUrl("https://ws.ticketmaster.com/archtics/ats/ticketing_services.aspx?dsn=" + Environment.get("DSN").trim());
//	}
	
	public String getAvailableSectionId(String accountId, String eventId) throws Exception {
		JSONObject obj = update(parseJsonFile(getVenueAvailability), "acct_id", accountId.trim());
		obj = update(obj, "event_name", eventId.trim());
		String payload = update(obj, "dsn", Environment.get("DSN").trim()).toString();
		String jsonText = utils.waitForTMInvoiceResponse(payload);
		JSONObject jsonObj = new JSONObject(jsonText);
		JSONArray sections = jsonObj.getJSONObject("command1").getJSONObject("manifest").getJSONObject("section_summary").getJSONArray("data");
		String sectionId =  sections.getString(0).trim().split(",")[0].trim();
		return sectionId;
	}
	
	public int getInvoiceListAndId(String AccountId, String invoicestatus) throws JSONException, Exception {
		JSONObject obj = update(parseJsonFile(invoiceRequest), "acct_id", AccountId);
		String payload = update(obj, "dsn", Environment.get("DSN").trim()).toString();
		String jsonText = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "InvoiceListArray", jsonText);
		
		int invoiceId = -1;
		if(!invoicestatus.trim().equalsIgnoreCase("")) {
			JSONObject jsonObj = new JSONObject(jsonText);
			if(!jsonObj.getJSONObject("command1").has("invoices"))
				return invoiceId;
			JSONArray invoices = jsonObj.getJSONObject("command1").getJSONArray("invoices");
			boolean success = false;
			for(int i = invoices.length() - 1 ; i >= 0 ; i--){
				JSONObject invoice = invoices.getJSONObject(i);
				float next_payment_amounts = Float.valueOf(invoice.getString("next_payment_amounts"));
				float balances = Float.valueOf(invoice.getString("balances"));
				float invoice_amounts = Float.valueOf(invoice.getString("invoice_amounts"));
				int payment_plan_id = invoice.has("payment_plan_id") ? Integer.valueOf(invoice.getString("payment_plan_id")) : -1;
//				float current_due_amounts = Float.valueOf(invoice.getString("current_due_amounts"));
				 
				switch(invoicestatus.trim().toUpperCase()){
					case "PAID":
						if(next_payment_amounts == balances && balances == 0.00)
				 			success = true;
				 		break;
				 	case "UNPAID":
				 		if(next_payment_amounts == balances && balances > 0 && payment_plan_id == -1 && invoice_amounts == balances)
				 			success = true;
			 			break;
				 	case "PARTIALLY PAID":
				 		if(next_payment_amounts == balances && balances > 0 && payment_plan_id == -1 && invoice_amounts > balances)
				 			success = true;
				 		break;
				 	default:
				 		if(next_payment_amounts <= balances && balances > 0 && payment_plan_id != -1) {
				 			Dictionary.put(invoicestatus.trim().toUpperCase() + "planId", String.valueOf(payment_plan_id));
				 			success = true;
				 		}
				 }
				 
				 if(success) {
					 invoiceId = Integer.valueOf(invoice.getString("invoice_ids"));
					 break;
				 }
			}
		}
		return invoiceId;
	}
	
	class Invoice implements Comparable<Invoice>{
		String desc;
		String balance;
		Date dueDate;
		Date invoiceDueDate;
		
		Invoice(String desc, String balance, Date dueDate, Date invoiceDueDate) {
			this.desc = desc;
			this.balance = balance;
			this.dueDate = dueDate;
			this.invoiceDueDate = invoiceDueDate;
		}

		@Override
		public int compareTo(Invoice o) {
			return dueDate.compareTo(o.dueDate);
		}
		
		public String getDesc() {
			return desc;
		}
		
		public String getBalance() {
			return balance;
		}
		
		public Date getDueDate() {
			return dueDate;
		}
		
		public Date getInvoiceDueDate() {
			return invoiceDueDate;
		}
	}
	
	public List<List<String>> getSortedInvoiceList(String AccountId, String invoicestatus) throws JSONException, Exception{
		JSONObject obj = update(parseJsonFile(invoiceRequest), "acct_id", AccountId);
		String payload = update(obj, "dsn", Environment.get("DSN").trim()).toString();
		String response = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "InvoiceListArray", response);
		
		JSONObject jsonObj = new JSONObject(response);
		if(!jsonObj.getJSONObject("command1").has("invoices")) {
			throw new SkipException("No invoices found for accountId - " + AccountId);
		}
		JSONArray invoices = jsonObj.getJSONObject("command1").getJSONArray("invoices");
		
		List<Invoice> unpaidinvoices = new ArrayList<Invoice>();
		List<Invoice> paidinvoices = new ArrayList<Invoice>();
		
		NumberFormat in = NumberFormat.getCurrencyInstance(Locale.US);
		
		for(int i = 0 ; i < invoices.length(); i++) {
			JSONObject invoice = invoices.getJSONObject(i);
			float next_payment_amounts = Float.valueOf(invoice.getString("next_payment_amounts"));
			float balances = Float.valueOf(invoice.getString("balances"));
			String invoiceName = invoice.getString("invoice_descriptions");
			if(next_payment_amounts == balances && balances == 0.00) {
				String invoideDueDate = invoice.getString("due_dates");
				String invoiceDate = invoice.getString("invoice_dates");
				SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
				Date dueDate = sm.parse(invoiceDate);
				Date invoiceDDate = sm.parse(invoideDueDate);
				String newamt = in.format(balances).replace("$", Environment.get("currency"));
				Invoice _invoice = new Invoice(invoiceName, newamt, dueDate, invoiceDDate);
				paidinvoices.add(_invoice);
			} else {
				String invoiceDate = invoice.getString("due_dates");
				SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
				Date dueDate = sm.parse(invoiceDate);
				String newamt = in.format(balances).replace("$", Environment.get("currency"));
				Invoice _invoice = new Invoice(invoiceName, newamt, dueDate, dueDate);
				unpaidinvoices.add(_invoice);
			}
		}
		
		Collections.sort(unpaidinvoices);
		Collections.sort(paidinvoices, Collections.reverseOrder());
		
		List<List<String>> sortedList = new ArrayList<List<String>>();
		
		for(int i = 0; i < unpaidinvoices.size(); i++) {
			Invoice _invoice = unpaidinvoices.get(i);
			List<String> invoice = new ArrayList<String>();
			invoice.add(_invoice.getDesc());
			invoice.add(_invoice.getBalance());
			
//			SimpleDateFormat sm = new SimpleDateFormat("MM/dd/yyyy");
//			invoice.add(sm.format(_invoice.getInvoiceDueDate()));
			sortedList.add(invoice);
		}
		
		for(int i = 0; i < paidinvoices.size(); i++) {
			Invoice _invoice = paidinvoices.get(i);
			List<String> invoice = new ArrayList<String>();
			invoice.add(_invoice.getDesc());
			invoice.add(_invoice.getBalance());
			
//			SimpleDateFormat sm = new SimpleDateFormat("MM/dd/yyyy");
//			invoice.add(sm.format(_invoice.getInvoiceDueDate()));
			sortedList.add(invoice);
		}
		
		return sortedList;
	}
	
	public int getPaymentPlanId(String AccountId, int invoiceId, String invoicestatus) throws JSONException, IOException, Exception {
		JSONObject obj = update(parseJsonFile(invoiceRequest), "acct_id", AccountId);
		String payload = update(obj, "dsn", Environment.get("DSN").trim()).toString();
		String response = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "InvoiceListArray", response);
		
		JSONObject jsonObj = new JSONObject(response);
		JSONArray invoices = jsonObj.getJSONObject("command1").getJSONArray("invoices");
		for(int i = 0 ; i < invoices.length(); i++){
			JSONObject invoice = invoices.getJSONObject(i);
			float next_payment_amounts = Float.valueOf(invoice.getString("next_payment_amounts"));
			float balances = Float.valueOf(invoice.getString("balances"));
			int payment_plan_id = invoice.has("payment_plan_id") ? Integer.valueOf(invoice.getString("payment_plan_id")) : -1;
			
	 		if(next_payment_amounts <= balances && balances > 0 && payment_plan_id != -1) {
	 			if(invoiceId == Integer.valueOf(invoice.getString("invoice_ids"))) {
	 				Dictionary.put(invoicestatus.trim().toUpperCase() + "planId", String.valueOf(payment_plan_id));
	 				break;
	 			}
	 		}
		}
		
		return Integer.valueOf(Dictionary.get(invoicestatus.trim().toUpperCase() + "planId"));
	}
	
	public int getPaymentPlanIdFromName(String planName, String invoicestatus) throws Exception {
		JSONObject getPaymentPlanDetail = parseJsonFile(paymentsPlanDetails);
		getPaymentPlanDetail = update(getPaymentPlanDetail, "dsn", Environment.get("DSN").trim());
		String response = utils.waitForTMInvoiceResponse(getPaymentPlanDetail.toString());
		Dictionary.put(invoicestatus.trim().toUpperCase() + "getPaymentPlanDetails", response);
		
		JSONObject jsonObject = new JSONObject(Dictionary.get(invoicestatus.trim().toUpperCase() + "getPaymentPlanDetails").trim());
		JSONObject command1 =(JSONObject) jsonObject.get("command1");
		JSONArray plans=(JSONArray) command1.get("plans");
		
		int planId = -1;
		
		for(int i = 0; i < plans.length(); i++){
			JSONObject plan = plans.getJSONObject(i);
			String planLongName = plan.getString("plan_long_name").trim();
			if(planLongName.equals(planName.trim())){
				planId = Integer.valueOf(plan.getString("payment_plan_id").trim());
				break;
			}
		}
		
		return planId;
	}
		
	public void getInvoiceDetails(String AccountId, int invoiceId, String invoicestatus) throws Exception{
		JSONObject invoiceWithAccount = update(parseJsonFile(invoiceDetails), "acct_id", AccountId);
		invoiceWithAccount = update(invoiceWithAccount, "dsn", Environment.get("DSN").trim());
		String payload = update(invoiceWithAccount, "invoice_id", String.valueOf(invoiceId)).toString();
		String response = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "invoiceDetails", response);
		System.out.println(Dictionary.get(invoicestatus.trim().toUpperCase() + "invoiceDetails"));
	}
	
	public void getPaymentPlans(String AccountId, int invoiceId, String invoicestatus) throws Exception{
		JSONObject invoiceWithAccount=update(parseJsonFile(paymentPlans), "acct_id", AccountId);
		invoiceWithAccount = update(invoiceWithAccount, "dsn", Environment.get("DSN").trim());
		String payload = update(invoiceWithAccount, "invoice_id", String.valueOf(invoiceId)).toString();
		String response = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "paymentPlans", response);
	}
	
	public void getPaymentSchedule(String AccountId, int invoiceId, String invoicestatus) throws Exception{
		JSONObject PmtScheduleAccount=update(parseJsonFile(paymentPlansSchedule), "acct_id", AccountId);
		PmtScheduleAccount = update(PmtScheduleAccount, "dsn", Environment.get("DSN").trim());
		JSONObject PmtScheduleInvoice=update(PmtScheduleAccount, "invoice_id", String.valueOf(invoiceId));
		String payload = update(PmtScheduleInvoice, "payment_plan_id", Dictionary.get(invoicestatus.trim().toUpperCase() + "planId")).toString();
		String response = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "paymentPlansSchedule", response);
	}
	
	public void getPaymentScheduleFieldValues(String paymentPlansSchedule, String[] keys, String invoicestatus) throws JSONException{
		JSONObject jsonObject=new JSONObject(paymentPlansSchedule);
		JSONObject command1=(JSONObject) jsonObject.get("command1");
		JSONArray payments=(JSONArray) command1.get("payments");
		JSONObject payment = payments.getJSONObject(0);
		for(int j = 0 ; j < keys.length; j++){
			String value = payment.getString(keys[j]);
			Dictionary.put(invoicestatus.trim().toUpperCase() + keys[j], value);
		}
	}
	
	public void getPaymentPlanDetails(String expectedPlanId, String invoicestatus) throws Exception{
		JSONObject getPaymentPlanDetail = parseJsonFile(paymentsPlanDetails);
		getPaymentPlanDetail = update(getPaymentPlanDetail, "dsn", Environment.get("DSN").trim());
		String payload = getPaymentPlanDetail.toString();
		String response = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "getPaymentPlanDetails", response);
		
		JSONObject jsonObject = new JSONObject(Dictionary.get(invoicestatus.trim().toUpperCase() + "getPaymentPlanDetails").trim());
		JSONObject command1 =(JSONObject) jsonObject.get("command1");
		if(command1.has("plans")) {
			JSONArray plans=(JSONArray) command1.get("plans");
			
			for(int i = 0; i < plans.length(); i++){
				JSONObject plan = plans.getJSONObject(i);
				String planId = plan.getString("payment_plan_id");
				if(planId.trim().equalsIgnoreCase(expectedPlanId.trim())){
					String planLongName = plan.getString("plan_long_name");
					Dictionary.put(invoicestatus.trim().toUpperCase() + "PLAN_LONG_NAME", planLongName);
					break;
				}
			}
		}
	}
	
	public void getCCQuery(String AccountId, String invoicestatus) throws Exception{
		JSONObject obj = update(parseJsonFile(ccQuery), "acct_id", AccountId);
		obj = update(obj, "dsn", Environment.get("DSN").trim());
		String payload = obj.toString();
		String response = utils.waitForTMInvoiceResponse(payload);
		Dictionary.put(invoicestatus.trim().toUpperCase() + "cc_Query", response);
	}
	
	public HashMap<String, String> getInvoiceFieldValues(String invoicesLists, String[] keys, int invoiceId, String invoicestatus) throws JSONException{
		JSONObject jsonObject=new JSONObject(invoicesLists);
		JSONObject command1=(JSONObject) jsonObject.get("command1");
		
		JSONArray invoices=(JSONArray) command1.get("invoices");
		
		HashMap<String, String> invoiceFields = new HashMap<String, String>();
		
		for(int i = 0 ; i < invoices.length(); i++){
			JSONObject obj = invoices.getJSONObject(i);
			if(invoiceId == obj.getInt("invoice_ids")){
				for(int j = 0; j < keys.length; j++){
					String value = obj.getString(keys[j]);
					invoiceFields.put(keys[j], value);
					Dictionary.put(invoicestatus.trim().toUpperCase() + keys[j], value);
				}
			}
		}
		return invoiceFields;
	}
	
	public String getTotalAmountDue(String invoicesLists) throws JSONException{
		double due=0;
		JSONObject jsonObject=new JSONObject(invoicesLists);
		JSONObject command1=(JSONObject) jsonObject.get("command1");
		JSONArray invoices=(JSONArray) command1.get("invoices");
		
		for(int i = 0 ; i < invoices.length(); i++) {
			JSONObject obj = invoices.getJSONObject(i);
			double value = obj.getDouble("balances");
			due=value+due;
		}
		DecimalFormat df=new DecimalFormat("0.00");
		String newDue = df.format(due);
		return newDue;
	}
	
   public void getCCQueryFieldValues(String ccQuery, String[] keys, String invoicestatus) throws JSONException{
		JSONObject jsonObject=new JSONObject(ccQuery);
		JSONObject command1=(JSONObject) jsonObject.get("command1");
		if(command1.has("credit_card")) {
			JSONArray credit_card=(JSONArray) command1.get("credit_card");
			JSONObject creditcard = credit_card.getJSONObject(0);
			
			for(int i = 0 ; i < keys.length; i++){
				String value = creditcard.getString(keys[i]);
				Dictionary.put(invoicestatus.trim().toUpperCase() + keys[i], value);
			}
		}
	}
   
   public void getInvoiceDetailFields1(String invoiceDetails,String[] keys, String invoicestatus) throws JSONException{
	   JSONObject jsonObject=new JSONObject(invoiceDetails);
	   JSONObject command1=(JSONObject) jsonObject.get("command1");
	   JSONArray items=(JSONArray) command1.get("items");
	   JSONObject item=items.getJSONObject(0);
	   for (int i=0;i<keys.length;i++) {
		   Object value = item.get(keys[i]);
		   Dictionary.put(invoicestatus.trim().toUpperCase() + keys[i], value.toString());
	   }
   }
		   
   public void getInvoiceDetailFields(String invoiceDetails,String[] keys, String invoicestatus) throws JSONException{
	   JSONObject jsonObject=new JSONObject(invoiceDetails);
	   JSONObject command1=(JSONObject) jsonObject.get("command1");
	   JSONArray items=(JSONArray) command1.get("items");
	   JSONObject item=items.getJSONObject(0);

	   for (int i=0;i<keys.length;i++){
		   if(item.has(keys[i])) {
			   Object value = item.get(keys[i]);
			   Dictionary.put(invoicestatus.trim().toUpperCase() + keys[i], value.toString());
		   }
	   }
   }
   
   public void getPaymentScheduleDetails(String paymentSchedule, String invoicestatus){
	   JSONObject jsonObject=new JSONObject(paymentSchedule);
	   JSONObject command1=(JSONObject) jsonObject.get("command1");
	   JSONArray schedule=(JSONArray) command1.get("payments");

	   for(int j = 0; j < schedule.length(); j++) {
		   Dictionary.put(invoicestatus.trim().toUpperCase() + "payment_due_date"+j, schedule.getJSONObject(j).getString("payment_due_date"));
		   Dictionary.put(invoicestatus.trim().toUpperCase() + "owed_amount"+j, schedule.getJSONObject(j).getString("owed_amount"));
		   System.out.println(Dictionary.get(invoicestatus.trim().toUpperCase() + "owed_amount"+j));
		   System.out.println(Dictionary.get(invoicestatus.trim().toUpperCase() + "payment_due_date"+j));
	   }
   }

   public void getPlanDetails(String planDetailsResponse, int planId, String invoicestatus) throws Exception {
	   JSONObject jsonObject=new JSONObject(planDetailsResponse);
	   JSONObject command1=(JSONObject) jsonObject.get("command1");
	   JSONArray plans=(JSONArray) command1.get("plans");

	   for(int i = 0 ; i < plans.length(); i++){
		   JSONObject obj = plans.getJSONObject(i);
		   if(planId == obj.getInt("payment_plan_id")){
			   Dictionary.put(invoicestatus.trim().toUpperCase() + "plan_long_name", obj.getString("plan_long_name"));
			   JSONArray planDetails=(JSONArray) obj.get("payment_plan_periods");
			   for(int j = 0; j < planDetails.length(); j++){
				   Dictionary.put(invoicestatus.trim().toUpperCase() + "payment_number"+j, planDetails.getJSONObject(j).getString("payment_number"));
				   Dictionary.put(invoicestatus.trim().toUpperCase() + "payment_due_date"+j, planDetails.getJSONObject(j).getString("payment_due_date"));
				   System.out.println(Dictionary.get(invoicestatus.trim().toUpperCase() + "payment_number"+j));
				   System.out.println(Dictionary.get(invoicestatus.trim().toUpperCase() + "payment_due_date"+j));
			   }
		   }
	   }
   }
   
   public void runInvoiceApis(String emailaddress, String password, int invoiceId, String invoicestatus) throws Exception {
	   String accessToken = accessTokens.getAccessToken(emailaddress, password);
	   System.out.println(accessToken);
	   String accId= accessTokens.getAccountId(accessToken);
		
//	   Driver.sDriver.get().navigate().to(Environment.get("strUrl").trim());			
//	   adminLogin();
	   Dictionary.put(invoicestatus.trim().toUpperCase() + "invoiceId", String.valueOf(invoiceId));
	   
	   int planId = getPaymentPlanIdFromName(Dictionary.get(invoicestatus.trim().toUpperCase() + "Payment_Plan"), invoicestatus);
	   Dictionary.put(invoicestatus.trim().toUpperCase() + "planId", String.valueOf(planId));
	   getPaymentSchedule(accId, invoiceId, invoicestatus);
	   getPaymentScheduleFieldValues(Dictionary.get(invoicestatus.trim().toUpperCase() + "paymentPlansSchedule"),new String[]{"amount_due_today","payment_period_amount"}, invoicestatus);
   	}
   
   public void beforeMtdInvoiceAPI(String invoicestatus, boolean... needAccessToken) throws Exception{
	   String emailaddress = Dictionary.get("EMAIL_ADDRESS");
	   String password = Dictionary.get("PASSWORD");
	   String accessToken = "";
	   String accId= "";
		
	   if (!invoicestatus.trim().equalsIgnoreCase("")) {
		   emailaddress = Dictionary.get(invoicestatus.trim().toUpperCase() + "_EMAIL_ADDRESS");
		   password = Dictionary.get(invoicestatus.trim().toUpperCase() + "_PASSWORD");
	   }
	   
	   assert needAccessToken.length <= 1;
		  boolean needtoken = needAccessToken.length > 0 ? needAccessToken[0] : true;
		  if(needtoken) {
			  accessToken = accessTokens.getAccessToken(emailaddress, password);
			  accId= accessTokens.getAccountId(accessToken);
		  } else {
			  accessToken = Dictionary.get("AccessToken").trim();
			  accId=Dictionary.get("accId1");
			  if(accessToken.equalsIgnoreCase("")) {
				  accessToken = accessTokens.getAccessToken(emailaddress, password);
				  accId=accessTokens.getAccountId(accessToken);
			  }
		  }
		  
//	   String accessToken = accessTokens.getAccessToken(emailaddress, password);
//	   String accId= accessTokens.getAccountId(accessToken);
		
//	   Driver.sDriver.get().navigate().to(Environment.get("strUrl").trim());			
//	   adminLogin();
		
	   int invoiceId = getInvoiceListAndId(accId, invoicestatus);
	   if(invoiceId != -1){
		   Dictionary.put(invoicestatus.trim().toUpperCase() + "invoiceId", String.valueOf(invoiceId));
		   getInvoiceDetails(accId, invoiceId, invoicestatus);
		   if(!Dictionary.get(invoicestatus.trim().toUpperCase() + "invoiceDetails").trim().equalsIgnoreCase(""))
			   getInvoiceDetailFields(Dictionary.get(invoicestatus.trim().toUpperCase() + "invoiceDetails"), new String[]{"num_seat","section_name","row_name","seat_num","last_seat","unit_price","item_name_long","invoiced_amount","paid_amount","current_due_amount"}, invoicestatus);
	   }
		
	   if(invoicestatus.trim().equalsIgnoreCase("PLAN") || invoicestatus.trim().equalsIgnoreCase("UNPAID") || invoicestatus.trim().equalsIgnoreCase("PARTIALLY PAID")){
		   if(!needtoken) {
			   accId=Dictionary.get("accId0");
			   getCCQuery(accId, invoicestatus);
		   } else {
			   getCCQuery(accId, invoicestatus);
		   }
		   if(invoiceId != -1){
			   getPaymentPlans(accId, invoiceId, invoicestatus);
			   getPaymentSchedule(accId, invoiceId, invoicestatus);
		   }
		   getPaymentPlanDetails(Dictionary.get(invoicestatus.trim().toUpperCase() + "planId").trim(), invoicestatus);
		   getCCQueryFieldValues(Dictionary.get(invoicestatus.trim().toUpperCase() + "cc_Query"),new String[]{"cc_name_first","cc_name_last","cc_postal_code","cc_address","cc_exp","data_mask"}, invoicestatus) ;
		}
   }
   
   public JSONObject updateJSONArrays(JSONObject obj, String[] keyName, List<List<String>> keyValue) throws IOException{
       	for(int i = 0; i< keyName.length; i++){
	   		if(obj.has(keyName[i])){
	   			obj.put(keyName[i], (List<String>)keyValue.get(i));
	   		}
       	}
       	return obj;
	}
}