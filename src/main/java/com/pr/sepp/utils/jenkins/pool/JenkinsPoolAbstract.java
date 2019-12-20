package com.pr.sepp.utils.jenkins.pool;

import com.pr.sepp.utils.jenkins.JenkinsClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JenkinsPoolAbstract extends Pool<JenkinsClient> {


    public JenkinsPoolAbstract(GenericObjectPoolConfig config, JenkinsFactory factory) {
        super(config, factory);
    }

    @Override
    public void returnBrokenResource(JenkinsClient resource) {
        super.returnBrokenResource(resource);
    }

    @Override
    public void returnResource(JenkinsClient resource) {
        super.returnResource(resource);
    }
}
