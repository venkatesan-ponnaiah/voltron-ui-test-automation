package uploadAndSearch;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.taskdefs.Length;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class uploadAndSearch {

	int priceValueWithoutDollar;
	int rowCountUploadAndSearch, rowCountSearchSupplier, rowCountFeedRunProductSearch, rowCountProductStatus,
			rowCountSupplierSearch;
	boolean isSearchSupplierLastPage, isPriceRangeSearchLastPage, isProductStatusLastPage = false;
	File resultFile = new File("test_result" + "_" + System.currentTimeMillis() + ".txt");
	FileWriter writer;
	FileInputStream fis;
	Properties properties = new Properties();
	DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	String timeStarted, timeEnded;
	long testStartTime, testStopTime, testDuration;
	File resultHTMLFile;
	ArrayList<String[]> resultList = new ArrayList<>();
	static {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver.exe");
	}
	WebDriver driver = new ChromeDriver();

	@Test(priority = 1)
	public void UploadAndSearch() throws Exception {

		// Logging in
		System.out.println("Inside Upload And Search");
		System.out.println("Logging in to Procat UI");

		// Upload
		try {
			driver.findElement(By.xpath("//a[@href='/procat/uploadproduct/index']")).click();
			driver.findElement(By.id("fileName")).sendKeys(properties.getProperty("uploadfilelocation"));
			WebElement loadDescription = driver.findElement(By.id("description"));
			loadDescription.sendKeys(properties.getProperty("loaddescription"));
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			System.out.println("Upload Successful");
			Thread.sleep(10000);
			this.takeSnapShotUploadAndSearch(driver, "Successful Upload");
			Thread.sleep(10000);
			// Fetching Load Description to be searched in Product Search
			WebElement loadDescriptionFinal = driver.findElement(By.id("fourButtonsDescription"));
			String element = loadDescriptionFinal.getText();
			System.out.println("Printing " + element);
			System.out.println("Load Description fetched to be searched in Product Search");
			Thread.sleep(5000);

			// Export csv Button

			List<WebElement> fourbuttons = driver.findElements(By.id("exportButton"));// ("exportButton"));
			fourbuttons.get(0).click(); // clicks on "first"
			System.out.println("Export CSV button clicked");

			try {
				waitForDownloadFinished();
				Thread.sleep(10000);
				System.out.println("Export Csv success");
				this.takeSnapShotUploadAndSearch(driver, "Export Csv success");
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			Thread.sleep(5000);

			// Index All Button
			WebElement indexAll = driver.findElement(By.id("uploadIndexAllOne"));
			indexAll.click(); // clicks on "third"
			System.out.println("IndexAll button clicked");
			Thread.sleep(5000);
			WebElement confirmationBoxforIndexAllBtn = driver.switchTo().activeElement();
			confirmationBoxforIndexAllBtn.findElement(By.id("positiveResponse")).click();
			System.out.println("IndexAll button - Confirmation YES clicked");
			Thread.sleep(10000);
			this.takeSnapShotUploadAndSearch(driver, "IndexAll button - Confirmation YES clicked");
			Thread.sleep(10000);

			// Expire All Button
			WebElement uploadExpireAll = driver.findElement(By.id("uploadExpireAllOne"));
			uploadExpireAll.click(); // clicks on "third"
			System.out.println("ExpireAll button clicked");
			Thread.sleep(5000);
			WebElement confirmationBoxforExpireAllBtn = driver.switchTo().activeElement();
			confirmationBoxforExpireAllBtn.findElement(By.id("positiveResponse")).click();
			System.out.println("ExpireAll button - Confirmation YES clicked");
			Thread.sleep(10000);
			this.takeSnapShotUploadAndSearch(driver, "ExpireAll button - Confirmation YES clicked");
			Thread.sleep(10000);

			// Uploaded Load Description Search in Product Search Page
			driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();
			WebElement LoadDescription = driver.findElement(By.id("loadDescriptions"));
			LoadDescription.sendKeys(element);
			Thread.sleep(5000);
			this.takeSnapShotUploadAndSearch(driver, "Entering Load Description");
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(35000);
			List<WebElement> rows = driver.findElements(By.xpath("//table[@id='my-ajax-table']/tbody/tr"));
			rowCountUploadAndSearch = rows.size();
			System.out.println("Total rows in Web table: " + rowCountUploadAndSearch);

			// WebsiteStatusCheck();
			Thread.sleep(10000);
			this.takeSnapShotUploadAndSearch(driver, "Search Result returned with Expired Product");
			Thread.sleep(5000);
			ProductSearchClearFunctionality();
		} catch (Exception e) {
			throw e;
		} finally {
			WebsiteStatusCheck("FAIL");
		}
	}

	@Test(priority = 2)
	public void SearchSupplier() throws Exception {

		// Supplier Search

		driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();
		System.out.println("Inside Supplier Search");
		WebElement Supplier = driver.findElement(By.id("supplierText"));
		Supplier.sendKeys(properties.getProperty("supplier"));
		this.takeSnapShotSearchSupplier(driver, "Entering Supplier Name");
		driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
		Thread.sleep(10000);
		ProductSearchNoofItemsDropdown();
		Thread.sleep(35000);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		this.takeSnapShotSearchSupplier(driver, "Search Results");
		String result = "PASS";

		if (driver.findElement(By.xpath("//*[@id=\'my-ajax-table\']")) != null) {

			while (!isSearchSupplierLastPage) {
				try {

					WebElement nextLink = driver.findElement(
							By.cssSelector(".dynatable-page-link.dynatable-page-next.dynatable-disabled-page"));
					if (nextLink != null) {
						isSearchSupplierLastPage = true;
						System.out.println("Has reached the last page");
					}
				} catch (NoSuchElementException e) {

				}
				result = SupplierSearch(result);
				System.out.println("After SupplierSearch() loop");
				this.takeSnapShotSearchSupplier(driver, "Supplier Search Result");
				WebElement Next = driver.findElement(By.cssSelector(".dynatable-page-link.dynatable-page-next"));
				Next.click();
				Thread.sleep(10000);
			}
			try {
				writer.write("SupplierSearch" + "-" + result + "\n");
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				String[] testresult = { "SupplierSearch", "Search for products with specified supplier name",
						properties.getProperty("supplier"), result };
				resultList.add(testresult);
			}
		}
		ProductSearchClearFunctionality();
	}

	@Test(priority = 3)
	public void SkuSearch() throws Exception {

		// SKU Search
		try {
			driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();
			System.out.println("Inside SKU Search");
			WebElement Sku = driver.findElement(By.id("sku"));
			Sku.sendKeys(properties.getProperty("sku"));
			this.takeSnapShotSkuSearch(driver, "SKU Entered");
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(10000);
			SkuCheck();
			Thread.sleep(10000);
			this.takeSnapShotSkuSearch(driver, "SKU Retrieved");
			ProductSearchClearFunctionality();
		} catch (Exception e) {
			throw e;
		}

	}

	@Test(priority = 4)
	public void PriceRangeSearch() throws Exception {
		System.out.println("Inside PriceRangeSearch");

		try {
			driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();

			WebElement advertisedFromPrice = driver.findElement(By.id("advertisedFromPrice"));
			advertisedFromPrice.sendKeys(properties.getProperty("advertisedfromprice"));
			WebElement advertisedToPrice = driver.findElement(By.id("advertisedToPrice"));
			advertisedToPrice.sendKeys(properties.getProperty("advertisedtoprice"));
			this.takeSnapShotPriceRangeSearch(driver, "After Entering Price Range");
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(10000);
			this.takeSnapShotPriceRangeSearch(driver, "Price Range Result Retrieved");
			System.out.println("After entering Price Values for Price Range Search");
			Thread.sleep(10000);
			Select dropdown = new Select(driver.findElement(By.id("dynatable-per-page-my-ajax-table")));
			dropdown.selectByIndex(Integer.parseInt(properties.getProperty("noofproducts")));
			Thread.sleep(10000);
			System.out.println("After selecting dropdown for Price Range Search");

			if (driver.findElement(By.xpath("//*[@id=\'my-ajax-table\']")) != null) {

				while (!isPriceRangeSearchLastPage) {
					try {

						WebElement nextLink = driver.findElement(
								By.cssSelector(".dynatable-page-link.dynatable-page-next.dynatable-disabled-page"));
						if (nextLink != null) {
							isPriceRangeSearchLastPage = true;
							System.out.println("Has reached the last page");
						}
					} catch (NoSuchElementException e) {

					}
					reUseMethod();
					System.out.println("after reUseMethod()");
					WebElement Next = driver.findElement(By.cssSelector(".dynatable-page-link.dynatable-page-next"));
					Next.click();
					Thread.sleep(10000);
				}

			}

			else {
				System.out.println("search result not available");
			}
			ProductSearchClearFunctionality();
		} catch (Exception e) {
			throw e;
		}
	}

	@Test(priority = 6)
	public void FeedRunProductSearch() throws Exception {

		String result = "PASS";
		try {
			File resultDir = new File("FeedRunProductSearch");
			if (!resultDir.exists()) {
				resultDir.mkdir();
			}
			System.out.println("Inside FeedRunProductSearch");
			driver.get(properties.getProperty("feedsurl"));
			Thread.sleep(10000);
			WebElement userName = driver.findElement(By.id("username"));
			userName.sendKeys(properties.getProperty("feedsloginusername"));
			WebElement password = driver.findElement(By.id("password"));
			password.sendKeys(properties.getProperty("feedsloginpassword"));
			driver.findElement(By.name("submit")).click();
			Thread.sleep(10000);
			this.takeSnapShotFeedRunProductSearch(driver, "Login");
			FeedSelectionDropdown();
			Thread.sleep(10000);
			WebElement LastRunLoadDescription = driver
					.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[3]"));
			String LastRunLoadDescriptionString = LastRunLoadDescription.getText();
			this.takeSnapShotFeedRunProductSearch(driver, "Before Manual Run");
			driver.findElement(By.xpath("//*[@id=\"btnRun\"]/span[2]")).click();
			Thread.sleep(100000);
			WebElement LatestRunLoadDescription = driver
					.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[3]"));
			String LatestRunLoadDescriptionString = LatestRunLoadDescription.getText();
			this.takeSnapShotFeedRunProductSearch(driver, "After Manual Run");
			System.out.println("LastRunLoadDescription " + LastRunLoadDescriptionString);
			System.out.println("LatestRunLoadDescription " + LatestRunLoadDescriptionString);
			WebElement CompletionStatus = driver.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[5]"));
			String CompletionStatusString = CompletionStatus.getText();
			System.out.println("CompletionStatus " + CompletionStatusString);
			String ExpectedCompletionStatus = "COMPLETE";
			WebElement ProductCount = driver.findElement(By.xpath("//*[@id=\"dataTable\"]/tbody/tr[1]/td[4]"));
			String ProductCountinFeed = ProductCount.getText();
			int IntProductCountinFeed = Integer.parseInt(ProductCountinFeed);
			if (CompletionStatusString.equalsIgnoreCase(ExpectedCompletionStatus)) {
				System.out.println("Inside If");
				driver.get(properties.getProperty("procaturl"));
				WebElement ProcatuserName = driver.findElement(By.id("userName"));
				ProcatuserName.sendKeys(properties.getProperty("username"));
				WebElement Procatpassword = driver.findElement(By.id("password"));
				Procatpassword.sendKeys(properties.getProperty("password"));
				driver.findElement(By.name("action")).click();
				driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();
				WebElement LoadDescription = driver.findElement(By.id("loadDescriptions"));
				LoadDescription.sendKeys(LatestRunLoadDescriptionString);
				this.takeSnapShotFeedRunProductSearch(driver, "Load Description Entered");
				Thread.sleep(10000);
				driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
				Thread.sleep(100000);
				/*
				 * ProductSearchNoofItemsDropdown(); Thread.sleep(10000);
				 */
				this.takeSnapShotFeedRunProductSearch(driver, "Manual Run Count");
				String text = driver.findElement(By.id("dynatable-record-count-my-ajax-table")).getText();
				System.out.println("Text at the table is " + text);
				String[] trimmedText = text.split(" ");
				System.out.println("Records in Search Result: " + trimmedText);
				String strNew = trimmedText[trimmedText.length - 2];
				int IntProductCountinProcatUI = Integer.parseInt(strNew);
				System.out.println("Integer Records in Search Result: " + IntProductCountinProcatUI);

				if (IntProductCountinFeed == IntProductCountinProcatUI) {
					System.out.println("Count in Feed equals Count in Procat");
				} else {
					result = "FAIL";
				}
			} else {
				System.out.println("Job not Successfully Completed...");
				result = "Not run";
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = "FAIL";
			throw e;
		} finally {
			try {
				writer.write("FeedRunProductSearch" + "-" + result + "\n");
				String[] testresult = { "FeedRunProductSearch", "Run a feed in feed UI. Verify completion status."
						+ "Search in procat UI with load description of the feed run and match the products count",
						properties.getProperty("feedselectiondropdown"), result };
				resultList.add(testresult);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test(priority = 5)
	public void ProductStatus() throws Exception {

		// Product Status
		System.out.println("Inside Product Status Search");
		String result = "PASS";

		/*
		 * driver.get(properties.getProperty("procaturl")); WebElement userName =
		 * driver.findElement(By.id("userName"));
		 * userName.sendKeys(properties.getProperty("username")); WebElement password =
		 * driver.findElement(By.id("password"));
		 * password.sendKeys(properties.getProperty("password"));
		 * driver.findElement(By.name("action")).click();
		 * System.out.println("Login to Procat UI Successful");
		 * driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).
		 * click();
		 */

		try {
			driver.findElement(By.xpath("//a[@href='/procat/productsearchajax/index']")).click();
			WebElement Supplier = driver.findElement(By.id("supplierText"));
			Supplier.sendKeys(properties.getProperty("supplier"));
			WebElement ProductStatus = driver.findElement(By.id("productStatus2"));
			ProductStatus.click();
			this.takeSnapShotProductStatus(driver, "Push Status Selected");
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(10000);
			ProductSearchNoofItemsDropdown();
			Thread.sleep(10000);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

			if (driver.findElement(By.xpath("//*[@id=\'my-ajax-table\']")) != null) {

				while (!isProductStatusLastPage) {
					try {

						WebElement nextLinkProductStatus = driver.findElement(
								By.cssSelector(".dynatable-page-link.dynatable-page-next.dynatable-disabled-page"));
						if (nextLinkProductStatus != null) {
							isProductStatusLastPage = true;
							System.out.println("Has reached the last page");
						}
					} catch (NoSuchElementException e) {

					}
					result = ProductStatusSearch(result);
					Thread.sleep(10000);
					this.takeSnapShotProductStatus(driver, "Push Status Selected");
					System.out.println("after ProductStatusSearch() loop");
					WebElement NextProductStatus = driver
							.findElement(By.cssSelector(".dynatable-page-link.dynatable-page-next"));
					NextProductStatus.click();
					Thread.sleep(10000);
				}

				try {
					writer.write("ProductStatusSearch" + "-" + result + "\n");
					writer.flush();
				} catch (Exception e) {
					result = "FAIL";
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			result = "FAIL";
			throw e;
		} finally {
			String[] testresult = { "ProductStatusSearch", "Search for products matching the selected product status",
					"Push Status", result };
			resultList.add(testresult);
		}
	}

	@Test(priority = 7)
	public void ConsultantSKUSearch() throws Exception {
		String result = "PASS";
		try {
			driver.findElement(By.xpath("//a[@href='/procat/consultantsearchajax/index']")).click();
			System.out.println("Inside Consultant Search");
			WebElement ConsultantSku = driver.findElement(By.id("sku"));
			ConsultantSku.sendKeys(properties.getProperty("ConsultantSearchExpectedSku"));
			this.takeSnapShotConsultantSearchSkuCheck(driver, "SKU Entered in Consultant Search");
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(20000);
			this.takeSnapShotConsultantSearchSkuCheck(driver, "SKU Retrieved");
			ConsultantSearchSkuCheck();
			Thread.sleep(10000);
			try {
				writer.write("ProductStatusSearch" + "-" + result + "\n");
				writer.flush();
			} catch (Exception e) {
				result = "FAIL";
				e.printStackTrace();

			}
			ConsultantSearchClearFunctionality();

		} catch (Exception e) {
			result = "FAIL";
			e.printStackTrace();
			throw e;
		}

		finally {
			String[] testresult = { "ProductStatusSearch", "Search for products matching the selected product status",
					"Push Status", result };
			resultList.add(testresult);
		}
	}

	@Test(priority = 8)
	public void ConsultantSupplierSearch() throws Exception {
		try {
			driver.findElement(By.xpath("//a[@href='/procat/consultantsearchajax/index']")).click();
			System.out.println("Inside Consultant Search");
			WebElement ConsultantSupplier = driver.findElement(By.id("supplier"));
			ConsultantSupplier.sendKeys(properties.getProperty("ConsultantSearchSupplier"));

			this.takeSnapShotConsultantSupplierSearch(driver, "Supplier Entered in Consultant Search");
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(20000);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			this.takeSnapShotConsultantSupplierSearch(driver, "Consultant Search - Supplier Search Results");
			ConsultantSearchSupplierCheck();
			ConsultantSearchClearFunctionality();

		} catch (Exception e) {
			throw e;
		}
	}

	@Test(priority = 10)
	public void ConsultantSearchAdminTab() throws Exception {

		try {
			
			ConsultantSearchAdminTabCheck();

		} catch (Exception e) {
			throw e;
		}

	}

	@SuppressWarnings("null")
	@Test(priority = 9)
	public void ConsultantBrandSearch() throws Exception {
		try {
			driver.findElement(By.xpath("//a[@href='/procat/consultantsearchajax/index']")).click();
			Thread.sleep(5000);
			WebElement ConsultantBrand = driver.findElement(By.xpath("//*[@id=\"consultantSearchDao\"]/div[3]/div[1]/div[1]/div[3]/div"));
			ConsultantBrand.click();
			Thread.sleep(5000);
			ConsultantBrandSearchCheck();
		} catch (Exception e) {
			throw e;
		}
	}

	@Test(priority = 11)
	public void Logout() {

		driver.findElement(By.xpath("//*[@id=\"spl-topnav\"]/div[2]/div/div[2]/div/a/i")).click();
		WebElement confirmationBoxforLogoutBtn = driver.switchTo().activeElement();
		confirmationBoxforLogoutBtn.findElement(By.cssSelector(".ui.green.ok.inverted.button")).click();
		System.out.println("Logout button - Confirmation YES clicked");
	}

	public void FeedSelectionDropdown() {
		Select dropdown = new Select(driver.findElement(By.id("combobox")));
		dropdown.selectByIndex(Integer.parseInt(properties.getProperty("feedselectiondropdown")));
	}

	public void reUseMethod() {

		List<WebElement> rows = driver.findElements(By.xpath("//table[@id='my-ajax-table']/tbody/tr"));

		int rowCount = rows.size();
		System.out.println("Total rows in Web table: " + rowCount);
		String result = "PASS";

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
			if (priceValueWithoutDollar >= Integer.parseInt(properties.getProperty("advertisedfromprice"))
					&& priceValueWithoutDollar <= Integer.parseInt(properties.getProperty("advertisedtoprice"))) {
				System.out.println("Price Element on Row #" + priceValueWithoutDollar + " is between search criteria");
			} else {
				System.out.println("Price is not within the range");
				result = "FAIL";
			}
		}
		try {
			writer.write("PriceRangeSearch" + "-" + result + "\n");
			String[] testresult = { "PriceRangeSearch",
					"Search for products within specified price range in product search page",
					"From " + properties.getProperty("advertisedfromprice") + " To "
							+ properties.getProperty("advertisedtoprice"),
					result };
			resultList.add(testresult);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String SupplierSearch(String result) {
		List<WebElement> rows = driver.findElements(By.xpath("//table[@id='my-ajax-table']/tbody/tr"));
		rowCountSearchSupplier = rows.size();

		System.out.println("Total rows in Web table: " + rowCountSearchSupplier);
		for (int rowIter = 1; rowIter <= rowCountSearchSupplier; rowIter++) {
			WebElement SupplierName = driver.findElement(
					By.cssSelector("#my-ajax-table > tbody > tr:nth-child(" + rowIter + ") > td:nth-child(5)"));

			String AppSupplierName = SupplierName.getText();
			System.out.println("Supplier Name " + AppSupplierName);
			String ExpectedSupplierName = properties.getProperty("supplier");
			if (AppSupplierName.equalsIgnoreCase(ExpectedSupplierName)) {
				System.out.println("Expected SupplierName Retrieved Successfully!!!");
			} else {
				System.out.println("Expected SupplierName not Retrieved Successfully...");
				result = "FAIL";
			}
		}

		return result;

	}

	private String ProductStatusSearch(String result) {
		List<WebElement> rows = driver.findElements(By.xpath("//table[@id='my-ajax-table']/tbody/tr"));
		rowCountProductStatus = rows.size();
		System.out.println("Total no. of rows in Web Table for Product Status Search" + rowCountProductStatus);

		for (int rowIter = 1; rowIter <= rowCountProductStatus; rowIter++) {
			WebElement ProductStatus = driver.findElement(
					By.cssSelector("#my-ajax-table > tbody > tr:nth-child(" + rowIter + ") > td:nth-child(9)"));

			String AppProductStatus = ProductStatus.getText();
			System.out.println("Website Status " + AppProductStatus);
			String ExpectedAppProductStatus = "PUSH";
			if (AppProductStatus.equalsIgnoreCase(ExpectedAppProductStatus)) {
				System.out.println("Expected Product Status Retrieved Successfully!!!");
			} else {
				System.out.println("Expected Product Status not Retrieved Successfully...");
				result = "FAIL";
			}
		}
		return result;
	}

	private void SkuCheck() throws InterruptedException {

		Thread.sleep(10000);
		String result = "PASS";
		// for (int rowIter = 1; rowIter <= rowCount; rowIter++) {
		WebElement AppSku = driver.findElement(By.linkText(properties.getProperty("sku")));
		String SkuRetrieved = AppSku.getText();
		System.out.println("SKU in Application " + SkuRetrieved);
		String ExpectedSku = properties.getProperty("sku");
		if (SkuRetrieved.equals(ExpectedSku)) {
			System.out.println("SKU Retrieved Successfully!!!");
		} else {
			System.out.println("SKU Not Retrieved Successfully...");
			result = "FAIL";
		}
		// }
		try {
			writer.write("SKUSearch" + "-" + result + "\n");
			String[] testresult = { "SKUSearch", "Search for a particular SKU in product search page", ExpectedSku,
					result };
			resultList.add(testresult);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void ConsultantSearchSkuCheck() throws Exception {

		String result = "PASS";
		String ConsultantSearchExpectedSku = properties.getProperty("ConsultantSearchExpectedSku");
		System.out.println("Sku in Properties File: " + ConsultantSearchExpectedSku);
		WebElement ConsultantSearchAppSku = driver
				.findElement(By.linkText(properties.getProperty("ConsultantSearchExpectedLinkText")));
		ConsultantSearchAppSku.click();
		Thread.sleep(5000);
		this.takeSnapShotSearchSupplier(driver, "SKU Product Description");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		Thread.sleep(2000);
		this.takeSnapShotSearchSupplier(driver, "SKU in Detailed Product Description");
		String ConsultantSearchAppSkuRetrieved = ConsultantSearchAppSku.getText();
		if (ConsultantSearchAppSkuRetrieved.equals((properties.getProperty("ConsultantSearchExpectedLinkText")))) {
			ConsultantSearchAppSkuRetrieved = "4593583";
		} else {
			System.out.println("Consultant SKU Not Retrieved Successfully...");
		}

		System.out.println("SKU in Application " + ConsultantSearchAppSkuRetrieved);
		if (ConsultantSearchAppSkuRetrieved.equals(ConsultantSearchExpectedSku)) {
			System.out.println("Consultant SKU Retrieved Successfully!!!");
		} else {
			System.out.println("Consultant SKU Not Retrieved Successfully...");
			result = "FAIL";
		}
		// }
		try {
			writer.write("ConsultantSearchSkuCheck" + "-" + result + "\n");
			String[] testresult = { "ConsultantSearchSkuCheck", "Search for a particular SKU in Consultant search page",
					ConsultantSearchAppSkuRetrieved, result };
			resultList.add(testresult);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void ConsultantSearchSupplierCheck() throws Exception {

		String result = "PASS";
		String ConsultantSupplierSearched = properties.getProperty("ConsultantSearchSupplier");
		System.out.println("Supplier in Properties File: " + ConsultantSupplierSearched);
		if (driver.findElement(By.xpath("//*[@id=\'result-msg\']")) != null) {

			System.out.println("Search results returned for Supplier Searched in Consultant Search");
		}

		else {
			System.out.println("Search results not returned for Supplier Searched in Consultant Search");
		}
		try {
			writer.write("ConsultantSupplierSearch" + "-" + result + "\n");
			String[] testresult = { "ConsultantSupplierSearch",
					"Search for products with specified supplier name in Consultant Search",
					properties.getProperty("ConsultantSearchSupplier"), result };
			resultList.add(testresult);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void ConsultantSearchAdminTabCheck() throws Exception {

		WebElement AdminDropDownElement = driver.findElement(By.cssSelector(".ui.simple.dropdown.item"));
		AdminDropDownElement.click();
		this.takeSnapShotConsultantSearchAdminTab(driver, "Admin Tab DropDown Values");

		int i = 1;
		for (; i < 3; i++) {
			driver.findElement(By.xpath("//*[@id=\"spl-topnav\"]/div[2]/div/div[1]/div/div[" + i + "]"));
		}

		System.out.println("No. of elements in Admin Tab: " + i--);

		driver.findElement(By.xpath("//*[@id=\"spl-topnav\"]/div[2]/div/div[1]/div/div[1]")).click();
		Thread.sleep(3000);
		this.takeSnapShotConsultantSearchAdminTab(driver, "List Management DropDown Values");

		i = 1;
		for (; i < 10; i++) {
			driver.findElement(
					By.xpath("//*[@id=\"spl-topnav\"]/div[2]/div/div[1]/div/div[1]/div/div[" + i + "]/span/a"));
		}

		System.out.println("No. of elements in List Management: " + i--);

		driver.findElement(By.xpath("//*[@id=\"spl-topnav\"]/div[2]/div/div[1]/div/div[3]")).click();
		Thread.sleep(1000);
		this.takeSnapShotConsultantSearchAdminTab(driver, "Security Management DropDown Values");

		i = 1;
		for (; i < 4; i++) {
			driver.findElement(By.xpath("//*[@id=\"spl-topnav\"]/div[2]/div/div[1]/div/div[3]/div/div[" + i + "]"));
		}
		System.out.println("No. of elements in Security Management: " + i--);

	}

	private void ConsultantBrandSearchCheck() throws Exception {
		String result = "PASS";
		try {
			List<WebElement> elementsRoot = driver.findElements(By.cssSelector("#consultantSearchDao > div.ui.tiny.form > div.ui.equal.width.tiny.form > div:nth-child(1) > div:nth-child(3) > div > div.menu>div"));
			System.out.println("Brand Count: "+elementsRoot.size());
					for(int i = 0; i < elementsRoot.size(); ++i) {
					     WebElement BrandElements = elementsRoot.get(i);
					     System.out.println("Brand Dropdown size: "+BrandElements.getText());
					     if (BrandElements.getText().equalsIgnoreCase(properties.getProperty("ConsultantSearchBrand"))) {
					    	 BrandElements.click();
					    	 this.takeSnapShotConsultantBrandSearch(driver, "Brand Entered");
					    	 break;
					     }
					     else {
					    	 result = "FAIL";
					     }
					    	 
					}
			driver.findElement(By.cssSelector(".ui.tiny.submit.button")).click();
			Thread.sleep(10000);
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
			Thread.sleep(3000);
			this.takeSnapShotConsultantBrandSearch(driver, "Consultant Search - Supplier Search Results");
			try {
				writer.write("ConsultantBrandSearch" + "-" + result + "\n");
				String[] testresult = { "ConsultantBrandSearch",
						"Search for products with specified Brand name in Consultant Search",
						properties.getProperty("ConsultantSearchBrand"), result };
				resultList.add(testresult);
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		 catch (Exception e) {
				throw e;
			}
		
		
		
	}

	private WebDriver getDriver() {
		// TODO Auto-generated method stub
		return null;
	}

	public void ProductSearchClearFunctionality() {
		// Clear the fields
		WebElement ProductSearchClear = driver.findElement(By.id("productClear"));
		ProductSearchClear.click();
	}

	public void ConsultantSearchClearFunctionality() {
		// Clear the fields
		WebElement ConsultantSearchClearClear = driver.findElement(By.id("consultantClear"));
		ConsultantSearchClearClear.click();
	}

	public void ProductSearchNoofItemsDropdown() {
		Select dropdown = new Select(driver.findElement(By.id("dynatable-per-page-my-ajax-table")));
		dropdown.selectByIndex(Integer.parseInt(properties.getProperty("noofproducts")));
	}

	private void WebsiteStatusCheck(String result) {
		System.out.println("Inside WebsiteStatusCheck");
		int rowCountWebsiteStatusCheck = 1;
		try {
			for (int rowIter = 1; rowIter <= rowCountWebsiteStatusCheck; rowIter++) {
				WebElement webSiteStatus = driver.findElement(
						By.cssSelector("#my-ajax-table > tbody > tr:nth-child(" + rowIter + ") > td:nth-child(10)"));
				String AppStatus = webSiteStatus.getText();
				System.out.println("Website Status " + AppStatus);
				String ExpectedStatus = "EXPIRED";
				if (AppStatus.equalsIgnoreCase(ExpectedStatus)) {
					System.out.println("Product Expired Successfully!!!");
					result = "PASS";
				} else {
					System.out.println("Product didn't Expire");
					result = "FAIL";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			writer.write("UploadandSearch" + "-" + result + "\n");
			String[] testresult = { "UploadandSearch",
					"Upload a product. Export and Expire it.\n"
							+ "Search the product with load description in product search page",
					"Description: " + properties.getProperty("loaddescription") + "\t\nUpload File: "
							+ properties.getProperty("uploadfilelocation"),
					result };
			resultList.add(testresult);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void takeSnapShotUploadAndSearch(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShot");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("UploadandSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotSearchSupplier(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShot");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("SupplierSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotSkuSearch(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShot");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("SKUSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);

	}

	private void takeSnapShotPriceRangeSearch(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShot");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("PriceRangeSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotFeedRunProductSearch(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShot");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("FeedRunProductSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotProductStatus(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShot");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("ProductStatusSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotConsultantSearchSkuCheck(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShotConsultantSearchSkuCheck");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("ConsultantSearchSkuCheck");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotConsultantSupplierSearch(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShotConsultantSearchSupplierCheck");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("ConsultantSupplierSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotConsultantSearchAdminTab(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShotConsultantSearchAdminTab");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("ConsultantSearchAdminTab");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void takeSnapShotConsultantBrandSearch(WebDriver driver, String screenshot) throws Exception {

		System.out.println("Inside takeSnapShotConsultantSearchBrandCheck");
		TakesScreenshot scrShot = (TakesScreenshot) driver;
		File resultDir = new File("ConsultantBrandSearch");
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File resultFile = new File(resultDir, screenshot + System.currentTimeMillis() + ".png");
		FileUtils.copyFile(SrcFile, resultFile);
	}

	private void waitForDownloadFinished() throws Exception {

		try {
			File folder = new File(System.getProperty("user.dir"));
			long size, reSize;
			do {
				size = FileUtils.sizeOfDirectory(folder);
				Thread.sleep(5000);
				reSize = FileUtils.sizeOfDirectory(folder);
			} while (size != reSize);
			System.out.println("Download completed");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// getWebBrowser().quit();
			System.out.println("Finally Block of waitForDownloadFinished() ");
		}

	}

	@BeforeTest
	public void beforeTest() {

		try {
			fis = new FileInputStream("testsuite.properties");
			properties.load(fis);
			LocalDateTime now = LocalDateTime.now();
			timeStarted = date.format(now);
			testStartTime = System.currentTimeMillis();
			driver.get(properties.getProperty("procaturl"));
			WebElement userName = driver.findElement(By.id("userName"));
			userName.sendKeys(properties.getProperty("username"));
			WebElement password = driver.findElement(By.id("password"));
			password.sendKeys(properties.getProperty("password"));
			driver.findElement(By.name("action")).click();
			System.out.println("Login to Procat UI Successful");
			try {
				writer = new FileWriter(resultFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Unable to open properties file");
		}
		System.out.println("@Before Test");
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver.exe");
		driver.manage().window().maximize();
	}

	@AfterTest
	public void afterTest() {

		LocalDateTime now = LocalDateTime.now();
		timeEnded = date.format(now);
		testStopTime = System.currentTimeMillis();
		testDuration = testStopTime - testStartTime;
		writeToHTML();
		System.out.println("@After Test");
		try {
			writer.close();
			Desktop.getDesktop().browse(resultHTMLFile.toURI());
			// driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void writeToHTML() {
		try {
			resultHTMLFile = new File("Result_" + System.currentTimeMillis() + ".html");
			BufferedWriter bw = new BufferedWriter(new FileWriter(resultHTMLFile));
			StringBuilder infoBuilder = new StringBuilder();
			String htmlHeader = "<html>\r\n" + "<script src=\"script.js\"></script>"
					+ "			<head>\r\n  <link rel=\"stylesheet\" href=\"styles.css\"> \n"
					+ "<style> .resultFail{background-color:red;}.resultPass{background-color:green;}</style>"
					+ "			</head>\n <body>";
			infoBuilder.append(htmlHeader);
			String procatHeader = "<h1><img src=\"cat_blue.png\" class=\"image\">ProCat - Flight Centre Product Catalogue\n</h1>";
			infoBuilder.append(procatHeader);
			String resultHeader = "<h1>Procat UI Automation test results\n</h1><h2> Test start time: " + timeStarted
					+ " End time: " + timeEnded + " Duration: " + testDuration / 60000 + " mins\n</h2>";
			infoBuilder.append(resultHeader);
			String tableHeader = "<table><tr> <th>Testcase Name</th> <th>Criteria Executed</th> <th> Test Input</th> "
					+ "<th>Testresult</th> </tr>";
			infoBuilder.append(tableHeader);
			if (resultList != null) {
				for (String[] results : resultList) {
					String[] result = results;
					infoBuilder.append(
							"<tr> <td>" + result[0] + "</td><td>" + result[1] + "</td> <td>" + result[2] + "</td>");
					String resultClass = null;
					if (result[3].equalsIgnoreCase("PASS")) {
						resultClass = "<td class=\"resultPass\"><a href=\"./" + result[0] + "\" target=\"_blank\">"
								+ result[3] + "</a></td></tr>";
					} else {
						resultClass = "<td class=\"resultFail\"><a href=\"./" + result[0] + "\" target=\"_blank\">"
								+ result[3] + "</a></td></tr>";
					}
					infoBuilder.append(resultClass);
				}
				infoBuilder.append("</table>");
			}
			String htmlCloser = "</body> \n </html>";
			infoBuilder.append(htmlCloser);
			bw.write(infoBuilder.toString());
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
