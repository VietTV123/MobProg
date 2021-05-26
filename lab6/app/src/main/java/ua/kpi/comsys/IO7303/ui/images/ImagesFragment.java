package ua.kpi.comsys.IO7303.ui.images;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import ua.kpi.comsys.IO7303.R;

public class ImagesFragment extends Fragment {
    View root;
    static int width;
    int height;
    private ImagesListAdapter adapter;
    ListView listView;
    static LinearLayout layout;
    String REQUEST = "\"hot+summer\"";
    String imageUrlTarget="\"webformatURL\":\"";
    int COUNT = 24;
    String API_KEY = "19193969-87191e5db266905fe8936d565";
    View elemSet;
    URL url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display screensize = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screensize.getSize(size);
        width = size.x;
        height = size.y;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_four_tab_images, container, false);
        elemSet = inflater.inflate(R.layout.images_list, container, false);

        listView = root.findViewById(R.id.imagesList);

        try {
            url = new URL("https://pixabay.com/api/?key="+API_KEY+"&q="+REQUEST+"&image_type=photo&per_page="+COUNT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new ParseJson("LoadImage").start();

        return root;
    }

    static class ImagesListAdapter extends ArrayAdapter<List<String>> {
        private final List<List<String>> taskImg;
        Activity generalAct;

        ImagesListAdapter(Context context, int textViewResourceId, List<List<String>> objects, Activity generalAct) {
            super(context, textViewResourceId, objects);
            this.taskImg = objects;
            this.generalAct = generalAct;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = inflater.inflate(R.layout.images_list, parent, false);

            layout = row.findViewById(R.id.imageSet);
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = width;
            params.width = width;
            layout.setLayoutParams(params);

            List<ImageView> imagesListToShow = new ArrayList<>();
            imagesListToShow.add(row.findViewById(R.id.gal_img1));
            imagesListToShow.add(row.findViewById(R.id.gal_img2));
            imagesListToShow.add(row.findViewById(R.id.gal_img3));
            imagesListToShow.add(row.findViewById(R.id.gal_img4));
            imagesListToShow.add(row.findViewById(R.id.gal_img5));
            imagesListToShow.add(row.findViewById(R.id.gal_img6));
            imagesListToShow.add(row.findViewById(R.id.gal_img7));
            imagesListToShow.add(row.findViewById(R.id.gal_img8));

            List<ProgressBar> loadingStatusList = new ArrayList<>();
            loadingStatusList.add(row.findViewById(R.id.load1));
            loadingStatusList.add(row.findViewById(R.id.load2));
            loadingStatusList.add(row.findViewById(R.id.load3));
            loadingStatusList.add(row.findViewById(R.id.load4));
            loadingStatusList.add(row.findViewById(R.id.load5));
            loadingStatusList.add(row.findViewById(R.id.load6));
            loadingStatusList.add(row.findViewById(R.id.load7));
            loadingStatusList.add(row.findViewById(R.id.load8));

            int imgNumber = taskImg.get(position).size();

            for (int i=0; i<8; i++){
                try {
                    if (i<imgNumber){
                        try {
                            new SetDownloadedImg(imagesListToShow.get(i)).execute(taskImg.get(position).get(i));
                        } catch (Exception e){}
                    }
                    else loadingStatusList.get(i).setVisibility(View.INVISIBLE);
                } catch (Exception ignored){}
            }

            return row;
        }
    }

    class ParseJson extends Thread {
        ParseJson(String name){
            super(name);
        }

        public void run(){
            List<String> urls = new ArrayList<>();
            List<List<String>> urls_final = new ArrayList<>();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                String json = "";
                while (true) {
                    if ((inputLine = br.readLine()) == null) break;
                    json += inputLine;
                }

                String S[] = json.split(imageUrlTarget);
                for (String str : S){
                    if (str.substring(0, 4).equals("http")){
                        urls.add(str.split("\",\"")[0]);
                    }
                }

                for (String currentUrl : urls){
                    if (urls_final != null){
                        if (urls_final.size()==0){
                            List<String> tempImageList = new ArrayList<>();
                            urls_final.add(tempImageList);
                        }
                        if (urls_final.get(urls_final.size()-1).size()>=8){
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(currentUrl);
                            urls_final.add(tempImageList);
                        }
                        else {
                            urls_final.get(urls_final.size()-1).add(currentUrl);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(urls != null){
                        adapter = new ImagesListAdapter(getActivity(), R.layout.images_list, urls_final, getActivity());
                        listView.setAdapter(adapter);
                    }
                    else{
                        Toast.makeText(getContext(), "Failed data load", Toast.LENGTH_LONG).show();
                    }
                }
            });

            System.out.println("URLS: " + urls_final.toString());
        }
    }

    private static class SetDownloadedImg extends AsyncTask<String, Void, Bitmap> {
        ImageView imageViewObj;

        public SetDownloadedImg(ImageView bmImage) {
            this.imageViewObj = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null)
                imageViewObj.setImageResource(R.drawable.no_image);
            else imageViewObj.setImageBitmap(result);
        }
    }
}
