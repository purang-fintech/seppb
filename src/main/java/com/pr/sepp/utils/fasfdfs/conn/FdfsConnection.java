package com.pr.sepp.utils.fasfdfs.conn;

import org.csource.fastdfs.TrackerServer;

public interface FdfsConnection {
    TrackerServer borrowObject();

    void returnObject(TrackerServer trackerServer);
}
