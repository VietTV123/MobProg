package ua.kpi.comsys.IO7303.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {

    @Query("SELECT * FROM book")
    List<BookEntities> getAll();

    @Query("SELECT * FROM book WHERE SearchRequest = :searchRequest")
    List<BookEntities> getByRequest(String searchRequest);

    @Query("SELECT * FROM book WHERE id = :id")
    BookEntities getById(long id);

    @Insert
    void insert(BookEntities film);

    @Update
    void update(BookEntities film);

    @Delete
    void delete(BookEntities film);
}

