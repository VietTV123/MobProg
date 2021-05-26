package ua.kpi.comsys.IO7303.ui.library;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class JsonHandler {
    private static int RESOURCE_NAME;
    private static String FILE_USER_NAME;
    private static Boolean userFileEnable = false;
    private static File f;

    public JsonHandler(int resourceName){
        RESOURCE_NAME = resourceName;
    }

    public static void setFileUserName(String fileUserName) {
        FILE_USER_NAME = fileUserName;
    }

    public static void setUserFileEnable(Boolean userFileEnable) {
        JsonHandler.userFileEnable = userFileEnable;
    }

    public static boolean exportToJSON(Context context, List<Book> dataList) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setBooks(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_USER_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            userFileEnable = true;
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


    public static List<Book> importBooksFromJSON(Context context) {
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;

        try{
            Gson gson = new Gson();

            f = new File(context.getFilesDir() + "/"+FILE_USER_NAME);
            if(f.exists()){
                userFileEnable = true;
            }
            else{
                try(FileWriter writer = new FileWriter(f)){
                    writer.write(getStringFromRawFile(context));
                    writer.flush();
                    userFileEnable = true;
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }

            DataItems dataItems = gson.fromJson(getStringFromRawFile(context), DataItems.class);

            return dataItems.getBooks();
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

    public static Book importBookFromJSON(Context context) {
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;

        try{
            Gson gson = new Gson();
            Book book = gson.fromJson(getStringFromRawFile(context), Book.class);
            return book;
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

        List<Book> getBooks() {
            return books;
        }
        void setBooks(List<Book> books) {
            this.books = books;
        }
    }

    public static String getStringFromRawFile(Context context) {
        InputStream is = null;
        if(!userFileEnable) {
            Resources r = context.getResources();
            is = r.openRawResource(RESOURCE_NAME);
        }
        else {
            try {
                is = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String myText = null;
        try {
            myText = convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  myText;
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
