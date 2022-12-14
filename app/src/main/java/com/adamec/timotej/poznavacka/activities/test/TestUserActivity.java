package com.adamec.timotej.poznavacka.activities.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamec.timotej.poznavacka.AnswerObject;
import com.adamec.timotej.poznavacka.ExamsAdapter;
import com.adamec.timotej.poznavacka.R;
import com.adamec.timotej.poznavacka.ResultObjectDB;
import com.adamec.timotej.poznavacka.Zastupce;
import com.adamec.timotej.poznavacka.activities.lists.MyExamsActivity;
import com.adamec.timotej.poznavacka.activities.lists.MyListsActivity;
import com.adamec.timotej.poznavacka.activities.lists.SharedListsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class TestUserActivity extends AppCompatActivity {
    private Button finishTest;
    private Button next;
    private Button previous;
    private ArrayList<Zastupce> zastupces;
    public static int index;
    private int first;
    private int last;
    public static  int []tempResult;//store results size = zastupces.size()
    public static String[] tempAnswer;
    private int maxResult;//max result = zastupces.size()*params
    private int urResult;//soucet tempResults
    public static int parametrs;
    public static boolean noWikiParams;
    //recyclerview
    private RecyclerView mRecyclerView;
    static private ExamsAdapter mExamsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static ArrayList<AnswerObject> answerObjectArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_user);


        if(SharedListsActivity.checkInternet(this)){
            noWikiParams=true;
            next= findViewById(R.id.next3);
            previous = findViewById(R.id.previous3);
            finishTest = findViewById(R.id.finishTest3);
            loadContent(TestPINFragment.firebaseTestID);
        }
    }

    /**
     * akce proveden?? p??i zm????knut?? zp??te??n??ho tla????tka
     */

    @Override
    public void onBackPressed() {
        if (SharedListsActivity.checkInternet(getApplication())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TestUserActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.ic_warning);
            builder.setMessage("Do you really want to leave test, all progress will be lost!");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
        Intent intent1 = new Intent(getApplication(), MyListsActivity.class);
        startActivity(intent1);
        overridePendingTransition(R.anim.ttlm_tooltip_anim_enter, R.anim.ttlm_tooltip_anim_exit);
        finish();

                    dialog.dismiss();
                }
            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } else {
            Toast.makeText(getApplication(), "reconnect!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * inicializuje test
     * @param documentName
     */
    private void loadContent(final String documentName){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("ActiveTests").document(documentName);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    initializeZastupces(documentSnapshot.getString("content"));
                    index = 0;
                    first = index;
                    if (zastupces.get(0).getParameter(0).isEmpty()) {
                        index++;
                        first = index;
                        noWikiParams=false;
                    }
                    testViewer(index, last, first);
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            index++;
                            testViewer(index, last, first);
                        }
                    });
                    previous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            index--;
                            testViewer(index, last, first);
                        }
                    });
                    finishTest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView result = findViewById(R.id.result3);
                            String finalResult = "";
                            urResult = 0;
                            maxResult = zastupces.get(0).getParameters() * (zastupces.size()-1);
                            if(noWikiParams){
                                maxResult++;
                            }
                            for (int answer : tempResult) {
                                urResult += answer;

                            }
                            Toast.makeText(getApplicationContext(), Arrays.toString(tempResult), Toast.LENGTH_SHORT).show();
                            finalResult = Integer.toString(urResult) + "/" + Integer.toString(maxResult);
                            result.setText(finalResult);
                            final ResultObjectDB item = new ResultObjectDB(FirebaseAuth.getInstance().getCurrentUser().getUid(), result.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            String userID = documentSnapshot.getString("userID");
                            String databaseID = documentSnapshot.getString("testDBID");
                            MyExamsActivity.updateResultsEmpty(userID, databaseID, getApplicationContext());
                            MyExamsActivity.addResultToDB(documentSnapshot.getId(), item, userID, databaseID);
                        }
                    });


                }
            }
            });
    }

    /**
     * vytvo???? pole typu z??stupce
     */
    private void createArr(){
        zastupces=new ArrayList<>();
    }

    /**
     * inicalizuje pole z??stupce
     * @param content
     */
    private void initializeZastupces(String content){
        createArr();
        Gson gson = new Gson();
        Type cType = new TypeToken<ArrayList<Zastupce>>() {
        }.getType();
        zastupces = gson.fromJson(content, cType);
        parametrs = zastupces.get(0).getParameters();
        if(noWikiParams){
            tempResult = new int[(zastupces.size())*zastupces.get(0).getParameters()];
            tempAnswer = new String[(zastupces.size())*zastupces.get(0).getParameters()];
        }else {
            tempResult = new int[(zastupces.size() - 1) * zastupces.get(0).getParameters()];
            tempAnswer = new String[(zastupces.size()-1)*zastupces.get(0).getParameters()];
        }

        last = zastupces.size()-1;
    }

    /**
     * m??n?? str??nky testu
     * @param index
     * @param last
     * @param first
     */
    private void testViewer(int index,int last,int first) {
        if(first!=0){
            setResults(index);
            buildRecyclerView();

        }else{
            setResults2(index);
            buildRecyclerView();
        }
        Zastupce item = zastupces.get(index);
        String imageUrl = item.getImageURL();
        ImageView img = findViewById(R.id.zastupceImage3);
        try {
            Picasso.get().load(imageUrl).resize(500, 500).onlyScaleDown().centerInside().error(R.drawable.ic_image).into(img);
        }catch (Exception e){

        }
        // previous button
        if (first == index) {
            previous.setEnabled(false);
        } else {
            previous.setEnabled(true);
        }
        //finish test button
        if (index == last) {
            finishTest.setEnabled(true);
        } else {
            finishTest.setEnabled(false);
        }
        //next button
        if(index==last){
            next.setEnabled(false);
        }else{
            next.setEnabled(true);
        }

    }

    /**
     * vytvo???? recycler view
     */
    private void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.examView3);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mExamsAdapter = new ExamsAdapter(answerObjectArrayList);
        mRecyclerView.setAdapter(mExamsAdapter);

        mExamsAdapter.notifyDataSetChanged();


    }

    /**
     * vytvo???? pole odpov??d??
     */
    private void createAnswerArr(){
        answerObjectArrayList=new ArrayList<>();
    }

    /**
     * nastav?? v??sledek
     * @param index
     */
    private void setResults(int index){
        createAnswerArr();

        for(int counter=0;counter<zastupces.get(index).getParameters();counter++){

            String fieldName = zastupces.get(0).getParameter(counter);
            String result = zastupces.get(index).getParameter(counter);

            if(counter==0){
                fieldName="N??zev";
            }
            AnswerObject item = new AnswerObject(result,fieldName);
            //AnswerObject item = new AnswerObject("","");
            answerObjectArrayList.add(item);
        }

    }

    /**
     * nastav?? v??sledek
     * @param Index
     */
    private void setResults2(int Index){
        createAnswerArr();
        String fieldName = "N??zev";
        String result = zastupces.get(index).getParameter(0);
        AnswerObject item = new AnswerObject(result,fieldName);
        answerObjectArrayList.add(item);
    }



    /*
     TextView content = findViewById(R.id.content3);
                initializeZastupces(documentSnapshot.getString("content"));
                content.setText(documentSnapshot.getString("content"));
                TextView result = findViewById(R.id.result3);
                result.setText("17/20");
                final ResultObjectDB item =  new ResultObjectDB(FirebaseAuth.getInstance().getCurrentUser().getUid(),result.getText().toString());
                finishTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyExamsActivity.addResultToDB(documentSnapshot.getId(),item);
                    }
                });

     */

}
