package com.example.stackoverflowprofileavatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    Context context;
    private final ArrayList<String> values;
    private final ArrayList<String> numbers;
    private final ArrayList<String> images;

    public ListAdapter(Context context, ArrayList<String> values, ArrayList<String> numbers, ArrayList<String> images){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.values = values;
        this.numbers = numbers;
        this.images = images;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.aVersiontxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText("User Id: " + values.get(position));
        viewHolder.txtDesc.setText(numbers.get(position));
        viewHolder.txtDesc.setTextColor(Color.RED);
        new DownloadImageTask(viewHolder.icon, viewHolder.txtDesc)
                .execute(images.get(position));

        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        TextView txtDesc;
        ImageView icon;

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        TextView textView;

        public DownloadImageTask(ImageView bmImage, TextView textView) {
            this.bmImage = bmImage;
            this.textView = textView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            // High-accuracy landmark detection and face classification
            // We could use this for more high accuracy face detection
            //FaceDetectorOptions highAccuracyOpts =
            //        new FaceDetectorOptions.Builder()
            //                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            //                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            //                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            //                .build();

            // Real-time contour detection
            FaceDetectorOptions realTimeOpts =
                    new FaceDetectorOptions.Builder()
                            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                            .build();
            InputImage image = InputImage.fromBitmap(result, 0);
            FaceDetector detector = FaceDetection.getClient(realTimeOpts);

            Task<List<Face>> taskResult =
                    detector.process(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<Face>>() {
                                        @Override
                                        public void onSuccess(List<Face> faces) {
                                            if (faces.size() > 0) {
                                                textView.setText(
                                                        "Yes, this user's profile photo has a " +
                                                                "human face!!");
                                                textView.setTextColor(Color.BLUE);
                                            } else {
                                                textView.setText(
                                                        "No, this user's profile photo doesn't " +
                                                                "have a human face!!");
                                                textView.setTextColor(Color.RED);
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });
        }
    }
}
