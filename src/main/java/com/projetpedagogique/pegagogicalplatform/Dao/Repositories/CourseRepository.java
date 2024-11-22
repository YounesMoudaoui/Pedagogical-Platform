package com.projetpedagogique.pegagogicalplatform.Dao.Repositories;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findCoursesByInterest(String interest);

    @Query("SELECT c FROM Course c WHERE c.advancedCourse = :course")
    List<Course> findAdvancedCoursesByCourse(@Param("course") Course course);
}
