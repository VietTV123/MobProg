//package ua.kpi.comsys.IO7303.ui.library;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.ByteArrayOutputStream;
//import java.util.List;
//
//import ua.kpi.comsys.IO7303.R;
//
//public class AddBookActivity extends AppCompatActivity {
//    int resBookList;
//    private List<Book> books;
//    ByteArrayOutputStream bos;
//    String booksFile =  "books_list_custom.txt";
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.add_book_layout);
//        bos = new ByteArrayOutputStream();
//        Bundle arguments = getIntent().getExtras();
//        resBookList = (int)arguments.get("booklistId");
//    }
//
//    public void addBtn(View view) { // ADD
//        String bookTitle = ((EditText)findViewById(R.id.addTitle)).getText().toString();
//        String bookSubtitle = ((EditText)findViewById(R.id.addSubbtitle)).getText().toString();
//        String bookPrice = ((EditText)findViewById(R.id.addPrice)).getText().toString();
//
//        String imageName;
//
//        if(bookTitle.length()<1){
//            Toast.makeText(view.getContext(), "Uncorrected title", Toast.LENGTH_LONG).show();
//        }
//        else {
//            JsonHandler jsonHandler = new JsonHandler(resBookList);
//            jsonHandler.setFileUserName(booksFile);
//            books = jsonHandler.importBooksFromJSON(view.getContext());
//
//            imageName = "";
//
//            books.add(new Book(bookTitle, bookSubtitle, bookPrice, "noID", imageName));
//
//            jsonHandler.exportToJSON(view.getContext(), books);
//
//            finish();
//            Toast.makeText(view.getContext(), "Added successfully", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void returnBtn(View view) { // RETURN
//        finish();
//    }
//}