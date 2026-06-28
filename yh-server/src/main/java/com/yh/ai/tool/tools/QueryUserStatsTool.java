package com.yh.ai.tool.tools;

import com.yh.ai.tool.AiTool;
import com.yh.service.ReportService;
import com.yh.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 用户统计查询工具
 */
@Component
@Slf4j
public class QueryUserStatsTool {

    @Autowired
    private ReportService reportService;

    @AiTool(
        name = "query_user_stats",
        description = "查询指定日期范围内的用户增长统计数据，包含新增用户数和总用户数。",
        parameters = "{\"type\":\"object\",\"properties\":{\"begin\":{\"type\":\"string\",\"description\":\"开始日期，格式yyyy-MM-dd\"},\"end\":{\"type\":\"string\",\"description\":\"结束日期，格式yyyy-MM-dd\"}},\"required\":[\"begin\",\"end\"]}"
    )
    public UserReportVO queryUserStats(String begin, String end) {
        log.info("AI 调用工具 query_user_stats: begin={}, end={}", begin, end);
        return reportService.getUserStatistics(LocalDate.parse(begin), LocalDate.parse(end));
    }
}
