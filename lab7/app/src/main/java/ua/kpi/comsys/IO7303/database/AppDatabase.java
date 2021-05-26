package ua.kpi.comsys.IO7303.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BookEntities.class, BookImageEntities.class, ImageEntities.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
    public abstract BookImageDao bookImageDao();
    public abstract ImageDao imageDao();
}
