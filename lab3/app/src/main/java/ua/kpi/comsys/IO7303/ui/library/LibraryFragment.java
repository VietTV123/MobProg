package ua.kpi.comsys.IO7303.ui.library;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import ua.kpi.comsys.IO7303.R;

public class LibraryFragment extends Fragment {
    private List<Book> books;
    private BookAdapter adapter;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_third_tab, container, false);

        listView = root.findViewById(R.id.bookList);
        books = JsonHandler.importFromJSON(getContext());

        if(books != null){
            adapter = new BookAdapter(getActivity(), R.layout.book_list, books);

            listView.setAdapter(adapter);
            Toast.makeText(getContext(), "Loaded", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "Load failed...", Toast.LENGTH_LONG).show();
        }
        return root;
    }

    private class BookAdapter extends ArrayAdapter<Book>{
        BookAdapter(Context context, int textViewResourceId, List<Book> objects) {
            super(context, textViewResourceId, objects);
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

            title.setText(handle(books.get(position).getTitle()));
            subtitle.setText(handle(books.get(position).getSubtitle()));
            price.setText("Price: " + handle(books.get(position).getPrice()));
            isbn13.setText("Isbn13: " + handle(books.get(position).getIsbn13()));

            ImageView currImg = row.findViewById(R.id.image);

            String imageName = books.get(position).getImage();
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