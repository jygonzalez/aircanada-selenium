package com.selenium.gui.utils;

import java.io.File;
import java.util.Properties;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReporter {
	
	public static ExtentReports generateExtentReport() {
		
		ExtentReports extentReport = new ExtentReports();
		File extentReportFile = new File(System.getProperty("user.dir") + "\\test-output\\ExtentReports\\extentReport.html");
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(extentReportFile);
		sparkReporter.config().setTheme(Theme.STANDARD);
		sparkReporter.config().setReportName("Assignment 4 GUI Test Automation Results Report");
		sparkReporter.config().setDocumentTitle("A4 Automation Report");
		
		extentReport.attachReporter(sparkReporter);
		
		Properties configProperties = Utilities.loadConfigProperties();
		extentReport.setSystemInfo("Application URL", configProperties.getProperty("url"));
		extentReport.setSystemInfo("Browser Name", configProperties.getProperty("browserName"));
		extentReport.setSystemInfo("Operating System", System.getProperty("os.name"));
		extentReport.setSystemInfo("Username", System.getProperty("user.name"));
		extentReport.setSystemInfo("Java Version", System.getProperty("java.version"));
		
		return extentReport;
	}
	
}
