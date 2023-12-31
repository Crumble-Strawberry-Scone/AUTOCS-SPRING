package com.css.autocsfinal.workstatus.repository;

import com.css.autocsfinal.stock.entity.Category;
import com.css.autocsfinal.workstatus.entity.EmployeeAndWorkStatus;
import com.css.autocsfinal.workstatus.entity.EmployeeByWorkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface EmployeeAndWorkStatusRepository extends JpaRepository<EmployeeAndWorkStatus, Integer> {
    @Query("SELECT distinct e FROM EmployeeAndWorkStatus e " +
            "LEFT JOIN fetch e.department d " +
            "LEFT JOIN fetch e.position p " +
            "LEFT JOIN fetch e.workStatusLists l " +
            "LEFT JOIN fetch l.workStatus w " +
            "ORDER BY e.name")
    List<EmployeeAndWorkStatus> findByOrderByName();


    @Query("SELECT distinct e FROM EmployeeAndWorkStatus e " +
            "LEFT JOIN fetch e.department d " +
            "LEFT JOIN fetch e.position p " +
            "LEFT JOIN fetch e.workStatusLists l " +
            "LEFT JOIN fetch l.workStatus w " +
            "WHERE e.name = :name " +
            "ORDER BY e.name")
    List<EmployeeAndWorkStatus> findByOrderByName(String name);

    @Query("SELECT distinct e FROM EmployeeAndWorkStatus e " +
            "LEFT JOIN fetch e.department d " +
            "LEFT JOIN fetch e.position p " +
            "LEFT JOIN fetch e.workStatusLists l " +
            "LEFT JOIN fetch l.workStatus w " +
            "ORDER BY e.name")
    List<EmployeeAndWorkStatus> findByOrderByName(Pageable paging);

    @Query("SELECT distinct e FROM EmployeeAndWorkStatus e " +
            "LEFT JOIN fetch e.department d " +
            "LEFT JOIN fetch e.position p " +
            "LEFT JOIN fetch e.workStatusLists l " +
            "LEFT JOIN fetch l.workStatus w " +
            "WHERE e.name = :name " +
            "ORDER BY e.name")
    List<EmployeeAndWorkStatus> findByOrderByName(Pageable paging, String name);

}
