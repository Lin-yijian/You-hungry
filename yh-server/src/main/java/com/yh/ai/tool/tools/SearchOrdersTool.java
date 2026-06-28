package com.yh.ai.tool.tools;

import com.yh.ai.tool.AiTool;
import com.yh.dto.OrdersPageQueryDTO;
import com.yh.result.PageResult;
import com.yh.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 订单搜索工具
 */
@Component
@Slf4j
public class SearchOrdersTool {

    @Autowired
    private OrderService orderService;

    @AiTool(
        name = "search_orders",
        description = "搜索订单，支持按手机号、订单号、状态、时间范围查询。返回分页的订单列表。",
        parameters = "{\"type\":\"object\",\"properties\":{\"phone\":{\"type\":\"string\",\"description\":\"手机号（可选）\"},\"number\":{\"type\":\"string\",\"description\":\"订单号（可选）\"},\"status\":{\"type\":\"integer\",\"description\":\"订单状态：1待付款 2待接单 3已接单 4派送中 5已完成 6已取消（可选）\"},\"beginTime\":{\"type\":\"string\",\"description\":\"开始时间（可选，格式yyyy-MM-dd HH:mm:ss）\"},\"endTime\":{\"type\":\"string\",\"description\":\"结束时间（可选，格式yyyy-MM-dd HH:mm:ss）\"}},\"required\":[]}"
    )
    public PageResult searchOrders(String phone, String number, Integer status, String beginTime, String endTime) {
        log.info("AI 调用工具 search_orders: phone={}, number={}, status={}", phone, number, status);
        OrdersPageQueryDTO dto = new OrdersPageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);
        if (phone != null) dto.setPhone(phone);
        if (number != null) dto.setNumber(number);
        if (status != null) dto.setStatus(status);
        if (beginTime != null) dto.setBeginTime(LocalDateTime.parse(beginTime));
        if (endTime != null) dto.setEndTime(LocalDateTime.parse(endTime));
        return orderService.conditionSearch(dto);
    }
}
