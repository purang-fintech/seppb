package com.pr.sepp.utils.fasfdfs.exception;

import lombok.Getter;

public enum FdfsError {
    FILE_PATH_ISNULL(1600, "文件路径为空"),
    FILE_ISNULL(1601, "文件为空"),
    FILE_UPLOAD_FAILED(1602, "文件上传失败"),
    FILE_NOT_EXIST(1603, "文件不存在"),
    FILE_DOWNLOAD_FAILED(1604, "文件下载失败"),
    FILE_DELETE_FAILED(1605, "删除文件失败"),
    FILE_SERVER_CONNECTION_FAILED(1606, "文件服务器连接失败"),
    FILE_OUT_SIZE(1607, "文件超过大小"),
    FILE_TYPE_ERROR_IMAGE(1608, "图片类型错误"),
    FILE_TYPE_ERROR_DOC(1609, "文档类型错误"),
    FILE_TYPE_ERROR_VIDEO(1610, "音频类型错误"),
    FILE_TYPE_ERROR_COMPRESS(1611, "压缩文件类型错误");
    ;

    @Getter
    private int code;
    @Getter
    private String message;
    @Getter
    private String des = null;

    FdfsError(int code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public String toString() {
        if (null == des) {
            String codeStr = String.valueOf(code);
            int capacity = codeStr.length() + message.length() + 5;
            des = new StringBuilder(capacity).append(codeStr).append(", ").append(message).toString();
        }
        return des;
    }
}
