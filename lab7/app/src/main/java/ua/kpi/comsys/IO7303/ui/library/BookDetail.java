package ua.kpi.comsys.IO7303.ui.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import ua.kpi.comsys.IO7303.R;

public class BookDetail extends AppCompatActivity {
    Book book;
    String isbn13;
    JsonHandler jsonHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);

        Bundle arguments = getIntent().getExtras();
        isbn13 = arguments.get("Isbn13").toString();

        jsonHandler = new JsonHandler();
        jsonHandler.setUserFileEnable(false);

        new LoadJson("LoadBook").start();
    }

    class LoadJson extends Thread {
        LoadJson(String name){
            super(name);
        }

        public void run(){
            try {
                URL oracle = new URL("https://api.itbook.store/1.0/books/"+isbn13);
                BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                String result = in.readLine();
                System.out.println("RESULT: "+result);

                book = jsonHandler.importBookFromString(result);

                System.out.println(book);

                TextView title = findViewById(R.id.bookDetailTitle);
                TextView subtitle = findViewById(R.id.subtitleDetail);
                TextView price = findViewById(R.id.priceDetail);
                TextView rating = findViewById(R.id.ratingDetail);
                TextView publisher = findViewById(R.id.publisherDetail);
                TextView authors = findViewById(R.id.authorsDetail);
                TextView year = findViewById(R.id.yearDetail);
                TextView pages = findViewById(R.id.pagesDetail);
                TextView desc = findViewById(R.id.desc);

                ImageView imageView = (ImageView) findViewById(R.id.imageDetail);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(book.getTitle());
                        subtitle.setText(book.getSubtitle());
                        price.setText("Price: "+ book.getPrice());
                        rating.setText("Rating: "+ book.getRating());
                        publisher.setText("Publisher: "+ book.getPublisher());
                        authors.setText("Authors: "+ book.getAuthors());
                        year.setText("Year: "+ book.getYear());
                        pages.setText("Pages: "+ book.getPages());
                        desc.setText(book.getDesc());

                        String imageUrl = book.getImage();
                        imageView.setImageResource(R.drawable.no_image);

                        try {
                            new LoadAndSetImage(imageView).execute(imageUrl);
                        } catch (Exception e){
                            imageView.setImageResource(R.drawable.no_image);
                        }
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void setBook(Book book) {
        this.book = book;
    }
}
