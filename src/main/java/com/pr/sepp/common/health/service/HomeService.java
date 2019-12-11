package com.pr.sepp.common.health.service;

import com.pr.sepp.common.health.dao.HomeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {
    @Autowired
    private HomeDAO homeDAO;

    public void select() {
        homeDAO.selectOne();
    }
}
