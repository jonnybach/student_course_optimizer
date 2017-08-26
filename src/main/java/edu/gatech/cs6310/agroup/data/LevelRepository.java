package edu.gatech.cs6310.agroup.data;

import edu.gatech.cs6310.agroup.model.Level;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by tim on 3/24/16.
 */
public interface LevelRepository extends CrudRepository<Level, Long> {

    List<Level> findByName(String name);
}
