package ru.kpfu.itis.water.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.water.model.Image;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
