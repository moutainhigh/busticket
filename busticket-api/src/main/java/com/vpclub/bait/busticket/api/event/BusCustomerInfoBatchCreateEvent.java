package com.vpclub.bait.busticket.api.event;

import cn.vpclub.moses.core.entity.BaseEntity;
import com.vpclub.bait.busticket.api.entity.Customer;
import lombok.Data;

import java.util.List;

@Data
public class BusCustomerInfoBatchCreateEvent extends BaseEntity{
    private Long id;
    private List<Customer> customers;
    private String orderCode;
}
