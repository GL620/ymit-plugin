
package io.jenkins.plugins.sample;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.util.FormValidation;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import io.jenkins.plugins.sample.entity.YmitResponse;
import io.jenkins.plugins.sample.util.HttpUtil;
import io.jenkins.plugins.sample.util.JsonUtil;
import io.jenkins.plugins.sample.util.ToUTF8;
import io.jenkins.plugins.sample.util.VariableReplacerUtil;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.StaplerRequest;


public class YmitBuilder extends Builder implements SimpleBuildStep {

    // 执行者名称 来自web界面
    private final String name;
    // 是否高级选项
    private boolean useAdv;
    // 服务器地址
    private final String serverAddr;
    // 项目编号
    private final String projectCode;
    // 测试集或文件夹
    private final String testSetsOrForder;
    // 测试报告地址
    private String ymitReportAddr;

    // 将页面值通过该方法注入到属性变量中 注解DataBoundConstructor jenkins扫描注解并执行构造注入
    @DataBoundConstructor
    public YmitBuilder(String name, boolean useAdv, String serverAddr, String projectCode, String testSetsOrForder, String ymitReportAddr) {
        this.name = name;
        this.useAdv = useAdv;
        this.serverAddr = serverAddr;
        this.projectCode = projectCode;
        this.testSetsOrForder = testSetsOrForder;
        this.ymitReportAddr = ymitReportAddr;
    }

    /**
    * 插件开始执行的地方，要在插件完成什么事情，在perform中完成
    * @param run 代表当前构建
     @param workspace 代表当前构建目录，可获得当前工作目录信息
     @param launcher 代表启动进程
     @param listener 监听器，可将运行内容通过listener输出到控制台
     *
    * */
    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        // 替换环境变量中值，若不存在原值填充
        String rep_serverAddr = VariableReplacerUtil.checkEnvVars(run,listener,serverAddr);
        String rep_name = VariableReplacerUtil.checkEnvVars(run,listener,name);
        String rep_projectCode = VariableReplacerUtil.checkEnvVars(run,listener,projectCode);
        String rep_testSetsOrForder = VariableReplacerUtil.checkEnvVars(run,listener,testSetsOrForder);

        // 服务器地址 任务名 项目名 测试集或文件夹
        String url = "http://"+rep_serverAddr+"/api/ymit/testset/executeFolder/"+rep_name+"/"+rep_projectCode+"/"+rep_testSetsOrForder;

        listener.getLogger().println("执行地址："+url);

        // 调用httpclient 执行地址，并将返回结果解析的报告地址解析放到jenkins上下文，供邮件使用
        String httpResponse = HttpUtil.doGet(url);

        // 封装返回结果到对象返回
        if (httpResponse!=null && !"".equals(httpResponse)){
            YmitResponse ymitResponse = JsonUtil.parseJsonToObject(httpResponse);
            YmitLeftAction helloWorldAction = new YmitLeftAction();
            if (ymitResponse.getCode() == 0){
                helloWorldAction.setCode(ymitResponse.getCode());
                helloWorldAction.setMessage(ymitResponse.getMessage());
                helloWorldAction.setSetNum(ymitResponse.getSetsum());
                helloWorldAction.setSetNames(ymitResponse.getSetsname());
                helloWorldAction.setReporturl(ymitResponse.getReporturl());
                this.ymitReportAddr = ymitResponse.getReporturl();
            }else {
                helloWorldAction.setCode(ymitResponse.getCode());
                helloWorldAction.setMessage(ymitResponse.getMessage());
                this.ymitReportAddr = "!!报告地址生成失败:"+ ToUTF8.execute(ymitResponse.getMessage());
            }
            // 存放变量
            listener.getLogger().println("注入环境变量测试报告地址开始");

            Map<String, String> variables = new HashMap<String, String>();

            // 插入测试报告变量到系统
            variables.put("YMITREPORTADDR",ymitReportAddr);

            run.addAction(new EnvInjectBuilderContributionAction(variables));

            run.addAction(helloWorldAction);
        }
    }

    /*
    * 静态类
    * */
    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

            // 校验ip地址和端口格式
        public FormValidation doCheckServerAddr(@QueryParameter String serverAddr)
                throws IOException, ServletException {
           // String url_port_mapping = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])";

            if (serverAddr.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());

//            if (!serverAddr.matches(url_port_mapping))
//               return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_warnings_notIPandPort());
            return FormValidation.ok();
        }

            // 校验项目编号
        public FormValidation doCheckProjectCode(@QueryParameter String projectCode){
            if (projectCode.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            return FormValidation.ok();
        }

            // 校验用户名
        public FormValidation doCheckName(@QueryParameter String name){
            if (name.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (name.length() < 2)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            return FormValidation.ok();
        }

        // 校验测试集名
        public FormValidation doCheckTestSetsOrForder(@QueryParameter String testSetsOrForder){
            if (testSetsOrForder.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            return FormValidation.ok();
        }



        // 标识决定构建在此处是否可用
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        // 插件显示名称
        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }
        // 全局配置
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            // 持久化配置到xml
            save();
            return super.configure(req, json);
        }
    }

        @DataBoundSetter
         public void setUseAdv(boolean useAdv) {
        this.useAdv = useAdv;
    }

        /*geter and seter method*/
        public String getName() {
            return name;
        }

        public boolean isUseAdv() {
            return useAdv;
        }

        public String getServerAddr() {
            return serverAddr;
        }

        public String getProjectCode() {
            return projectCode;
        }

        public String getTestSetsOrForder() {
            return testSetsOrForder;
        }

}
