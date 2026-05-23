package com.ai.learning.service;

import com.ai.learning.entity.LearningReport;
import com.ai.learning.mapper.LearningReportMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningReportService extends ServiceImpl<LearningReportMapper, LearningReport> {

    public LearningReport createReport(Long userId, String reportType, String reportData, String summary) {
        LearningReport report = new LearningReport();
        report.setUserId(userId);
        report.setReportType(reportType);
        report.setReportData(reportData);
        report.setSummary(summary);
        this.save(report);
        return report;
    }

    public Page<LearningReport> getReportList(Long userId, Integer page, Integer size) {
        Page<LearningReport> pageParam = new Page<>(page, size);
        QueryWrapper<LearningReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("create_time");
        return this.page(pageParam, queryWrapper);
    }

    public LearningReport getLatestReport(Long userId, String reportType) {
        QueryWrapper<LearningReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("report_type", reportType).orderByDesc("create_time").last("LIMIT 1");
        return this.getOne(queryWrapper);
    }
}
