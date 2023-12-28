package com.example.MHSmanitobahistoricalsites;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.MHSmanitobahistoricalsites.Database.SitePhotos;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SiteImagesAdapter extends RecyclerView.Adapter<SiteImagesAdapter.ViewHolder> {

    List<SitePhotos> sitePhotos;
    ViewPager2 viewPager2;
    Context context;

    public SiteImagesAdapter (List<SitePhotos> sitePhotos, ViewPager2 viewPager2, Context context)
    {
        this.sitePhotos = sitePhotos;
        this.viewPager2 = viewPager2;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.image_holder_layout, parent, false
                ));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SitePhotos currentPhoto = sitePhotos.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.setTvSiteImageInfo(Html.fromHtml(currentPhoto.info, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
        } else {
            holder.setTvSiteImageInfo(Html.fromHtml(currentPhoto.info));
        }
        //((TextView) mainView.findViewById(R.id.tvDescription)).setText(formattedDescription);
        holder.tvSiteImageInfo.setMovementMethod(LinkMovementMethod.getInstance());
        //holder.setTvSiteImageInfo(currentPhoto.info);
        String imageCount = (position + 1) + "/" + getItemCount();
        holder.setTvImageCount(imageCount);

        holder.imageView.setMinimumHeight(currentPhoto.getHeight());
        holder.imageView.setMinimumWidth(currentPhoto.getWidth());
        Picasso.Builder builder = new Picasso.Builder(viewPager2.getContext());
        builder.listener((picasso, uri, e) -> Log.e("Error", "Picasso: Error displaying site photos " + currentPhoto.getPhoto_url() +"\n" + e.getMessage()));
        builder.build().load(currentPhoto.getPhoto_url()).error(R.drawable.baseline_error_outline_50)
                .into(holder.imageView);


        holder.imageView.setOnLongClickListener(v -> {
            try{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentPhoto.getPhoto_url()));
                context.startActivity(browserIntent);
            }
            catch (Exception e)
            {
                Log.e("Error", "SiteImageAdapter: Error launching photo url\n" + e.getMessage());
            }

            return false;
        });
        holder.getImageView().setContentDescription(currentPhoto.getPhoto_url());
        /*Picasso.get().load(currentPhoto.photo_url).error(R.drawable.baseline_error_outline_24)
                .into(holder.imageView);*/
        //holder.setImageView(currentPhoto.photo_url);
        //holder.setTvSiteImageInfo(currentPhoto.info);





    }


    @Override
    public int getItemCount() {
        return sitePhotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvSiteImageInfo;

        TextView tvImageCount;
        //TextView tvEpithet;

        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.ivSiteImage);
            tvSiteImageInfo = view.findViewById(R.id.tvSiteImageInfo);
            tvImageCount = view.findViewById(R.id.tvImageCount);
            //tvEpithet = view.findViewById(R.id.tvDisplayEpithet);
        }
        public void setImageView(String url)
        {
            Picasso.get().load(url). into(imageView);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public  void setTvSiteImageInfo(Spanned info)
        {
            tvSiteImageInfo.setText(info);
        }

        public  void setTvImageCount(String info)
        {
            tvImageCount.setText(info);
        }

    }

}
