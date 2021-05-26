package ua.kpi.comsys.IO7303.ui.library;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ua.kpi.comsys.IO7303.R;

public class BookDetail extends AppCompatActivity {
    Book book;
    String imbdId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);

        Bundle arguments = getIntent().getExtras();
        imbdId = arguments.get("Isbn13").toString();

        int res = this.getResources().getIdentifier(imbdId, "raw", this.getPackageName()); // поиск ИД по имени

        JsonHandler jsonHandler = new JsonHandler(res);
        jsonHandler.setUserFileEnable(false);


        if(res!=0) {
            book = jsonHandler.importBookFromJSON(this); // если есть такой ИД
            TextView title = findViewById(R.id.bookDetailTitle);
            TextView subtitle = findViewById(R.id.subtitleDetail);
            TextView price = findViewById(R.id.priceDetail);
            TextView rating = findViewById(R.id.ratingDetail);
            TextView publisher = findViewById(R.id.publisherDetail);
            TextView authors = findViewById(R.id.authorsDetail);
            TextView year = findViewById(R.id.yearDetail);
            TextView pages = findViewById(R.id.pagesDetail);
            TextView desc = findViewById(R.id.desc);

            ImageView poster = (ImageView) findViewById(R.id.imageDetail);

            int img;
            try {
                String posterName = book.getImage().replaceAll(".png","").toLowerCase();
                img = getResources().getIdentifier(posterName, "drawable", getPackageName()); // поиск ИД по имени
            } catch (Exception e){img = 0;};


            if(img!=0) poster.setImageResource(img); // если есть такой ИД
            else poster.setImageResource(R.drawable.no_image); // стандартная картинка

            title.setText(book.getTitle());
            subtitle.setText(book.getSubtitle());
            price.setText("Price: "+ book.getPrice());
            rating.setText("Rating: "+ book.getRating());
            publisher.setText("Publisher: "+ book.getPublisher());
            authors.setText("Authors: "+ book.getAuthors());
            year.setText("Year: "+ book.getYear());
            pages.setText("Pages: "+ book.getPages());
            desc.setText(book.getDesc());

        }
        else {Toast.makeText(this, "Information not found", Toast.LENGTH_LONG).show(); finish();}
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
