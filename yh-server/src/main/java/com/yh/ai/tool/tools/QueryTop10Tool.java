package com.yh.ai.tool.tools;

import com.yh.ai.tool.AiTool;
import com.yh.service.ReportService;
import com.yh.vo.SalesTop10ReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 销量排行查询工具
 */
@Component
@Slf4j
public class QueryTop10Tool {

    @Autowired
    private ReportService reportService;

    @AiTool(
        name = "query_top10",
        description = "查询指定日期范围内销量排名前10的菜品或套餐，返回名称和销量列表。",
        parameters = "{\"type\":\"object\",\"properties\":{\"begin\":{\"type\":\"string\",\"description\":\"开始日期，格式yyyy-MM-dd\"},\"end\":{\"type\":\"string\",\"description\":\"结束日期，格式yyyy-MM-dd\"}},\"required\":[\"begin\",\"end\"]}"
    )
    public SalesTop10ReportVO queryTop10(String begin, String end) {
        log.info("AI 调用工具 query_top10: begin={}, end={}", begin, end);
        return reportService.getSalesTop10(LocalDate.parse(begin), LocalDate.parse(end));
    }
}
