package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.NodeBo;
import com.sun.org.apache.xpath.internal.operations.Bool;

public interface NodesManagementService {

    Boolean insertNode(NodeBo nodeBo);

    Boolean deleteNode(String nodeName);

    Boolean deployNode();
}
