package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.NodeBo;

import java.util.List;

public interface NodesManagementService {

    List<NodeBo> getNodeList();

    Boolean insertNode(NodeBo nodeBo);

    Boolean deleteNode(String nodeName);

    Boolean deployNode(
            String dhcpIPPond,
            String dhcpMask,
            String productType,
            String productVersion,
            List<NodeBo> nodes);
}
