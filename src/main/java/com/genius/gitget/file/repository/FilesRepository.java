package com.genius.gitget.file.repository;

import com.genius.gitget.file.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<Files, Long> {
}
