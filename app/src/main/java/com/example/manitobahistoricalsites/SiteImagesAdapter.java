package com.example.manitobahistoricalsites;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.manitobahistoricalsites.Database.SitePhotos;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class SiteImagesAdapter extends RecyclerView.Adapter<SiteImagesAdapter.ViewHolder> {

    List<SitePhotos> sitePhotos;
    ViewPager2 viewPager2;

    public SiteImagesAdapter (List<SitePhotos> sitePhotos, ViewPager2 viewPager2)
    {
        this.sitePhotos = sitePhotos;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.image_holder_layout, parent, false
                ) );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SitePhotos currentPhoto = sitePhotos.get(position);
        holder.tvSiteImageInfo.setText(currentPhoto.info);

        Picasso.Builder builder = new Picasso.Builder(viewPager2.getContext());
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                Log.e("Error", "Picasso: Error displaying site photos " + currentPhoto.getPhoto_url() +"\n" + e.getMessage());
            }
        });
        builder.build().load(currentPhoto.photo_url).error(R.drawable.baseline_error_outline_24)
                .into(holder.imageView);
        /*Picasso.get().load(currentPhoto.photo_url).error(R.drawable.baseline_error_outline_24)
                .into(holder.imageView);*/
        //holder.setImageView(currentPhoto.photo_url);
        //holder.setTvSiteImageInfo(currentPhoto.info);




    }

    @Override
    public int getItemCount() {
        return sitePhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvSiteImageInfo;
        //TextView tvEpithet;

        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.ivSiteImage);
            tvSiteImageInfo = view.findViewById(R.id.tvSiteImageInfo);
            //tvEpithet = view.findViewById(R.id.tvDisplayEpithet);
        }
        public void setImageView(String url)
        {
            Picasso.get().load(url). into(imageView);
        }

        public  void setTvSiteImageInfo(String info)
        {
            tvSiteImageInfo.setText(info);
        }



    }

}
