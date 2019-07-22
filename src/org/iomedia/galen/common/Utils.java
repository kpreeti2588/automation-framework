package org.iomedia.galen.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.ssl.SSLContexts;
import org.iomedia.common.BaseUtil;
import org.iomedia.framework.Driver.HashMapNew;
import org.iomedia.galen.framework.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.iomedia.framework.Assert;
import org.iomedia.framework.Reporting;
import org.iomedia.framework.SoftAssert;
import org.iomedia.framework.WebDriverFactory;

public class Utils extends BaseUtil {
	
	private static long DEFAULT_FIND_ELEMENT_TIMEOUT;
	private String driverType;
	
	public Utils(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment, Reporting Reporter, org.iomedia.framework.Assert Assert, org.iomedia.framework.SoftAssert SoftAssert, ThreadLocal<HashMapNew> sTestDetails) {
		super(driverFactory, Dictionary, Environment, Reporter, Assert, SoftAssert, sTestDetails);
		Dictionary = Dictionary == null || Dictionary.size() == 0 ? (driverFactory.getDictionary() == null ? null : driverFactory.getDictionary().get()) : Dictionary;
		Environment = Environment == null || Environment.size() == 0 ? (driverFactory.getEnvironment() == null ? null : driverFactory.getEnvironment().get()) : Environment;
		DEFAULT_FIND_ELEMENT_TIMEOUT = Long.valueOf(Environment.get("implicitWait")) / 1000;
		driverType = driverFactory.getDriverType().get();
	}
	
	private By accept = By.id("accept-button");
	private By agree = By.cssSelector("button.default");
	private By yesButton = By.xpath(".//div[@class='popup']//button[text()='Yes']");
	private By submitBtn = By.xpath(".//button[@type='submit']");
	private By rememberCheckbox = By.cssSelector("form div[class*='container-fluid'] div[class*='field'] label[data-react-toolbox='checkbox'] div");
	private By termsConditionsCheckBox = By.cssSelector("form div[class*='container-fluid'] div[class*='midFormLink'] > label div");
//	private By surveyBlock = By.cssSelector(".accordionSurvey:not([class*='invoice-hide'])");
	
