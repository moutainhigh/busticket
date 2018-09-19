package com.vpclub.bait.busticket.api.entity;

import lombok.Data;

import java.util.List;

@Data
/**
 * 对象数据分组类，按照实例的某个属性首字母进行分组
 */
public class ObjectOrdered<T> {
    private String index;
    private List<T> list;
}
