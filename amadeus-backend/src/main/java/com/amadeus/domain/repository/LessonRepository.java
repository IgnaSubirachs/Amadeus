package com.amadeus.domain.repository;

import com.amadeus.domain.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

    List<Lesson> findByLevelIdOrderByOrderNumberAsc(String levelId);

    List<Lesson> findByMusicTheoryTopic(String topic);
}
