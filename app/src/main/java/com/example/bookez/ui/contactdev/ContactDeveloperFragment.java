package com.example.bookez.ui.contactdev;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookez.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ContactDeveloperFragment extends Fragment {
    private String severity;
    private String name;
    private EditText username,problemdesc;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_contact_developer, container, false);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String a = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        Spinner spinner = root.findViewById(R.id.severityspinner);
        username = root.findViewById(R.id.user_name);
        problemdesc = root.findViewById(R.id.problem_description);
        Button sendmail = root.findViewById(R.id.sendmail);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Very Low");
        arrayList.add("Low");
        arrayList.add("Medium");
        arrayList.add("High");
        arrayList.add("Severe");
        arrayList.add("Urgent");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireActivity(),android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        DocumentReference typeref = db.collection("Users").document(a);
        typeref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    name = documentSnapshot.getString("firstname")+" "+documentSnapshot.getString("lastname");
                    username.setText(name);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                severity = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                severity ="Very Low";
            }
        });

        sendmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Store text
                String descrip= problemdesc.getText().toString().trim();

                if(TextUtils.isEmpty(descrip)){
                    problemdesc.setError("Details are required");
                    return;
                }

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("message/rfc822");
                // emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"atharva464@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Report Severity : "+severity+" reported by "+name);
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, descrip);
                startActivity(Intent.createChooser(emailIntent, "Choose an email client"));

            }
        });
        return root;
    }
}

