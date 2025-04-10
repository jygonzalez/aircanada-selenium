package com.selenium.gui.pageobjects;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.selenium.gui.utils.Utilities;

// https://www.aircanada.com/booking/ca/en/aco/availability/rt/outbound
public class BookingPage {

	WebDriver driver;
	private WebDriverWait wait;

	// OBJECTS (add locators here)
	@FindBy(css = "h1")
	private WebElement header;

	@FindBy(css = ".city-pairing-origin-city")
	private WebElement departureCity;

	@FindBy(css = ".city-pairing-origin-city-code")
	private WebElement departureCode;

	@FindBy(css = ".city-pairing-destination-city")
	private WebElement destinationCity;

	@FindBy(css = ".city-pairing-destination-city-code")
	private WebElement destinationCode;

	@FindBy(css = ".date.ng-star-inserted")
	private WebElement travelDate;

	@FindBy(css = "abc-tab-button")
	private List<WebElement> datePicker;

	@FindBy(css = ".genesis-loader-flight-search-info")
	private WebElement flightSearchLoader;

	public BookingPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(Utilities.EXPLICIT_WAIT_TIMEOUT));
		PageFactory.initElements(driver, this);
	}

	// ACTIONS (methods)
	public List<String> getBookingDetails() {

		String departureCity = getDepartureCity();
		String departureCode = getDepartureCode();
		String destinationCity = getDestinationCity();
		String destinationCode = getDestinationCode();
		String travelDate = getTravelDate();

		return Arrays.asList(departureCity, departureCode, destinationCity, destinationCode, travelDate);
	}

	public String getDepartureCity() {

		return departureCity.getText().trim();
	}

	public String getDepartureCode() {

		return departureCode.getText().replaceAll("[()]", "").trim();
	}

	public String getDestinationCity() {

		return destinationCity.getText().trim();
	}

	public String getDestinationCode() {

		return destinationCode.getText().replaceAll("[()]", "").trim();
	}

	public String getTravelDate() {

		return travelDate.getText().trim();
	}

	public void waitForBookingPageToLoad() {

		wait.until(ExpectedConditions.urlContains("/booking/"));
		wait.until(ExpectedConditions.invisibilityOf(flightSearchLoader));
		wait.until(ExpectedConditions.visibilityOf(header));
	}

	public void selectDateFromPicker(String date) {

		WebElement dateButton = getDateElementFromPicker(date);
		Utilities.waitAndClick(driver, dateButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
	}

	public String getLowestPriceOfDate(String date) {
		WebElement dateButton = getDateElementFromPicker(date);

		WebElement price = dateButton.findElement(By
				.xpath(".//span[contains(@class, 'price') and contains(@class, 'lowest')]/span[@aria-hidden='true']"));

		return price.getText();

	}

	private WebElement getDateElementFromPicker(String targetDate) {

		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("abc-tab-button")));

		WebElement result = null;

		for (WebElement dateElement : datePicker) {

			String date = dateElement.findElement(By.cssSelector(".departure-date")).getText().trim();

			if (targetDate.equalsIgnoreCase(date)) {
				result = dateElement;
				break;
			}

		}

		return result;
	}

}
