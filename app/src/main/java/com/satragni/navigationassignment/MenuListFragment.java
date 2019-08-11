package com.satragni.navigationassignment;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.satragni.navigationassignment.Utils.CircleTransform;
import com.satragni.navigationassignment.Utils.DatabaseHelper;
import com.satragni.navigationassignment.Utils.MyBounceInterpolator;
import com.satragni.navigationassignment.Utils.Params;
import com.squareup.picasso.Picasso;

import com.fivehundredpx.android.blur.BlurringView;
/**
 * A simple {@link Fragment} subclass.
 */
public class MenuListFragment extends Fragment {



    private ImageView ivMenuUserProfilePhoto;
    TextView textView;
    Animation myBounceAnim;
    MyBounceInterpolator interpolator;
    FrameLayout frameLayout;

    private GoogleMap googleMap;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }



    public MenuListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_list,container,false);
        ivMenuUserProfilePhoto = (ImageView)view.findViewById(R.id.ivMenuUserProfilePhoto);
        textView = (TextView)view.findViewById(R.id.Name_tv);
        frameLayout = (FrameLayout)view.findViewById(R.id.headerFrame);
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.vNavigation);
//        initialize navigation header image
        Picasso.with(getActivity())
                .load("https://picsum.photos/200/?random")
                .placeholder(R.mipmap.ic_launcher)
                .resize(64,64)
                .centerCrop()
                .transform(new CircleTransform())
                .into(ivMenuUserProfilePhoto);

        myBounceAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        interpolator = new MyBounceInterpolator(0.2, 20);
        myBounceAnim.setInterpolator(interpolator);




        frameLayout.setOnClickListener(textViewListener);

        String name =  DatabaseHelper.getAccountDetails(getActivity(),Params.name);
        if(name.equalsIgnoreCase("NOTFOUND")){
            textView.setText("Name");
        }else{
            textView.setText(name.trim());
        }


        //setting the navigation behaviour on menu item click
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getTitle().toString().toLowerCase()){
                    case "about":
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/satragni-sarkar-71892396/"));
                        startActivity(browserIntent);
                        displayToast("Redirecting you to LikendIn.");
                        break;
                    case "satellite map":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        displayToast("Changing map to "+item.getTitle().toString());
                        break;
                    case "hybrid map":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        displayToast("Changing map to "+item.getTitle().toString());
                        break;
                    case "terrain map":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        displayToast("Changing map to "+item.getTitle().toString());
                        break;
                    case "normal map":
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        displayToast("Changing map to "+item.getTitle().toString());
                        break;
                }
                return false;
            }
        });
        return view;
    }

    View.OnClickListener textViewListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shopPopup();
        }
    };


//popup for profile edit
    private void shopPopup() {
        final Dialog popupdialog = new Dialog(getActivity(), R.style.ProfileDialogAnimation);
        popupdialog.setContentView(R.layout.profile_popup);
        popupdialog.findViewById(R.id.popup_container);
        final TextInputLayout textInputLayout = (TextInputLayout) popupdialog.findViewById(R.id.nameETContainer);
        BlurringView blurringView = new BlurringView(getActivity());
        blurringView.setBlurredView(popupdialog.findViewById(R.id.popup_container));
        blurringView.setBlurRadius(16);
        blurringView.setElevation(4);
        blurringView.setAlpha(0.8f);
        Button saveBTN = (Button)popupdialog.findViewById(R.id.selectCityBtn);
        final EditText nameET = (EditText)popupdialog.findViewById(R.id.nameET);
        ImageView imageView  = (ImageView)popupdialog.findViewById(R.id.cityView);
        Picasso.with(getActivity()).load("https://picsum.photos/200/?random").into(imageView);
        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }@Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameET.setError(null);
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }@Override
            public void afterTextChanged(Editable s) {
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameET.getText().toString().isEmpty()){
                    textInputLayout.setError("Cannot be empty.");
                    textInputLayout.setErrorEnabled(true);
                    Animation hintTextTransition = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                    textInputLayout.startAnimation(hintTextTransition);
                }else{
                    DatabaseHelper.setAccountDetails(getActivity(), Params.name,nameET.getText().toString().trim());
                    textView.setText("");
                    displayToast("Data saved.");
                    textView.setText(DatabaseHelper.getAccountDetails(getActivity(),Params.name).trim());
                    popupdialog.dismiss();
                    popupdialog.cancel();
                }
            }
        });
        popupdialog.setCanceledOnTouchOutside(true);
        popupdialog.setCancelable(true);
        popupdialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        String name =  DatabaseHelper.getAccountDetails(getActivity(),Params.name);
        myBounceAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        interpolator = new MyBounceInterpolator(0.2, 20);
        myBounceAnim.setInterpolator(interpolator);
        if(name.equalsIgnoreCase("NOTFOUND")){
            textView.setText("Name");
        }else{
            textView.setText(name.trim());
        }
    }

    private void displayToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

}
