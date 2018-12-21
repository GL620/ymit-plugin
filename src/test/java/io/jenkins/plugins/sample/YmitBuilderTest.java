package io.jenkins.plugins.sample;

import hudson.model.Cause;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import io.jenkins.plugins.sample.entity.YmitResponse;
import io.jenkins.plugins.sample.util.HttpUtil;
import io.jenkins.plugins.sample.util.JsonUtil;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertThat;


public class YmitBuilderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    final String name = "Bobby";
    final boolean useAdv = false;
    final String serverAddr = "192.168.251.187:8086";
    final String projectCode = "P0200";
    final String testSetsOrForder = "test";
    final String reportUrl = "null";

    @Test
    public void testConfigRoundtrip() throws Exception {

        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new YmitBuilder(name, useAdv, serverAddr, projectCode, testSetsOrForder, reportUrl));
        project = jenkins.configRoundtrip(project);
        jenkins.assertEqualDataBoundBeans(new YmitBuilder(name, useAdv, serverAddr, projectCode, testSetsOrForder, reportUrl), project.getBuildersList().get(0));
    }

    @Test
    public void testConfigRoundtripFrench() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        YmitBuilder builder = new YmitBuilder(name, useAdv, serverAddr, projectCode, testSetsOrForder, reportUrl);
        builder.setUseAdv(true);
        project.getBuildersList().add(builder);
        project = jenkins.configRoundtrip(project);

        YmitBuilder lhs = new YmitBuilder(name, useAdv, serverAddr, projectCode, testSetsOrForder, reportUrl);
        lhs.setUseAdv(true);
        jenkins.assertEqualDataBoundBeans(lhs, project.getBuildersList().get(0));
    }

    @Test
    public void testBuild() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        YmitBuilder builder = new YmitBuilder(name, useAdv, serverAddr, projectCode, testSetsOrForder, reportUrl);
        project.getBuildersList().add(builder);

        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Hello, " + name, build);
    }

    @Test
    public void testBuildFrench() throws Exception {

        FreeStyleProject project = jenkins.createFreeStyleProject();
        YmitBuilder builder = new YmitBuilder(name, useAdv, serverAddr, projectCode, testSetsOrForder, reportUrl);
        builder.setUseAdv(true);
        project.getBuildersList().add(builder);

        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Bonjour, " + name, build);
    }

    @Test
    public void testScriptedPipeline() throws Exception {
        String agentLabel = "my-agent";
        jenkins.createOnlineSlave(Label.get(agentLabel));
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline");
        String pipelineScript
                = "node {\n"
                + "  greet '" + name + "'\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));
        String expectedString = "Hello, " + name + "!";
        jenkins.assertLogContains(expectedString, completedBuild);
    }

    @Test
    public void testReg() {

        String url_port_mapping = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])";
        String ip_port = "192.168.251.187:8080";
        boolean matches = ip_port.matches(url_port_mapping);
        Assert.assertTrue(matches);
        }

    @Test
    public void testCurl() throws IOException {
        String url = "http://192.168.251.187:10012/api/ymit/testset/executeFolder?prjcode=ymit-plugin&testsets=测试集列表集合&jobname=tester";
        String result = HttpUtil.doGet(url);
        Assert.assertNotNull(result);
    }

    @Test
    public void testJson() {
        String jsonStr = "{\n" +
                "    \"code\": 0,\n" +
                "    \"total\": 0,\n" +
                "    \"message\": \"success\",\n" +
                "    \"data\": {\n" +
                "        \"setsnum\": 12,\n" +
                "        \"setsname\": [\n" +
                "            \"易日升接口测试集/a3\",\n" +
                "            \"易日升接口测试集/中文\",\n" +
                "            \"易日升接口测试集/18期申请放款流程\",\n" +
                "            \"易日升接口测试集/G测试集\",\n" +
                "            \"易日升接口测试集/版本1.0/复制测试集验证1\",\n" +
                "            \"易日升接口测试集/版本1.0/18期申请放款流程\",\n" +
                "            \"易日升接口测试集/版本1.0/二级子文件夹/复制测试集验证2\",\n" +
                "            \"易日升接口测试集/18期申请放款流程_copy\",\n" +
                "            \"易日升接口测试集/a2\",\n" +
                "            \"易日升接口测试集/后台新模式修改\",\n" +
                "            \"易日升接口测试集/a1\",\n" +
                "            \"易日升接口测试集/测试集一\"\n" +
                "        ],\n" +
                "        \"reporturl\": \"192.168.251.151:9088/testReportDetail.html?taskId=b295ec627a974a3d8e25738c230a1bff\"\n" +
                "    }\n" +
                "}";
        YmitResponse ymitResponse = JsonUtil.parseJsonToObject(jsonStr);
        Assert.assertNotNull(ymitResponse.getReporturl());
    }
    @Test
    public void testDate(){
        Date date = new Date();
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.DAY_OF_MONTH,-1);
        Date time = instance.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(time);

        //System.out.println(format.contains("sdfsdf"));
        Assert.assertNotNull(format);
    }

    @Test
    public void testURLEncoding(){
        String url = "http://192.168.251.151:9086/api/ymit/testset/executeFolder/ymit0925/P0200/";
        String encode = null;
        try {
             encode = URLEncoder.encode("易日升接口测试集", "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(encode);
        Assert.assertNotNull(encode);
        try {
            String s = HttpUtil.doGet(url+encode);
            System.out.println(s.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
