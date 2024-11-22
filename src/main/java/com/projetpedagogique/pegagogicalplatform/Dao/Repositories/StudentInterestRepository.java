package com.projetpedagogique.pegagogicalplatform.Dao.Repositories;

import com.projetpedagogique.pegagogicalplatform.Dao.Entities.Student;
import com.projetpedagogique.pegagogicalplatform.Dao.Entities.StudentInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentInterestRepository extends JpaRepository<StudentInterest,Long > {

    @Query("SELECT si.interest FROM StudentInterest si WHERE si.student.id = :studentId")
    List<String> findInterestsByStudentId(@Param("studentId") Long studentId);

    List<StudentInterest> findByStudentId(Long studentId);
}

