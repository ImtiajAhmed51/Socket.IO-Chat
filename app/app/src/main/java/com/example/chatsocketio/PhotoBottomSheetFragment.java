package com.example.chatsocketio;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatsocketio.adapter.GalleryCustomAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;
public class PhotoBottomSheetFragment extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private TextView countPicture;
    private TextView messagePicture;
    private List<String> mList;
    private GalleryCustomAdapter galleryCustomAdapter;
    //private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View rootView;
    private Context mContext;
    private ImageView adminPanel;
    private OnImageClickListener onImageClickListener;
    private GridLayoutManager manager;

    public PhotoBottomSheetFragment(List<String> mList, OnImageClickListener onImageClickListener, Context mContext) {
        this.mList = mList;
        this.onImageClickListener=onImageClickListener;
        this.mContext=mContext;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        rootView=inflater.inflate(R.layout.dialog_gallery,container,false);
    return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        galleryCustomAdapter=new GalleryCustomAdapter(mList,mContext,onImageClickListener);
        recyclerView=view.findViewById(R.id.recyclerViewId);
        recyclerView.setNestedScrollingEnabled(false);
        countPicture=view.findViewById(R.id.countPicture);
        adminPanel=view.findViewById(R.id.adminPanel);
        messagePicture=view.findViewById(R.id.messagePicture);
       // recyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(galleryCustomAdapter);
        galleryCustomAdapter.notifyItemRangeChanged(0,mList.size());



//        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.bottomSheetLayout);
//        final BottomSheetBehavior behavior = BottomSheetBehavior.from(recyclerView);
//        if(behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        } else {
//            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        }
//        recyclerView.scrollToPosition(0);
//        galleryCustomAdapter.notifyItemInserted(0);
  //        bottomSheetBehavior=BottomSheetBehavior.from((View) view.getParent());
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        CoordinatorLayout layout=view.findViewById(R.id.bottomSheetLayout);
//        assert layout!=null;
//        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        adminPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        if(mList.size()!=0){
            messagePicture.setVisibility(View.GONE);
            countPicture.setText("Photos: "+String.valueOf(mList.size()));

        }else{
            messagePicture.setVisibility(View.VISIBLE);
            messagePicture.setText("Do not have any picture in your phone.");
        }
    }
}
