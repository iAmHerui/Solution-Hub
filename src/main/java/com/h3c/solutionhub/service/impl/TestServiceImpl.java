package com.h3c.solutionhub.service.impl;

import com.h3c.solutionhub.mapper.TestMapper;
import com.h3c.solutionhub.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public String test() {
        return testMapper.test();
    }
}
