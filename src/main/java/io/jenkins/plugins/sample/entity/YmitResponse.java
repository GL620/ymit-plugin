package io.jenkins.plugins.sample.entity;

import java.util.List;

/**
 * Created by phy on 2018/10/16.
 */
public class YmitResponse {

    private  Integer code;
    private String message;
    private  Integer setsum;
    private List<String> setsname;
    private String reporturl;

    public Integer getSetsum() {
        return setsum;
    }

    public void setSetsum(Integer setsum) {
        this.setsum = setsum;
    }

    public List<String> getSetsname() {
        return setsname;
    }

    public void setSetsname(List<String> setsname) {
        this.setsname = setsname;
    }

    public String getReporturl() {
        return reporturl;
    }

    public void setReporturl(String reporturl) {
        this.reporturl = reporturl;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
