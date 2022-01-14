package ru.tehworks.scriptgo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marat Sadretdinov
 */
public class DataBaseLite {
    private int id;
    private String car;
    private String placeOfLoading;
    private String placeOfDelivery;
    private String shipmentStart;
    private String shipmentEnd;
    private String loading;
    private String unloading;
    private String colCheck;
    private int NDS;
    private String numTask;
    private List<String> ErrorList;
    private Map<Integer, String> ErrorsCol;
    private List<String> jErr;
    private boolean inputCheck;

    public List<String> getjErr() {
        List<String> l = new ArrayList<>();
        if(jErr != null) {
            if (jErr.size() >= 3) {
                l.add(jErr.get(jErr.size() - 3));
                l.add(jErr.get(jErr.size() - 2));
                l.add(jErr.get(jErr.size() - 1));
            } else l = jErr;
        }
        return l;
    }

    public void setjErr(List<String> jErr) {
        this.jErr = jErr;
    }

    public DataBaseLite(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getPlaceOfLoading() {
        return placeOfLoading;
    }

    public void setPlaceOfLoading(String placeOfLoading) {
        this.placeOfLoading = placeOfLoading;
    }

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

    public String getColCheck() { return colCheck; }
    public boolean getInputCheck(){return inputCheck;}

    public void setColCheck(String colCheck) {
        switch (colCheck){
            case "1550":{
                setInputCheck(colCheck);
                colCheck = "";
                break;
            }
            case "1551": colCheck = "table-success";
            break;
            case "1552": colCheck = "table-danger";
            break;
            default: colCheck = "";
        }
        this.colCheck = colCheck;
    }

    public void setInputCheck(String inputCheck) {
        if(inputCheck.equals("1553")) this.inputCheck = false;
        else {
            this.inputCheck = true;
        }
    }

    public int getNDS() { return NDS; }

    public void setNDS(int NDS) { this.NDS = NDS;}

    public String getNumTask() {return numTask;}

    public void setNumTask(String numTask) {this.numTask = numTask;}

    public List<String> getErrorList() {
        return ErrorList;
    }

    public Map<Integer, String> getError() {
        return ErrorsCol;
    }
    public void setError(Map<Integer,String> map){
        this.ErrorsCol = map;
    }

    public void setErrorList(List<String> errorList) {
        this.ErrorList = errorList;
    }
}
