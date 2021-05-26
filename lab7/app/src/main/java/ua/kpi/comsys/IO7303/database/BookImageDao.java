package ua.kpi.comsys.IO7303.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookImageDao {
    @Query("SELECT * FROM bookImage")
    List<BookImageEntities> getAll();

    @Query("SELECT * FROM bookImage WHERE id = :id")
    BookImageEntities getById(long id);

    @Query("SELECT * FROM bookImage WHERE url = :url")
    List<BookImageEntities> getByUrl(String url); // здесь в списке всегда должен быть один элемент,
    // List сделан для предотвращения краша, если будет добавлен ещё один элемент

    @Query("SELECT COUNT(*) FROM bookImage")
    int getDataCount();

    @Insert
    void insert(BookImageEntities bookImageEntities);

    @Update
    void update(BookImageEntities bookImageEntities);

    @Delete
    void delete(BookImageEntities bookImageEntities);
}