package ru.tehworks.scriptgo.DAO;


import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tehworks.scriptgo.model.DataBaseLite;
import ru.tehworks.scriptgo.selenium.MainSelenium;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author Marat Sadretdinov
 */
@Component
public class TehDAO {

    @Value("${site}")
    private String url;
    @Value("${user}")
    private String user;
    @Value("${password}")
    private String password;
    @Value("${mail.smtp.host}")
    private String mailHost;
    @Value("${mail.from.Email}")
    private String mailFromEmail;
    @Value("${mail.to.Email}")
    private String mailToEmail;
    @Value("${mail.smtp.user}")
    private String mailUser;
    @Value("${mail.smtp.password}")
    private String mailPassword;
    @Value("${mail.smtp.port}")
    private String mailPort;
    @Value("${mail.smtp.auth")
    private String mailSmtpAuth;

    private List<String> jErr;

    private static final String URL = "jdbc:sqlite:" + System.getProperty("user.dir") +"\\db\\tehno.db";
    private static Connection connection;

    {
        jErr = new ArrayList<>();
    }

    static {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<DataBaseLite> index(){
        List <DataBaseLite> dataBaseLites = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM request ORDER BY id DESC";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next()){
                DataBaseLite dataBaseLite = new DataBaseLite();

                dataBaseLite.setId(resultSet.getInt("id"));
                dataBaseLite.setCar(resultSet.getString("car"));
                dataBaseLite.setPlaceOfLoading(resultSet.getString("placeOfLoading"));
                dataBaseLite.setPlaceOfDelivery(resultSet.getString("placeOfDelivery"));
                dataBaseLite.setShipmentStart(resultSet.getString("shipmentStart"));
                dataBaseLite.setShipmentEnd(resultSet.getString("shipmentEnd"));
                dataBaseLite.setLoading(resultSet.getString("loading"));
                dataBaseLite.setUnloading(resultSet.getString("unloading"));
                dataBaseLite.setColCheck(resultSet.getString("COL_CHECK"));
                dataBaseLite.setNumTask(resultSet.getString("NumTask"));
                dataBaseLite.setNDS(resultSet.getInt("SummInNDS"));
                dataBaseLite.setInputCheck(resultSet.getString("COL_CHECK"));
                dataBaseLites.add(dataBaseLite);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dataBaseLites;
    }
    
    public void save(DataBaseLite dataBaseLite){
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM request WHERE id=" + dataBaseLite.getId();

            ResultSet resultSet = statement.executeQuery(SQL);
            if(resultSet.next()) {

                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE request set car = ?, placeOfLoading = ?, placeOfDelivery = ?, shipmentStart = ?, shipmentEnd = ?, loading = ?, unloading = ?, Date = (SELECT date('now')), COL_CHECK = 1550, SummInNDS = ? WHERE id=" + dataBaseLite.getId());

                preparedStatement.setString(1, dataBaseLite.getCar().trim());
                preparedStatement.setString(2, dataBaseLite.getPlaceOfLoading().trim());
                preparedStatement.setString(3, dataBaseLite.getPlaceOfDelivery().trim());
                preparedStatement.setString(4, dataBaseLite.getShipmentStart().trim() + " 00:00");
                preparedStatement.setString(5, dataBaseLite.getShipmentEnd().trim() + " 23:59");
                preparedStatement.setString(6, dataBaseLite.getLoading().trim());
                preparedStatement.setString(7, dataBaseLite.getUnloading().trim());
                preparedStatement.setInt(8, dataBaseLite.getNDS());

                preparedStatement.executeUpdate();
            }
            else {

                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO request (car, placeOfLoading, placeOfDelivery, shipmentStart, shipmentEnd, loading, unloading, Date, COL_CHECK, SummInNDS) VALUES (?,?,?,?,?,?,?,(SELECT date('now')),1550,?)");
                preparedStatement.setString(1, dataBaseLite.getCar().trim());
                preparedStatement.setString(2, dataBaseLite.getPlaceOfLoading().trim());
                preparedStatement.setString(3, dataBaseLite.getPlaceOfDelivery().trim());
                preparedStatement.setString(4, dataBaseLite.getShipmentStart().trim() + " 00:00");
                if (dataBaseLite.getShipmentEnd().trim().equals(""))
                    preparedStatement.setString(5, dataBaseLite.getShipmentEnd().trim());
                else
                    preparedStatement.setString(5, dataBaseLite.getShipmentEnd().trim() + " 23:59");
                preparedStatement.setString(6, dataBaseLite.getLoading().trim());
                preparedStatement.setString(7, dataBaseLite.getUnloading().trim());
                preparedStatement.setInt(8, dataBaseLite.getNDS());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            jErr.add("Ошибка опдключения к базе данных (save)");
            dataBaseLite.setjErr(jErr);
            throwables.printStackTrace();
        }
    }
    public void delete(String idInt){
        try {
            int id = Integer.parseInt(idInt);
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM request WHERE id=" + id;
            ResultSet resultSet = statement.executeQuery(SQL);
            if(resultSet.next()) {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM request WHERE id=?");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    public DataBaseLite errorList(){

        DataBaseLite dataBaseLite = new DataBaseLite();
        try {
            int count = 0;
            List<String> list = new ArrayList<>();
            Map <Integer,String> map = new HashMap<>();
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM error_list";
            ResultSet resultSet = statement.executeQuery(SQL);
            jErr.clear();
            while (resultSet.next()) {
                count++;
                if(count <= 3) list.add(resultSet.getString("ErrorText"));
                map.put(resultSet.getInt("id"),resultSet.getString("ErrorText"));
            }
            SQL = "SELECT * FROM error_list WHERE id = 0";
            resultSet = statement.executeQuery(SQL);
            while (resultSet.next()){
                jErr.add(resultSet.getString("ErrorText"));
                dataBaseLite.setjErr(jErr);
            }
            dataBaseLite.setErrorList(list);
            dataBaseLite.setError(map);
            return dataBaseLite;
        } catch (SQLException throwables) {
            jErr.add("Ошибка опдключения к базе данных (errorList)");
            dataBaseLite.setjErr(jErr);
            throwables.printStackTrace();
        }
        return dataBaseLite;
    }

    public ResultSet getListQuery(){
        try {
            Statement statement = connection.createStatement();
            String SQL = "SELECT id, car, placeOfLoading, placeOfDelivery, shipmentStart, shipmentEnd, loading, unloading, SummInNDS  FROM request WHERE COL_CHECK = 1550";
            ResultSet resultSet = statement.executeQuery(SQL);
            return resultSet;
        } catch (SQLException throwables) {
            jErr.add("Ошибка опдключения к базе данных (getListQuery)");
            throwables.printStackTrace();
            return null;
        }
    }

    public void deleteQuery(){
        try {
            PreparedStatement prStatement = connection.prepareStatement("DELETE FROM error_list WHERE Time <= (SELECT datetime('now','+05:00','-12 hours'))");
            prStatement.executeUpdate();
        } catch (SQLException throwables) {
            jErr.add("Ошибка опдключения к базе данных (deleteQuery)");
            throwables.printStackTrace();
        }
    }

    public void insertQuery(Integer id, String str){
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO error_list (ErrorText,Time,id) VALUES (?,(SELECT datetime('now','+05:00'),?)");
            DateFormat formatd = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            if (id == 599999995) {
                preparedStatement.setString(1, formatd.format(new Date()) + " " + str);
                preparedStatement.setString(2, "0");
            }
            else {
                preparedStatement.setString(1, formatd.format(new Date()) + " Поле №" + id + " " + str);
                preparedStatement.setString(2, id.toString());
            }
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    public void updateErrorQuery(Integer id){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE request set COL_CHECK = ? WHERE id=" + id);
            preparedStatement.setString(1, "1552");
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void updateSuccessQuery(Integer id, String num){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE request set COL_CHECK = ?, NumTask = ? WHERE id=" + id);
            preparedStatement.setString(1, "1551");
            preparedStatement.setString(2, num);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void searchActive(String id, String check){
        try {
            PreparedStatement preparedStatement = null;
            switch (check){
                case "1550": {
                    preparedStatement = connection.prepareStatement("UPDATE request set COL_CHECK = ?, NumTask = ? WHERE id=" + Integer.parseInt(id) + " and COL_CHECK != 1552");
                    preparedStatement.setString(1, check);
                    preparedStatement.setString(2, "");
                    preparedStatement.executeUpdate();
                    break;
                }
                case "1551": {
                    preparedStatement = connection.prepareStatement("INSERT INTO request (car, placeOfLoading, placeOfDelivery, shipmentStart, shipmentEnd, loading, unloading, Date, COL_CHECK, SummInNDS) SELECT car, placeOfLoading, placeOfDelivery, shipmentStart, shipmentEnd, loading, unloading, (SELECT date('now')), 1550, SummInNDS FROM request WHERE id=" + Integer.parseInt(id) + " and COL_CHECK != 1552");
                    preparedStatement.executeUpdate();
                    preparedStatement = connection.prepareStatement("DELETE FROM request WHERE id=" + Integer.parseInt(id) + " and COL_CHECK != 1552");
                    preparedStatement.executeUpdate();
                    break;
                }
                case "1553": {
                    preparedStatement = connection.prepareStatement("UPDATE request set COL_CHECK = ? WHERE id=" + Integer.parseInt(id)  + " and COL_CHECK != 1552");
                    preparedStatement.setString(1, check);
                    preparedStatement.executeUpdate();
                    break;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    @Scheduled(cron = "${cron.expression}")
    public void getCron() throws ParseException, InterruptedException {
        MainSelenium selenium = new MainSelenium();
        selenium.getHtml(url, user, password, mailHost,
                mailFromEmail, mailToEmail, mailUser,
                mailPassword, mailPort, mailSmtpAuth);
    }

}
