package ua.kpi.comsys.IO7303.ui.library;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ua.kpi.comsys.IO7303.R;

public class JsonHandler {
    private static final String FILE_NAME = "bookslist.txt";

    public static boolean exportToJSON(Context context, List<Book> dataList) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setSearch(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static List<Book> importFromJSON(Context context) {
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try{
            Gson gson = new Gson();
            DataItems dataItems = gson.fromJson(getStringFromRawFile(context), DataItems.class); // создание объектов из файла
            return dataItems.getSearch();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private static class DataItems {
        private List<Book> books;
        List<Book> getSearch() {
            return books;
        }
        void setSearch(List<Book> search) {
            this.books = search;
        }
    }

    private static String getStringFromRawFile(Context context) {
        Resources r = context.getResources();
        InputStream is = r.openRawResource(R.raw.bookslist);
        String txt = null;
        try {
            txt = convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  txt;
    }

    static String convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while( i != -1)
        {
            baos.write(i);
            i = is.read();
        }
        return  baos.toString();
    }
}
