package com.selenium.gui.testcases;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.selenium.gui.base.Base;
import com.selenium.gui.pageobjects.HotelsPage;

public class HotelSearchTest extends Base {

    public WebDriver driver;
    HotelsPage hotelsPage;

    public HotelSearchTest() {
        super();
    }

    @BeforeMethod
    public void setup() {
        driver = initializeBrowserAndOpenAppURL(properties.getProperty("browserName"));
        hotelsPage = new HotelsPage(driver);
        //flightsPage.acceptCookiesIfExists();
    }


    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test(priority = 1)
    public void verifyValidHotel() {


        hotelsPage.selectHotelsPage();
        
        hotelsPage.selectDestinationTextBox("calgary");
        
        hotelsPage.selectDateRange("2025-04-01", "2025-04-05");
        // Only checking for new tab to be opened as sometimes it requires a captcha.
        Assert.assertTrue(hotelsPage.verifyNewTabOpened(), "Hotels Search Tab should show up after clicking the 'search' button.");

    }
}
