package com.example.bookez.ui.seller_profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookez.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SellerProfileFragment extends Fragment {

    private EditText ufirstname,ulastname,uphno_owner,ushopname,ugstno,uaddress,upincode,uphno_company,verifyemail;
    private String semail,sfirstname,slastname,saddress,spincode,userid,sphno_owner,sphno_company,sshopname,sgstno;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_seller_profile, container, false);

        //Edittext
        ufirstname = root.findViewById(R.id.ufirstname);
        ulastname = root.findViewById(R.id.ulastname);
        uphno_owner = root.findViewById(R.id.uphno_owner);
        uphno_company = root.findViewById(R.id.uphno_company);
        uaddress = root.findViewById(R.id.uaddress);
        upincode = root.findViewById(R.id.upincode);
        ushopname = root.findViewById(R.id.ushopname);
        ugstno = root.findViewById(R.id.ugstno);
        verifyemail =root.findViewById(R.id.verifyemail);
        //Button
        Button updateButton = root.findViewById(R.id.updateButton);

        ugstno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        userid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("Users").document(userid);
        typeref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    semail=documentSnapshot.getString("email");
                    sfirstname=documentSnapshot.getString("firstname");
                    slastname=documentSnapshot.getString("lastname");
                    sphno_owner=documentSnapshot.getString("phno");
                    sphno_company=documentSnapshot.getString("phno_company");
                    saddress=documentSnapshot.getString("address");
                    spincode=documentSnapshot.getString("pincode");
                    sshopname=documentSnapshot.getString("shop");
                    sgstno=documentSnapshot.getString("gstno");

                    ufirstname.setText(sfirstname);
                    ulastname.setText(slastname);
                    uphno_owner.setText(sphno_owner);
                    uphno_company.setText(sphno_company);
                    uaddress.setText(saddress);
                    upincode.setText(spincode);
                    ushopname.setText(sshopname);
                    ugstno.setText(sgstno);
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrive the updated strings
                String updatefn= ufirstname.getText().toString().trim();
                String updateln= ulastname.getText().toString().trim();
                String updatephnoown= uphno_owner.getText().toString().trim();
                String updatephnocom= uphno_company.getText().toString().trim();
                String updateadd= uaddress.getText().toString().trim();
                String updatepc= upincode.getText().toString().trim();
                String updatesn= ushopname.getText().toString().trim();
                String verifythis = verifyemail.getText().toString().trim();
                //cant update gstno

                //Check for empty input
                if(TextUtils.isEmpty(verifythis)){
                    verifyemail.setBackgroundColor(getResources().getColor(R.color.red));
                    Toast.makeText(getActivity(),"Enter your correct email to save changes",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(updatefn)){
                    Toast.makeText(getActivity(),"Please enter your First Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(updateln)){
                    Toast.makeText(getActivity(),"Please enter your Last Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(updateadd)){
                    Toast.makeText(getActivity(),"Please enter your address",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(updatepc)){
                    Toast.makeText(getActivity(),"Please enter your pincode",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(updatephnoown)){
                    Toast.makeText(getActivity(),"Please enter your phone number",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(updatephnocom)){
                    Toast.makeText(getActivity(),"Please enter your company phone number",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(updatesn)){
                    Toast.makeText(getActivity(),"Please enter your Shop Name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(updatephnocom.length()!=10){
                    uphno_company.setError("Number must be of 10 digits");
                    return;
                }
                if(updatephnoown.length()!=10){
                    uphno_owner.setError("Number must be of 10 digits");
                    return;
                }
                if(updatepc.length()!=6){
                    upincode.setError("Pincode must be of 6 digits");
                    return;
                }
                else{
                    if(verifythis.equals(semail)) {
                        UpdateAccountInfo(updatefn,updateln,updatephnoown,updatephnocom,updateadd,updatepc,updatesn,sgstno,semail,userid);
                    }else{
                        Toast.makeText(getActivity(),"Enter correct email",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return root;
    }

    private void UpdateAccountInfo(String updatefn, String updateln, String updatephnoown, String updatephnocom, String updateadd, String updatepc, String updatesn, String sgstno, String semail, String userid) {

        Map<String, Object> user = new HashMap<>();
        user.put("userid", userid);
        user.put("email", semail);
        user.put("firstname", updatefn);
        user.put("lastname", updateln);
        user.put("phno", updatephnoown);
        user.put("address", updateadd);
        user.put("pincode", updatepc);
        user.put("phno_company", updatephnocom);
        user.put("shop", updatesn);
        user.put("gstno", sgstno);
        user.put("usertype", "Seller");

        db.collection("Users").document(userid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getActivity(),"Information updated successfully", Toast.LENGTH_LONG).show();

                } else{
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
