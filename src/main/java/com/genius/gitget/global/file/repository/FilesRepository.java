package com.genius.gitget.global.file.repository;

import com.genius.gitget.global.file.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<Files, Long> {
}
