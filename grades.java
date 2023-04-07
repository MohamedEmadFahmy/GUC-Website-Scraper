import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

public class grades {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:/Program Files (x86)/chromedriver.exe");

        // Configuring options for driver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless"); // setting headless mode to true.. so there isn't any ui
        options.addArguments("log-level=1"); // sets log level to display only error logs

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);

        String[] Credentials = getCredentials();

        String username = Credentials[0];
        String password = Credentials[1];

        // Navigate to a webpage
        driver.get("https://" + username + ":" + password + "@student.guc.edu.eg/");
        driver.get("https://" + username + ":" + password + "@apps.guc.edu.eg/student_ext/Default.aspx");

        getGrades(driver);
        System.out.println();
        System.out.println("-------------Midterms-------------");
        getMidterms(driver);

        try {
            Thread.sleep(60 * 1000); // Keep the website running for 60 seconds
        } catch (InterruptedException e) {
            System.out.println("Sleep Interrupted");
        }

        // Close the browser
        driver.quit();
    }

    public static String[] getCredentials() {
        String[] Credentials = new String[2];
        try {
            File file = new File("Credentials.txt");
            Scanner myReader = new Scanner(file);
            Credentials[0] = myReader.nextLine();
            Credentials[1] = myReader.nextLine();
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        return Credentials;
    }

    public static void getGrades(WebDriver driver) {
        String gradeLink = "https://student.guc.edu.eg/external/student/grade/CheckGrade.aspx";
        driver.get(gradeLink);
        WebElement selectCourse = driver.findElement(By.id("smCrsLst"));
        Select dropdown = new Select(selectCourse);
        List<WebElement> options = dropdown.getOptions();
        int numCourses = options.size();
        String courseNames[] = new String[numCourses];

        System.out.println();
        System.out.println();

        int j = 0;
        for (WebElement option : options) {
            String optionText = option.getText();
            courseNames[j] = optionText;
            j++;
        }

        for (int i = 1; i < courseNames.length; i++) {
            selectCourse = driver.findElement(By.id("smCrsLst"));
            selectCourse.click();
            dropdown = new Select(selectCourse);
            dropdown.selectByVisibleText(courseNames[i] + "");
            System.out.println(i + ") " + courseNames[i]);

            WebElement gradesTable = null;

            try {
                System.out.println();
                gradesTable = driver.findElement(By.id("nttTr"));
                printTable(gradesTable, "quiz");

            } catch (NoSuchElementException e) {
                System.out.println("No table");
            }

            System.out.println();
            System.out.println("----------------------------------");
            System.out.println();
        }
    }

    public static void printTable(WebElement table, String typeTable) {
        List<WebElement> rowsList = table.findElements(By.tagName("tr"));
        List<WebElement> columnsList = null;
        for (int i = 1; i < rowsList.size(); i++) {
            WebElement row = rowsList.get(i);
            System.out.println();
            columnsList = row.findElements(By.tagName("td"));
            for (int j = 0; j < columnsList.size(); j++) {
                WebElement column = columnsList.get(j);
                if (j != columnsList.size() - 1 || !typeTable.equals("quiz")) {
                    // System.out.print(column.getText() + " | ");
                    System.out.print(column.getText());
                    if (j != columnsList.size() - 1) {
                        System.out.print("     ");
                    }
                }
                if (typeTable.equals("midterm") && j == columnsList.size() - 1) {
                    System.out.print("%");
                }
            }
            System.out.println();
        }
    }

    public static void getMidterms(WebDriver driver) {
        String gradeLink = "https://student.guc.edu.eg/external/student/grade/CheckGrade.aspx";
        driver.get(gradeLink);
        WebElement table = driver.findElement(By.id("midDg"));
        printTable(table, "midterm");
    }

}