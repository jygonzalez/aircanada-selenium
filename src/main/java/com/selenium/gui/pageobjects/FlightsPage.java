package com.selenium.gui.pageobjects;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.selenium.gui.utils.Utilities;

// https://www.aircanada.com/home/ca/en/aco/flights
public class FlightsPage {

	WebDriver driver;
	private WebDriverWait wait;

	// OBJECTS (add locators here)
	@FindBy(id = "onetrust-accept-btn-handler")
	private WebElement acceptCookiesButton;

	@FindBy(id = "flightsOriginLocationbkmgLocationContainer")
	private WebElement originContainer;

	@FindBy(id = "flightsOriginLocation")
	private WebElement originTextBox;

	@FindBy(id = "flightsOriginDestinationbkmgLocationContainer")
	private WebElement destinationContainer;

	@FindBy(id = "flightsOriginDestination")
	private WebElement destinationTextBox;

	@FindBy(xpath = "//li[contains(@id, 'flightsOriginLocationSearchResult')]")
	private List<WebElement> originResults;

	@FindBy(xpath = "//li[contains(@id, 'flightsOriginDestinationSearchResult')]")
	private List<WebElement> destinationResults;

	@FindBy(xpath = "//button[contains(@id, 'findButton')]")
	private WebElement searchButton;

	@FindBy(xpath = "//button[contains(@id, 'travelDates-showCalendar')]")
	private WebElement showCalendarButton;

	@FindBy(css = ".abc-calendar-month-name")
	private List<WebElement> monthYearDisplay;

	@FindBy(xpath = "//button[contains(@id, 'travelDates_nextMonth')]")
	private WebElement nextMonthButton;

	@FindBy(xpath = "//button[contains(@id, 'travelDates_previousMonth')]")
	private WebElement previousMonthButton;

	@FindBy(xpath = "//button[contains(@id, 'travelDates_1_confirmDates')]")
	private WebElement confirmDatesButton;

	@FindBy(css = ".abc-ripple-wrapper")
	private WebElement sessionSettingsHeaderButton;

	@FindBy(id = "siteEditionDropdownLibra")
	private WebElement sessionSettingsCurrencyDropdown;

	@FindBy(xpath = "//ul[contains(@aria-label, 'currency') and @role='listbox']//li")
	private List<WebElement> sessionSettingsCurrencyOptions;

	@FindBy(id = "siteLanguageDropdown")
	private WebElement sessionSettingsLanguageDropdown;

	@FindBy(xpath = "//ul[contains(@aria-label, 'language') and @role='listbox']//li")
	private List<WebElement> sessionSettingsLanguageOptions;

	@FindBy(xpath = "//button[contains(@data-analytics-val, 'Find')]")
	private WebElement findbutton;

	@FindBy(id = "acSiteSearchInput")
	private WebElement findtextinput;

	@FindBy(xpath = "//div[@id='fatHeader']")
	private WebElement fatHeader;

	@FindBy(className = "form-label")
	private WebElement searchResultsLabel;

	@FindBy(css = "abc-inset-loader .abc-material-spinner-container")
	private WebElement spinner;

	@FindBy(css = ".ac-h1.ac-page-title")
	private WebElement pageTitle;

	// Confirm button
	@FindBy(id = "acEditionSelectorConfirmButton")
	private WebElement sessionSettingsConfirmButton;

	@FindBy(xpath = "//a[.//p[contains(text(),'Shop with points')]]")
	private WebElement shopWithPointsButton;

	@FindBy(xpath = "//*[@id=\"main\"]/div[1]/div/div/article/div/div/div/div[2]/h1")
	private WebElement shopWithPointsHeading;

