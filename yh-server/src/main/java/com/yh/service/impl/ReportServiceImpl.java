package com.yh.service.impl;

import com.yh.dto.GoodsSalesDTO;
import com.yh.entity.User;
import com.yh.mapper.OrderMapper;
import com.yh.mapper.UserMapper;
import com.yh.service.ReportService;
import com.yh.service.WorkspaceService;
import com.yh.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {



        //首先，先把时间列表给建立了
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(! begin.equals(end)){
            begin = begin.plusDays(1);

            dateList.add(begin);
        }


        //获取每天的营业额金额
        List<Double> turnoverList = new ArrayList<>();
        //查询语句 select sum(amount) from orders where order_time >be
        for(LocalDate localDate : dateList){
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status",5);

            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }


        return TurnoverReportVO.builder()
                .dateList(StringUtil.join(dateList.toArray(),","))
                .turnoverList(StringUtil.join(turnoverList.toArray(),","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //先封装好时间列表进行返回
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(! begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //获取新增用户数量，还有当前使用的用户数量
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("end",endTime);
            totalUserList.add(userMapper.getUserByMap(map));

            map.put("begin",beginTime);
            newUserList.add(userMapper.getUserByMap(map));
            //持久层获取新增用户数量
            //select count(id) from user where create_time > begin and create_time < endo

        }


        return UserReportVO
                .builder()
                .dateList(StringUtil.join(dateList.toArray(),",")).
                newUserList(StringUtil.join(newUserList.toArray(),","))
                .totalUserList(StringUtil.join(totalUserList.toArray(),","))
                .build();

    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {

        //先封装好时间列表进行返回
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(! begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }



//        //总数量统计
//        LocalDateTime beginTimeNum = LocalDateTime.of(begin,LocalTime.MIN);
//        LocalDateTime endTimeNum = LocalDateTime.of(end,LocalTime.MAX);
//        Map mapTotalOrderCount = new HashMap();
//        mapTotalOrderCount.put("begin",beginTimeNum);
//        mapTotalOrderCount.put("end",endTimeNum);
//         Integer totalOrderCount = orderMapper.getOrderByMap(mapTotalOrderCount);
//
//         Map mapValidOrderCount = new HashMap();
//         mapValidOrderCount.put("begin",beginTimeNum);
//         mapValidOrderCount.put("end",endTimeNum);
//         mapValidOrderCount.put("status",5);
//         Integer validOrderCount = orderMapper.getOrderByMap(mapValidOrderCount);

        //每天总数量统计
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate,LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            //select count(id) from orders where order_time > begin and order_time < en
            orderCountList.add(orderMapper.getOrderByMap(map));

            map.put("status",5);
            validOrderCountList.add(orderMapper.getOrderByMap(map));
        }

        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }


        return OrderReportVO
                .builder()
                .dateList(StringUtil.join(dateList.toArray(),","))
                .orderCountList(StringUtil.join(orderCountList.toArray(),","))
                .validOrderCountList(StringUtil.join(validOrderCountList.toArray(),","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计指定时间区间内的销量排名前10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        //封装返回结果数据
        return SalesTop10ReportVO
                .builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1,查询数据库，获取营业数据--查询最近30天的运营数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessDataVO=workspaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end,LocalTime.MAX));


        //2，通过POI将数据写入到Excel文件中

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间： "+begin + " 至 " + end);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }


            //3，通过输入流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();

            excel.write(out);

            //关闭资源
            out.close();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
