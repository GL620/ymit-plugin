package io.jenkins.plugins.sample;

import hudson.PluginManager;
import hudson.PluginWrapper;
import hudson.model.Action;
import hudson.model.Run;
import jenkins.model.Jenkins;
import jenkins.model.RunAction2;

import javax.annotation.CheckForNull;
import java.util.List;

/**
 * Created by phy on 2018/10/13.
 */
public class YmitLeftAction implements RunAction2 {


    private transient Run run;
    private String name;
    private boolean useAdv;
    private String serverAddr;
    private String projectCode;
    private String testSetsOrForder;

    // http响应信息
    private int code;
    private String message;
    private int setNum;
    private List<String> setNames;
    private String reporturl;



    public YmitLeftAction() {
    }

    @Override
    public void onAttached(Run<?, ?> run) {

    }

    @Override
    public void onLoad(Run<?, ?> run) {

    }
    @CheckForNull
    @Override
    public String getIconFileName() {
        /*Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
            PluginManager pluginManager = jenkins.getPluginManager();
            if (pluginManager != null) {
                PluginWrapper wrapper = pluginManager.getPlugin("ymit-auto-test");
                if (wrapper != null){
                    return "/plugin/" + wrapper.getShortName() + "/images/ymit_48x48.png";
                }
            }
        }*/
        return "/plugin/ymit-plugin/images/ymit.png";
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return "YMIT自动化";
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return "ymit接口自动化执行结果";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Run getRun() {
        return run;
    }

    public void setRun(Run run) {
        this.run = run;
    }

    public boolean isUseAdv() {
        return useAdv;
    }

    public void setUseAdv(boolean useAdv) {
        this.useAdv = useAdv;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getTestSetsOrForder() {
        return testSetsOrForder;
    }

    public void setTestSetsOrForder(String testSetsOrForder) {
        this.testSetsOrForder = testSetsOrForder;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }

    public List<String> getSetNames() {
        return setNames;
    }

    public void setSetNames(List<String> setNames) {
        this.setNames = setNames;
    }

    public String getReporturl() {
        return "http://"+reporturl;
    }

    public void setReporturl(String reporturl) {
        this.reporturl = reporturl;
    }


}
