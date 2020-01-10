package com.pr.sepp.notify.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.notify.dao.AlertDAO;
import com.pr.sepp.notify.model.Alert;
import com.pr.sepp.notify.model.AlertNoticeLog;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertDAO alertDAO;

    public PageInfo<Alert> alertListPaging(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Alert> alerts = alertDAO.findAlertsByUser(userId);
        return new PageInfo<>(alerts);
    }

    public void insertAlertNotify(@NonNull AlertNoticeLog alertNoticeLog) {
        alertDAO.insertWarningNotify(alertNoticeLog);
    }
}
