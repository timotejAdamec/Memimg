package com.adamec.timotej.poznavacka;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adamec.timotej.poznavacka.activities.lists.MyListsActivity;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

//ad holder
class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {

    private UnifiedNativeAdView adView;

    public UnifiedNativeAdView getAdView() {
        return adView;
    }

    UnifiedNativeAdViewHolder(View view) {
        super(view);
        adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
    }
}

/**
 * Karti??ka s informacemi o pozn??va??ce a tla????tky k proveden?? akc?? s pozn??va??kou.
 */
class PoznavackaInfoViewHolder extends RecyclerView.ViewHolder {
    public ContentLoadingProgressBar progressBar;
    public TextView textView1;
    public TextView textView2;
    public TextView languageURL;
    public ImageView practiceImg;
    public ImageView shareImg;
    public ImageView deleteImg;
    public ImageView prewiewImg;
    public ImageView testImg;
    public CardView cView;

    /**
     * Konstruktor - inicializuje prom??nn?? a Event Listenery.
     *
     * @param itemView
     * @param listener
     */
    PoznavackaInfoViewHolder(@NonNull View itemView, final RWAdapter.OnItemClickListener listener) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.item_loading_progressBar);
        textView1 = itemView.findViewById(R.id.itemText1);
        textView2 = itemView.findViewById(R.id.itemText2);
        languageURL = itemView.findViewById(R.id.languageURL);
        practiceImg = itemView.findViewById(R.id.img_practice);
        shareImg = itemView.findViewById(R.id.img_share);
        deleteImg = itemView.findViewById(R.id.img_delete);
        prewiewImg = itemView.findViewById(R.id.img_prewiew);
        testImg = itemView.findViewById(R.id.img_test); //TODO EXAM
        cView = itemView.findViewById(R.id.cardView1);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });

        practiceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setEnabled(true);
                        progressBar.animate();
                        progressBar.show();
                        listener.onPracticeClick(position);
                    }
                }
            }
        });

        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onShareClick(position);
                    }
                }
            }
        });

        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            }
        });

        //TODO EXAM
        testImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onTestClick(position);
                    }
                }
            }
        });
    }
}

/**
 * ????d?? Recycler View
 **/
