package com.h3c.solutionhub.entity;

import lombok.Data;

@Data
public class NodeBo {

    // 节点ID
    private int nodeId;

    // 节点名称
    private String nodeName;

    // 节点HDM IP
    private String nodeHDMIP;

    // 节点类型
    private String nodeType;

    // 节点状态
    private String nodeStatus;

    // 节点HDM密码
    private String nodeHDMPaasword;

    // 节点管理IP
    private String managementIP;

    private String  managementUserName;

    private String  managementPassword;

    // 节点管理mac
    private String managementMAC;

    // 节点业务IP
    private String businessIP;

    // 节点管理掩码
    private String managementMask;

    // 节点业务掩码
    private String businessMask;

    // 节点管理网关
    private String managementGateway;

    // 节点业务网关
    private String businessGateway;

    // token
    private String token;

    // 存储管理IP
    private String storageIP;

    // 存储管理掩码
    private String storageMask;

    // 存储管理网关
    private String storageGateway;

}
