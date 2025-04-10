package com.selenium.gui.testcases;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.selenium.gui.base.Base;
import com.selenium.gui.pageobjects.BookingPage;
import com.selenium.gui.pageobjects.FlightsPage;

public class FlightBookingTest extends Base {

	public WebDriver driver;
	FlightsPage flightsPage;
	BookingPage bookingPage;

	public FlightBookingTest() {

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
	public void verifySwitchingBookingDate() {

		flightsPage.searchOrigin("Calgary");
		flightsPage.selectOrigin("Calgary", "Alberta", "Canada");
		flightsPage.searchDestination("Toronto");
		flightsPage.selectDestination("Toronto", "Ontario", "Canada");
		flightsPage.selectDateRange("2025-09-18", "2025-09-25");

		flightsPage.searchFlight(true);
		bookingPage.waitForBookingPageToLoad();

		String actualTravelDate = bookingPage.getTravelDate();
		Assert.assertEquals(actualTravelDate, "Thursday, September 18", "The travel date should be equal to expected");

		bookingPage.selectDateFromPicker("Sat Sep 20");
		bookingPage.waitForBookingPageToLoad();

		actualTravelDate = bookingPage.getTravelDate();
		Assert.assertEquals(actualTravelDate, "Saturday, September 20", "The travel date should be equal to expected");
	}
}
