package com.h3c.solutionhub.entity;

import lombok.Data;

@Data
public class NodeBo {

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

    // 节点业务IP
    private String businessIP;

    // 节点管理网关
    private String managementGateway;

    // 节点业务网关
    private String businessGateway;
}
