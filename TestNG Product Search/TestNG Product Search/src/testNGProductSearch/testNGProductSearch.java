package testNGProductSearch;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.openqa.selenium.OutputType;


public class testNGProductSearch {
	int priceValueWithoutDollar;
	boolean isLastPage = false;
	
	static {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ponnaiahv\\Documents\\Tools\\chromedriver.exe");
	}

	WebDriver driver = new ChromeDriver();

	@Test
	public void f() throws Exception {
		System.out.println("@Test");
		driver.get("http://procat-admin.dev.voltron.aws.au.fcl.internal/procat/login/index");
		WebElement userName = driver.findElement(By.id("userName"));
		userName.sendKeys("ponnaiahv");
		WebElement password = driver.findElement(By.id("password"));
		password.sendKeys("Tuesday452");
		driver.findElement(By.name("action")).click();
		driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();
		WebElement advertisedFromPrice = driver.findElement(By.id("advertisedFromPrice"));
		advertisedFromPrice.sendKeys("1");
		WebElement advertisedToPrice = driver.findElement(By.id("advertisedToPrice"));
		advertisedToPrice.sendKeys("20");
		this.takeSnapShot(driver, "After Entering Price Range");
		driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
		Thread.sleep(10000);
		Select dropdown = new Select(driver.findElement(By.id("dynatable-per-page-my-ajax-table")));
		dropdown.selectByIndex(3);
		Thread.sleep(10000);

		if (driver.findElement(By.xpath("//*[@id=\'my-ajax-table\']")) != null ){
			
			while (!isLastPage) {
				try {
					
					WebElement nextLink = driver.findElement(By.cssSelector(".dynatable-page-link.dynatable-page-next.dynatable-disabled-page"));
					if (nextLink != null) {
						isLastPage = true;
						System.out.println("Has reached the last page");
					}
				} catch (NoSuchElementException e) {
					
				}
				reUseMethod();
				this.takeSnapShot(driver, "Search Results") ;
				System.out.println("After takeSnapShot");
				System.out.println("after while loop");
				WebElement Next = driver.findElement(By.cssSelector(".dynatable-page-link.dynatable-page-next"));
				Next.click();
				Thread.sleep(10000);
			}
			
			}
		
		else {
			System.out.println("search result not available");
		}

	}

	
	
	private void takeSnapShot(WebDriver driver, String screenshot)throws Exception {
		
		System.out.println("Inside takeSnapShot");
		TakesScreenshot scrShot =(TakesScreenshot)driver;
		File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(SrcFile, new File("C:\\Users\\ponnaiahv\\Documents\\Automation\\Retail\\Sanity_Automation\\Screenshots\\"+screenshot+".png"));

	}

	public void reUseMethod() {

		List<WebElement> rows = driver.findElements(By.xpath("//table[@id='my-ajax-table']/tbody/tr"));
		
		int rowCount = rows.size();
		System.out.println("Total rows in Web table: " + rowCount);
		
		for (int rowIter = 1; rowIter <= rowCount; rowIter++) {
			WebElement dollar = driver.findElement(
					By.cssSelector("#my-ajax-table > tbody > tr:nth-child(" + rowIter + ") > td:nth-child(4)"));
			String priceValueWithDollar = dollar.getText();
			
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(priceValueWithDollar);
			String price = "";
			while (m.find()) {
				price = m.group();
				break;
			}
			priceValueWithoutDollar = Integer.parseInt(price);
			if (priceValueWithoutDollar >= 1 && priceValueWithoutDollar <= 20) {
				System.out.println("Price Element on Row #" + priceValueWithoutDollar + " is between search criteria");
			} else
				System.out.println("Price is not within the range");
		}
	}

	@BeforeTest
	public void beforeTest() {
		System.out.println("@Before Test");
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ponnaiahv\\Documents\\Tools\\chromedriver.exe");
		driver.manage().window().maximize();
	}

	@AfterTest
	public void afterTest() {
		System.out.println("@After Test");
	}

}
