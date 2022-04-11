package com.example.tachographanalysis.database.driver.driverInfo;

public class Data {
    Integer id, sum_road, driver_id;
    String date_work, data_add, work_info, sum_work, sum_break, file, file_type;

    public Data(Integer id, Integer queryDriverId, String date_work, String data_add, String work_info, String sum_work, String sum_break, String file, String queryFile, Integer querySumRoad) {
        this.id = id;
        this.sum_road = sum_road;
        this.driver_id = driver_id;
        this.date_work = date_work;
        this.data_add = data_add;
        this.work_info = work_info;
        this.sum_work = sum_work;
        this.sum_break = sum_break;
        this.file = file;
        this.file_type = file_type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSum_road() {
        return sum_road;
    }

    public void setSum_road(Integer sum_road) {
        this.sum_road = sum_road;
    }

    public Integer getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(Integer driver_id) {
        this.driver_id = driver_id;
    }

    public String getDate_work() {
        return date_work;
    }

    public void setDate_work(String date_work) {
        this.date_work = date_work;
    }

    public String getData_add() {
        return data_add;
    }

    public void setData_add(String data_add) {
        this.data_add = data_add;
    }

    public String getWork_info() {
        return work_info;
    }

    public void setWork_info(String work_info) {
        this.work_info = work_info;
    }

    public String getSum_work() {
        return sum_work;
    }

    public void setSum_work(String sum_work) {
        this.sum_work = sum_work;
    }

    public String getSum_break() {
        return sum_break;
    }

    public void setSum_break(String sum_break) {
        this.sum_break = sum_break;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }
}
