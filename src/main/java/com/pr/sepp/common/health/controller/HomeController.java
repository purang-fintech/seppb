package com.pr.sepp.common.health.controller;

import com.pr.sepp.common.health.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HomeController {

    @Autowired
    private HomeService homeService;
    @RequestMapping(value = {"/","/sepp"},method = RequestMethod.GET)
    public String home() {
        homeService.select();
        return "sepp is ok";
    }

}
