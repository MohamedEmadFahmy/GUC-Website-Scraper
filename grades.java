
// import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
    private static Scanner terminalScanner = new Scanner(System.in);
    private static String dropdownID = "UnFormMainContent_smCrsLst";
    private static String quizTableID = "UnFormMainContent_nttTr";
    private static String midtermTableID = "UnFormMainContent_midDg";
    private static String gradeLink = "https://apps.guc.edu.eg//student_ext/Grade/CheckGrade.aspx";
    static String result = "";

    public static void main(String[] args) {
        // System.setProperty("webdriver.chrome.driver", "C:/Program Files
        // (x86)/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        // Configuring options for driver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless"); // setting headless mode to true.. so there
        // isn't any ui
        options.addArguments("log-level=1"); // sets log level to display only error logs

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);

        String[] Credentials = getCredentials();

        String username = Credentials[0];
        String password = Credentials[1];

        System.out.print("\033[H\033[2J");
        // System.out.flush();

        // Navigate to a webpage
        driver.get("https://" + username + ":" + password + "@student.guc.edu.eg/");
        driver.get("https://" + username + ":" + password + "@apps.guc.edu.eg/student_ext/Default.aspx");

        getGrades(driver);
        System.out.println();
        System.out.println("-------------Midterms-------------");
        result += "\n";
        result += "\n" + "---------------Midterms--------------";
        getMidterms(driver);

        System.out.println();
        System.out.println();

        driver.quit(); // Close the webdriver
        System.out.println("Grade fetch done");
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(result);

        // Keep the program running until user presses ENTER button
        System.out.println("Press ENTER to quit program");
        terminalScanner.nextLine();
        terminalScanner.close();

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
            try {
                File myFile = new File("Credentials.txt");
                myFile.createNewFile(); // Creates Credentials.txt if its not already present

                FileWriter fileWriter = new FileWriter("Credentials.txt");
                BufferedWriter writer = new BufferedWriter(fileWriter);

                System.out.print("Enter username:  ");
                String usernameString = terminalScanner.nextLine();
                System.out.print("Enter password:  ");
                String passwordString = terminalScanner.nextLine(); // Takes as input username and password

                writer.write(usernameString);
                writer.newLine();
                writer.write(passwordString); // Writes username and password to Credentials.txt

                writer.close(); // Closes BufferedWriter
                System.out.println("Successfully wrote username and password to Credentials.txt");
                return getCredentials();
            } catch (IOException e2) {
                System.out.println("Error while writing to file");
            }

        }
        return Credentials;
    }

    public static void getGrades(WebDriver driver) {

        String currentLink = driver.getCurrentUrl();

        // Checks to see if on the grades site, if not redirect to the grades site
        if (!currentLink.equals(gradeLink)) {
            driver.get(gradeLink);
        }

        WebElement selectCourse = driver.findElement(By.id(dropdownID));
        Select dropdown = new Select(selectCourse);
        List<WebElement> options = dropdown.getOptions();
        int numCourses = options.size();
        String courseNames[] = new String[numCourses];

        result += "\n";
        result += "\n";
        System.out.println();
        System.out.println();

        int j = 0;
        for (WebElement option : options) {
            String optionText = option.getText();
            courseNames[j] = optionText;
            j++;
        }

        for (int i = 1; i < courseNames.length; i++) {
            selectCourse = driver.findElement(By.id(dropdownID));
            // selectCourse.click();
            dropdown = new Select(selectCourse);
            dropdown.selectByVisibleText(courseNames[i] + "");
            System.out.println(i + ") " + courseNames[i]);
            result += (i + ") " + courseNames[i]);

            WebElement gradesTable = null;

            try {
                System.out.println();
                result += "\n";
                gradesTable = driver.findElement(By.id(quizTableID));
                printTable(gradesTable, "quiz");

            } catch (NoSuchElementException e) {
                System.out.println("No table");
            }

            System.out.println();
            System.out.println("----------------------------------");
            System.out.println();
            result += "\n";
            result += "----------------------------------";
            result += "\n";
        }
    }

    public static void printTable(WebElement table, String typeTable) {
        List<WebElement> rowsList = table.findElements(By.tagName("tr"));
        List<WebElement> columnsList = null;
        for (int i = 1; i < rowsList.size(); i++) {
            WebElement row = rowsList.get(i);
            System.out.println();
            result += "\n";
            columnsList = row.findElements(By.tagName("td"));
            for (int j = 0; j < columnsList.size(); j++) {
                WebElement column = columnsList.get(j);
                if (j != columnsList.size() - 1 || !typeTable.equals("quiz")) {
                    // System.out.print(column.getText() + " | ");
                    System.out.print(column.getText());
                    result += (column.getText());
                    if (j != columnsList.size() - 1) {
                        System.out.print("     ");
                        result += ("     ");
                    }
                }
                if (typeTable.equals("midterm") && j == columnsList.size() - 1) {
                    System.out.print("%");
                    result += ("%");
                }
            }
            System.out.println();
            result += "\n";
        }
    }

    public static void getMidterms(WebDriver driver) {
        String currentLink = driver.getCurrentUrl();

        // Checks to see if on the grades site, if not redirect to the grades site
        if (!currentLink.equals(gradeLink)) {
            driver.get(gradeLink);
        }
        WebElement table = driver.findElement(By.id(midtermTableID));
        printTable(table, "midterm");
    }

}