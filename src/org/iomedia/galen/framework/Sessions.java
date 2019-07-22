package org.iomedia.galen.framework;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.iomedia.framework.Driver;
import org.iomedia.framework.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Sessions extends Driver {
	HashMapNew Environment;
	WebDriverFactory driverFactory;
	
	public Sessions(WebDriverFactory driverFactory, HashMapNew Environment) {
		this.Environment = Environment;
		this.driverFactory = driverFactory;
	}
	
	public void setSessionLimit(String session, String surveyValue, boolean enableParking, boolean enablehandling) throws Exception {
		String url = Environment.get("APP_URL").trim();
		String userName = System.getProperty("adminUserName") != null && !System.getProperty("adminUserName").trim().equalsIgnoreCase("") ? System.getProperty("adminUserName").trim() : Environment.get("adminUserName").trim();
		String password = System.getProperty("adminPassword") != null && !System.getProperty("adminPassword").trim().equalsIgnoreCase("") ? System.getProperty("adminPassword").trim() : Environment.get("adminPassword").trim();
		String tm_oauth_url = Environment.get("TM_OAUTH_URL").trim();
    	if(userName.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
    		if(tm_oauth_url.contains("app.ticketmaster.com")){
    			String clientId = url.substring(url.lastIndexOf("/") + 1);
    			if(clientId.trim().endsWith("/")) {
    				clientId = clientId.substring(0, clientId.trim().length() - 1);
    			}
    			userName = "admin";
    			password = clientId + "1234";
    		} else {
    			return;
    		}
    	}
		
    	super.driverFactory = this.driverFactory;
    	super.driverType = driverFactory.getDriverType().get();
    	super.sTestDetails = driverFactory.getTestDetails();
    	String videoGifIntegration = Environment.get("videoGifIntegration").trim();
    	Environment.put("videoGifIntegration", "false");
    	super.Environment = Environment;
		WebDriver driver = createDriver(new Object[]{});
		try {
			driver.get(url + "/user/login");
			driver.findElement(By.id("edit-name")).sendKeys(userName);
			driver.findElement(By.id("edit-pass")).sendKeys(password);
			JavascriptExecutor ex = (JavascriptExecutor)driver;
			ex.executeScript("arguments[0].click();", driver.findElement(By.id("edit-submit")));
			
			if(!BaseUtil.checkIfElementPresent(By.id("toolbar-bar"), 10))	{
				if(userName.trim().equalsIgnoreCase("admin") && password.trim().equalsIgnoreCase("123456")) {
					return;
				} else {
					userName = "admin";
					password = "123456";
					driver.findElement(By.id("edit-name")).clear();
					driver.findElement(By.id("edit-name")).sendKeys(userName);
					driver.findElement(By.id("edit-pass")).clear();
					driver.findElement(By.id("edit-pass")).sendKeys(password);
					ex.executeScript("arguments[0].click();", driver.findElement(By.id("edit-submit")));
					if(!BaseUtil.checkIfElementPresent(By.id("toolbar-bar"), 10))	{
						return;
					}
					setXMLNodeValue(Environment.get("appCredentialsPath").trim(), Environment.get("env").trim(), Environment.get("version").trim(), "adminUserName", userName);
					setXMLNodeValue(Environment.get("appCredentialsPath").trim(), Environment.get("env").trim(), Environment.get("version").trim(), "adminPassword", password);
				}
			}
			
			/* Set session limit*/
			driver.navigate().to(url + "/admin/config/people/session-limit");
			WebElement sessionInput = driver.findElement(By.id("edit-session-limit-max"));
			String oldValue = sessionInput.getAttribute("value");
			
//			if(oldValue.trim().equalsIgnoreCase(session)) {
//				return;
//			}
			
			sessionInput.clear();
			sessionInput.sendKeys(session);
			ex.executeScript("arguments[0].click();", driver.findElement(By.id("edit-submit")));
			setXMLNodeValue(Environment.get("appCredentialsPath").trim(), Environment.get("env").trim(), Environment.get("version").trim(), "sessions", oldValue);
			System.out.println("Session limit set");
			
			Util util= new Util(Environment);
			String datasheet = System.getProperty("calendar") != null && !System.getProperty("calendar").trim().equalsIgnoreCase("") ? System.getProperty("calendar").trim() : Environment.get("calendar").trim();
			if(util.isProd() && datasheet.trim().equalsIgnoreCase("PROD_SANITY")) {
				return;
			}
			
			driver.navigate().to(url + "/admin/invoice/list");
			By selectedInvoiceTemplate = By.xpath(".//table[@id='search']//tbody//tr//td//input[@checked='']/../../..//td//i");
			if(BaseUtil.checkIfElementPresent(selectedInvoiceTemplate)) {
				WebElement settings = driver.findElement(selectedInvoiceTemplate);
				settings.click();
			
				WebElement allinvoices = driver.findElement(By.xpath(".//label[text()='All Invoices']/../input"));
				allinvoices.click();
				boolean parking = false, handling = false;
				/*Enable parking and handling*/
				if(BaseUtil.checkIfElementPresent(By.xpath(".//strong[text()='INVOICE LABELS']"), 1)) {
					WebElement invoiceLabels = driver.findElement(By.xpath(".//strong[text()='INVOICE LABELS']"));
					ex.executeScript("arguments[0].click();", invoiceLabels);
					WebElement enable_parking = driver.findElement(By.cssSelector("input[type='checkbox'][name*='park']"));
					if(enableParking && enable_parking.getAttribute("checked") == null) {
						ex.executeScript("arguments[0].click();", enable_parking);
						parking = true;
					} else if(!enableParking && enable_parking.getAttribute("checked") != null && enable_parking.getAttribute("checked").trim().equalsIgnoreCase("true")) {
						ex.executeScript("arguments[0].click();", enable_parking);
						parking = false;
					}
					WebElement enable_handling = driver.findElement(By.cssSelector("input[type='checkbox'][name*='hand']"));
					if(enablehandling && enable_handling.getAttribute("checked") == null) {
						ex.executeScript("arguments[0].click();", enable_handling);
						handling = true;
					} else if(!enablehandling && enable_handling.getAttribute("checked") != null && enable_handling.getAttribute("checked").trim().equalsIgnoreCase("true")) {
						ex.executeScript("arguments[0].click();", enable_handling);
						handling = false;
					}
				} else {
					WebElement invoiceSummary = driver.findElement(By.xpath(".//strong[text()='INVOICE SUMMARY'] "));
					ex.executeScript("arguments[0].click();", invoiceSummary);
					By locatorParking = By.cssSelector("input[value*='parking' i]");
					if(enableParking && !BaseUtil.checkIfElementPresent(locatorParking, 1)) {
						click(By.cssSelector("input[value*='Add Another']"), By.cssSelector("#multi-replace div[class*='field-name'] input[value='']"), 10);
						WebElement parkingField = BaseUtil.getElementWhenVisible(By.cssSelector("#multi-replace div[class*='field-name'] input[value='']"));
						parkingField.sendKeys("Parking Fees");
						parking = true;
					} else if(!enableParking && BaseUtil.checkIfElementPresent(locatorParking, 1)) {
						click(By.xpath(".//input[contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'parking')]/../..//following-sibling::div[2]/input"));
						BaseUtil.sync(2000L);
						parking = false;
					}
					By locatorHandling = By.cssSelector("input[value*='handling' i]");
					if(enablehandling && !BaseUtil.checkIfElementPresent(locatorHandling, 1)) {
						click(By.cssSelector("input[value*='Add Another']"), By.cssSelector("#multi-replace div[class*='field-name'] input[value='']"), 10);
						BaseUtil.getElementWhenVisible(locatorParking, 5);
						WebElement handlingField = BaseUtil.getElementWhenVisible(By.cssSelector("#multi-replace div[class*='field-name'] input[value='']"));
						handlingField.sendKeys("Handling Fees");
						handling = true;
					} else if(!enablehandling && BaseUtil.checkIfElementPresent(locatorHandling, 1)) {
						click(By.xpath(".//input[contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), 'handling')]/../..//following-sibling::div[2]/input"));
						BaseUtil.sync(2000L);
						handling = false;
					}
				}
				
				setXMLNodeValue(Environment.get("appCredentialsPath").trim(), Environment.get("env").trim(), Environment.get("version").trim(), "enableParking", String.valueOf(!parking));
				setXMLNodeValue(Environment.get("appCredentialsPath").trim(), Environment.get("env").trim(), Environment.get("version").trim(), "enableHandling", String.valueOf(!handling));
				
				/*Set survey setting*/
				if(BaseUtil.checkIfElementPresent(By.xpath(".//strong[text()='SURVEY']"), 10)) {
					WebElement surveyTab = driver.findElement(By.xpath(".//strong[text()='SURVEY']"));
					ex.executeScript("arguments[0].click();", surveyTab);
					WebElement showSurvey = driver.findElement(By.cssSelector("div[class*='survey_position'] select"));
					Select select = new Select(showSurvey);
					String selectedValue = select.getFirstSelectedOption().getText();
					if(surveyValue.trim().equalsIgnoreCase(""))
						select.selectByVisibleText("Payments");
					else
						select.selectByVisibleText(surveyValue.trim());
					setXMLNodeValue(Environment.get("appCredentialsPath").trim(), Environment.get("env").trim(), Environment.get("version").trim(), "showSurvey", selectedValue);
					System.out.println("Survey setting set");
				}
				
				ex.executeScript("arguments[0].click();", driver.findElement(By.id("edit-submit")));
			}
			driver.navigate().to(url + "/user/logout");
			BaseUtil.getElementWhenPresent(By.xpath(".//input[@name='email'] | .//div[@class='mobile-signin']//span[text()='Sign In']"));
		} catch(Exception ex) {
			throw ex;
		}
		finally {
			driver.quit();
			Environment.put("videoGifIntegration", videoGifIntegration);
	    	super.Environment = Environment;
		}
	}
	
	public void setXMLNodeValue(String path, String env, String version, String tag, String value){
	    String RootPath = System.getProperty("user.dir");
	    try
	    {
	      String xmlPath = RootPath + path;
	      File fXmlFile = new File(xmlPath);
	      
	      if(!fXmlFile.exists())
	    	  return;
	      
	      DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
	      DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
	      Document xmldoc = docBuilder.parse(fXmlFile);
	      
	      Node ENV = xmldoc.getElementsByTagName("ENV").item(0);
	      
	      XPathFactory xPathfac = XPathFactory.newInstance();
	      XPath xpath = xPathfac.newXPath();

	      XPathExpression expr = xpath.compile("//" + env.trim().toUpperCase());
	      Object obj = expr.evaluate(xmldoc, XPathConstants.NODESET);
	      if(obj != null) {
	    	  Node node = ((NodeList)obj).item(0);
	    	  if(node == null) {
		    	  Element envNode = xmldoc.createElement(env.trim().toUpperCase());
		    	  envNode.appendChild(xmldoc.createElement(version.trim().toUpperCase()));
		    	  ENV.appendChild(envNode);
	    	  }
	      }
	      
	      expr = xpath.compile("//" + env.trim().toUpperCase() + "/" + version.trim().toUpperCase() + "/COMMON");
	      obj = expr.evaluate(xmldoc, XPathConstants.NODESET);
	      if(obj != null) {
	    	  Node node = ((NodeList)obj).item(0);
	    	  if(node != null) {
	    		  NodeList nl = node.getChildNodes();
	    		  boolean flag = false;
	    		  for (int child = 0; child < nl.getLength(); child++) {
	    			  String nodeName = nl.item(child).getNodeName().trim();
	    			  if(nodeName.trim().equalsIgnoreCase(tag.trim())) {
	    				  nl.item(child).setTextContent(value);
	    				  flag = true;
	    				  break;
	    			  }
	    		  }
	    		  if(!flag) {
	    			  Element _node = xmldoc.createElement(tag.trim());
					  _node.appendChild(xmldoc.createTextNode(value));
					  node.appendChild(_node);
	    		  }
	    	  } else {
	    		  expr = xpath.compile("//" + env.trim().toUpperCase() + "/" + version.trim().toUpperCase()); 
	    		  obj = expr.evaluate(xmldoc, XPathConstants.NODESET);
	    		  node = ((NodeList)obj).item(0);
	    		  
	    		  Element commonNode = xmldoc.createElement("COMMON");
	    		  Element _node = xmldoc.createElement(tag.trim());
				  _node.appendChild(xmldoc.createTextNode(value));
				  commonNode.appendChild(_node);
		    	  node.appendChild(commonNode);
	    	  }
	      }
	      
	      //write the content into xml file
	      TransformerFactory transformerFactory = TransformerFactory.newInstance();
	      Transformer transformer = transformerFactory.newTransformer();
	      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	      
	      DOMSource source = new DOMSource(xmldoc);
	      System.out.println("Path : " + xmlPath);
	      StreamResult result = new StreamResult(new FileOutputStream(xmlPath));
	      transformer.transform(source, result);
	      System.out.println("Done");
	    }
	    catch (Exception excep){
	    	excep.printStackTrace();
	    }
	}
	
	public void click(By locator, long... waitSeconds){
		int counter = !Environment.get("noOfRetriesForSameOperation").trim().equalsIgnoreCase("") ? Integer.valueOf(Environment.get("noOfRetriesForSameOperation").trim()) : 2;
		WebElement we = BaseUtil.getElementWhenClickable(locator, waitSeconds);
		while(counter >= 0){
			try{
				if(we != null){
					javascriptClick(we);
					break;
				}
			} catch(Exception ex){
				if(counter == 0){
					throw ex;
				}
				BaseUtil.sync(500L);
				counter--;
			}
		}
	}
	
	public void click(By locator, By expectedLocator, long expectedLocatorWaitSeconds, long... waitSeconds){
		int counter = !Environment.get("noOfRetriesForSameOperation").trim().equalsIgnoreCase("") ? Integer.valueOf(Environment.get("noOfRetriesForSameOperation").trim()) : 2;
		WebElement we = BaseUtil.getElementWhenClickable(locator, waitSeconds);
		while(counter >= 0){
			try{
				if(we != null){
					javascriptClick(we);
					BaseUtil.getElementWhenVisible(expectedLocator, expectedLocatorWaitSeconds);
					break;
				}
			} catch(Exception ex){
				if(counter == 0){
					throw ex;
				}
				BaseUtil.sync(500L);
				counter--;
			}
		}
	}
	
	public boolean javascriptClick(WebElement webElement){   	 		
        //Click on the WebElement    		
        int intCount = 1;        
        while (intCount<=4){
        	try {
        		BaseUtil.scrollingToBottomofAPage();
    			webElement.click();
        		break;
	        }catch (Exception e){
	        	BaseUtil.sync(500L);
	        	if(intCount==4){
	    	    	throw e;
	        	}
    	    }  	    
    	    intCount++;
        }	        
        return true;    	       
    }
}