	public FlightsPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(Utilities.EXPLICIT_WAIT_TIMEOUT));
		PageFactory.initElements(driver, this);
	}

	// ACTIONS (methods)
	public void acceptCookiesIfExists() {

		try {
			if (acceptCookiesButton.isDisplayed())
				Utilities.waitAndClick(driver, acceptCookiesButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		} catch (NoSuchElementException e) {
			// Cookies popup is not displayed, no action needed
		}
	}

	public void searchOrigin(String location) {

		Utilities.waitAndClick(driver, originContainer, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		originTextBox.sendKeys(location);
	}

	public void searchDestination(String location) {

		Utilities.waitAndClick(driver, destinationContainer, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		destinationTextBox.sendKeys(location);
	}

	public void selectOrigin(String city, String state, String country) {

		for (WebElement location : originResults) {
			city = city.trim();
			state = state.trim();
			country = country.trim();

			String cityName = location.findElement(By.cssSelector(".location-city-name")).getText().trim();
			String countryName = location.findElement(By.cssSelector(".location-country-name")).getText().trim();

			// Remove commas from extracted text
			cityName = cityName.replace(",", "").trim();
			countryName = countryName.replace(",", "").trim();

			// Check state if provided
			boolean isStateMatching = true;
			if (!state.isEmpty()) {
				List<WebElement> stateElements = location.findElements(By.cssSelector(".location-state-name"));
				if (stateElements.isEmpty()) {
					isStateMatching = false;
				} else {
					String stateName = stateElements.get(0).getText().trim().replace(",", "");
					isStateMatching = stateName.equalsIgnoreCase(state);
				}
			}

			if (cityName.equalsIgnoreCase(city) && isStateMatching && countryName.equalsIgnoreCase(country)) {
				Utilities.waitAndClick(driver, location, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
				break;
			}
		}
	}

	public void selectDestination(String city, String state, String country) {

		Utilities.waitAndClick(driver, findDestinationInDropdown(city, state, country),
				Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
	}

	public boolean destinationExists(String city, String state, String country) {

		WebElement destination = findDestinationInDropdown(city, state, country);

		if (destination != null) {
			return true;
		}

		return false;
	}

	private WebElement findDestinationInDropdown(String city, String state, String country) {

		WebElement result = null;

		for (WebElement location : destinationResults) {
			String cityName = location.findElement(By.cssSelector(".location-city-name")).getText().trim();
			String countryName = location.findElement(By.cssSelector(".location-country-name")).getText().trim();

			// Remove commas from extracted text
			cityName = cityName.replace(",", "").trim();
			countryName = countryName.replace(",", "").trim();

			// Also remove commas from input parameters
			city = city.trim();
			state = state.trim();
			country = country.trim();

			// Check state if provided
			boolean isStateMatching = true;
			if (!state.isEmpty()) {
				List<WebElement> stateElements = location.findElements(By.cssSelector(".location-state-name"));

				if (stateElements.isEmpty()) {
					isStateMatching = false;
				} else {
					String stateName = stateElements.get(0).getText().replace(",", "").trim();
					isStateMatching = stateName.equalsIgnoreCase(state);
				}
			}

			if (cityName.equalsIgnoreCase(city) && isStateMatching && countryName.equalsIgnoreCase(country)) {
				result = location;
				break;
			}
		}

		return result;
	}

	public void searchFlight(boolean waitToChangedPage) {

		Utilities.waitAndClick(driver, searchButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);

		if (waitToChangedPage) {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".top-nav")));
		}
	}

	public List<String> getAlertMessages() {

		List<WebElement> visibleAlerts = driver.findElements(By.cssSelector(".abc-theme-alert")).stream()
				.filter(WebElement::isDisplayed).collect(Collectors.toList());

		return visibleAlerts.stream().map(alert -> alert.findElement(By.cssSelector(".abc-theme-alert-message-text"))
				.getText().replaceAll("[\\r\\n]", "").trim()).collect(Collectors.toList());
	}

	public void selectDate(String dateString) {

		String dateXPath = String.format("//*[contains(@id, 'travelDates-date-%s')]", dateString);
		WebElement dateElement = driver.findElement(By.xpath(dateXPath));

		Utilities.waitAndClick(driver, dateElement, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
	}

	public void selectDateRange(String startDate, String endDate) {

		Utilities.waitAndClick(driver, showCalendarButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);

		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);

		navigateToMonthYear(start.getMonth().toString() + " " + start.getYear());
		selectDate(start.toString());

		navigateToMonthYear(end.getMonth().toString() + " " + end.getYear());
		selectDate(end.toString());

		Utilities.waitAndClick(driver, confirmDatesButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
	}

	public String getCurrentMonthYear() {

		for (WebElement monthYear : monthYearDisplay) {
			if (!monthYear.getText().trim().isEmpty())
				return monthYear.getText().trim();
		}

		return "";
	}

	private void navigateToMonthYear(String targetMonthYear) {

		while (true) {

			String currentMonthYear = getCurrentMonthYear();

			if (currentMonthYear.isEmpty()) {
				break;
			}

			// Try to find an element that matches target date
			Optional<WebElement> targetElement = monthYearDisplay.stream()
					.filter(element -> element.getText().trim().equalsIgnoreCase(targetMonthYear)).findFirst();

			// If found, break out of the loop
			if (targetElement.isPresent()) {
				break;
			}

			// Otherwise, determine navigation direction
			LocalDate currentDate = parseMonthYear(currentMonthYear);
			LocalDate targetDate = parseMonthYear(targetMonthYear);

			if (targetDate.isAfter(currentDate)) {
				Utilities.waitAndClick(driver, nextMonthButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
			} else {
				Utilities.waitAndClick(driver, previousMonthButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
			}
		}
	}

	private LocalDate parseMonthYear(String monthYear) {
		String[] parts = monthYear.split(" ");
		String month = parts[0];
		int year = Integer.parseInt(parts[1]);
		return LocalDate.of(year, Month.valueOf(month.toUpperCase()), 1);
	}

	public void selectCurrencyAndLanguage(String currency, String language) {

		Utilities.waitAndClick(driver, sessionSettingsHeaderButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);

		Utilities.selectOptionFromDropdown(driver, sessionSettingsCurrencyDropdown, sessionSettingsCurrencyOptions,
				currency);
		Utilities.selectOptionFromDropdown(driver, sessionSettingsLanguageDropdown, sessionSettingsLanguageOptions,
				language);

		Utilities.waitAndClick(driver, sessionSettingsConfirmButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		Utilities.waitInvisibilityOfElement(driver, spinner, Utilities.EXPLICIT_WAIT_TIMEOUT);
	}

	public void selectCurrency(String currency) {

		Utilities.waitAndClick(driver, sessionSettingsHeaderButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);

		Utilities.selectOptionFromDropdown(driver, sessionSettingsCurrencyDropdown, sessionSettingsCurrencyOptions,
				currency);

		Utilities.waitAndClick(driver, sessionSettingsConfirmButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		Utilities.waitInvisibilityOfElement(driver, spinner, Utilities.EXPLICIT_WAIT_TIMEOUT);
	}

	public void selectLanguage(String language) {

		Utilities.waitAndClick(driver, sessionSettingsHeaderButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);

		Utilities.selectOptionFromDropdown(driver, sessionSettingsLanguageDropdown, sessionSettingsLanguageOptions,
				language);

		Utilities.waitAndClick(driver, sessionSettingsConfirmButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		Utilities.waitInvisibilityOfElement(driver, spinner, Utilities.EXPLICIT_WAIT_TIMEOUT);
	}

	public void inputFind(String findterms) {
		Utilities.waitAndClick(driver, findtextinput, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		findtextinput.sendKeys(findterms);
	}

	public void clickFind() {
		Utilities.waitAndClick(driver, findbutton, 2);
	}

	public String waitUntilFindPageLoads() {
		wait.until(ExpectedConditions.visibilityOf(fatHeader));
		return searchResultsLabel.getText().trim();
	}

	public String getPageTitle() {
		return pageTitle.getText().trim();
	}

	public void clickShopWithPoints() {

		Utilities.waitAndClick(driver, shopWithPointsButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		wait.until(ExpectedConditions.visibilityOf(shopWithPointsHeading));
	}

	public String getShopWithPointsHeadingText() {
		return shopWithPointsHeading.getText().trim();
	}

}
