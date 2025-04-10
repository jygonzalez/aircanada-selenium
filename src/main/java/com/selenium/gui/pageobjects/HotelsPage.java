package com.selenium.gui.pageobjects;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.selenium.gui.utils.Utilities;
import java.util.List;
// Define objects
// Inspect element -> copy -> xpath/id 

import org.openqa.selenium.support.ui.ExpectedConditions;
public class HotelsPage {
	
	WebDriver driver; 
	private WebDriverWait wait; 

	// Hotels button 
	@FindBy(id = "nav-button-30")
	private WebElement hotelsButton;
	
	@FindBy(xpath = "//*[@id=\"hotelsTab_location\"]")
	private WebElement destinationTextBox; 
	
	@FindBy(xpath = "//*[@id=\"hotelsTab_checkInDates-showCalendar\"]")
	private WebElement showCalendarButton; 
	
	@FindBy(css = ".abc-calendar-month-name")
	private List<WebElement> monthYearDisplay; 
	
	@FindBy(xpath = "//button[contains(@id, 'hotelsTab_checkInDates_nextMonthContent")
	private WebElement nextMonthButton;
	
	@FindBy(xpath = "//button[contains(@id, 'hotelsTab_checkInDates_previousMonth")
	private WebElement previousMonthButton; 
	
	@FindBy(xpath = "//span[@id='abcButtonElement67Content']/parent::button")
	private WebElement searchButton; 
	
	@FindBy(id = "onetrust-accept-btn-handler")
	private WebElement acceptCookiesButton;
	

	public HotelsPage(WebDriver driver) {
		this.driver = driver; 
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(Utilities.EXPLICIT_WAIT_TIMEOUT));
		PageFactory.initElements(driver, this);
	}
	
	public void acceptCookiesIfExists() {

		try {
			if (acceptCookiesButton.isDisplayed())
				Utilities.waitAndClick(driver, acceptCookiesButton, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		} catch (NoSuchElementException e) {
			// Cookies popup is not displayed, no action needed
		}
	}
	
	public void selectHotelsPage() {
		Utilities.waitAndClick(driver, hotelsButton, 2);
	}
	
	public boolean verifyNewTabOpened() {
	    // Original window count
	    String originalWindow = driver.getWindowHandle();
	    int originalWindowCount = driver.getWindowHandles().size();

	    // Click the search button
	    clickSearchButton();
	    System.out.println("SEARCH BUTTON PRESSED");

	    try {
	        // Wait for a new tab to open
	        WebDriverWait newTabWait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        newTabWait.until(driver -> driver.getWindowHandles().size() > originalWindowCount);

	        // Switch to the new tab
	        for (String windowHandle : driver.getWindowHandles()) {
	            if (!originalWindow.equals(windowHandle)) {
	                driver.switchTo().window(windowHandle);
	                break;
	            }
	        }

	        // Verify expected tab
	        wait.until(ExpectedConditions.urlContains("shopping"));

	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public void clickSearchButton() {
		// Need to wait for button to load
		wait.until(ExpectedConditions.elementToBeClickable(searchButton));
		
		Utilities.waitAndClick(driver, searchButton, 5);
	}
	
	public void selectDestinationTextBox(String location) {
		Utilities.waitAndClick(driver, destinationTextBox, Utilities.CLICKABLE_ELEMENT_WAIT_TIMEOUT);
		destinationTextBox.sendKeys(location);
	}
	
	public void selectDate(String dateString) {
		String dateXPath = String.format("//*[contains(@id, 'hotelsTab_checkInDates-date-%s')]", dateString);
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
	
	public String getCurrentMonthYear() {

		for (WebElement monthYear : monthYearDisplay) {
			if (!monthYear.getText().trim().isEmpty())
				return monthYear.getText().trim();
		}

		return "";
	}
	
	private LocalDate parseMonthYear(String monthYear) {
		String[] parts = monthYear.split(" ");
		String month = parts[0];
		int year = Integer.parseInt(parts[1]);
		return LocalDate.of(year, Month.valueOf(month.toUpperCase()), 1);
	}
	// Actions (Methods) 
}
