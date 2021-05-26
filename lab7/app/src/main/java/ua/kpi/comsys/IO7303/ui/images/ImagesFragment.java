package ua.kpi.comsys.IO7303.ui.images;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.IO7303.R;
import ua.kpi.comsys.IO7303.database.App;
import ua.kpi.comsys.IO7303.database.AppDatabase;
import ua.kpi.comsys.IO7303.database.ImageDao;
import ua.kpi.comsys.IO7303.database.ImageEntities;

public class ImagesFragment extends Fragment {
    View root;
    static int width;
    int height;
    private ImagesListAdapter adapter;
    ListView listView;
    static LinearLayout layout;
    String REQUEST = "\"hot+summer\"";
    String imageUrlTarget="\"previewURL\":\"";
    int COUNT = 24;
    String API_KEY = "19193969-87191e5db266905fe8936d565";
    View currentPage;
    URL url;
    static AppDatabase appDatabase = App.getInstance().getDatabase();
    static ImageDao imageDao = appDatabase.imageDao();
    List<List<String>> urlsLists = new ArrayList<>();
//    static AddToDB2 addToDB2 = new AddToDB2("Adder2");
//    static AddToDB2 adder = new AddToDB2();
    Thread adderTh;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display screensize = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screensize.getSize(size);
        width = size.x;
        height = size.y;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            adderTh.destroy();
        }
        catch (Exception e){}
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_four_tab_images, container, false);
        currentPage = inflater.inflate(R.layout.images_list, container, false);

        try {
            url = new URL("https://pixabay.com/api/?key="+API_KEY+"&q="+REQUEST+"&image_type=photo&per_page="+COUNT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Thread thread = new Thread(adder);
//        thread.start();

        new ParseJson("LoadImage").start();

        return root;
    }

    class ImagesListAdapter extends ArrayAdapter<List<String>> {
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
                        System.out.println("TASK TO SAVE IMAGE: "+i);
                        Thread.sleep(10);
                        LoadOrDownloadImageAndSet handler = new LoadOrDownloadImageAndSet(imagesListToShow.get(i), getActivity(), taskImg.get(position).get(i), position, getContext());
                        Thread thread = new Thread(handler);
                        thread.start();
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
            if (internetAccess()) {
                List<String> urls = new ArrayList<>();
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    String inputLine;
                    String json = "";
                    while (true) {
                        if ((inputLine = br.readLine()) == null) break;
                        json += inputLine;
                    }

                    String S[] = json.split(imageUrlTarget);
                    for (String str : S) {
                        if (str.substring(0, 4).equals("http")) {
                            urls.add(str.split("\",\"")[0]);
                        }
                    }

                    try {
                        urlsLists.clear();
                    } catch (Exception e) {}

                    for (String currentUrl : urls) {
                        if (urlsLists != null) {
                            if (urlsLists.size() == 0) {
                                List<String> tempImageList = new ArrayList<>();
                                urlsLists.add(tempImageList);
                            }
                            if (urlsLists.get(urlsLists.size() - 1).size() >= 8) {
                                List<String> tempImageList = new ArrayList<>();
                                tempImageList.add(currentUrl);
                                urlsLists.add(tempImageList);
                            } else {
                                urlsLists.get(urlsLists.size() - 1).add(currentUrl);
                            }
                        }
                    }

                    br.close();

                    listView = root.findViewById(R.id.imagesList);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (urlsLists != null) {
                                adapter = new ImagesListAdapter(getActivity(), R.layout.images_list, urlsLists, getActivity());

                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                List<ImageEntities> imageEntities = imageDao.getAll();

                for (ImageEntities currentEntity : imageEntities) {
                    if (urlsLists != null) {
                        if (urlsLists.size() == 0) {
                            List<String> tempImageList = new ArrayList<>();
                            urlsLists.add(tempImageList);
                        }
                        if (urlsLists.get(urlsLists.size() - 1).size() >= 8) {
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(currentEntity.getUrl());
                            urlsLists.add(tempImageList);
                        } else {
                            urlsLists.get(urlsLists.size() - 1).add(currentEntity.getUrl());
                        }
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();

                        if (urlsLists != null) {
                            listView = root.findViewById(R.id.imagesList);

                            adapter = new ImagesListAdapter(getActivity(), R.layout.images_list, urlsLists, getActivity());

                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
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
            e.printStackTrace();
        }
        return false;
    }

    public static class LoadOrDownloadImageAndSet implements Runnable {
        protected ImageView imageView;
        protected Activity uiActivity;
        protected String imageUrl;
        protected Context context;
        protected int position;

        public LoadOrDownloadImageAndSet(ImageView imageView, Activity uiActivity, String imageUrl, int position, Context context) {
            this.imageView = imageView;
            this.uiActivity = uiActivity;
            this.imageUrl = imageUrl;
            this.position = position;
            this.context = context;
        }

        public void run() {
            ImageEntities currentImage = new ImageEntities();
            String fileName;

            if (imageUrl.startsWith("http")) {
                List<ImageEntities> daoByUrl = imageDao.getByUrl(imageUrl);
                String cacheDir = context.getCacheDir() + "";

                boolean imageExist = false;
                if (daoByUrl.size() != 0) {
                    String imageCachePath = cacheDir + "/" + daoByUrl.get(0).getFileName();
                    imageExist = new File(imageCachePath).exists();
                }

                if (daoByUrl.size() == 0 | !imageExist) {
                    if (!imageExist & daoByUrl.size()>0)
                        fileName = daoByUrl.get(0).getFileName();
                    else {
                        fileName = "image_" + hashCode() +".png";
                        while (true){
                            if (!(new File(cacheDir + "/" + fileName).exists())) break;
                            fileName = "image_" + hashCode()+".png";
                        }
                    }

                    URL urlDownload;
                    try {
                        urlDownload = new URL(imageUrl);
                        InputStream input = urlDownload.openStream();
                        try {
                            OutputStream output = new FileOutputStream(cacheDir + "/" + fileName);
                            try {
                                byte[] buffer = new byte[4096];
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
                    if (imageDao.getByUrl(imageUrl).size()==0) {
                        currentImage.url = imageUrl;
                        currentImage.fileName = fileName;
                        imageDao.insert(currentImage);
//                        new AddToDB(imageUrl, fileName)
//                        AddToDB adder = new AddToDB(imageUrl, fileName);
//                        Thread thread = new Thread(adder);
//                        thread.start();
//                        adder.addTask(imageUrl, fileName);
                    }
                }

                try {
                    String imageNameDB = imageDao.getByUrl(imageUrl).get(0).getFileName();

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

//    public static class AddToDB implements Runnable {
//        protected String imageUrl;
//        protected String fileName;
//
//        public AddToDB(String imageUrl, String fileName) {
//            this.imageUrl = imageUrl;
//            this.fileName = fileName;
//        }
//
//        public void run() {
//            try {
//                Thread.sleep((long) Math.random()*200+50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            ImageEntities currentImage = new ImageEntities();
//            currentImage.url = imageUrl;
//            currentImage.fileName = fileName;
//            imageDao.insert(currentImage);
//
//        }
//    }
//
//    static class AddToDB2 implements Runnable {
//        AddToDB2(){
////            super(name);
//        }
//        List<List<String>> tasks = new ArrayList<>();
//        List<String> task = new ArrayList<>();
//
//
//        public void run(){
//            while (true){
//                try {
////                    System.out.println("work.");
//                    Thread.sleep(10);
//                    if (tasks.size()!=0){
//                        task = tasks.remove(0);
//                        System.out.println(">>>>>>TASK: "+task.toString());
//                        if(imageDao.getByUrl(task.get(0)).size()==0){
//                            System.out.println(">>>>TSK ADD PROCESS...");
//                            ImageEntities currentImage = new ImageEntities();
//                            currentImage.url = task.get(0);
//                            currentImage.fileName = task.get(1);
//                            imageDao.insert(currentImage);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void addTask(String imageUrl, String fileName){
//            List<String> temp = new ArrayList<>();
//            temp.add(imageUrl);
//            temp.add(fileName);
//            tasks.add(temp);
//            System.out.println("TASK ADDED: "+imageUrl+"; "+fileName);
//        }
//    }

}
