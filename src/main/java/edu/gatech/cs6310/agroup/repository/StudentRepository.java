package edu.gatech.cs6310.agroup.repository;

import edu.gatech.cs6310.agroup.model.Course;
import edu.gatech.cs6310.agroup.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by mlarson on 4/1/16.
 */
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT MAX(s.id) FROM Student s")
    public int getMaxId();

}