public class RWAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Object> mPoznavackaInfoList;
    private OnItemClickListener mListener;
    private Context mContext;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The unified native ad view type.
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = mPoznavackaInfoList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onPracticeClick(int position);

        void onShareClick(int position);

        void onDeleteClick(int position);

        void onTestClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public RWAdapter(ArrayList<Object> poznavackaInfoList, Context context) {
        mPoznavackaInfoList = poznavackaInfoList;
        mContext = context;
    }

    /**
     * Vytvo??en?? karti??ky v Recycler View
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.plant(new Timber.DebugTree());
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_unified, parent, false);
                return new UnifiedNativeAdViewHolder(v);
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.poznavacka_info, parent, false);
        PoznavackaInfoViewHolder pivh = new PoznavackaInfoViewHolder(v, mListener);
        return pivh;
    }

    /**
     * P??i??azen?? objektu ke karti??ce.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            int viewType = getItemViewType(position);

            switch (viewType) {
                case UNIFIED_NATIVE_AD_VIEW_TYPE:
                    UnifiedNativeAd nativeAd = (UnifiedNativeAd) mPoznavackaInfoList.get(position);
                    populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                    break;

                case MENU_ITEM_VIEW_TYPE:
                    //fall through
                default:
                    PoznavackaInfoViewHolder pivh = (PoznavackaInfoViewHolder) holder;
                    PoznavackaInfo currentPoznavackaInfo = (PoznavackaInfo) mPoznavackaInfoList.get(position);
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(pivh.textView1, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(pivh.textView2, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);

                    pivh.textView1.setText(currentPoznavackaInfo.getName());
                    pivh.textView2.setText(currentPoznavackaInfo.getAuthor());
                    pivh.languageURL.setText(currentPoznavackaInfo.getLanguageURL());
                    pivh.testImg.setImageResource(R.drawable.ic_school_dark_purple_24dp); //TODO EXAM


                    Drawable d = MyListsActivity.getSMC(pivh.prewiewImg.getContext()).readDrawable(currentPoznavackaInfo.getId() + "/", currentPoznavackaInfo.getPrewievImageLocation(), ((PoznavackaInfoViewHolder) holder).prewiewImg.getContext());
                    pivh.prewiewImg.setImageDrawable(d);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    /*
                    if (!user.getUid().equals(currentPoznavackaInfo.getAuthorsID())) {
                        pivh.shareImg.setEnabled(false);
                    } else {
                        if (currentPoznavackaInfo.isUploaded()) {
                            pivh.shareImg.setImageResource(R.drawable.ic_share_blue_24dp);
                        } else {
                            pivh.shareImg.setImageResource(R.drawable.ic_share_dark_purple_24dp);
                        }
                    }*/
                    /*if (user.getUid().equals(currentPoznavackaInfo.getAuthorsID())
                            || doesExistInFirestore(currentPoznavackaInfo)) {*/

                    pivh.shareImg.setImageResource(R.drawable.ic_share_dark_purple_24dp);
                    /*if (user.getUid().equals(currentPoznavackaInfo.getAuthorsID())) {
                        pivh.shareImg.setImageResource(R.drawable.ic_share_dark_purple_24dp);
                    } else if (currentPoznavackaInfo.isUploaded()) {
                        pivh.shareImg.setImageResource(R.drawable.ic_share_dark_purple_24dp);
                    } else {
                        pivh.shareImg.setEnabled(false);
                    }

                    //TODO move this to welcome?
                    if (currentPoznavackaInfo.isUploaded() && !user.getUid().equals(currentPoznavackaInfo.getAuthorsID())) {
                        if (SharedListsActivity.checkInternet(mContext)) {
                            if (!doesExistInFirestore(currentPoznavackaInfo)) {
                                currentPoznavackaInfo.setUploaded(false);
                                pivh.shareImg.setEnabled(false);
                                notifyItemChanged(mPoznavackaInfoList.indexOf(currentPoznavackaInfo));
                            }
                        }
                    }*/


                    if (MyListsActivity.sPositionOfActivePoznavackaInfo == position) {
                        //selected
                        pivh.cView.setCardBackgroundColor(pivh.prewiewImg.getResources().getColor(R.color.colorAccentSecond));
                    } else {
                        //not selected
                        pivh.cView.setCardBackgroundColor(pivh.prewiewImg.getResources().getColor(R.color.colorAccentSecond));
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean doesExistInFirestore(PoznavackaInfo currentPoznavackaInfo) {
        //return false;
        final boolean[] success = {false};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            DocumentReference poznavackaReference = db.collection("Users").document(currentPoznavackaInfo.getAuthorsID()).collection("Poznavacky").document(currentPoznavackaInfo.getId());

            poznavackaReference.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Timber.d("%s exists in firebase, onSuccess()", currentPoznavackaInfo.getName());
                            success[0] = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.d("%s exists in firebase, onFailure()", currentPoznavackaInfo.getName());
                            e.printStackTrace();
                            success[0] = false;
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Timber.d("%s exists in firebase, onComplete", currentPoznavackaInfo.getName());
                            success[0] = task.isSuccessful();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (success[0]) {
            Timber.d("%s exists in firebase = true", currentPoznavackaInfo.getName());
        } else {
            Timber.d("%s exists in firebase = false", currentPoznavackaInfo.getName());
        }
        return success[0];
        /*Future<DocumentSnapshot> future = (Future<DocumentSnapshot>) poznavackaReference.get();

        try {
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                Timber.d("doesExistInFirestore() is true");
                return true;
            } else {
                Timber.d("doesExistInFirestore() is false");
                return false;
            }
        } catch (ExecutionException e) {
            Timber.d("doesExistInFirestore() is exception0");
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            Timber.d("doesExistInFirestore() is exception1");
            e.printStackTrace();
            return false;


            if (poznavackaReference.get().isSuccessful()) {
                Timber.d("doesExistInFirestore() is true");
            } else {
                Timber.d("doesExistInFirestore() is false");
            }
            //return poznavackaReference.get().isSuccessful();
        }*/
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    /**
     * Vrac?? po??et polo??ek v Recycler View
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mPoznavackaInfoList.size();
    }
}
