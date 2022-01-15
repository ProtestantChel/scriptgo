package ru.tehworks.scriptgo.selenium;

import com.sun.mail.smtp.SMTPTransport;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import ru.tehworks.scriptgo.DAO.TehDAO;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Marat Sadretdinov
 */
public class MainSelenium {

    private Integer id;
    private String car;
    private String placeOfLoading;
    private String placeOfDelivery;
    private String shipmentStart;
    private String shipmentEnd;
    private String loading;
    private String unloading;
    private String NDS;


    public String getPlaceOfLoading() {
        return placeOfLoading;
    }

    public void setPlaceOfLoading(String placeOfLoading) {
        this.placeOfLoading = placeOfLoading;
    }
    public String getCar() { return car; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCar(String car) { this.car = car; }

    public String getPlaceOfDelivery() {
        return placeOfDelivery;
    }

    public void setPlaceOfDelivery(String placeOfDelivery) {
        this.placeOfDelivery = placeOfDelivery;
    }

    public String getShipmentStart() {
        return shipmentStart;
    }

    public void setShipmentStart(String shipmentStart) {
        this.shipmentStart = shipmentStart;
    }

    public String getShipmentEnd() {
        return shipmentEnd;
    }

    public void setShipmentEnd(String shipmentEnd) {
        this.shipmentEnd = shipmentEnd;
    }

    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }

    public String getUnloading() {
        return unloading;
    }

    public void setUnloading(String unloading) {
        this.unloading = unloading;
    }

    public void sendMail(String mailHost, String transportProtocol, String userMail, String passwordMail){ }

    public String getNDS() { return NDS; }

    public void setNDS(String NDS) { this.NDS = NDS; }

    public boolean checkElement(WebElement webElement, String str){
        try {
            webElement.findElement(By.tagName(str));
            return true;
        }catch (NoSuchElementException e){
            return false;
        }
    }
    public String getButton (String aria, String text){
        return "var link = document.querySelector(\"div[" + aria + "]\").querySelectorAll('.ui-button-text'); " + "link = Array.from( link ).filter( e => (/" + text + "/i).test( e.textContent ) ); " + "link[0].click();";
    }

