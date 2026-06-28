package com.yh.ai.tool.tools;

import com.yh.ai.tool.AiTool;
import com.yh.service.ReportService;
import com.yh.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 营业额查询工具
 */
@Component
@Slf4j
public class QueryTurnoverTool {

    @Autowired
    private ReportService reportService;

    @AiTool(
        name = "query_turnover",
        description = "查询指定日期范围内的营业额数据。返回每日营业额列表。",
        parameters = "{\"type\":\"object\",\"properties\":{\"begin\":{\"type\":\"string\",\"description\":\"开始日期，格式yyyy-MM-dd\"},\"end\":{\"type\":\"string\",\"description\":\"结束日期，格式yyyy-MM-dd\"}},\"required\":[\"begin\",\"end\"]}"
    )
    public TurnoverReportVO queryTurnover(String begin, String end) {
        log.info("AI 调用工具 query_turnover: begin={}, end={}", begin, end);
        return reportService.getTurnoverStatistics(LocalDate.parse(begin), LocalDate.parse(end));
    }
}
