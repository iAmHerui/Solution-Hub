package com.h3c.solutionhub.mapper;

import com.h3c.solutionhub.entity.ProjectBo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectManagementMapper {

    List<ProjectBo> selectProjectList();

    Boolean insertProject(@Param("projectName") String projectName,
                          @Param("projectDescribe") String projectDescribe);

    Boolean updateProject(@Param("projectName") String projectName,
                          @Param("projectDescribe") String projectDescribe);

    Boolean deleteProject(@Param("projectId") int projectId);

    String getProjectNameById(@Param("projectId") int projectId);

    Boolean insertRefProjectProduct(@Param("projectName") String projectName,
                                    @Param("productName") String productName);

    Boolean deleteRefProjectProduct(@Param("projectName") String projectName);

    List<String> selectProductName(@Param("projectName") String projectName);
}