    public void getHtml(String url, String user, String password, String mailHost,
                        String mailFromEmail, String mailToEmail, String mailUser,
                        String mailPassword, String mailPort, String mailSmtpAuth) throws ParseException, InterruptedException {

        int dataCount = 0;

        TehDAO tehDAO = new TehDAO();

        tehDAO.deleteQuery();

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");

         ChromeDriver driver = new ChromeDriver(chromeOptions);

        try {
            driver.get(url);
            driver.manage().timeouts().pageLoadTimeout(20,TimeUnit.SECONDS);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            try {
                WebElement userSend = driver.findElement(By.id("ctl00_Main_tbUserName"));
                userSend.sendKeys(user);
                WebElement passwordSend = driver.findElement(By.id("ctl00_Main_tbPassword"));
                passwordSend.sendKeys(password);

                WebElement clickLogin = driver.findElement(By.id("ctl00_Main_btnLogin"));
                js.executeScript("arguments[0].click()", clickLogin);
                Thread.sleep(2000);


                if(driver.getCurrentUrl().contains("login")) {
                     try {
                        WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
                        WebElement jGrowlMessage = driver.findElement(By.xpath("//div[@class='jGrowl-message']"));
                        webDriverWait.until(ExpectedConditions.visibilityOf(jGrowlMessage));
                        if (jGrowlMessage.isDisplayed()) {
                            tehDAO.insertQuery(getId(),jGrowlMessage.getText());
                            driver.close();
                        }
                    } catch (NoSuchElementException ex) {
                        ex.printStackTrace();
                        driver.close();
                    }
                }


            }catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e){
                driver.close();
                driver.quit();
                return;
            }


            int a[] = new int[7];
            List<String> listSend = new ArrayList<>();

            ResultSet resultSet = tehDAO.getListQuery();

            while (resultSet.next()){
                boolean check = true;
                this.setId(resultSet.getInt("id"));
                this.setCar(resultSet.getString("car"));
                this.setPlaceOfLoading(resultSet.getString("placeOfLoading"));
                this.setPlaceOfDelivery(resultSet.getString("placeOfDelivery"));
                this.setShipmentStart(resultSet.getString("shipmentStart"));
                this.setShipmentEnd(resultSet.getString("shipmentEnd"));
                this.setLoading(resultSet.getString("loading"));
                this.setUnloading(resultSet.getString("unloading"));
                this.setNDS(Integer.toString(resultSet.getInt("SummInNDS")));

                DateFormat formatd = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                DateFormat formaShipment = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date dateStart = (!this.getShipmentStart().equals("")) ? (Date)formaShipment.parse(this.getShipmentStart()) : (Date)formaShipment.parse("2021-12-01 00:00");
                Date dateEnd = (!this.getShipmentEnd().equals("")) ? (Date)formaShipment.parse(this.getShipmentEnd()) : (Date)formaShipment.parse("2030-12-31 23:59");


                List<String> list = new ArrayList<>();
                int con = 0;
                while (check) {

                    WebDriverWait webDriverWait = new WebDriverWait(driver, 20);
                    try {
                        WebElement treeTable = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("treeTable")));

                        WebElement thead = treeTable.findElement(By.tagName("thead"));
                        WebElement trHead = thead.findElement(By.tagName("tr"));
                        List<WebElement> thHead = trHead.findElements(By.tagName("th"));
                        for (int i = 0; i < thHead.size(); i++) {
                            if (thHead.get(i).getText().equalsIgnoreCase("Место погрузки")) a[0] = i;
                            if (thHead.get(i).getText().equalsIgnoreCase("Место доставки")) a[1] = i;
                            if (thHead.get(i).getText().equalsIgnoreCase("Отгрузка")) a[2] = i;
                            if (thHead.get(i).getText().equalsIgnoreCase("Погрузка")) a[3] = i;
                            if (thHead.get(i).getText().equalsIgnoreCase("Разгрузка")) a[4] = i;
                            if (thHead.get(i).getText().equalsIgnoreCase("Номер")) a[5] = i;
                            if (thHead.get(i).getText().equalsIgnoreCase("Моя")) a[6] = i;
                        }

                        WebElement tbody = treeTable.findElement(By.tagName("tbody"));
                        List<WebElement> trList = tbody.findElements(By.tagName("tr"));

                        for (WebElement tr : trList) {
                            try {
                                List<WebElement> tdList = tr.findElements(By.tagName("td"));
                                if(tdList.size() <= 1) {
                                    check = false;
                                    break;
                                }
                                Date tdDate = formatd.parse(tdList.get(a[2]).getText());
                                if (
                                        //&& checkElement(tdList.get(a[6]),"img")
                                        !list.contains(tdList.get(a[5]).getText()) && (con != getId()) && checkElement(tdList.get(a[6]),"img")
                                                && (tdList.get(a[0]).getText().equalsIgnoreCase(getPlaceOfLoading())
                                                && tdList.get(a[1]).getText().equalsIgnoreCase(getPlaceOfDelivery())
                                                && ((getShipmentStart() != "" || getShipmentEnd() != "") && (tdDate.getTime() >= dateStart.getTime() && tdDate.getTime() <= dateEnd.getTime()))
                                                || tdList.get(a[0]).getText().equalsIgnoreCase(getPlaceOfLoading())
                                                && tdList.get(a[1]).getText().equalsIgnoreCase(getPlaceOfDelivery())
                                                && tdList.get(a[3]).getText().equalsIgnoreCase(getLoading())
                                                || tdList.get(a[0]).getText().equalsIgnoreCase(getPlaceOfLoading())
                                                && tdList.get(a[1]).getText().equalsIgnoreCase(getPlaceOfDelivery())
                                                && (!tdList.get(a[4]).getText().isEmpty() && tdList.get(a[4]).getText().equalsIgnoreCase(getUnloading()))
                                                || tdList.get(a[0]).getText().equalsIgnoreCase(getPlaceOfLoading())
                                                && ((getShipmentStart() != "" || getShipmentEnd() != "") && (tdDate.getTime() >= dateStart.getTime() && tdDate.getTime() <= dateEnd.getTime()))
                                                && tdList.get(a[3]).getText().equalsIgnoreCase(getLoading())
                                                || tdList.get(a[0]).getText().equalsIgnoreCase(getPlaceOfLoading())
                                                && ((getShipmentStart() != "" || getShipmentEnd() != "") && (tdDate.getTime() >= dateStart.getTime() && tdDate.getTime() <= dateEnd.getTime()))
                                                && (!tdList.get(a[4]).getText().isEmpty() && tdList.get(a[4]).getText().equalsIgnoreCase(getUnloading()))
                                                || tdList.get(a[0]).getText().equalsIgnoreCase(getPlaceOfLoading())
                                                && tdList.get(a[3]).getText().equalsIgnoreCase(getLoading())
                                                && (!tdList.get(a[4]).getText().isEmpty() && tdList.get(a[4]).getText().equalsIgnoreCase(getUnloading()))
                                                || tdList.get(a[1]).getText().equalsIgnoreCase(getPlaceOfDelivery())
                                                && ((getShipmentStart() != "" || getShipmentEnd() != "") && (tdDate.getTime() >= dateStart.getTime() && tdDate.getTime() <= dateEnd.getTime()))
                                                && tdList.get(a[3]).getText().equalsIgnoreCase(getLoading())
                                                || tdList.get(a[1]).getText().equalsIgnoreCase(getPlaceOfDelivery())
                                                && ((getShipmentStart() != "" || getShipmentEnd() != "") && (tdDate.getTime() >= dateStart.getTime() && tdDate.getTime() <= dateEnd.getTime()))
                                                && (!tdList.get(a[4]).getText().isEmpty() && tdList.get(a[4]).getText().equalsIgnoreCase(getUnloading()))
                                                || ((getShipmentStart() != "" || getShipmentEnd() != "") && (tdDate.getTime() >= dateStart.getTime() && tdDate.getTime() <= dateEnd.getTime()))
                                                && tdList.get(a[3]).getText().equalsIgnoreCase(getLoading())
                                                && (!tdList.get(a[4]).getText().isEmpty() && tdList.get(a[4]).getText().equalsIgnoreCase(getUnloading())))
                                        ) {
                                    list.add(tdList.get(a[5]).getText());
                                    JavascriptExecutor jse = (JavascriptExecutor) driver;
                                    jse.executeScript("arguments[0].click()", tr);
                                    jse.executeScript("arguments[0].click()", tr);

                                    WebDriverWait webDriverWaitTable = new WebDriverWait(driver, 10);

                                    WebElement uiDialog = driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-dogovor']"));
                                    webDriverWaitTable.until(ExpectedConditions.visibilityOf(uiDialog));

                                    String dov = getId() < 10 ? "0" + getId() : getId().toString();

                                    con = getId();

                                    String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());


                                    WebElement aweb = uiDialog.findElement(By.id("aweb"));
                                    aweb.sendKeys(getCar());

                                    WebElement uiAutocomplete = driver.findElement(By.className("ui-autocomplete"));
                                    try {
                                        webDriverWaitTable.until(ExpectedConditions.visibilityOf(uiAutocomplete));
                                    }catch (org.openqa.selenium.TimeoutException ex){
                                        WebElement uiClose = uiDialog.findElement(By.className("ui-dialog-titlebar-close"));
                                        jse.executeScript("arguments[0].click()", uiClose);
                                        tehDAO.updateErrorQuery(getId());
                                        continue;
                                    }
                                    WebElement li = uiAutocomplete.findElement(By.className("ui-menu-item"));
                                    webDriverWaitTable.until(ExpectedConditions.visibilityOf(li));
                                    li.click();

                                    WebElement clickFraht = webDriverWaitTable.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only']//span[text()='Фрахт']")));
                                    jse.executeScript("arguments[0].click()",clickFraht);
                                    WebElement uiDialogNDS = driver.findElement(By.xpath("//div[@aria-labelledby='ui-dialog-title-c_price']"));
                                    webDriverWaitTable.until(ExpectedConditions.visibilityOf(uiDialogNDS));
                                    WebElement c_amount = driver.findElement(By.id("c_amount"));
                                    jse.executeScript("arguments[0].value = ''",c_amount);
                                    c_amount.sendKeys(getNDS());
                                    jse.executeScript("SetCPrice(null)");
                                    jse.executeScript("arguments[0].querySelector('.ui-button-text').click()",uiDialogNDS);

                                    try{
                                        driver.switchTo().alert();
                                        String msg = driver.switchTo().alert().getText();
                                        tehDAO.insertQuery(getId(), msg);
                                        tehDAO.updateErrorQuery(getId());
                                        driver.switchTo().alert().accept();
                                        jse.executeScript("arguments[0].querySelector('.ui-button-text').click()",uiDialogNDS);
                                        try {
                                            WebElement jGrowlMessage1 = driver.findElement(By.xpath("//div[@class='jGrowl-notification default']//div[@class='message'][contains(text(),'Агентское вознаграждение не может быть меньше 100 руб. Укажите другую сумму.')]"));
                                            WebElement jGrowlMessage2 = driver.findElement(By.xpath("//div[@class='jGrowl-notification default']//div[@class='message'][contains(text(),'Сумма услуги перевозчика была заменена на максимально возможную')]"));
                                            if(jGrowlMessage1.isDisplayed() || jGrowlMessage2.isDisplayed()){
                                                tehDAO.insertQuery(getId(),jGrowlMessage2.getText() +"<br>" + jGrowlMessage1.getText());
                                                WebElement jGrowlCloser = driver.findElement(By.xpath("//div[@class='jGrowl-closer default']"));
                                                jse.executeScript("arguments[0].click()",jGrowlCloser);
                                                continue;
                                            }
                                        }catch (NoSuchElementException | UnhandledAlertException e){

                                        }
                                        try {
                                            WebElement jGrowl = driver.findElement(By.id("jGrowl"));
                                            WebElement jClose = jGrowl.findElement(By.className("close"));
                                            js.executeScript("arguments[0].click()", jClose);
                                            continue;
                                        }catch (NoSuchElementException ex){
                                        }
                                        WebElement uiClose = uiDialog.findElement(By.className("ui-dialog-titlebar-close"));
                                        jse.executeScript("arguments[0].click()", uiClose);
                                        continue;

                                    }catch (NoAlertPresentException ex){

                                    }

                                    jse.executeScript(getButton("aria-labelledby='ui-dialog-title-dogovor'","Подтвердить"));
                                    try{
                                        driver.switchTo().alert().accept();
                                    }catch (NoAlertPresentException ex){

                                    }
                                    WebElement dlgdoveren = webDriverWaitTable.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgdoveren']")));

                                    WebElement dovnumfield = dlgdoveren.findElement(By.id("dovnumfield"));
                                    dovnumfield.sendKeys(dov);
                                    WebElement dovdatefield = dlgdoveren.findElement(By.id("dovdatefield"));
                                    jse.executeScript("arguments[0].value = '" + date + "'",dovdatefield);
                                    Thread.sleep(1000);
                                    jse.executeScript("var link = document.querySelector(\"div[aria-labelledby='ui-dialog-title-dlgdoveren']\").querySelectorAll('.ui-button-text'); " + "link = Array.from( link ).filter( e => (/ОК/i).test( e.textContent ) ); " + "link[0].click();");

                                    WebElement dlgterminal= webDriverWaitTable.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-labelledby='ui-dialog-title-dlgterminal']")));
                                    Thread.sleep(1000);
                                    jse.executeScript("var link = document.querySelector(\"div[aria-labelledby='ui-dialog-title-dlgterminal']\").querySelectorAll('.ui-button-text'); " + "link = Array.from( link ).filter( e => (/Отмена/i).test( e.textContent ) ); " + "link[0].click();");

                                    WebElement uiClose = uiDialog.findElement(By.className("ui-dialog-titlebar-close"));
                                    jse.executeScript("arguments[0].click()", uiClose);
                                    tehDAO.updateSuccessQuery(getId(), list.get(list.size()-1));
                                    listSend.add("Создана заявка №" + list.get(list.size()-1) + " для машины, место погрузки - "+ getPlaceOfLoading() + ", место доставки - " + getPlaceOfDelivery() + ", cумма - " + getNDS() + " (Поле №" + getId() + ").");
                                }else {
                                    if (!list.contains(tdList.get(a[5]).getText()))
                                        list.add(tdList.get(a[5]).getText());
                                }
                            } catch (org.openqa.selenium.StaleElementReferenceException | IndexOutOfBoundsException | JavascriptException ex ) {
                                break;
                            }
                        }

