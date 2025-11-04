package com.unicauca.ProjectManagement.repository;

import com.unicauca.ProjectManagement.entity.DegreeProject;
import com.unicauca.ProjectManagement.entity.EnumFileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DegreeProjectRepository extends JpaRepository<DegreeProject,Long>{

    @Query("SELECT dp FROM DegreeProject dp JOIN dp.students s WHERE s.email = :email")
    List<DegreeProject> findProjectsByStudentEmail(@Param("email") String email);

    @Query("SELECT dp FROM DegreeProject dp JOIN dp.files f WHERE f.type = :type")
    List<DegreeProject> findDegreeProjectsByFileType(@Param("type") EnumFileType type);

    @Query("""
    SELECT dp FROM DegreeProject dp
    JOIN dp.files f
    WHERE f.type = 'FormatoA'
      AND f.version = (
            SELECT MAX(f2.version)
            FROM DegreeProject dp2
            JOIN dp2.files f2
            WHERE dp2 = dp
            AND f2.type = 'FormatoA'
      )
      AND dp.state.name = 'ApprovedFormatA'
    """)
    List<DegreeProject> findProjectsWithApprovedFormatA();

}
