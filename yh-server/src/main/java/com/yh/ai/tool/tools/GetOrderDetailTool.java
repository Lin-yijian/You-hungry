package com.yh.ai.tool.tools;

import com.yh.ai.tool.AiTool;
import com.yh.service.OrderService;
import com.yh.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单详情查询工具
 */
@Component
@Slf4j
public class GetOrderDetailTool {

    @Autowired
    private OrderService orderService;

    @AiTool(
        name = "get_order_detail",
        description = "根据订单ID查询订单详情，包含订单基本信息、订单明细和配送地址。",
        parameters = "{\"type\":\"object\",\"properties\":{\"orderId\":{\"type\":\"integer\",\"description\":\"订单ID\"}},\"required\":[\"orderId\"]}"
    )
    public OrderVO getOrderDetail(Long orderId) {
        log.info("AI 调用工具 get_order_detail: orderId={}", orderId);
        return orderService.details(orderId);
    }
}