                        if (list.size() >= trList.size()) {
                            check = false;
                            break;

                        }
                    }
                    catch (org.openqa.selenium.StaleElementReferenceException ex){
                        ex.printStackTrace();
                        continue;
                    }
                    catch (NoSuchElementException e){
                        try {
                            WebElement jGrowl = driver.findElement(By.id("jGrowl"));
                            WebElement jMessage = jGrowl.findElement(By.className("jGrowl-message"));
                            WebElement jClose = jGrowl.findElement(By.className("close"));
                            tehDAO.insertQuery(getId(),jMessage.getText());
                            js.executeScript("arguments[0].click()", jClose);
                            driver.close();
                            driver.quit();
                        }catch (NoSuchElementException ex){
                            continue;
                        }
                    }
                }
            }
            try {
                WebElement exitButton = driver.findElement(By.id("Menu1_exitButton"));
                js.executeScript("arguments[0].click()", exitButton);
            }catch (ElementClickInterceptedException e){
                WebElement jGrowl = driver.findElement(By.id("jGrowl"));
                WebElement jClose = jGrowl.findElement(By.className("close"));
                js.executeScript("arguments[0].click()", jClose);
            }
            driver.close();
            driver.quit();
            System.out.println("---- driver.quit ---");
            String lineEnd = "";
            try {
                if (!listSend.isEmpty()) {
                    System.out.println("---- Email-send ---");
                    Properties props = System.getProperties();
                    props.put("mail.smtps.host", mailHost.trim());
                    props.put("mail.smtps.port", mailPort.trim());
                    props.put("mail.smtps.auth", mailSmtpAuth.trim());
                    Session session = Session.getInstance(props, null);
                    Message msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(mailFromEmail.trim()));
                    msg.setRecipients(Message.RecipientType.CC,
                            InternetAddress.parse(mailToEmail.trim(), false));
                    msg.setSubject("Созданы новые заявки");
                    for (String line : listSend) {
                        lineEnd = lineEnd + line + " \n";
                    }
                    msg.setText(lineEnd);
                    msg.setHeader("Content-Transfer-Encoding", "base64");
                    msg.setSentDate(new Date());
                    SMTPTransport t =
                            (SMTPTransport) session.getTransport("smtps");
                    t.connect(mailHost.trim(), mailUser.trim(), mailPassword.trim());
                    t.sendMessage(msg, msg.getAllRecipients());
                    System.out.println("Response: " + t.getLastServerResponse());
                    t.close();
                }
            }catch (MessagingException e){
                tehDAO.insertQuery(599999995,"Не удалось отправить почту");
                e.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            driver.close();
            driver.quit();
        }
    }
}
