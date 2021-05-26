package ua.kpi.comsys.IO7303.ui.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ua.kpi.comsys.IO7303.R;
import ua.kpi.comsys.IO7303.database.App;
import ua.kpi.comsys.IO7303.database.AppDatabase;
import ua.kpi.comsys.IO7303.database.BookDao;
import ua.kpi.comsys.IO7303.database.BookEntities;
import ua.kpi.comsys.IO7303.database.BookImageDao;
import ua.kpi.comsys.IO7303.database.BookImageEntities;

public class LibraryFragment extends Fragment {
    private List<Book> books;
    private List<Book> foundBooks = new ArrayList<>();
    private List<Book> booksToShow = new ArrayList<>();
    private BookAdapter adapter;
    ListView listView;
    String customBooksList = "books_list_custom.txt";
    String REQUEST_BOOK_TITLE;
    JsonHandler jsonHandler;
    static AppDatabase db = App.getInstance().getDatabase();
    BookDao bookDao = db.bookDao();
    static BookImageDao bookImageDao = db.bookImageDao();
    String searchRequest = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_third_tab_books, container, false);

        jsonHandler = new JsonHandler();
        jsonHandler.setFileUserName(customBooksList);

        books = jsonHandler.importBookListFromJSON(getContext());
        listView = root.findViewById(R.id.booksList);

        EditText searchRequest = root.findViewById(R.id.searchField);
        Button searchBtn = root.findViewById(R.id.buttonSearch);

        if(books != null){
            adapter = new BookAdapter(getActivity(), R.layout.book_list, books);

            listView.setAdapter(adapter);
        }
        else{
            Toast.makeText(getContext(), "Load failed...", Toast.LENGTH_LONG).show();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //DETAIL
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Toast.makeText(getContext(), booksToShow.get((int)id).getTitle(),
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getContext(), BookDetail.class).putExtra("Isbn13", booksToShow.get((int)id).getIsbn13()));
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() { // FIND
            public void onClick(View view) {
                LibraryFragment.this.searchRequest = searchRequest.getText().toString().toLowerCase();
                if (books!=null)
                    books.clear();

                if(!LibraryFragment.this.searchRequest.equals("") & LibraryFragment.this.searchRequest.length()>=3){
                    try {
                        REQUEST_BOOK_TITLE = LibraryFragment.this.searchRequest;
                        REQUEST_BOOK_TITLE = REQUEST_BOOK_TITLE.replace(" ", "%20");

                        while (REQUEST_BOOK_TITLE.startsWith("%20")) REQUEST_BOOK_TITLE = REQUEST_BOOK_TITLE.substring(1);
                        while (REQUEST_BOOK_TITLE.endsWith("%20")) REQUEST_BOOK_TITLE = REQUEST_BOOK_TITLE.substring(0, REQUEST_BOOK_TITLE.length()-2);

                        System.out.println("REQUEST: "+ REQUEST_BOOK_TITLE);

                        new LoadJson("LoadJson").start();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(getContext(), "Uncorrected request", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }


    class LoadJson extends Thread {
        LoadJson(String name){
            super(name);
        }

        public void run(){
            List<BookEntities> entityByRequest = bookDao.getByRequest(REQUEST_BOOK_TITLE);
            try {
                if (!internetAccess() & entityByRequest.size() > 0) {
                    foundBooks = Book.listBookEntitiesToListBooks(entityByRequest);

                    jsonHandler.exportToJSON(getContext(), foundBooks);

                    BookAdapter adapter3 = new BookAdapter(getActivity(), R.layout.book_list, foundBooks);
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(adapter3);
                            }
                        });
                    } catch (Exception e2) {
                    }

                    // HIDE KEYBOARD
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    View view = getActivity().getCurrentFocus();
                    if (view == null) {
                        view = new View(getActivity());
                    }
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                else if(!internetAccess() & entityByRequest.size()==0){
                    foundBooks = new ArrayList<>();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "No data in DB", Toast.LENGTH_LONG).show();
                        }
                    });
                    BookAdapter adapter3 = new BookAdapter(getActivity(), R.layout.book_list, foundBooks);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter3);
                        }
                    });

                }
                else {
                    URL oracle = new URL("https://api.itbook.store/1.0/search/" + REQUEST_BOOK_TITLE);
                    BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                    String result = in.readLine();

                    if(result==null){
                        Toast.makeText(getContext(), "There is no internet connection, books were not found in the database.", Toast.LENGTH_LONG).show();
                        foundBooks = new ArrayList<>();
                    }
                    else
                        foundBooks = jsonHandler.importBookListFromString(result);

                    jsonHandler.exportToJSON(getContext(), foundBooks);

                    new SaveBooksToDB("save").start();
                    try {
                        if (foundBooks==null){
                            foundBooks = new ArrayList<>();
                            Toast.makeText(getContext(), "Nothing found", Toast.LENGTH_LONG).show();
                        }
                        BookAdapter adapter3 = new BookAdapter(getActivity(), R.layout.book_list, foundBooks);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(adapter3);
                            }
                        });
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class BookAdapter extends ArrayAdapter<Book>{
        BookAdapter(Context context, int textViewResourceId, List<Book> objects) {
            super(context, textViewResourceId, objects);
            booksToShow = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.book_list, parent, false);
            TextView title = row.findViewById(R.id.bookTitle);
            TextView subtitle = row.findViewById(R.id.bookSubtitle);
            TextView price = row.findViewById(R.id.bookPrice);
            TextView isbn13 = row.findViewById(R.id.bookIsbn13);

            title.setText(handle(booksToShow.get(position).getTitle()));
            subtitle.setText(handle(booksToShow.get(position).getSubtitle()));
            price.setText("Price: " + handle(booksToShow.get(position).getPrice()));
            isbn13.setText("Isbn13: " + handle(booksToShow.get(position).getIsbn13()));

            ImageView currImg = row.findViewById(R.id.image);
            String imageName = booksToShow.get(position).getImage();
            System.out.println(booksToShow.get(position));

            try {
                try {
                    BookImgHandler handler = new BookImgHandler(currImg, getActivity(), imageName, position, getContext());
                    Thread thread = new Thread(handler);
                    thread.start();
                }
                catch (Exception e){}

            }
            catch(Exception e) {currImg.setImageResource(R.drawable.no_image);}
            return row;
        }

        public String handle(String str){
            if(str.equals("")) return "None";
            else return str;

        }
    }

    class SaveBooksToDB extends Thread {
        SaveBooksToDB(String name){
            super(name);
        }

        public void run(){
            try {
                if (bookDao.getByRequest(REQUEST_BOOK_TITLE).size() == 0) {
                    BookEntities bookEntity;
                    if (foundBooks != null)
                        for (Book currentBook : foundBooks) {
                            bookEntity = new BookEntities();
                            bookEntity.title = currentBook.getTitle();
                            bookEntity.subtitle = currentBook.getSubtitle();
                            bookEntity.isbn13 = currentBook.getIsbn13();
                            bookEntity.price = currentBook.getPrice();
                            bookEntity.image = currentBook.getImage();
                            bookEntity.SearchRequest = REQUEST_BOOK_TITLE;
                            bookDao.insert(bookEntity);
                        }
                } else System.out.println("Request '" + searchRequest + "' now already in DB");
            } catch (Exception e){}
        }
    }

    private static boolean internetAccess() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static class BookImgHandler implements Runnable {
        protected ImageView imageView;
        protected Activity uiActivity;
        protected String imgUrl;
        protected Context context;
        protected int position;

        public BookImgHandler(ImageView imageView, Activity uiActivity, String imgUrl, int position, Context context) {
            this.imageView = imageView;
            this.uiActivity = uiActivity;
            this.imgUrl = imgUrl;
            this.position = position;
            this.context = context;
        }

        public void run() {
            BookImageEntities currentImage = new BookImageEntities();
            System.out.println("Pos:"+position+"; URL:"+ imgUrl);
            String fileName;

            if (imgUrl ==null)
                imgUrl ="";
            if (imgUrl.startsWith("http") | imgUrl.startsWith("https")) {
                System.out.println(">>>>>>>>>>>>>>>>>ENTER BLOCK");
                List<BookImageEntities> daoByUrl = bookImageDao.getByUrl(imgUrl);
                String cacheDir = context.getCacheDir() + "";

                boolean imageExist = false;
                if (daoByUrl.size() != 0) {
                    String imageCachePath = cacheDir + "/" + daoByUrl.get(0).getFileName();
                    imageExist = new File(imageCachePath).exists();
                    System.out.println("FILE:"+daoByUrl.get(0).getFileName()+"; Exist:"+imageExist);
                }

                if (daoByUrl.size() == 0 | !imageExist) {
                    if (!imageExist & daoByUrl.size()>0)
                        fileName = daoByUrl.get(0).getFileName();
                    else {
                        int rndInt = new Random().nextInt(9999);
                        fileName = "image_" + hashCode()+ "_" + rndInt+".png";
                    }

                    URL urlDownload;
                    try {
                        urlDownload = new URL(imgUrl);
                        InputStream input = urlDownload.openStream();
                        try {
                            OutputStream output = new FileOutputStream(cacheDir + "/" + fileName);
                            try {
                                byte[] buffer = new byte[2048];
                                int bytesRead = 0;
                                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                                    output.write(buffer, 0, bytesRead);
                                }
                            } finally {
                                output.close();
                            }
                        } finally {
                            input.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println(">>>>>>>>>>>>URL IMAGES BEFORE ADDING: "+ imgUrl);
                    currentImage.url = imgUrl;
                    currentImage.fileName = fileName;
                    bookImageDao.insert(currentImage);
                }

                try {
                    System.out.println("TRY SET CACHED IMAGE");
                    String imageNameDB = bookImageDao.getByUrl(imgUrl).get(0).getFileName();

                    File imageFile = new File(context.getCacheDir() + "/" + imageNameDB);
                    InputStream is = new FileInputStream(imageFile);

                    Bitmap userImage = BitmapFactory.decodeStream(is);

                    uiActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(userImage);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (imageView != null) {
                uiActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageResource(R.drawable.no_image);
                    }
                });
            }
        }
    }
}