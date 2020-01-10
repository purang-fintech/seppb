package com.pr.sepp.notify.service;


import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.sep.build.model.sonar.SonarResult;
import com.pr.sepp.sep.build.model.sonar.SonarScan;
import com.pr.sepp.sep.build.service.SonarScanService;
import com.pr.sepp.utils.sonar.SonarData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class SonarService {
    @Autowired
    private SonarScanService sonarScanService;

    @Autowired
    private SettingDAO settingDAO;

    public void syncSonarData() {
        List<SonarScan> listSonar = sonarScanService.getSyncList();
        SonarData sonarData =new SonarData();
        if (listSonar.size() > 0) {
            listSonar.forEach(listOfSonarScan -> {
                SonarResult sonarResult = null;
                int scanId =listOfSonarScan.getId();
                try {
                    sonarResult = sonarData.getProjectAnalyses(listOfSonarScan.getProjectKey(), listOfSonarScan.getProjectVersion(), settingDAO);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(sonarResult!=null){
                    int id =sonarScanService.saveSonarResult(sonarResult);
                    sonarScanService.syncProjectSuccess(scanId,id);
                }
            });
        }
    }
}