	public WebElement getElementWhenRefreshed(final By locater, final String attribute, long... waitSeconds){
		assert waitSeconds.length <= 1;
		long seconds = waitSeconds.length > 0 ? waitSeconds[0] : DEFAULT_FIND_ELEMENT_TIMEOUT;
		WebElement we = null;
		int counter = 0;
		do{
			long time = 20;
			if(seconds <= 20)
				time = seconds;
			WebDriverWait wait  = new WebDriverWait(getDriver(), time);
			try{
				Boolean val = wait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						String value = getDriver().findElement(locater).getAttribute(attribute);
						return value == null ? true : !value.trim().equalsIgnoreCase(""); 
					}
					
				}));
				if(val){
					we = getDriver().findElement(locater);
					break;
				}
			}
			catch(Exception ex){
				boolean flag = false;
				if(!Environment.get("methodHandleUnwantedPopups").trim().equalsIgnoreCase("")){
					String[] words = Environment.get("methodHandleUnwantedPopups").trim().split("\\.");
					String methodName = words[words.length - 1];
					String className = Environment.get("methodHandleUnwantedPopups").trim().substring(0, Environment.get("methodHandleUnwantedPopups").trim().indexOf("." + methodName));
					Object[] params = new Object[0];
					Class<?> thisClass;
					try {
						thisClass = Class.forName(className);
						Object busFunctions = thisClass.getConstructor(new Class[] {WebDriverFactory.class, HashMapNew.class, HashMapNew.class, Reporting.class, Assert.class, SoftAssert.class, ThreadLocal.class }).newInstance(new Object[] {this.driverFactory, this.Dictionary, this.Environment, this.Reporter, this.Assert, this.SoftAssert, this.sTestDetails });
						Method method = thisClass.getDeclaredMethod(methodName, new Class[0]);
						Object objReturn = method.invoke(busFunctions, params);
						if (objReturn.equals(Boolean.valueOf(true))) {
							flag = true;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					
				}
				if(flag && seconds <= 20){
					try{
						Boolean val = wait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>(){
							@Override
							public Boolean apply(WebDriver driver) {
								String value = getDriver().findElement(locater).getAttribute(attribute);
								return value == null ? true : !value.trim().equalsIgnoreCase("");
							}
							
						}));
						if(val){
							we = getDriver().findElement(locater);
							break;
						}
					} catch(Exception ex1){
						String value = getDriver().findElement(locater).getAttribute(attribute);
						Reporter.log(locater.toString() + " - " + attribute, "", value, "Done");
						throw ex1;
					}
				}
				else{
					if(counter >= seconds || seconds <= 20) {
						String value = getDriver().findElement(locater).getAttribute(attribute);
						Reporter.log(locater.toString() + " - " + attribute, "", value, "Done");
						throw ex;
					}
				}
			}
			finally{
				counter += time;
			}
		}while(true);
		
		return we;
	}
	
	public WebElement getInvoiceWhenSelected(final By locater, final String attribute, final String text, long... waitSeconds){
		assert waitSeconds.length <= 1;
		long seconds = waitSeconds.length > 0 ? waitSeconds[0] : DEFAULT_FIND_ELEMENT_TIMEOUT;
		WebElement we = null;
		int counter = 0;
		do{
			long time = 20;
			if(seconds <= 20)
				time = seconds;
			WebDriverWait wait  = new WebDriverWait(getDriver(), time);
			try{
				Boolean val = wait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						String value = getDriver().findElement(locater).getAttribute(attribute);
						if(attribute.trim().equalsIgnoreCase("disabled"))
							return value == null ? true : value.trim().contains(text);
						else
							return value == null ? false : value.trim().contains(text);
					}
					
				}));
				if(val){
					we = getDriver().findElement(locater);
					break;
				}
			}
			catch(Exception ex){
				boolean flag = false;
				if(!Environment.get("methodHandleUnwantedPopups").trim().equalsIgnoreCase("")){
					String[] words = Environment.get("methodHandleUnwantedPopups").trim().split("\\.");
					String methodName = words[words.length - 1];
					String className = Environment.get("methodHandleUnwantedPopups").trim().substring(0, Environment.get("methodHandleUnwantedPopups").trim().indexOf("." + methodName));
					Object[] params = new Object[0];
					Class<?> thisClass;
					try {
						thisClass = Class.forName(className);
						Object busFunctions = thisClass.getConstructor(new Class[] {WebDriverFactory.class, HashMapNew.class, HashMapNew.class, Reporting.class, Assert.class, SoftAssert.class, ThreadLocal.class }).newInstance(new Object[] {this.driverFactory, this.Dictionary, this.Environment, this.Reporter, this.Assert, this.SoftAssert, this.sTestDetails });
						Method method = thisClass.getDeclaredMethod(methodName, new Class[0]);
						Object objReturn = method.invoke(busFunctions, params);
						if (objReturn.equals(Boolean.valueOf(true))) {
							flag = true;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					
				}
				if(flag && seconds <= 20){
					try{
						Boolean val = wait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>(){
							@Override
							public Boolean apply(WebDriver driver) {
								String value = getDriver().findElement(locater).getAttribute(attribute);
								if(attribute.trim().equalsIgnoreCase("disabled"))
									return value == null ? true : value.trim().contains(text);
								else
									return value == null ? false : value.trim().contains(text);
							}
							
						}));
						if(val){
							we = getDriver().findElement(locater);
							break;
						}
					} catch(Exception ex1){
						String value = getDriver().findElement(locater).getAttribute(attribute);
						Reporter.log(locater.toString() + " - " + attribute, text, value, "Done");
						throw ex1;
					}
				}
				else{
					if(counter >= seconds || seconds <= 20) {
						String value = getDriver().findElement(locater).getAttribute(attribute);
						Reporter.log(locater.toString() + " - " + attribute, text, value, "Done");
						throw ex;
					}
				}
			}
			finally{
				counter += time;
			}
		}while(true);
		
		return we;
	}
	
	public boolean envcheck() throws IOException {
		if(Environment.get("TM_OAUTH_URL").trim().equalsIgnoreCase(""))
			return true;
		
		String emailaddress = Environment.get("EMAIL_ADDRESS").trim();
		String password = Environment.get("PASSWORD").trim();
		if(emailaddress.equalsIgnoreCase("") || password.equalsIgnoreCase("") || emailaddress.equalsIgnoreCase("EMPTY") || password.equalsIgnoreCase("EMPTY"))
			return true;
		
		if(Environment.get("TM_OAUTH_URL").trim().endsWith("/")) {
			Environment.put("TM_OAUTH_URL", Environment.get("TM_OAUTH_URL").trim().substring(0, Environment.get("TM_OAUTH_URL").trim().length() - 1));
		}
		
		URL url = new URL(Environment.get("TM_OAUTH_URL").trim());
		Environment.put("ENV_CHECK_URL", Environment.get("TM_OAUTH_URL").trim());
	    Map<String,Object> params = new LinkedHashMap<>();
	    params.put("client_id", Environment.get("CLIENT_ID").trim());
	    params.put("client_secret", Environment.get("CLIENT_SECRET").trim());
	    params.put("grant_type", "password");
	    params.put("username", Environment.get("EMAIL_ADDRESS").trim());
	    params.put("password", Environment.get("PASSWORD").trim());
	    
	    StringBuilder postData = new StringBuilder();
	    for (Map.Entry<String,Object> param : params.entrySet()) {
	        if (postData.length() != 0) postData.append('&');
	        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
	        postData.append('=');
	        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
	    }
	    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

	    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	    
	    String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(conn, rawRequest);
	    
	    conn.setDoOutput(true);
	    conn.getOutputStream().write(postDataBytes);
	    
		try{
			int responseCode = ((HttpURLConnection)conn).getResponseCode();
			String responseMessage = ((HttpURLConnection)conn).getResponseMessage();
			System.out.println("Environment check response code : " + responseCode);
			Environment.put("ENV_CHECK_RESPONSE_CODE", String.valueOf(responseCode + " " + responseMessage));
	        
	        rawResponse += getResponseHeaders(conn, rawResponse) + "\n";
	        rawRequest += "Url : " + Environment.get("ENV_CHECK_URL").trim() + "\n";
	        rawRequest += "Request method : POST\n\n";
	        rawRequest += postData.toString() + "\n";
	        
	        if(responseCode != 200 && responseCode != 201 && responseCode != 204) {
	        	InputStream[] input = getClonedStream(conn.getErrorStream(), 2);
	        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
	        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
	        	Environment.put("ENV_CHECK_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
	        	Environment.put("ENV_CHECK_OUTPUT", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
	        }
	        
	        if(responseCode == 200 || responseCode == 201 || responseCode == 204) {
				String cookies = loginThruDrupalApi(emailaddress, password);
				String versionNumber = getTerms(cookies);
				String token = getCsrfToken(cookies);
				acceptTerms(cookies, token, versionNumber);
	        }
			
			return responseCode == 200 || responseCode == 201 || responseCode == 204 ? true : false;
		} catch(Exception ex){
			return true;
		}
	}
	
	public String loginThruDrupalApi(String emailaddress, String password) throws Exception {
		Util util = new Util(Environment);
		String sessionCookie = util.post(Environment.get("APP_URL") + "/user/login?_format=json", "{\"name\":\"" + emailaddress + "\",\"pass\":\"" + password + "\",\"remember_me\":0}", new String[]{"accept", "accept-encoding", "accept-language", "content-type", "user-agent"}, new String[]{"application/json, text/plain, */*", "gzip, deflate, br", "en-GB,en-US;q=0.8,en;q=0.6", "application/json", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"});
		return sessionCookie;
	}
	   
	public String getTerms(String cookies) throws Exception {
		Util util = new Util(Environment);
		InputStream is = util.get(Environment.get("APP_URL") + "/api/text/terms?_format=json", new String[]{"accept", "accept-encoding", "accept-language", "user-agent", "cookie"}, new String[]{"application/json, text/plain, */*", "gzip, deflate, br", "en-GB,en-US;q=0.8,en;q=0.6", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1", cookies});
		JSONObject jsonObject = util.convertToJSON(new BufferedReader(new InputStreamReader(is, "UTF-8")));
		return jsonObject.has("version") ? jsonObject.getString("version") : "";
	}
	   
	public void acceptTerms(String cookies, String token, String versionNumber) throws Exception {
		if(!versionNumber.trim().equalsIgnoreCase("")) {
			Util util = new Util(Environment);
			util.post(Environment.get("APP_URL") + "/api/accept/terms?_format=json", "{\"response\" : true, \"version\" : \"" +versionNumber + "\"}", new String[]{"accept", "accept-encoding", "accept-language", "user-agent", "cookie", "content-type", "x-csrf-token"}, new String[]{"application/json, text/plain, */*", "gzip, deflate, br", "en-GB,en-US;q=0.8,en;q=0.6", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1", cookies, "application/json", token});
		}
	}
	
	public String getCsrfToken(String cookies) throws Exception {
		Util util = new Util(Environment);
		InputStream is = util.get(Environment.get("APP_URL") + "/rest/session/token", new String[]{"accept", "accept-encoding", "accept-language", "user-agent", "cookie"}, new String[]{"application/json, text/plain, */*", "gzip, deflate, br", "en-GB,en-US;q=0.8,en;q=0.6", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1", cookies});
		Scanner scan = new Scanner(is);
		String token = new String();
		while (scan.hasNext())
			token += scan.nextLine();
		scan.close();
		return token;
	}
	
	public boolean handlePopups() throws IOException {
//		boolean success = envcheck();
//		if(!success)
//			throw new AssertionError(Environment.get("env") + " is not up. Automation execution aborted.");
		boolean flag = false;
		String screenText = "";
		boolean runLocally = System.getProperty("runLocally") != null && !System.getProperty("runLocally").trim().equalsIgnoreCase("") ? Boolean.valueOf(System.getProperty("runLocally").trim().toLowerCase()) : Boolean.valueOf(Environment.get("runLocally").trim().toLowerCase());
		boolean safari = driverType.trim().toUpperCase().contains("SAFARI");
		try{
			if(runLocally || !safari)
				screenText = getDriver().getPageSource();
		} catch(Exception ex){
			//Do Nothing
		}
		if(screenText.trim().toLowerCase().contains(">agree<") || safari){
			if(checkIfElementPresent(agree, 1)) {
				getDriver().findElement(agree).click();
				flag = true;
			}
		}
		if(screenText.trim().toLowerCase().contains("button in buttons") || safari){
			if(checkIfElementPresent(yesButton, 1)) {
				getDriver().findElement(yesButton).click();
				flag = true;
			}
		}
		if(screenText.trim().toLowerCase().contains(">accept<") || safari){
			if(checkIfElementPresent(accept, 1)) {
				getDriver().findElement(accept).click();
				flag = true;
			}
		}
		if(screenText.trim().toLowerCase().contains(">Remember Me<") || safari) {
			if(checkIfElementPresent(rememberCheckbox, 1) && checkIfElementPresent(submitBtn, 1)) {
				getDriver().findElement(submitBtn).click();
				flag = true;
			}
		}
		if(screenText.trim().toLowerCase().contains("account-midFormLink") || safari) {
			if(checkIfElementPresent(termsConditionsCheckBox, 1) && checkIfElementPresent(submitBtn, 1)) {
				getDriver().findElement(submitBtn).click();
				flag = true;
			}
		}
//		if(screenText.trim().toLowerCase().contains("accordionsurvey") || safari){
//			if(checkIfElementPresent(surveyBlock, 1)) {
//				driverFactory.getDriver().get().switchTo().frame(0);
//				try {
//					fillSurveyForm();
//				} finally {
//					driverFactory.getDriver().get().switchTo().parentFrame();
//				}
//				flag = true;
//			}
//		}
		return flag;
	}
	
	private WebElement getElementWhenVisible(By locator) {
		WebDriverWait wait  = new WebDriverWait(getDriver(), 1);
		try{
			return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		} catch(Exception ex) {
			return null;
		}
	}
	
	private boolean typeTextBox(By locator, By ok, boolean flag) {
		if(!flag) {
			WebElement wtextbox = getElementWhenVisible(locator);
			if(wtextbox != null) {
				wtextbox.sendKeys("Test");
				click(ok, "Ok");
				flag = true;
			}
		}
		return flag;
	}
	
	private boolean clickRadioButton(By locator, By ok, boolean flag) {
		if(!flag) {
			WebElement wradiobutton = getElementWhenVisible(locator);
			if(wradiobutton != null) {
				wradiobutton.click();
				click(ok, "Ok");
				flag = true;
			}
		}
		return flag;
	}
	
	private boolean selectDropDownValue(By locator, By ok, boolean flag) {
		if(!flag) {
			WebElement wdropdown = getElementWhenVisible(locator);
			if(wdropdown != null) {
				Select _select = new Select(wdropdown);
				_select.selectByIndex(1);
				click(ok, "Ok");
				flag = true;
			}
		}
		return flag;
	}
	
	private boolean selectCheckbox(By locator, By ok, boolean flag) {
		if(!flag) {
			WebElement wcheckbox = getElementWhenVisible(locator);
			if(wcheckbox != null) {
				wcheckbox.click();
				click(ok, "Ok");
				flag = true;
			}
		}
		return flag;
	}
	
	public void fillSurveyForm() {
		By ok = By.cssSelector("#typeform li div[class*='button'] span:first-child");
		By submit = By.cssSelector("#typeform #unfixed-footer div[class*='submit']");
		By textBox = By.cssSelector("#typeform li input[type='text']");
		By radioButton = By.cssSelector("#typeform li input[type='radio']");
		By checkBox = By.cssSelector("#typeform li input[type='checkbox']");
		By dropDown = By.cssSelector("#typeform li select[name*='question']");
		WebElement wesubmit = null;
		boolean flag = false;
		do {
			flag = false;
			flag = typeTextBox(textBox, ok, flag);
			flag = clickRadioButton(radioButton, ok, flag);
			flag = selectDropDownValue(dropDown, ok, flag);
			flag = selectCheckbox(checkBox, ok, flag);
			if(flag) {
				wesubmit = getElementWhenVisible(submit);
			}
		} while(flag && wesubmit == null);
		wesubmit.click();
	}
	
	public WebElement getElementWhenEditable(final By locater, final String attribute, final String text, long... waitSeconds) {
		assert waitSeconds.length <= 1;
		long seconds = waitSeconds.length > 0 ? waitSeconds[0] : DEFAULT_FIND_ELEMENT_TIMEOUT;
		WebElement we = null;
		int counter = 0;
		do{
			long time = 20;
			if(seconds <= 20)
				time = seconds;
			WebDriverWait wait  = new WebDriverWait(getDriver(), time);
			try{
                Boolean val = wait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver driver) {
						String value = "";
						if(driverType.trim().toUpperCase().contains("FIREFOX") && attribute.trim().equalsIgnoreCase("innerHTML")) {
							value = getDriver().findElement(locater).getText();
						} else {
							value = getDriver().findElement(locater).getAttribute(attribute);
						}
                        return value == null ? false : value.trim().contains(text); 
					}
					
				}));
				if(val){
					we = getDriver().findElement(locater);
					break;
				}
			}
			catch(Exception ex){
				boolean flag = false;
				if(!Environment.get("methodHandleUnwantedPopups").trim().equalsIgnoreCase("")){
					String[] words = Environment.get("methodHandleUnwantedPopups").trim().split("\\.");
					String methodName = words[words.length - 1];
					String className = Environment.get("methodHandleUnwantedPopups").trim().substring(0, Environment.get("methodHandleUnwantedPopups").trim().indexOf("." + methodName));
					Object[] params = new Object[0];
					Class<?> thisClass;
					try {
						thisClass = Class.forName(className);
						Object busFunctions = thisClass.getConstructor(new Class[] {WebDriverFactory.class, HashMapNew.class, HashMapNew.class, Reporting.class, Assert.class, SoftAssert.class, ThreadLocal.class }).newInstance(new Object[] {this.driverFactory, this.Dictionary, this.Environment, this.Reporter, this.Assert, this.SoftAssert, this.sTestDetails });
						Method method = thisClass.getDeclaredMethod(methodName, new Class[0]);
						Object objReturn = method.invoke(busFunctions, params);
						if (objReturn.equals(Boolean.valueOf(true))) {
							flag = true;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					
				}
				if(flag && seconds <= 20){
					try{
						Boolean val = wait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>(){
							@Override
							public Boolean apply(WebDriver driver) {
								String value = "";
								if(driverType.trim().toUpperCase().contains("FIREFOX") && attribute.trim().equalsIgnoreCase("innerHTML")) {
									value = getDriver().findElement(locater).getText();
								} else {
									value = getDriver().findElement(locater).getAttribute(attribute);
								}
		                        return value == null ? false : value.trim().contains(text); 
							}
						}));
						if(val){
							we = getDriver().findElement(locater);
							break;
						}
					} catch(Exception ex1){
						String value = "";
						if(driverType.trim().toUpperCase().contains("FIREFOX") && attribute.trim().equalsIgnoreCase("innerHTML")) {
							value = getDriver().findElement(locater).getText();
						} else {
							value = getDriver().findElement(locater).getAttribute(attribute);
						}
						Reporter.log(locater.toString() + " - " + attribute, text, value, "Done");
						throw ex1;
					}
				}
				else{
					if(counter >= seconds || seconds <= 20) {
						String value = "";
						if(driverType.trim().toUpperCase().contains("FIREFOX") && attribute.trim().equalsIgnoreCase("innerHTML")) {
							value = getDriver().findElement(locater).getText();
						} else {
							value = getDriver().findElement(locater).getAttribute(attribute);
						}
						Reporter.log(locater.toString() + " - " + attribute, text, value, "Done");
						throw ex;
					}
				}
			}
			finally{
				counter += time;
			}
		}while(true);
		
		return we;
	}

	public void navigateTo(String uri) {
		By logout = By.xpath(".//input[@name='email'] | .//div[@class='mobile-signin']//span[text()='Sign In']");
		if(uri.trim().contains("logout")) {
			String currentUrl = getDriver().getCurrentUrl();
			String appurl = Environment.get("APP_URL").trim();
			if(appurl.trim().endsWith("/"))
				appurl = appurl.trim().substring(0, appurl.trim().length() - 1);
			String clientId = appurl.substring(appurl.lastIndexOf("/") + 1).trim();
			if(currentUrl.trim().contains(clientId.trim()) && checkIfElementPresent(logout, 1)) {
				return;
			}
		}
		sync(2000L);
		getDriver().navigate().to(Environment.get("APP_URL").trim() + uri);
		if(uri.trim().contains("logout")) {
			getElementWhenPresent(logout);
		}
		try {
			Object obj = ((JavascriptExecutor) getDriver()).executeScript("var obj = drupalSettings.componentConfigData.siteconfig;return JSON.stringify(obj);");
			JSONObject json = new JSONObject(obj.toString());
			Environment.put("currency", json.has("currency")? json.getString("currency") : "$");
			((JavascriptExecutor) getDriver()).executeScript("$('#doorbell-button').remove()");
		} catch(Exception ex) {
			//Do Nothing
		}
		Assert.assertTrue(true, "Verify page launched - " + Environment.get("APP_URL").trim() + uri);
	}
	
	public String waitForTMInvoiceResponse(String payload) throws JSONException, Exception {
		String response = null;
		int counter = 5;
		String cookies = null;
		do{
			String[] allfields = getTMInvoiceAPIResponse(payload, cookies);
			response = allfields[0];
			cookies = allfields[1];
			sync(100L);
			counter--;
		} while(counter > 0 && (response == null || response.trim().equalsIgnoreCase("")));
		return response;
	}
	
	public String getResponseCookies(URLConnection urlCon) {
		Map<String, List<String>> responseheaders = urlCon.getHeaderFields();
        Set<String> keys = responseheaders.keySet();
        Iterator<String> iter = keys.iterator();
        String cookies = "";
        while(iter.hasNext()){
        	String keyName = iter.next();
        	if(keyName == null || keyName.trim().equalsIgnoreCase("null"))
        		continue;
        	if ("Set-Cookie".equalsIgnoreCase(keyName)) {
        		List<String> headerFieldValue = responseheaders.get(keyName);
        		for (String headerValue : headerFieldValue) {
        			String[] fields = headerValue.split(";\\s*");
        			String cookieValue = fields[0];
        			cookies += cookieValue + ";";
        		}
        		break;
        	}
        }
        return cookies;
	}
	
	public String[] getTMInvoiceAPIResponse(String payload, String cookies) throws JSONException, Exception {
		String keyPassphrase = "";

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(new FileInputStream("live.iomedia_tm_v2.pfx"), keyPassphrase.toCharArray());

		SSLContext sslContext = SSLContexts.custom()
		        .loadKeyMaterial(keyStore, null)
		        .build();
        
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();            
        String url = "https://ws.ticketmaster.com/archtics/ats/ticketing_services.aspx?dsn=" + Environment.get("DSN").trim();
        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "GET");
		urlCon.setRequestProperty("Accept", "application/json");
		if(cookies != null) {
			urlCon.setRequestProperty("Cookie", cookies);
		}
		
		String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);
        
        urlCon.setDoOutput(true);
        ( (HttpsURLConnection) urlCon ).setSSLSocketFactory(sslSocketFactory);
		
		// Send request
        DataOutputStream wr = new DataOutputStream(( (HttpsURLConnection) urlCon ).getOutputStream());
    	wr.writeBytes(payload);
        
        wr.flush();
        wr.close();
        
    	int responseCode = ((HttpsURLConnection)urlCon).getResponseCode();
    	if(cookies == null) {
    		cookies = getResponseCookies(urlCon);
    	}
    	String responseMessage = ((HttpsURLConnection)urlCon).getResponseMessage();
    	
    	InputStream is = null;
    	
        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : GET\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";
        
        if(responseCode == 200 || responseCode == 204) {
        	is = urlCon.getInputStream();  
        	InputStream[] input = getClonedStream(is, 2);
            String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
            
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
            sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
            sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
            sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            is = input[1];
			Reporter.log("Verify GET response is generated", "GET response should be generated for url - " + url, "GET response is generated successfully", "Pass");
            return new String[]{response, cookies};
        }
        else {
        	if(url.trim().toLowerCase().startsWith("https://"))
        		is = ((HttpsURLConnection)urlCon).getErrorStream();
    		else
    			is = ((HttpURLConnection)urlCon).getErrorStream();
        	
        	if(is != null) {
	        	InputStream[] input = getClonedStream(is, 2);
	        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
	        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
        	}
        	sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
        	sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
        	sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
        	sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
        	Reporter.log("Verify GET response is generated", "GET response should be generated for url - " + url, "GET response is not generated", "Fail");
        	return new String[]{null, cookies};
        }
	}
	
	public WebElement getElementWhenPresent(By locater, long... waitSeconds){
		assert waitSeconds.length <= 1;
		long seconds = waitSeconds.length > 0 ? waitSeconds[0] : DEFAULT_FIND_ELEMENT_TIMEOUT;
		WebElement element = null;
		
		waitForJStoLoad();
		
		int counter = 0;
		do{
			long time = 20;
			if(seconds <= 20)
				time = seconds;
			WebDriverWait wait  = new WebDriverWait(getDriver(), time);
			try{
				element = wait.until(ExpectedConditions.presenceOfElementLocated(locater));
				break;
			}
			catch(Exception ex){
				boolean flag = false;
				if(!Environment.get("methodHandleUnwantedPopups").trim().equalsIgnoreCase("")){
					String[] words = Environment.get("methodHandleUnwantedPopups").trim().split("\\.");
					String methodName = words[words.length - 1];
					String className = Environment.get("methodHandleUnwantedPopups").trim().substring(0, Environment.get("methodHandleUnwantedPopups").trim().indexOf("." + methodName));
					Object[] params = new Object[0];
					Class<?> thisClass;
					try {
						thisClass = Class.forName(className);
						Object busFunctions = thisClass.getConstructor(new Class[] { WebDriverFactory.class, HashMapNew.class, HashMapNew.class, Reporting.class, Assert.class, SoftAssert.class, ThreadLocal.class }).newInstance(new Object[] { this.driverFactory, this.Dictionary, this.Environment, this.Reporter, this.Assert, this.SoftAssert, this.sTestDetails });
						Method method = thisClass.getDeclaredMethod(methodName, new Class[0]);
						Object objReturn = method.invoke(busFunctions, params);
						if (objReturn.equals(Boolean.valueOf(true))) {
							flag = true;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					
				}
				if(flag && seconds <= 20){
					try{
						element = wait.until(ExpectedConditions.presenceOfElementLocated(locater));
						break;
					} catch(Exception ex1){
						throw ex1;
					}
				}
				else{
					if(counter >= seconds || seconds <= 20)
						throw ex;
				}
			}
			finally{
				counter += time;
			}
		}while(true);
		
		return element;
	}
	
	public void del(String url, String[] key, String[] value) throws Exception {
		InputStream is = null;
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        
        final SSLContext sslContext = SSLContext.getInstance( "SSL" );
        sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();            
        
        URLConnection urlCon = new URL(url).openConnection();
        
        if(key != null && value != null)
        	urlCon = setHeaders(urlCon, key, value);
        
        String rawResponse = "", rawRequest = "";
        
        int responseCode = 0;
        String responseMessage = "";
        
        if(url.trim().toLowerCase().startsWith("https://")){
        	( (HttpsURLConnection) urlCon ).setSSLSocketFactory( sslSocketFactory );
        	( (HttpsURLConnection) urlCon ).setRequestMethod("DELETE");
        	rawRequest = getRequestHeaders(urlCon, rawRequest);
        	responseCode = ((HttpsURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpsURLConnection)urlCon).getResponseMessage();
        }
        else{
        	( (HttpURLConnection) urlCon ).setRequestMethod("DELETE");
        	rawRequest = getRequestHeaders(urlCon, rawRequest);
        	responseCode = ((HttpURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpURLConnection)urlCon).getResponseMessage();
        }
        
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : DELETE\n\n";
        
        if(responseCode == 200 || responseCode == 204) {
        	is = urlCon.getInputStream();  
        	InputStream[] input = getClonedStream(is, 2);
            String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
            
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
            sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
            sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
//            sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            is = input[1];
        }
        else {
        	if(url.trim().toLowerCase().startsWith("https://"))
        		is = ((HttpsURLConnection)urlCon).getErrorStream();
    		else
    			is = ((HttpURLConnection)urlCon).getErrorStream();
        	
        	InputStream[] input = getClonedStream(is, 2);
        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
        	sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
        	sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
        	sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
//        	sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
        	throw new Exception("Server response code : " + responseCode);
        }
	}
	
	public InputStream post(String url, String payload, String[] key, String[] value) throws Exception{
		InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        
        final SSLContext sslContext = SSLContext.getInstance( "SSL" );
        sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();            
        
        URLConnection urlCon = new URL(url).openConnection();            
        urlCon.setRequestProperty("Method", "POST");
        
        if(key != null && value != null)
        	urlCon = setHeaders(urlCon, key, value);
        
        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);
        
        urlCon.setDoOutput(true);	            	            
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
    	wr.writeBytes(payload);
        wr.flush();
        wr.close();
        
        int responseCode = 0;
        String responseMessage = "";
        
        if(url.trim().toLowerCase().startsWith("https://")){
        	( (HttpsURLConnection) urlCon ).setSSLSocketFactory( sslSocketFactory );
        	responseCode = ((HttpsURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpsURLConnection)urlCon).getResponseMessage();
        }
        else{
        	responseCode = ((HttpURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpURLConnection)urlCon).getResponseMessage();
        }
        
        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : POST\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";
        
        if(responseCode == 201 || responseCode == 200 || responseCode == 204){
        	is = urlCon.getInputStream();
        	InputStream[] input = getClonedStream(is, 2);
            String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
            
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
            sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
            sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
            sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            is = input[1];
            Reporter.log("Verify POST response is generated", "POST response should be generated for url - " + url, "POST response is generated successfully", "Pass");
        }
        else{
        	if(url.trim().toLowerCase().startsWith("https://"))
        		is = ((HttpsURLConnection)urlCon).getErrorStream();
    		else
    			is = ((HttpURLConnection)urlCon).getErrorStream();
        	
        	InputStream[] input = getClonedStream(is, 2);
        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
        	sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
        	sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
        	sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
        	sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
        	Reporter.log("Verify POST response is generated", "POST response should be generated for url - " + url, "POST response is not generated", "Fail");
        	throw new Exception("Server response code : " + responseCode);
        }
		return is;
	}
	
	public Object[] post(String url, String payload, String[] key, String[] value, boolean skipException) throws Exception{
		InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        
        final SSLContext sslContext = SSLContext.getInstance( "SSL" );
        sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();            
        
        URLConnection urlCon = new URL(url).openConnection();            
        urlCon.setRequestProperty("Method", "POST");
        
        if(key != null && value != null)
        	urlCon = setHeaders(urlCon, key, value);
        
        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);
        
        urlCon.setDoOutput(true);	            	            
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
    	wr.writeBytes(payload);
        wr.flush();
        wr.close();
        
        int responseCode = 0;
        String responseMessage = "";
        
        if(url.trim().toLowerCase().startsWith("https://")){
        	( (HttpsURLConnection) urlCon ).setSSLSocketFactory( sslSocketFactory );
        	responseCode = ((HttpsURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpsURLConnection)urlCon).getResponseMessage();
        }
        else{
        	responseCode = ((HttpURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpURLConnection)urlCon).getResponseMessage();
        }
        
        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : POST\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";
        
        if(responseCode == 201 || responseCode == 200 || responseCode == 204){
        	is = urlCon.getInputStream();
        	InputStream[] input = getClonedStream(is, 2);
            String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
            
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
            sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
            sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
            sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            is = input[1];
            Reporter.log("Verify POST response is generated", "POST response should be generated for url - " + url, "POST response is generated successfully", "Pass");
        }
        else{
        	if(url.trim().toLowerCase().startsWith("https://"))
        		is = ((HttpsURLConnection)urlCon).getErrorStream();
    		else
    			is = ((HttpURLConnection)urlCon).getErrorStream();
        	
        	InputStream[] input = getClonedStream(is, 2);
        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
        	sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
        	sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
        	sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
        	sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
        	is = input[1];
        	Reporter.log("Verify POST response is generated", "POST response should be generated for url - " + url, "POST response is not generated", "Fail");
        	if(!skipException)
        		throw new Exception("Server response code : " + responseCode);
        }
		return new Object[]{is, responseCode};
	}
	
	public InputStream patch(String url, String payload, String[] key, String[] value) throws Exception{
		InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        } };
        
        final SSLContext sslContext = SSLContext.getInstance( "SSL" );
        sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();            
        
        URLConnection urlCon = new URL(url).openConnection();            
        urlCon.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        urlCon.setRequestProperty("Method", "POST");
        
        if(key != null && value != null)
        	urlCon = setHeaders(urlCon, key, value);
        
        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);
        
        urlCon.setDoOutput(true);	            	            
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
    	wr.writeBytes(payload);
        wr.flush();
        wr.close();
        
        int responseCode = 0;
        String responseMessage = "";
        
        if(url.trim().toLowerCase().startsWith("https://")){
        	( (HttpsURLConnection) urlCon ).setSSLSocketFactory( sslSocketFactory );
        	responseCode = ((HttpsURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpsURLConnection)urlCon).getResponseMessage();
        }
        else{
        	responseCode = ((HttpURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpURLConnection)urlCon).getResponseMessage();
        }
        
        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : PATCH\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";
        
        if(responseCode == 201 || responseCode == 200 || responseCode == 204){
        	is = urlCon.getInputStream();
        	InputStream[] input = getClonedStream(is, 2);
            String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
            
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
            sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
            sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
            sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            is = input[1];
            Reporter.log("Verify PATCH response is generated", "PATCH response should be generated for url - " + url, "PATCH response is generated successfully", "Pass");
        }
        else{
        	if(url.trim().toLowerCase().startsWith("https://"))
        		is = ((HttpsURLConnection)urlCon).getErrorStream();
    		else
    			is = ((HttpURLConnection)urlCon).getErrorStream();
        	
        	InputStream[] input = getClonedStream(is, 2);
        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
        	sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
        	sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
        	sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
        	sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
        	Reporter.log("Verify PATCH response is generated", "PATCH response should be generated for url - " + url, "PATCH response is not generated", "Fail");
        	throw new Exception("Server response code : " + responseCode);
        }
		return is;
	}
	
	public InputStream waitForTMManageTicketsResponse(String url, String[] key, String[] value, boolean skipException) throws JSONException, Exception {
		Object[] response = null;
		int responseCode = 0;
		int counter = 3;
		do{
			response = get(url, key, value);
			responseCode = (int)response[1];
			sync(100L);
			counter--;
		} while(counter > 0 && (responseCode != 200 && responseCode != 204));
		if(responseCode != 200 && responseCode != 204) {
			if(!skipException)
				throw new Exception("Server response code : " + responseCode);
		}
		return (InputStream) response[0];
	}
	
	public Object[] get(String url, String[] key, String[] value) throws Exception{
		InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				
			}
		}};
        
        final SSLContext sslContext = SSLContext.getInstance( "SSL" );
        sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();            
        
        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "GET");
        
        if(key != null && value != null){
        	urlCon = setHeaders(urlCon, key, value);
        }
        
        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);
        
        int responseCode = 0;
        String responseMessage = "";
        
        if(url.trim().toLowerCase().startsWith("https://")){
        	( (HttpsURLConnection) urlCon ).setSSLSocketFactory( sslSocketFactory );
        	responseCode = ((HttpsURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpsURLConnection)urlCon).getResponseMessage();
        }
        else{
        	responseCode = ((HttpURLConnection)urlCon).getResponseCode();
        	responseMessage = ((HttpURLConnection)urlCon).getResponseMessage();
        }
        
        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : GET\n\n";
        
        if(responseCode == 200 || responseCode == 204) {
        	is = urlCon.getInputStream();  
        	InputStream[] input = getClonedStream(is, 2);
            String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
            
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
            sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
            sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
            sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            is = input[1];
            Reporter.log("Verify GET response is generated", "GET response should be generated for url - " + url, "GET response is generated successfully", "Pass");
        }
        else {
        	if(url.trim().toLowerCase().startsWith("https://"))
        		is = ((HttpsURLConnection)urlCon).getErrorStream();
    		else
    			is = ((HttpURLConnection)urlCon).getErrorStream();
        	
        	InputStream[] input = getClonedStream(is, 2);
        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
        	sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
        	sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
        	sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
        	sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
        	is = input[1];
        	Reporter.log("Verify GET response is generated", "GET response should be generated for url - " + url, "GET response is not generated", "Fail");
        }
     
		return new Object[]{is, responseCode};
	}
	
	public InputStream[] getClonedStream(InputStream input, int count) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = input.read(buffer)) > -1 ) {
		    baos.write(buffer, 0, len);
		}
		baos.flush();

		InputStream[] is = new InputStream[count];
		for(int i = 0 ; i < count; i++){
			is[i] = new ByteArrayInputStream(baos.toByteArray());
		}
		return is;
	}
	
	public URLConnection setHeaders(URLConnection urlCon, String[] key, String[] value){
		for(int i = 0 ; i < key.length; i++){
			urlCon.setRequestProperty(key[i].trim(), value[i].trim());
		}
		
		return urlCon;
	}
	
	public HttpURLConnection setHeaders(HttpURLConnection urlCon, String[] key, String[] value){
		for(int i = 0 ; i < key.length; i++){
			urlCon.setRequestProperty(key[i].trim(), value[i].trim());
		}
		
		return urlCon;
	}
	
	public JSONObject convertToJSON(Reader jsonStream) throws JSONException, IOException{
		String jsonText = readAll(jsonStream);
		if(!jsonText.trim().equalsIgnoreCase("")) {
			JSONObject json = new JSONObject(jsonText);  	    
			return json;
		} else
			return null;
	}
	
	public String getRequestHeaders(URLConnection urlCon, String rawRequest) {
		Map<String, List<String>> requestheaders = urlCon.getRequestProperties();
        Set<String> keys = requestheaders.keySet();
        Iterator<String> iter = keys.iterator();
        while(iter.hasNext()){
        	String keyName = iter.next();
        	if(keyName == null || keyName.trim().equalsIgnoreCase("null"))
        		continue;
        	rawRequest += keyName + " : ";
        	List<String> values = requestheaders.get(keyName);
        	int i = 0;
        	rawRequest += values.get(i);
        	for(i = 1 ; i < values.size(); i++){
        		rawRequest += ", " + values.get(i);
        	}
        	rawRequest += "\n";
        }
        if(Dictionary != null)
        	Dictionary.put("REQUEST_HEADERS", rawRequest);
        return rawRequest;
	}
	
	public String getResponseHeaders(URLConnection urlCon, String rawResponse) {
		Map<String, List<String>> responseheaders = urlCon.getHeaderFields();
        Set<String> keys = responseheaders.keySet();
        Iterator<String> iter = keys.iterator();
        String cookies = "";
        while(iter.hasNext()){
        	String keyName = iter.next();
        	if ("Set-Cookie".equalsIgnoreCase(keyName)) {
        		List<String> headerFieldValue = responseheaders.get(keyName);
        		for (String headerValue : headerFieldValue) {
        			String[] fields = headerValue.split(";\\s*");
        			String cookieValue = fields[0];
        			cookies += cookieValue + ";";
        		}
        	}

        	if(keyName == null || keyName.trim().equalsIgnoreCase("null"))
        		continue;
        	rawResponse += keyName + " : ";
        	List<String> values = responseheaders.get(keyName);
        	int i = 0;
        	rawResponse += values.get(i);
        	for(i = 1 ; i < values.size(); i++){
        		rawResponse += ", " + values.get(i);
        	}
        	rawResponse += "\n";
        }
        Dictionary.put("RESPONSE_HEADERS", rawResponse);
        Dictionary.put("RESPONSE_COOKIES", cookies);
        return rawResponse;
	}
	
	public String getCreditCardNumber(String cardType) throws Exception {
		int card = 0;
		String input = "";
		switch(cardType.trim().toUpperCase()) {
			case "VISA":
				card = 1;
				input = "visa";
				break;
			case "MASTERCARD":
				card = 2;
				input = "mastercard";
				break;
			default:
				card = 3;
				input = "amex";
		}
		
		try {
			String cookies = "__cfduid=d5cfc3521dde8b71e0f1e6b06d5f860f51500026598; __gads=ID=5dafddbceb1044b5:T=1500026603:S=ALNI_MbgbGMInCIoHet1ZW6NX0LuBP9LCg; __utma=19553963.1287276680.1500026607.1500026607.1500026607.1; __utmc=19553963; __utmz=19553963.1500026607.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)";
			Object[] obj = (Object[]) get("https://api.bincodes.com/cc-gen/?format=json&api_key=f218881aaaef57bdc4f788bf8fbbf4e8&input=" + input, new String[]{"cookie"}, new String[]{cookies});
			InputStream is = (InputStream) obj[0];
			JSONObject jsonObject = convertToJSON(new BufferedReader(new InputStreamReader(is, "UTF-8")));
			return jsonObject.getString("number");
		} catch(Exception ex) {
			//Do Nothing
		}
		
		URL url = new URL("http://credit-card-generator.2-ee.com/gencc.htm");
	    Map<String,Object> params = new LinkedHashMap<>();
	    params.put("card", card);
	    
	    StringBuilder postData = new StringBuilder();
	    for (Map.Entry<String,Object> param : params.entrySet()) {
	        if (postData.length() != 0) postData.append('&');
	        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
	        postData.append('=');
	        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
	    }
	    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

	    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	    conn.setDoOutput(true);
	    try {
	    	conn.getOutputStream().write(postDataBytes);
	    } catch(Exception ex) {
	    	return "4111111111111111";
	    }
	    
	    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	    String output = "";
        Boolean keepGoing = true;
        while (keepGoing) {
            String currentLine = br.readLine();
            if (currentLine == null) {
                keepGoing = false;
            } else {
                output += currentLine;
            }
        }
        
        Pattern r = Pattern.compile("(?:(\\d+ {2}\\d+ {2}\\d+[ ]{2}[0-9]+)|(\\d+ {2}\\d+ {2}\\d+))");
        Matcher m = r.matcher(output);
        String creditCardNumber = "";
        if (m.find( )) {
        	creditCardNumber = m.group(0).replaceAll(" ", "");
            System.out.println("Found value: " + creditCardNumber);
         }else {
        	 creditCardNumber = "4111111111111111";
        	 System.out.println("Found value: " + creditCardNumber);
         }
        
        return creditCardNumber;
	}
	
	public String toCamelCase(String s){
 	   String[] parts = s.split(" ");
 	   String camelCaseString = "";
 	   for (String part : parts){
 	      camelCaseString = camelCaseString + toProperCase(part) + " ";
 	   }
 	   return camelCaseString.trim();
 	}
    
    String toProperCase(String s) {
	    return s.substring(0, 1).toUpperCase() +
	               s.substring(1).toLowerCase();
	}
    
    public String getRelatedEnv() {
		String APP_URL = Environment.get("APP_URL").trim();
		String clientId = APP_URL.substring(APP_URL.lastIndexOf("/") + 1);
		if(clientId.trim().endsWith("/")) {
			clientId = clientId.substring(0, clientId.trim().length() - 1);
		}
		String env = "";
		switch(clientId.trim().toUpperCase()) {
			case "IOMEDIAQAUNITAS" :
			case "TAG7" :
				env = "UNITAS";
				break;
			case "IOMEDIA3" :
				env = "DEMO";
				break;
			case "IOMEDIAQACMS" :
				env = "UNITAS-CMS";
				break;
			case "D84":
				env = "IOMEDIA2";
				break;
			default :
				String relatedEnv = System.getProperty("relatedEnv") != null && !System.getProperty("relatedEnv").trim().equalsIgnoreCase("") ? System.getProperty("relatedEnv").trim().toUpperCase() : clientId.trim().toUpperCase();
				env = relatedEnv;
		}
		
		return env;
    }
    
    public boolean getManageTicketConfiguration(String flag) {
    	String dsn = Environment.get("DSN").trim();
    	if(dsn.equalsIgnoreCase("genesis")) {
    		if(flag.trim().equalsIgnoreCase("sell"))
    			return false;
    	} else if(dsn.equalsIgnoreCase("iomedia") || dsn.equalsIgnoreCase("field700")) {
    		if(flag.trim().equalsIgnoreCase("sell") || flag.trim().equalsIgnoreCase("donate"))
    			return false;
    	}
    	try {
    		Object obj = ((JavascriptExecutor) getDriver()).executeScript("var obj = drupalSettings.componentConfigData.siteconfig;return JSON.stringify(obj);");
			JSONObject json = new JSONObject(obj.toString());
			JSONObject manageTicketConf = json.getJSONObject("manage_ticket_configuration");
			return manageTicketConf.getInt(flag.trim().toLowerCase()) == 1 ? true : false;
		} catch(Exception ex) {
			//Do Nothing
		}
    	return true;
    }
    
    public void setSeleniumUri() {
    	String tm_oauth_url = Environment.get("TM_OAUTH_URL").trim();
    	if(!tm_oauth_url.contains("qa1-oauth.acctmgr.nonprod-tmaws.io")) {
    		System.setProperty("seleniumURI", "ondemand.saucelabs.com:80");
    	}
    }
    
    public String[] postTMInvoiceAPIResponse(JSONObject payload, String cookies) throws JSONException, Exception {
		String keyPassphrase = "";

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(new FileInputStream("live.iomedia_tm_v2.pfx"), keyPassphrase.toCharArray());

		SSLContext sslContext = SSLContexts.custom()
		        .loadKeyMaterial(keyStore, null)
		        .build();
        
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();            
        String url = "https://ws.ticketmaster.com/archtics/ats/ticketing_services.aspx?dsn=" + Environment.get("DSN").trim();
        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "POST");
		urlCon.setRequestProperty("Accept", "application/json");
		if(cookies != null) {
			urlCon.setRequestProperty("Cookie", cookies);
		}
		
		String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);
		
        urlCon.setDoOutput(true);
        ( (HttpsURLConnection) urlCon ).setSSLSocketFactory(sslSocketFactory);
		
		// Send request
        DataOutputStream wr = new DataOutputStream(( (HttpsURLConnection) urlCon ).getOutputStream());
    	wr.writeBytes(payload.toString());
        
        wr.flush();
        wr.close();
        
    	int responseCode = ((HttpsURLConnection)urlCon).getResponseCode();
    	if(cookies == null) {
    		cookies = getResponseCookies(urlCon);
    	}
    	String responseMessage = ((HttpsURLConnection)urlCon).getResponseMessage();
    	
    	InputStream is = null;
    	
        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : GET\n\n";
        rawRequest += payload.toString().startsWith("{") ? new JSONObject(payload.toString()).toString(2) : payload.toString().startsWith("[") ? new JSONArray(payload.toString()).toString(2) : payload.toString() + "\n";
    	
        if(responseCode == 200 || responseCode == 204 || responseCode == 202) {
        	is = urlCon.getInputStream();  
        	InputStream[] input = getClonedStream(is, 2);
            String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
            
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
            sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
            sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
            sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            is = input[1];
			Reporter.log("Verify GET response is generated", "GET response should be generated for url - " + url, "GET response is generated successfully", "Pass");
            return new String[]{response, cookies};
        }
        else {
        	if(url.trim().toLowerCase().startsWith("https://"))
        		is = ((HttpsURLConnection)urlCon).getErrorStream();
    		else
    			is = ((HttpURLConnection)urlCon).getErrorStream();
        	InputStream[] input = getClonedStream(is, 2);
        	String response = readAll(new BufferedReader(new InputStreamReader(input[0])));
        	rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
        	sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
        	sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
        	sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
        	sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
        	Reporter.log("Verify GET response is generated", "GET response should be generated for url - " + url, "GET response is not generated", "Fail");
        	return new String[]{response, cookies};
        }
	}
    
    public String getSessionCookie(String cookies) {
    	if(cookies.trim().contains("SSESS"))
    		return cookies.substring(cookies.indexOf("SSESS")).split(";")[0];
    	else
    		return cookies.substring(cookies.indexOf("SESS")).split(";")[0];
    }
}
