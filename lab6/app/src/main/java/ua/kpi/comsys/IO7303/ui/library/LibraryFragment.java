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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import ua.kpi.comsys.IO7303.R;

public class LibraryFragment extends Fragment {
    private List<Book> books;
    private List<Book> foundBooks = new ArrayList<>();
    private List<Book> booksToShow = new ArrayList<>();
    private BookAdapter adapter;
    ListView listView;
    String customBooksList = "books_list_custom_6.txt";
    String REQUEST_BOOK_TITLE;
    JsonHandler jsonHandler;

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
//            Toast.makeText(getContext(), "Loaded", Toast.LENGTH_LONG).show();
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
                String fieldText = searchRequest.getText().toString().toLowerCase();
                if (books!=null)
                    books.clear();

                if(!fieldText.equals("") & fieldText.length()>=3){
                    try {
                        REQUEST_BOOK_TITLE = fieldText;
                        new LoadJson("LoadJson").start();
                    } catch (Exception e){
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
            try {
                URL oracle = new URL("https://api.itbook.store/1.0/search/"+ REQUEST_BOOK_TITLE);
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                String result = in.readLine();
                System.out.println("RESULT: "+result);

                foundBooks = jsonHandler.importBookListFromString(result);

                jsonHandler.exportToJSON(getContext(), foundBooks);

                BookAdapter adapter3 = new BookAdapter(getActivity(), R.layout.book_list, foundBooks); // адаптер с новыми фильмами
                try {
                    listView.setAdapter(adapter3);
                } catch (Exception e2){}

                // HIDE KEYBOARD
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getActivity().getCurrentFocus();
                if (view == null) {
                    view = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

            try {new LoadAndSetImage(currImg).execute(imageName);}
            catch(Exception e) {currImg.setImageResource(R.drawable.no_image);}
            return row;
        }

        public String handle(String str){
            if(str.equals("")) return "None";
            else return str;

        }
    }

    private class LoadAndSetImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadAndSetImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}