package com.h3c.solutionhub.mapper;

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
                           @Param("businessGateway") String businessGateway);

    Boolean deleteNodeInfo(@Param("nodeName") String nodeName);
}
