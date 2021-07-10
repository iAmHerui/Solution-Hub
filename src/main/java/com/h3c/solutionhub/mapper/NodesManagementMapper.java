package com.h3c.solutionhub.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NodesManagementMapper {

    Boolean insertNodeInfo(@Param("nodeName") String nodeName,
                           @Param("nodeHDMIP") String nodeHDMIP,
                           @Param("nodeType") String nodeType,
                           @Param("nodeStatus") String nodeStatus,
                           @Param("nodeHDMPaasword") String nodeHDMPaasword,
                           @Param("managementIP") String managementIP,
                           @Param("businessIP") String businessIP,
                           @Param("managementGateway") String managementGateway,
                           @Param("businessGateway") String businessGateway);

    Boolean deleteNodeInfo(@Param("nodeName") String nodeName);
}
