package feedsPocAutomation;

import uploadAndSearch.uploadAndSearch;

public class MainProject {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		uploadAndSearch testBegin = new uploadAndSearch();
		try {
			testBegin.beforeTest();
/*			try {
				testBegin.UploadAndSearch();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				testBegin.SearchSupplier();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				testBegin.SkuSearch();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				testBegin.PriceRangeSearch();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				testBegin.ProductStatus();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				testBegin.FeedRunProductSearch();
			} catch (Exception e) {
				e.printStackTrace();;
			}
			
			try {
				testBegin.ConsultantSKUSearch();
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				testBegin.ConsultantSupplierSearch();
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				testBegin.Logout();
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}*/
			
			try {
				testBegin.ConsultantBrandSearch();
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
			
			/*try {
				testBegin.ConsultantSearchAdminTab();
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}*/
			
			
			
			
			
			
			testBegin.afterTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
