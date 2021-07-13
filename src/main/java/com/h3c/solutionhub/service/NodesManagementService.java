package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.NodeBo;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

public interface NodesManagementService {

    Boolean insertNode(NodeBo nodeBo);

    Boolean deleteNode(String nodeName);

    Boolean deployNode(
            String dhcpIPPond,
            String dhcpMask,
            List<NodeBo> nodes);
}
