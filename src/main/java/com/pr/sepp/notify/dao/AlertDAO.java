package com.pr.sepp.notify.dao;

import com.pr.sepp.notify.model.Alert;
import com.pr.sepp.notify.model.AlertNotice;
import com.pr.sepp.notify.model.AlertNoticeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlertDAO {
    List<Alert> findAlertsByUser(Integer productId);

    List<AlertNotice> findAllAlertsOfToday();

    void insertWarningNotify(@Param("noticeLog") AlertNoticeLog noticeLog);
}
