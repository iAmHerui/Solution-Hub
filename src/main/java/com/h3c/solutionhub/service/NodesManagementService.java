package com.h3c.solutionhub.service;

import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;

import java.util.List;

public interface NodesManagementService {

    List<NodeBo> getNodeList();

    Boolean insertNode(NodeBo nodeBo);

    Boolean editNode(NodeBo nodeBo);

    Boolean deleteNode(String nodeName);

//    Boolean deployNode(
//            String productType,
//            String productVersion,
//            List<NodeBo> nodes);

    Boolean deploySingleNode(NodeBo node,String productVersion);

    Boolean addDHCPInfo(String dhcpIPPond, String dhcpMask);

    DhcpBO getDHCPInfo();

    Boolean isNodeExist(String nodeName);
}
