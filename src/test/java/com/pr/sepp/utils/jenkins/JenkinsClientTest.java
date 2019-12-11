package com.pr.sepp.utils.jenkins;

import com.pr.sepp.utils.jenkins.model.PipelineStep;
import com.google.common.collect.Lists;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.util.EncodingUtils;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@Ignore
@RunWith(MockitoJUnitRunner.Silent.class)
public class JenkinsClientTest {

    @Mock
    private JenkinsClient jenkinsClient;


    @Test
    public void jobParams() throws IOException, DocumentException {
        JenkinsHttpClient client = new JenkinsHttpClient(URI.create("http://ci.purang.com"), "cctest", "11c66ae929bd59799d4038c12013fdbedf");
        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(URI.create("http://ci.purang.com"), "cctest", " cctest");
//        jenkinsHttpClient.get("/job/" + EncodingUtils.encode("Sky_普兰_Test") + "/" + 1637 + "/wfapi/describe", PipelineStep.class);
        JenkinsServer jenkinsServer = new JenkinsServer(client);
        JenkinsServer server = new JenkinsServer(jenkinsHttpClient);
        JobWithDetails bill_proxy_test = server.getJob("bill_proxy_Test");
//        JenkinsClient.MyJobWithDetails myJobWithDetails = jenkinsHttpClient.get(UrlUtils.toJobBaseUrl(null, "redeploy_demo"), JenkinsClient.MyJobWithDetails.class);
        JobWithDetails job = jenkinsServer.getJob("bill_proxy_Test");
        List<Build> builds = job.getBuilds();
        for (Build build : builds) {
            try {
                PipelineStep subPbq_deploy_test = jenkinsHttpClient.get("/job/" + EncodingUtils.encode("SubPbq_Deploy_Test") + "/" + build.getNumber() + "/wfapi/describe", PipelineStep.class);

            } catch (Exception e) {
                System.out.println("===================================================" + build.getNumber());
                e.printStackTrace();
            }

//            BuildHistory buildHistory = BuildHistory.apply(build.getNumber(), build.details(), "sepp_Deploy");
//            System.out.println(buildHistory);
        }
        List<String> params = jenkinsClient.jobParams("Sky_普兰_Test");
        jenkinsServer.getJob("").getAllBuilds();
        System.out.println(params);

    }


    @Test
    public void test01() {
        String s = "哈哈hhh@";
        String sss = StringUtils.replace(s, "@", "sss");
        System.out.println(sss);
    }

    public void o(List<String> list, String s) {
        s.replace("1", "2");
        list.add("b");
        list = Lists.newArrayList();
        list.add("c");

    }


    public String inverseSentence(String in, String sep) {
        if (in == null) return null;
        String[] strArrays = in.split(sep);
        StringBuilder sb = new StringBuilder();
        for (int i = strArrays.length - 1; i >= 0; i--) {
            sb.append(strArrays[i]).append(sep);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    @Test
    public void test02() {
//        BuildFile buildFile = BuildFile.builder().paramKey("sb").paramValue(null).build();
//        ArrayList<BuildFile> buildFiles = Lists.newArrayList(buildFile);
//        Map<String, String> collect = buildFiles.stream().collect(Collectors.toMap(BuildFile::getParamKey, BuildFile::getParamValue));
//        System.out.println(collect);
        boolean b = checkRepeatBuild(new ArrayList<>(), null, null);
        System.out.println(b);
    }

    private boolean checkRepeatBuild(List<QueueItem> items, String paramKey, String paramValue) {
        String param = paramKey + "=" + paramValue;
        if (Objects.equals(items.size(), 0)) return false;
        return items.stream().anyMatch(item -> item.getParams().contains(param));
    }


    @Test
    public void nullException() throws IOException {
        JenkinsHttpClient client = new JenkinsHttpClient(URI.create("http://ci.purang.com"), "cctest", "11c66ae929bd59799d4038c12013fdbedf");
        JenkinsServer jenkinsServer = new JenkinsServer(client);
        JenkinsClient jenkinsClient = new JenkinsClient(jenkinsServer, client);
        jenkinsClient.startBuild("testss", new HashMap<>());
    }

}