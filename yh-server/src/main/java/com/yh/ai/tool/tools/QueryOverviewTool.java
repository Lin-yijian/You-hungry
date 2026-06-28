package com.yh.ai.tool.tools;

import com.yh.ai.tool.AiTool;
import com.yh.service.WorkspaceService;
import com.yh.vo.BusinessDataVO;
import com.yh.vo.OrderOverViewVO;
import com.yh.vo.DishOverViewVO;
import com.yh.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 经营概览查询工具
 */
@Component
@Slf4j
public class QueryOverviewTool {

    @Autowired
    private WorkspaceService workspaceService;

    @AiTool(
        name = "query_business_overview",
        description = "查询今日经营概览数据，包含营业额、有效订单数、订单完成率、客单价、新增用户数。",
        parameters = "{\"type\":\"object\",\"properties\":{}}"
    )
    public Map<String, Object> queryBusinessOverview() {
        log.info("AI 调用工具 query_business_overview");
        LocalDate today = LocalDate.now();
        BusinessDataVO data = workspaceService.getBusinessData(
                LocalDateTime.of(today, LocalTime.MIN),
                LocalDateTime.of(today, LocalTime.MAX)
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("turnover", data.getTurnover());
        result.put("validOrderCount", data.getValidOrderCount());
        result.put("orderCompletionRate", data.getOrderCompletionRate());
        result.put("unitPrice", data.getUnitPrice());
        result.put("newUsers", data.getNewUsers());
        result.put("date", today.toString());
        return result;
    }

    @AiTool(
        name = "query_order_overview",
        description = "查询当前各状态订单数量分布，包含待接单、配送中、已完成、已取消、总订单数。",
        parameters = "{\"type\":\"object\",\"properties\":{}}"
    )
    public OrderOverViewVO queryOrderOverview() {
        log.info("AI 调用工具 query_order_overview");
        return workspaceService.getOrderOverView();
    }

    @AiTool(
        name = "query_dish_overview",
        description = "查询菜品总览，包含在售数量和停售数量。",
        parameters = "{\"type\":\"object\",\"properties\":{}}"
    )
    public DishOverViewVO queryDishOverview() {
        log.info("AI 调用工具 query_dish_overview");
        return workspaceService.getDishOverView();
    }

    @AiTool(
        name = "query_setmeal_overview",
        description = "查询套餐总览，包含在售数量和停售数量。",
        parameters = "{\"type\":\"object\",\"properties\":{}}"
    )
    public SetmealOverViewVO querySetmealOverview() {
        log.info("AI 调用工具 query_setmeal_overview");
        return workspaceService.getSetmealOverView();
    }
}
