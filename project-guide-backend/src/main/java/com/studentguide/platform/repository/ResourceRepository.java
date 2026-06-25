package com.studentguide.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.studentguide.platform.entity.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource,Long> {
    List<Resource> findByTitleContainingIgnoreCase(String title);

    List<Resource> findByCategory(String category);

}
