package com.marcha.Messaging.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.marcha.entity.AllowanceEntity;
import com.marcha.entity.AllowanceEntity.Who;
import com.marcha.resourcepojo.TotalCalcPojo;

public class AllowanceList2TotalCalcPojo {
    public static TotalCalcPojo allowanceList2TotalCalcPojo(List<AllowanceEntity> list) {
                
        TotalCalcPojo totalCalcPojo = new TotalCalcPojo();

        Map<Who, Long> counts = list.stream()
        .collect(Collectors.groupingBy(
            (Function<AllowanceEntity, Who>) e -> e.who,
            Collectors.counting()
        ));
    
        totalCalcPojo.selfCount = counts.getOrDefault(Who.self, 0L);
        totalCalcPojo.momCount = counts.getOrDefault(Who.mom, 0L);
        totalCalcPojo.buyCount = counts.getOrDefault(Who.buy, 0L);

        totalCalcPojo.totalCount = Long.valueOf(list.size());

        totalCalcPojo.totalAllowance = list.stream()
            .mapToLong(e -> e.earned)
            .sum();

        totalCalcPojo.selfRatio = totalCalcPojo.totalCount > 0
            ? (double) totalCalcPojo.selfCount / totalCalcPojo.totalCount
            : 0.0;

        totalCalcPojo.userId = totalCalcPojo.totalCount > 0
            ? list.get(0).userId
            : null;

        return totalCalcPojo;
    }
}
