package com.h3c.solutionhub.entity;

import lombok.Data;

import java.util.List;

@Data
public class ProjectBo {

    // 工程ID
    private int projectId;

    // 工程名称
    private String projectName;

    // 工程描述
    private String projectDescribe;

    // 工程描述
    private List<String> projectProductList;

}
