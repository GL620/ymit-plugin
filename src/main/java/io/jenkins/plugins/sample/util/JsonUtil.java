package io.jenkins.plugins.sample.util;

import io.jenkins.plugins.sample.entity.YmitResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by phy on 2018/10/16.
 */
public class JsonUtil {
    public static YmitResponse parseJsonToObject(String jsonStrings){

        YmitResponse yr = new YmitResponse();
        JSONObject data = JSONObject.fromObject(jsonStrings);
        int code = data.optInt("code");

        if (code==0) {
            String dataString = data.getString("data");
            JSONObject dataBody = JSONObject.fromObject(dataString);
            String message = data.getString("message");
            int setNum = dataBody.getInt("setsnum");
            List<String> setnames = (List<String>) dataBody.get("setsname");
            String reportUrl = dataBody.getString("reporturl");
                yr.setCode(0);
                yr.setReporturl(reportUrl);
                yr.setSetsum(setNum);
                yr.setSetsname(setnames);
                yr.setMessage(message);

        }else if(code==1) {
            String message = data.getString("message");
            yr.setCode(1);
            yr.setMessage(message);
        }
        return yr;
    }
}
