package feedsPocAutomation;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class NewTest {
	
	int rowCount; 
	  static {
			System.setProperty("webdriver.chrome.driver", "C:\\Users\\ponnaiahv\\Documents\\Tools\\chromedriver.exe");
		}
	WebDriver driver = new ChromeDriver();
  @Test
  public void f() throws InterruptedException {
	  
		System.out.println("@Test");
		driver.get("http://feeds.dev.voltron.aws.au.fcl.internal/gadventures/secure/index");
		WebElement userName = driver.findElement(By.id("username"));
		userName.sendKeys("venkatesan.ponnaiah@flightcentre.com.au");
		WebElement password = driver.findElement(By.id("password"));
		password.sendKeys("Tuesday452");
		driver.findElement(By.name("submit")).click();
		Thread.sleep(10000);
		FeedSelectionDropdown();
		Thread.sleep(10000);
		WebElement LastRunLoadDescription = driver.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[3]"));
		String LastRunLoadDescriptionString = LastRunLoadDescription.getText();
		driver.findElement(By.xpath("//*[@id=\"btnRun\"]/span[2]")).click();
		Thread.sleep(10000);
		WebElement LatestRunLoadDescription = driver.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[3]"));
		String LatestRunLoadDescriptionString = LatestRunLoadDescription.getText();
		System.out.println("LastRunLoadDescription " +LastRunLoadDescriptionString);
		System.out.println("LatestRunLoadDescription " +LatestRunLoadDescriptionString);
		WebElement CompletionStatus = driver.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[5]"));
		String CompletionStatusString = CompletionStatus.getText();
		System.out.println("CompletionStatus " +CompletionStatusString);
		String ExpectedCompletionStatus = "COMPLETE";
		WebElement ProductCount = driver.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[4]"));
		String ProductCountinFeed = ProductCount.getText();
		int IntProductCountinFeed = Integer.parseInt(ProductCountinFeed);
						if (CompletionStatusString.equalsIgnoreCase(ExpectedCompletionStatus)) {
							System.out.println("Inside If");
							driver.get("http://procat-admin.dev.voltron.aws.au.fcl.internal/procat/login/index");
							WebElement ProcatuserName = driver.findElement(By.id("userName"));
							ProcatuserName.sendKeys("ponnaiahv");
							WebElement Procatpassword = driver.findElement(By.id("password"));
							Procatpassword.sendKeys("Tuesday452");
							driver.findElement(By.name("action")).click();
							driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();	
							WebElement LoadDescription = driver.findElement(By.id("loadDescriptions"));
							LoadDescription.sendKeys(LatestRunLoadDescriptionString);
							Thread.sleep(5000);
							driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
							Thread.sleep(10000);
							ProductSearchNoofItemsDropdown();
							Thread.sleep(10000);
							List<WebElement> rows = driver.findElements(By.xpath("//table[@id='my-ajax-table']/tbody/tr"));
							rowCount = rows.size();
							System.out.println("Total rows in Web table: " + rowCount);
							
								if (IntProductCountinFeed==rowCount) {
									System.out.println("Count in Feed equals Count in Procat");
									
								}
												  }
						else
			System.out.println("Job not Successfully Completed...");
		}

  public void FeedSelectionDropdown() {
		Select dropdown = new Select(driver.findElement(By.id("combobox")));
		dropdown.selectByIndex(4);
	}
  public void ProductSearchNoofItemsDropdown() {
		Select dropdown = new Select(driver.findElement(By.id("dynatable-per-page-my-ajax-table")));
		dropdown.selectByIndex(3);
		
//		Reporter.log("Test Reporter");
	}
  @BeforeTest
  public void beforeTest() {
	/*  ExtentReports extent = new ExtentReports("C:\\Users\\ponnaiahv\\Documents\\Automation\\Retail", true);
	  String baseurl = "http://feeds.dev.voltron.aws.au.fcl.internal/gadventures/secure/index";
	  test = extent.startTest("Verify Open Browser");*/
	  
	  System.out.println("@Before Test");
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ponnaiahv\\Documents\\Tools\\chromedriver.exe");
		driver.manage().window().maximize();
  }

  @AfterTest
  public void afterTest() {
	  
	  System.out.println("@After Test");
	  //driver.quit();
  }

}
