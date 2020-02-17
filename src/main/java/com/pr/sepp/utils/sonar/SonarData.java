package com.pr.sepp.utils.sonar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pr.sepp.mgr.system.constants.SettingType;
import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.mgr.system.model.SystemSetting;
import com.pr.sepp.sep.build.model.sonar.Result;
import com.pr.sepp.sep.build.model.sonar.SonarResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class SonarData {

    private String projectStatusUrl = "api/qualitygates/project_status";
    private String projectAnalyses = "api/project_analyses/search";
    private String measures = "api/measures/search_history";
    private String charset = "GBK";

    //1. 根据projectKey,projectVersion可以找到analysisId，date
    public SonarResult getProjectAnalyses(String projectKey, String projectVersion, SettingDAO settingDAO) throws IOException {
        SystemSetting sonarconfig = settingDAO.findSetting(SettingType.SONAR.getValue());
        if(sonarconfig == null){
            log.info("尚未查询到SONAR系统配置" );
            return null;
        }
        List<SonarProperties.SonarConfig> sonarConfigs = SonarProperties.settingToSonarConfig(sonarconfig);
        String url = sonarConfigs.get(0).getBaseHost() + projectAnalyses;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("project", projectKey));
        params.add(new BasicNameValuePair("login", sonarConfigs.get(0).getLogin()));
        params.add(new BasicNameValuePair("password", sonarConfigs.get(0).getPassword()));
        HashMap<String, String> keyDate = new HashMap<>();
        try {
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, charset));
            HttpGet httpGet = new HttpGet(url + "?" + str);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {// 请求成功
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, HashMap<String, String>> mp = mapper.readValue(inputStream, HashMap.class);
                List<HashMap<String, String>> listJson = (List<HashMap<String, String>>) mp.get("analyses");
                for (HashMap<String, String> json : listJson) {
                    if (json.get("projectVersion").equals(projectVersion)) {
                        keyDate.put("projectKey", projectKey);
                        keyDate.put("key", json.get("key"));
                        keyDate.put("date", json.get("date"));
                        keyDate.put("projectVersion", projectVersion);
                        break;
                    }
                }
                keyDate.put("status", getProjectStatus(keyDate, settingDAO));
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                log.info("尚未查询到SONAR对应项目：{}", projectKey);
                return null;
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                log.info("尚未查询到SONAR对应项目：{}", projectKey);
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.close();
        }

        return getMeasures(keyDate, settingDAO);
    }

    //获取项目分析结果状态
    //2.根据analysisId 找到状态
    public String getProjectStatus(HashMap<String, String> keyDate, SettingDAO settingDAO) throws IOException {
        SystemSetting sonarconfig = settingDAO.findSetting(SettingType.SONAR.getValue());
        List<SonarProperties.SonarConfig> sonarConfigs = SonarProperties.settingToSonarConfig(sonarconfig);
        String url = sonarConfigs.get(0).getBaseHost() + projectStatusUrl;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("analysisId", keyDate.get("key")));
        params.add(new BasicNameValuePair("login", sonarConfigs.get(0).getLogin()));
        params.add(new BasicNameValuePair("password", sonarConfigs.get(0).getPassword()));
        String status = "";
        try {
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, charset));
            HttpGet httpGet = new HttpGet(url + "?" + str);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {// 请求成功
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, HashMap<String, String>> mp = mapper.readValue(inputStream, HashMap.class);
                HashMap<String, String> aa = mp.get("projectStatus");
                status = aa.get("status");
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                log.info("尚未查询到SONAR对应项目：{}", keyDate.get("key"));
                return null;
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                log.info("尚未查询到SONAR对应项目：{}", keyDate.get("key"));
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.close();
        }
        return status;
    }


    //3. 根据projectKey，date可以找到指标参数
    public SonarResult getMeasures(HashMap<String, String> keyDate, SettingDAO settingDAO) throws IOException {
        SystemSetting sonarconfig = settingDAO.findSetting(SettingType.SONAR.getValue());
        List<SonarProperties.SonarConfig> sonarConfigs = SonarProperties.settingToSonarConfig(sonarconfig);
        String url = sonarConfigs.get(0).getBaseHost() + measures;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String metrics = "bugs,vulnerabilities,security_hotspots,sqale_index,duplicated_lines_density,ncloc,coverage,code_smells";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("component", keyDate.get("projectKey")));
        params.add(new BasicNameValuePair("from", keyDate.get("date")));
        params.add(new BasicNameValuePair("to", keyDate.get("date")));
        params.add(new BasicNameValuePair("metrics", metrics));
        params.add(new BasicNameValuePair("login", sonarConfigs.get(0).getLogin()));
        params.add(new BasicNameValuePair("password", sonarConfigs.get(0).getPassword()));
        SonarResult sonarResult = new SonarResult();
        try {
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, charset));
            HttpGet httpGet = new HttpGet(url + "?" + str);
            HttpResponse response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {// 请求成功
                sonarResult.setKey(keyDate.get("key"));
                sonarResult.setProjectKey(keyDate.get("projectKey"));
                sonarResult.setProjectVersion(keyDate.get("projectVersion"));
                sonarResult.setScanDate(keyDate.get("date"));
                sonarResult.setAnalysisStatus(keyDate.get("status"));

                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                ObjectMapper mapper = new ObjectMapper();
                Result mp = mapper.readValue(inputStream, Result.class);
                HashMap<String, Double> data = new HashMap<>();
                mp.getMeasures().forEach(measure -> {
                    measure.getHistory().forEach(history -> {
                        data.put(measure.getMetric(), history.getValue());
                    });
                });
                sonarResult.setNcloc(data.get("ncloc"));
                sonarResult.setCoverage(data.get("coverage"));
                sonarResult.setDuplicatedLinesDensity(data.get("duplicated_lines_density"));
                sonarResult.setBugs(data.get("bugs"));
                sonarResult.setCodeSmells(data.get("code_smells"));
                sonarResult.setVulnerabilities(data.get("vulnerabilities"));
                sonarResult.setHotspots(data.get("security_hotspots"));
                sonarResult.setSqaleIndex(data.get("sqale_index"));
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                log.info("尚未查询到SONAR对应项目：{}", keyDate.get("projectKey"));
                return null;
            } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                log.info("尚未查询到SONAR对应项目：{}", keyDate.get("projectKey"));
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpClient.close();
        }

        return sonarResult;
    }
}
