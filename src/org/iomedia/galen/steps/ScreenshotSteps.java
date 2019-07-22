package org.iomedia.galen.steps;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.iomedia.common.BaseUtil;
import org.iomedia.framework.OSValidator;
import org.iomedia.galen.common.Screenshot;
import org.iomedia.galen.pages.Homepage;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.SkipException;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ScreenshotSteps {
	
	Screenshot shot;
	BaseUtil base;
	Homepage homepage;
	org.iomedia.framework.Assert Assert;
	
	public ScreenshotSteps(Homepage homepage,BaseUtil base,Screenshot shot, org.iomedia.framework.Assert Assert) {
		this.homepage = homepage;
		this.shot=shot;
		this.base = base;
		this.Assert = Assert;
	}
	
	@When("^User create directory$")
	public String create_directory() throws Exception {
		String path = shot.getFilepath(true);
		base.Dictionary.put("FILEPATH", path);
		return path;
	}
	
	@Given("^Get client directory$")
	public void get_client_directory() throws Exception {
		String path = shot.getFilepath(false);
		base.Dictionary.put("FILEPATH", path);
	}
	
	@When("^User take second screenshot of homepage Page at (.+)$")
	public void user_take_second_screenshot_homepage_page(String Filepath) throws Exception {
		Filepath = (String) base.getGDValue(Filepath);
		File folder1 = ((TakesScreenshot)base.driverFactory.getDriver().get()).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(folder1, new File(Filepath + OSValidator.delimiter + "Homepage_2.png"));
	}
	
	@Then("^Take screenshot of homepage Page at (.+)$")
	public void take_screenshot_homepage_page(String Filepath) throws Exception {
		Filepath = (String) base.getGDValue(Filepath);
		File folder1 = ((TakesScreenshot)base.driverFactory.getDriver().get()).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(folder1, new File(Filepath + OSValidator.delimiter + "Homepage_1.png"));
	}
	
	@When("^User take second screenshot of CreateAccount Page at (.+)$")
	public void user_take_second_screenshot_CreateAccount_page(String Filepath) throws Exception {
		Filepath = (String) base.getGDValue(Filepath);
		homepage.clickSignUpLink();
		File folder1 = ((TakesScreenshot)base.driverFactory.getDriver().get()).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(folder1, new File(Filepath+ OSValidator.delimiter + "CreateAccount_2.png"));
		homepage.clickSignInReactLink();
	}
	
	@Then("^Take screenshot of CreateAccount Page at (.+)$")
	public void take_screenshot_CreateAccount_page(String Filepath) throws Exception {
		Filepath = (String) base.getGDValue(Filepath);
		homepage.clickSignUpLink();
		File folder1 = ((TakesScreenshot)base.driverFactory.getDriver().get()).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(folder1, new File(Filepath + OSValidator.delimiter + "CreateAccount_1.png"));
		homepage.clickSignInReactLink();
	}
	
	@When("^User take second screenshot of ForgotPassword Page at (.+)$")
	public void user_take_second_screenshot_ForgotPassword_page(String Filepath) throws Exception {
		Filepath = (String) base.getGDValue(Filepath);
		homepage.clickForgotPasswordLink();
		File folder1 = ((TakesScreenshot)base.driverFactory.getDriver().get()).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(folder1, new File(Filepath+ OSValidator.delimiter + "ForgotPassword_2.png"));
	}
	
	@Then("^Take screenshot of ForgotPassword Page at (.+)$")
	public void take_screenshot_ForgotPassword_page(String Filepath) throws Exception {
		Filepath = (String) base.getGDValue(Filepath);
		homepage.clickForgotPasswordLink();
		File folder1 = ((TakesScreenshot)base.driverFactory.getDriver().get()).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(folder1, new File(Filepath+ OSValidator.delimiter + "ForgotPassword_1.png"));
	}
     
	@Then("^Compare the screenshot at (.+)$")
	public void compare_screenshot(String Filepath) throws IOException {
		String pixelMatch = System.getProperty("pixelMatch") != null && !System.getProperty("pixelMatch").trim().equalsIgnoreCase("") ? System.getProperty("pixelMatch").trim() : base.Environment.get("pixelMatch").trim();
		Filepath = (String) base.getGDValue(Filepath);
		File[] listOfFiles = listofFiles_1(Filepath);
		File[] listOfFiles1 = listofFiles_2(Filepath);
		int counter = 0;
	    int q = 0;
	    File file1 = new File(Filepath + OSValidator.delimiter + "filename.txt"); //$NON-NLS-1$
	    if (!file1.exists()) {
            file1.createNewFile();
	    }

	    FileWriter fw = new FileWriter(file1.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
		
	    for (int i = 0, j = 0; i < listOfFiles.length; i++, j++) {
            counter++;
            BufferedImage img1 = ImageIO.read(listOfFiles[i]);
            BufferedImage img2 = ImageIO.read(listOfFiles1[j]);
            int width = img1.getWidth(null);
            int height = img1.getHeight(null);
            int[][] clr = new int[width][height];
            int widthe = img2.getWidth(null);
            int heighte = img2.getHeight(null);
            int[][] clre = new int[widthe][heighte];
            int smw = 0;
            int smh = 0;
            int p = 0;
            if (width > widthe) {
            	smw = widthe;
            } else {
                smw = width;
            }
            if (height > heighte) {
                smh = heighte;
            } else {
                smh = height;
            }
            // CHECKING NUMBER OF PIXELS SIMILARITY
            for (int a = 0; a < smw; a++) {
            	for (int b = 0; b < smh; b++) {
            		clre[a][b] = img2.getRGB(a, b);
            		clr[a][b] = img1.getRGB(a, b);
            		if (clr[a][b] == clre[a][b]) {
        				p = p + 1;
						bw.write("\t"); //$NON-NLS-1$
						bw.write(Integer.toString(a));
						bw.write("\t"); //$NON-NLS-1$
						bw.write(Integer.toString(b));
						bw.write("\n"); //$NON-NLS-1$
            		} else
            			q = q + 1;
            	}
            }

            float w, h = 0;
            if (width > widthe) {
            	w = width;
            } else {
            	w = widthe;
            }
            if (height > heighte) {
            	h = height;
            } else {
            	h = heighte;
            }
            float s = (smw * smh);
            // CALUCLATING PERCENTAGE
            float x = (100 * p) / s;
            System.out.println("====================================================");
            System.out.println("Comparision results for file# " + counter);
            System.out.println("THE PERCENTAGE SIMILARITY IS APPROXIMATELY = " + x + "%");
            Assert.assertTrue(x >= Float.valueOf(pixelMatch), "Two images are matched");
                        
            // long stop = System.currentTimeMillis();
            // System.out.println("TIME TAKEN IS = "+(stop-start));
            System.out.println("NO OF PIXEL GETS VARIED:= " + q);
            System.out.println("NO OF PIXEL GETS MATCHED:= " + p);
        }
	    bw.close();
        System.out.println("Screenshot Comparison Completed.");
	}

	public File[] listofFiles_1(String Filepath) throws IOException {
		Filepath = (String) base.getGDValue(Filepath);
	    File[] files = new File[3];
	
	    File shot1 = new File(Filepath);
		File[] listOfFiles = shot1.listFiles();
		int j=0;
		boolean success = false;
		for(int i=0; i< listOfFiles.length ;  i++) {	
			if(listOfFiles[i].getCanonicalPath().contains("_1")) {
				success = true;
				files[j]=listOfFiles[i]; 
				j++;
			}	
		}
		
		if(!success){
			throw new SkipException("Screenshots not found before deployment");
		}
		return files;
	}
	
	public File[] listofFiles_2(String Filepath) throws IOException {
		Filepath = (String) base.getGDValue(Filepath);
	   File[] afile = new File[3];
	   File shot2 = new File(Filepath);
	   File[] lof = shot2.listFiles();
		 int j=0;
			for(int i=0; i<lof.length;  i++) {	
				if(lof[i].getCanonicalPath().contains("_2")) {
					afile[j]=lof[i];
					j++;
				}					
			}
		return afile;
	}
}