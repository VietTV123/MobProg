package ua.kpi.comsys.IO7303.ui.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.IO7303.R;

public class LibraryFragment extends Fragment {
    private List<Book> books;
    private List<Book> foundBooks = new ArrayList<>();
    private List<Book> booksToShow = new ArrayList<>();
    private BookAdapter adapter;
    ListView listView;
    String customBooksList =  "books_list_custom.txt";
    Boolean addStatus = false;
    Boolean searchMode = false;
    Book removedBook = null;
    Boolean elemAddOnStop = false;


    @Override
    public void onResume() {
        super.onResume();
        if(addStatus){
            requireActivity().recreate();
            addStatus = false;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_third_tab, container, false);

        JsonHandler jsonHandler = new JsonHandler(R.raw.bookslist);
        jsonHandler.setFileUserName(customBooksList);

        books = jsonHandler.importBooksFromJSON(getContext());
        listView = root.findViewById(R.id.booksList);

        EditText searchRequest = root.findViewById(R.id.searchField);
        Button searchBtn = root.findViewById(R.id.buttonSearch);
        FloatingActionButton addBookButton = root.findViewById(R.id.buttonAddBook);

        if(books != null){
            adapter = new BookAdapter(getActivity(), R.layout.book_list, books);

            listView.setAdapter(adapter);
            Toast.makeText(getContext(), "Loaded", Toast.LENGTH_LONG).show();
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

        addBookButton.setOnClickListener(new View.OnClickListener() { // ADD
            public void onClick(View view) {
                addStatus = true;
                startActivity(new Intent(getContext(), AddBookActivity.class).putExtra("booklistId", R.raw.bookslist));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()  { // DELETE
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position,
                                           long id) {
                if(!searchMode) {
                    itemClicked.setBackgroundResource(R.color.green);
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Deleting");
                        builder.setMessage("Do you want to delete this book?");
                        builder.setCancelable(true);
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                itemClicked.setBackgroundResource(R.color.white);
                            }
                        });

                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // YES
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removedBook = books.remove((int) id);
                                adapter.notifyDataSetChanged();
                                jsonHandler.exportToJSON(getContext(), books);
                                elemAddOnStop = true;
                                dialog.dismiss();
                            }

                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Deleting error", Toast.LENGTH_LONG).show();
                    }
                }
                else Toast.makeText(getContext(), "To delete. you must leave the search mode", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() { // FIND
            public void onClick(View view) {
                String fieldText = searchRequest.getText().toString().toLowerCase();
                BookAdapter adapter2;
                foundBooks.clear();

                if (fieldText.equals("!reset")){
                    File userFile = new File(view.getContext().getFilesDir() + "/" + customBooksList);

                    try(FileWriter writer = new FileWriter(userFile)){
                        jsonHandler.setUserFileEnable(false);
                        writer.write(jsonHandler.getStringFromRawFile(getContext()));
                        writer.flush();
                    }
                    catch(IOException ex){
                        ex.printStackTrace();
                    }
                    getActivity().recreate();
                    Toast.makeText(getContext(), "User list has been reset", Toast.LENGTH_LONG).show();
                    adapter2 = new BookAdapter(getActivity(), R.layout.book_list, books);
                }

                else if(!fieldText.equals("")){
                    searchMode = true;
                    for (int i = 0; i < books.size(); i++) {
                        if(books.get(i).getTitle().toLowerCase().contains(fieldText)){
                            foundBooks.add(books.get(i));
                        }
                    }

                    if(foundBooks.isEmpty()){
                        Toast.makeText(getContext(), "Nothing found", Toast.LENGTH_LONG).show();
                    }
                    else Toast.makeText(getContext(), "OK", Toast.LENGTH_LONG).show();
                    adapter2 = new BookAdapter(getActivity(), R.layout.book_list, new ArrayList<>(foundBooks));
                }
                else {
                    searchMode = false;
                    adapter2 = new BookAdapter(getActivity(), R.layout.book_list, books);
                };
                listView.setAdapter(adapter2);
            }
        });

        return root;
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
            int res = getContext().getResources().getIdentifier(imageName.replaceAll(".png",
                    ""), "drawable", getContext().getPackageName());

            if(res!=0) currImg.setImageResource(res);
            else currImg.setImageResource(R.drawable.no_image);
            return row;
        }

        public String handle(String str){
            if(str.equals("")) return "None";
            else return str;

        }
    }
}