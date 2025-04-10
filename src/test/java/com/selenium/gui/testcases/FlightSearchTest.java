package com.selenium.gui.testcases;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.selenium.gui.base.Base;
import com.selenium.gui.pageobjects.BookingPage;
import com.selenium.gui.pageobjects.FlightsPage;

public class FlightSearchTest extends Base {

	public WebDriver driver;
	FlightsPage flightsPage;
	BookingPage bookingPage;

	public FlightSearchTest() {

		super();
	}

	@BeforeMethod
	public void setup() {

		driver = initializeBrowserAndOpenAppURL(properties.getProperty("browserName"));

		// page objects
		flightsPage = new FlightsPage(driver);
		bookingPage = new BookingPage(driver);

		// initial context setup
		flightsPage.acceptCookiesIfExists();
	}

	@AfterMethod
	public void tearDown() {

		driver.quit();
	}

	@Test(priority = 1)
	public void verifyInvalidFlight() {

		flightsPage.searchOrigin("Springfield");
		flightsPage.selectOrigin("Springfield", "Illinois", "United States");
		flightsPage.searchDestination("Springfield");
		flightsPage.selectDestination("Springfield", "Illinois", "United States");
		flightsPage.selectDateRange("2025-09-18", "2025-09-25");

		flightsPage.searchFlight(false);

		List<String> expectedAlerts = List.of(
				"The departure and arrival cities/airports you've selected are the same. Please review your selection and try again.",
				"It is not possible to search for flights which have both an origin and a destination in the United States.",
				"It is not possible to book a flight segment between two cities in the same country, unless that country is Canada."
						+ "Please contact Air Canada Reservations" + "Opens in a new tab"
						+ "for assistance with this type of booking.");
		List<String> actualAlerts = flightsPage.getAlertMessages();

		Assert.assertEquals(actualAlerts, expectedAlerts, "Alert messages should be equal to expected.");
	}

	@Test(priority = 2)
	public void verifyValidFlight() {

		flightsPage.searchOrigin("Calgary");
		flightsPage.selectOrigin("Calgary", "Alberta", "Canada");
		flightsPage.searchDestination("Toronto");
		flightsPage.selectDestination("Toronto", "Ontario", "Canada");
		flightsPage.selectDateRange("2025-09-18", "2025-09-25");

		flightsPage.searchFlight(true);
		bookingPage.waitForBookingPageToLoad();

		List<String> expectedBookingDetails = List.of("Calgary", "YYC", "Toronto", "YTO", "Thursday, September 18");
		List<String> actualBookingDetails = bookingPage.getBookingDetails();

		Assert.assertEquals(actualBookingDetails, expectedBookingDetails,
				"Booking details should be equal to expected.");
	}

	@Test(priority = 3)
	public void verifyForeignCurrency() {

		flightsPage.searchOrigin("Calgary");
		flightsPage.selectOrigin("Calgary", "Alberta", "Canada");
		flightsPage.searchDestination("Guadalajara");
		flightsPage.selectDestination("Guadalajara", "", "Mexico");
		flightsPage.selectDateRange("2025-09-18", "2025-09-25");

		flightsPage.selectCurrency("Japan - ¥");

		flightsPage.searchOrigin("Calgary");
		flightsPage.selectOrigin("Calgary", "Alberta", "Canada");
		flightsPage.searchDestination("Guadalajara");
		boolean destinationExists = flightsPage.destinationExists("Guadalajara", "", "Mexico");
		Assert.assertTrue(destinationExists,
				"The same destination, i.e., 'Guadalajara, Mexico', should be available after currency change");

		flightsPage.selectDestination("Guadalajara", "", "Mexico");
		flightsPage.selectDateRange("2025-09-18", "2025-09-25");

		flightsPage.searchFlight(true);
		bookingPage.waitForBookingPageToLoad();

		String priceTag = bookingPage.getLowestPriceOfDate("Sat Sep 20");
		double amount = Double.valueOf(priceTag.replace("¥", "").replace(",", ""));
		Assert.assertTrue(priceTag.contains("¥") && amount > 10000, "The price should be in Japanese Yen");
	}

	@Test(priority = 4)
	public void verifyForeignLanguage() {

		flightsPage.searchOrigin("Calgary");
		flightsPage.selectOrigin("Calgary", "Alberta", "Canada");
		flightsPage.searchDestination("Guadalajara");
		flightsPage.selectDestination("Guadalajara", "", "Mexico");
		flightsPage.selectDateRange("2025-09-18", "2025-09-25");

		flightsPage.selectLanguage("Français");
		Assert.assertEquals(flightsPage.getPageTitle(), "Où pouvons-nous vous emmener?",
				"The title of the page should be updated to French");

		flightsPage.searchOrigin("Calgary");
		flightsPage.selectOrigin("Calgary", "Alberta", "Canada");
		flightsPage.searchDestination("Guadalajara");
		boolean destinationExists = flightsPage.destinationExists("Guadalajara", "", "Mexique");
		Assert.assertTrue(destinationExists,
				"The same destination, i.e., 'Guadalajara, Mexico', should be available after language change");
	}

	@Test(priority = 5)
	public void testFind() {

		flightsPage.inputFind("test");
		flightsPage.clickFind();

		String actualAlerts = flightsPage.waitUntilFindPageLoads();

		String expectedText = "Search results for";

		Assert.assertEquals(actualAlerts, expectedText, "Find page text should be equal to expected.");
	}
	
	@Test(priority = 6)
	public void verifyShopWithPoints() {
	    flightsPage.clickShopWithPoints();
	    
	    String headingText = flightsPage.getShopWithPointsHeadingText();
	    Assert.assertTrue(headingText.contains("Stop sitting on your next trip"), "Expected page heading to mention 'Stop sitting on your next trip' after clicking 'Shop with points'.");
	}
}
