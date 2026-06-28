package com.yh.ai.tool.tools;

import com.yh.ai.tool.AiTool;
import com.yh.service.ReportService;
import com.yh.vo.OrderReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 订单统计查询工具
 */
@Component
@Slf4j
public class QueryOrderStatsTool {

    @Autowired
    private ReportService reportService;

    @AiTool(
        name = "query_order_stats",
        description = "查询指定日期范围内的订单统计数据，包含每日订单数、有效订单数和订单完成率。",
        parameters = "{\"type\":\"object\",\"properties\":{\"begin\":{\"type\":\"string\",\"description\":\"开始日期，格式yyyy-MM-dd\"},\"end\":{\"type\":\"string\",\"description\":\"结束日期，格式yyyy-MM-dd\"}},\"required\":[\"begin\",\"end\"]}"
    )
    public OrderReportVO queryOrderStats(String begin, String end) {
        log.info("AI 调用工具 query_order_stats: begin={}, end={}", begin, end);
        return reportService.getOrderStatistics(LocalDate.parse(begin), LocalDate.parse(end));
    }
}
