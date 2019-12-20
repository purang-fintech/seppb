package com.pr.sepp.utils.jenkins.pool;

import com.pr.sepp.common.exception.SeppServerException;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Slf4j
public class JenkinsPool extends JenkinsPoolAbstract {

    public JenkinsPool(String url, String username, String password) {
        this(new JenkinsPoolConfig(), url, username, password);
    }

    public JenkinsPool(final GenericObjectPoolConfig config, final String url,
                       final String username, final String password) {
        super(config, new JenkinsFactory(url, username, password));
    }

    @Override
    public JenkinsClient getResource() {
        printJenkinsPoolLog();
        JenkinsClient jenkinsClient = super.getResource();
        jenkinsClient.setSource(this);
        return jenkinsClient;
    }

    @Override
    public void returnBrokenResource(final JenkinsClient resource) {
        printJenkinsPoolLog();
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }

    @Override
    public void returnResource(final JenkinsClient resource) {
        if (resource != null) {
            try {
                returnResourceObject(resource);
            } catch (Exception e) {
                returnBrokenResource(resource);
                throw new SeppServerException("Resource is returned to the pool as broken", e);
            }
        }
    }


}
