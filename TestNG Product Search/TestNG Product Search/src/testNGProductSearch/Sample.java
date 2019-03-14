package testNGProductSearch;

import org.testng.annotations.Test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Sample {
	static {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ponnaiahv\\Documents\\Tools\\chromedriver.exe");
	}
	WebDriver driver = new ChromeDriver();
  @Test
  public void f() throws InterruptedException {

		System.out.println("Inside FeedRunProductSearch");
			driver.get("http://procat-admin.dev.voltron.aws.au.fcl.internal/procat/login/index");
			WebElement ProcatuserName = driver.findElement(By.id("userName"));
			ProcatuserName.sendKeys("ponnaiahv");
			WebElement Procatpassword = driver.findElement(By.id("password"));
			Procatpassword.sendKeys("Tuesday452");
			driver.findElement(By.name("action")).click();
			driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(10000);
			String text = driver.findElement(By.id("dynatable-record-count-my-ajax-table")).getText();
			String[] trimmedText = text.split(" ");
			System.out.println("Records in Search Result: " +trimmedText);
			String strNew = trimmedText[trimmedText.length-2];
			int IntProductCountinFeed = Integer.parseInt(strNew);
		    System.out.println("Integer Records in Search Result: " +IntProductCountinFeed);
		    writeToHTML();
			/*for (String str : trimmedText) {
				String strNew = str.replace("records", "");
			    System.out.println("Records in Search Result: " +str);
			    System.out.println("Records in Search Result: " +strNew);
			    int IntProductCountinFeed = Integer.parseInt(strNew);
			    System.out.println("Integer Records in Search Result: " +IntProductCountinFeed);
			}*/

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
		// driver.quit();
	  
  }

  public void writeToHTML() {
	  try {
		  File file = new File ("Result_" + System.currentTimeMillis() + ".html");
		  String[][] testResults = {{"Test1", "PASS"}, {"Test2", "FAIL"}, {"Test3", "Didn't run"}};
  BufferedWriter bw = new BufferedWriter(new FileWriter(file));
  StringBuilder infoBuilder = new StringBuilder();
  String tableHeader = "<table><tr> <th>Testcase Name</th> <th>Testresult</th> </tr>";
  infoBuilder.append(tableHeader);
  if (testResults != null) {
	  for (String[] results: testResults) {
		  String[] result = results;
		  infoBuilder.append("<tr> <td>" + result[0] + "</td><td>" + result[1] + "</td></tr>");
	  }
  /*<tr> <td>Jill</td> <td>Smith</td> <td>50</td>"
  		+ " </tr> <tr> <td>Eve</td> <td>Jackson</td> <td>94</td> </tr> </table>";*/
	  infoBuilder.append("</table>");
  }
  bw.write("<html><head><title>New Page</title></head><body><p>This is Body</p></body></html>");
  bw.write(infoBuilder.toString());
  bw.close();
	  } catch (Exception e) {
		  e.printStackTrace();
	  }
  }
}
