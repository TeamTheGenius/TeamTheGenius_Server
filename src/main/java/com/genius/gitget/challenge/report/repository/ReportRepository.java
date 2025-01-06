package com.genius.gitget.challenge.report.repository;

import com.genius.gitget.challenge.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
