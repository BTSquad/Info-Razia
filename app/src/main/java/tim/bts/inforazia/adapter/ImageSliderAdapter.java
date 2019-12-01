package tim.bts.inforazia.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import tim.bts.inforazia.R;
import tim.bts.inforazia.model.DataUpload_model;
import tim.bts.inforazia.model.Upload_model;
import tim.bts.inforazia.model.Users_model;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH> {

    Context mContext;
    List<DataUpload_model> mDataUpload;
    List<Upload_model> mUploadModel;


    public ImageSliderAdapter(Context mContext, List<DataUpload_model> mDataUpload, List<Upload_model> mUploadModel) {
        this.mContext = mContext;
        this.mDataUpload = mDataUpload;
        this.mUploadModel = mUploadModel;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_detail, null);
        return new SliderAdapterVH(inflate);

    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

        final Upload_model upload_model = mUploadModel.get(position);

        Picasso.get()
                .load(upload_model.getmImageUrl())
                .fit()
                .centerCrop()
                .into(viewHolder.imageViewBackground);

    }

    @Override
    public int getCount() {
        return mUploadModel.size();
    }

 class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterVH(View itemView) {
            super(itemView);

            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;


        }
    }
}
