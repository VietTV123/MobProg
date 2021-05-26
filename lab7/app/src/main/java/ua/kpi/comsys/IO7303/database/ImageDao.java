package ua.kpi.comsys.IO7303.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM image")
    List<ImageEntities> getAll();

    @Query("SELECT * FROM image WHERE id = :id")
    ImageEntities getById(long id);

    @Query("SELECT * FROM image WHERE url = :url")
    List<ImageEntities> getByUrl(String url); // здесь в списке всегда должен быть один элемент,
    // List сделан для предотвращения краша, если будет добавлен ещё один элемент

    @Query("SELECT COUNT(*) FROM image")
    int getDataCount();

    @Insert
    void insert(ImageEntities imageEntities);

    @Update
    void update(ImageEntities imageEntities);

    @Delete
    void delete(ImageEntities imageEntities);
}