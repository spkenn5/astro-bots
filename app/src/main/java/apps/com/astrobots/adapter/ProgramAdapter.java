package apps.com.astrobots.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import apps.com.astrobots.R;
import apps.com.astrobots.model.Program;

/**
 * Created by kenji on 1/21/17.
 */

public class ProgramAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Program> mDataset;

    public ProgramAdapter(Context mContext, List<Program> mDataset) {
        this.mContext = mContext;
        this.mDataset = mDataset;
    }

    @Override
    public int getCount() {
        return mDataset.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Program prog = mDataset.get(position);


        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.adapter_program_item, null);
        }

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        final TextView tvProgramTitle = (TextView)convertView.findViewById(R.id.tvProgramTitle);
        final TextView tvProgramChannelTitle = (TextView)convertView.findViewById(R.id.tvProgramChannelTitle);
        final TextView tvProgramTime = (TextView) convertView.findViewById(R.id.tvProgramTime);
        final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        tvProgramTitle.setText(prog.getProgramTitle());
        tvProgramChannelTitle.setText(prog.getChannelTitle());
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        SimpleDateFormat format2 = new SimpleDateFormat("hh:mm a");
        Date dt = new Date();
        try{
            dt = format1.parse(prog.getDisplayTime());
        }catch(Exception e){
            Log.e("ExceptionCaught",e.getMessage());
        }
        tvProgramTime.setText(format2.format(dt));
        if(prog.getProgramImage() != null){
            new ImageLoadTask(prog.getProgramImage(),imageView);
        }
        return convertView;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            Log.d("KENBUG","Attempting to download from " + url);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
