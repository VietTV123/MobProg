package ua.kpi.comsys.IO7303.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookImage")
public class BookImageEntities {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String url, fileName;

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }
}