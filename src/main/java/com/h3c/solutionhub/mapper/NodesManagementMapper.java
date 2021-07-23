package com.h3c.solutionhub.mapper;

import com.h3c.solutionhub.entity.DhcpBO;
import com.h3c.solutionhub.entity.NodeBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NodesManagementMapper {

    List<NodeBo> getNodeList();

    Boolean insertNodeInfo(@Param("nodeName") String nodeName,
                           @Param("nodeHDMIP") String nodeHDMIP,
                           @Param("nodeType") String nodeType,
                           @Param("nodeStatus") String nodeStatus,
                           @Param("nodeHDMPaasword") String nodeHDMPaasword,
                           @Param("managementIP") String managementIP,
                           @Param("businessIP") String businessIP,
                           @Param("managementMask") String managementMask,
                           @Param("businessMask") String businessMask,
                           @Param("managementGateway") String managementGateway,
                           @Param("businessGateway") String businessGateway,
                           @Param("storageIP") String storageIP,
                           @Param("storageMask") String storageMask,
                           @Param("storageGateway") String storageGateway);

    Boolean deleteNodeInfo(@Param("nodeName") String nodeName);

    Boolean insertDHCPInfo(@Param("dhcpIPPond") String dhcpIPPond,@Param("dhcpMask") String dhcpMask);

    DhcpBO selectDHCPInfo();

    int isDhcpExist();

    Boolean deleteDHCPInfo();

    Boolean editNodeInfo(@Param("nodeName") String nodeName,
                         @Param("nodeHDMIP") String nodeHDMIP,
                         @Param("nodeType") String nodeType,
                         @Param("nodeStatus") String nodeStatus,
                         @Param("nodeHDMPaasword") String nodeHDMPaasword,
                         @Param("managementIP") String managementIP,
                         @Param("businessIP") String businessIP,
                         @Param("managementMask") String managementMask,
                         @Param("businessMask") String businessMask,
                         @Param("managementGateway") String managementGateway,
                         @Param("businessGateway") String businessGateway,
                         @Param("storageIP") String storageIP,
                         @Param("storageMask") String storageMask,
                         @Param("storageGateway") String storageGateway);

    Boolean updateNodeStatus(@Param("nodeId") int nodeId);

    String selectNodeStatus(@Param("nodeName") String nodeName);
}
