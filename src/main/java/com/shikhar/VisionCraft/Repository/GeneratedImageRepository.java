package com.shikhar.VisionCraft.Repository;

import com.shikhar.VisionCraft.entity.GeneratedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, Long> {
}