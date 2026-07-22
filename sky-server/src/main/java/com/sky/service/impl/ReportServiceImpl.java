package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;


    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList();

        dateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumbyMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
        return turnoverReportVO;
    }

    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //新增
        List<Integer> newUserList = new ArrayList<>();
        //总
        List<Integer> totalUserList = new ArrayList<>();

        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            Integer TotalUser = userMapper.countbyDate(map);

            map.put("begin", beginTime);
            Integer NewUser = userMapper.countbyDate(map);

            TotalUser = TotalUser == null ? 0 : TotalUser;
            NewUser = NewUser == null ? 0 : NewUser;

            totalUserList.add(TotalUser);
            newUserList.add(NewUser);
        }

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
        return userReportVO;
    }

    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> CountOrderList = new ArrayList<>();
        List<Integer> VaildOrderList = new ArrayList<>();
        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer CountOrder = orderMapper.countbyMap(map);
            map.put("status", Orders.COMPLETED);
            Integer VaildDOrder = orderMapper.countbyMap(map);
            CountOrderList.add(CountOrder);
            VaildOrderList.add(VaildDOrder);
        }
        Integer TotalOrderCount = CountOrderList.stream().reduce(Integer::sum).get();
        Integer TotalVaildOrderCount = VaildOrderList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = TotalOrderCount==0.0 ? 0: TotalVaildOrderCount.doubleValue() / TotalOrderCount;

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .orderCountList(StringUtils.join(CountOrderList,","))
                .orderCompletionRate(orderCompletionRate)
                .dateList(StringUtils.join(dateList,","))
                .totalOrderCount(TotalOrderCount)
                .validOrderCount(TotalVaildOrderCount)
                .validOrderCountList(StringUtils.join(VaildOrderList,","))
                .build();
        return orderReportVO;
    }

    public SalesTop10ReportVO saleStatictis(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.saleStatictis(beginTime,endTime);
        List<String> name = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> number = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(name,","))
                .numberList(StringUtils.join(number,","))
                .build();
    }
}
