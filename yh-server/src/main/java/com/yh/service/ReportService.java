package com.yh.service;

import com.yh.vo.OrderReportVO;
import com.yh.vo.SalesTop10ReportVO;
import com.yh.vo.TurnoverReportVO;
import com.yh.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {


    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间区间内的销量排名前10
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);


    void exportBusinessData(HttpServletResponse response);

    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
