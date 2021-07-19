package com.h3c.solutionhub.entity;

import lombok.Data;

@Data
public class DhcpBO {

    // DHCP IP段
    private String dhcpIPPond;

    // DHCP 掩码
    private String dhcpMask;
}
