package com.example.timad.poznavacka.activities.lists;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.timad.poznavacka.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import static com.example.timad.poznavacka.activities.lists.CreateListFragment.languageURL;

public class PopActivity extends Activity {

    private static final String TAG = "PopActivity";

    Button DONEButton;
    ProgressBar progressBar;

    LinearLayout layout1;
    ViewGroup viewGroup;
    ScrollView sv;

    static ArrayList<String> userScientificClassification;
    static ArrayList<String> reversedUserScientificClassification;

    static int userParametersCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DONEButton = findViewById(R.id.PopDONEButton);
        progressBar = findViewById(R.id.progressBar2);

        userScientificClassification = new ArrayList<>();
        reversedUserScientificClassification = new ArrayList<>();

        viewGroup = findViewById(R.id.layout_id);
        sv = new ScrollView(this);
        layout1 = new LinearLayout(this);
        layout1.setOrientation(LinearLayout.VERTICAL);
        sv.addView(layout1);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.8));

        //getting the scientific classification selected by the user
        //needs to be added from broad to specific

        final PopActivity.WikiSearchClassification WikiSearchClassification = new PopActivity.WikiSearchClassification(PopActivity.this);
        WikiSearchClassification.execute();
/*
        //REPLACE with classific selected by user
        userScientificClassification.add("Říše");
        userScientificClassification.add("Kmen");
        userScientificClassification.add("Třída");
        userScientificClassification.add("Řád");
        userScientificClassification.add("Čeleď");
        userScientificClassification.add("Rod");
        userScientificClassification.add("Druh");
        userParametersCount = 8; //REPLACE with number of params selected by user + 1
 */

        //DONEButton
        DONEButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                userParametersCount = 1;
                for (int i = 0; i < layout1.getChildCount(); i++) {
                    View nextChild = layout1.getChildAt(i);

                    if (nextChild instanceof CheckBox) {

                        CheckBox check = (CheckBox) nextChild;
                        if (check.isChecked()) {
                            Log.d(TAG, "checbox text to be added = " + check.getText().toString());
                            userScientificClassification.add(check.getText().toString());
                            userParametersCount++;
                        }
                    }
                }
                reversedUserScientificClassification.addAll(userScientificClassification);
                Collections.reverse(reversedUserScientificClassification);
            }
        });
    }

    private static class WikiSearchClassification extends AsyncTask<Void, String, Void> {

        private WeakReference<PopActivity> fragmentWeakReference;
        private boolean checkboxAdded = false;

        WikiSearchClassification(PopActivity context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PopActivity fragment = fragmentWeakReference.get();
            fragment.progressBar.setVisibility(View.VISIBLE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getApplicationContext(), android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            PopActivity fragment = fragmentWeakReference.get();
            Log.d(TAG, "AsyncTask finished");
            fragment.progressBar.setVisibility(View.GONE);
            fragment.progressBar.startAnimation(AnimationUtils.loadAnimation(fragment.getApplicationContext(), android.R.anim.fade_out));
            fragment.viewGroup.removeAllViews();
            fragment.viewGroup.addView(fragment.sv);
            if (!checkboxAdded) {
                Log.d(TAG, "checkboxAdded is false");
                Toast.makeText(fragment, "Check the language", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "checkboxAdded is true");
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PopActivity fragment = fragmentWeakReference.get();
            checkboxAdded = true;

            //trimming the array
            int count = 0;
            for (String value :
                    values) {
                if (value != null) {
                    count++;
                }
            }
            String[] trimmedValues = new String[count];
            int index = 0;
            for (String value :
                    values) {
                if (value != null) {
                    trimmedValues[index++] = value;
                }
            }

            for (String value :
                    trimmedValues) {
                CheckBox ch = new CheckBox(fragment.getApplicationContext());
                ch.setText(value);
                ch.setTextColor(Color.WHITE);
                fragment.layout1.addView(ch);

                Log.d(TAG, "Checkbox " + values[0] + " added");
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PopActivity fragment = fragmentWeakReference.get();
            ArrayList<String> representatives = CreateListFragment.representatives;
            for (String representative :
                    representatives) {
                String searchText = representative.replace(" ", "_");

                Log.d(TAG, "Connecting to site");
                Document doc = null;
                try {
                    doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(searchText, "UTF-8") + "?redirects=true").userAgent("Mozilla").get();
                    Log.d(TAG, "Classification - connected to " + representative);
                } catch (IOException e) {
                    //not connected to internet
                    e.printStackTrace();
                    Log.d(TAG, "Classification - no wiki for " + representative);
                    continue;
                }

                Element infoBox = null;

                if (doc != null && doc.head().hasText()) {
                    boolean redirects = false;
                    try {
                        Elements rawTables = doc.getElementsByTag("table");
                        redirects = true;
                        for (Element table :
                                rawTables) {
                            if (languageURL.equals("en") || languageURL.equals("cs")) {
                                if (table.id().toLowerCase().contains("info")) {
                                    Log.d(TAG, "doesn't contain id info = " + table.id());
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                } else if (table.attr("class").toLowerCase().contains("info")) {
                                    Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                }

                            } else if (languageURL.equals("de")) {

                                if (table.id().toLowerCase().contains("taxo")) {
                                    Log.d(TAG, "doesn't contain id info = " + table.id());
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                } else if (table.attr("class").toLowerCase().contains("taxo")) {
                                    Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                                    infoBox = table.selectFirst("tbody");
                                    redirects = false;
                                    break;
                                }
                            }

                        }

                    } catch (NullPointerException e) {
                        //rozcestník
                        redirects = true;
                        e.printStackTrace();
                    }

                    if (redirects) {
                        //if it is a redirecting site
                        ArrayList redirectedSiteAndTable = redirect_getTable(doc);
                        if (redirectedSiteAndTable.get(2).equals(false)) { //if new site is not found
                            continue;
                        } else {
                            infoBox = (Element) redirectedSiteAndTable.get(1);
                        }
                    }

                    String[] newData;
                    int classificationPointer;
                    //harvesting the infoBox
                    if (infoBox != null) {
                        ArrayList harvestedInfoBox = harvestClassification(infoBox);
                        newData = (String[]) harvestedInfoBox.get(0);
                        classificationPointer = (int) harvestedInfoBox.get(1);
                    } else {
                        continue;
                    }
                    publishProgress(newData);
                    break;
                }

                //for russian sites
/*
                for (Element tr : trs) {
                    Log.d(TAG, "foring in russian style trs :))");
                    if (tr.getElementsByAttribute("class").first().val().equals("")) { //detected tr for classification
                        Log.d(TAG, "whole text = " + tr.wholeText());
                        Elements elementsOfText = tr.child(1).children();
                        for (Element textElement :
                                elementsOfText) {
                            String text = textElement.wholeText();
                            Log.d(TAG, "russian text = " + text);
                        }


                        return null;
                    }
                }
*/

            }


            return null;
        }

        private ArrayList redirect_getTable(Document doc) {
            ArrayList returnDocAndInfobox = new ArrayList();
            Element infoBox = null;

            Log.d(TAG, "ROZCESTNÍK");
            Elements linkElements = doc.getElementsByAttributeValue("rel", "mw:WikiLink");
            boolean newSiteFound = false;
            for (Element linkElement :
                    linkElements) {
                if (!linkElement.hasClass("new")) {
                    newSiteFound = true;
                    //found element with the link
                    String newWikipediaURLsuffix = linkElement.attr("href");
                    String newSearchText = newWikipediaURLsuffix.substring(2);
                    try {
                        Log.d(TAG, "redirected and connecting to new wikipedia for " + newSearchText);
                        doc = Jsoup.connect("https://" + languageURL + ".wikipedia.org/api/rest_v1/page/html/" + URLEncoder.encode(newSearchText, "UTF-8") + "?redirect=true").userAgent("Mozilla").get();

                        try {
                            Elements rawTables = doc.getElementsByTag("table");
                            for (Element table :
                                    rawTables) {
                                //cz and en infoTable
                                if (languageURL.equals("en") || languageURL.equals("cs")) {
                                    if (table.id().contains("info")) {
                                        Log.d(TAG, "doesn't contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("info")) {
                                        Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    }
                                    //de infoTable
                                } else if (languageURL.equals("de")) {

                                    if (table.id().contains("taxo") || table.id().contains("Taxo")) {
                                        Log.d(TAG, "doesn't contain id info = " + table.id());
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    } else if (table.attr("class").contains("taxo")) {
                                        Log.d(TAG, "doesn't contain class info = " + table.attr("class"));
                                        infoBox = table.selectFirst("tbody");
                                        break;
                                    }
                                }

                            }

                        } catch (NullPointerException e) {
                            //rozcestník
                            e.printStackTrace();
                            Log.d(TAG, "no wiki (redirect)");
                        }

                    } catch (IOException er) {
                        //this probably won't happen
                        er.printStackTrace();
                        Log.d(TAG, "no wiki (redirect)");
                        newSiteFound = false;
                    }
                    break;
                }
            }
            returnDocAndInfobox.add(doc);
            returnDocAndInfobox.add(infoBox);
            returnDocAndInfobox.add(newSiteFound);
            return returnDocAndInfobox;
        }

        private ArrayList<String> harvestClassification(Element infoBox) {

            ArrayList returnList = new ArrayList();

            String[] newData = new String[50];
            int classificationPointer = 0;

            String[] dataPair;
            int trCounter = 0;


            //Element infoBox = doc.getElementsByTag("table").first().selectFirst("tbody");
            Elements trs = infoBox.select("tr");
            for (Element tr :
                    trs) {

                trCounter++;
                Log.d(TAG, "current tr = " + trCounter);
                if (!tr.getAllElements().hasAttr("colspan")) {
                    dataPair = tr.wholeText().split("\n", 2);
                    if (dataPair.length == 1) { //detected wrong table
                        Log.d(TAG, "different table");
                        break;
                    }
                    for (int i = 0; i < 2; i++) {
                        dataPair[i] = dataPair[i].trim();
                        Log.d(TAG, "found " + dataPair[i]);
                    }

                    Log.d(TAG, "classPointer = " + classificationPointer);

                    newData[classificationPointer] = dataPair[0];
                    classificationPointer++;
                    //newData.add(dataPair[0]);
                    Log.d(TAG, "adding new data to newData = " + dataPair[1]);
                }
            }

            returnList.add(newData);
            returnList.add(classificationPointer);
            return returnList;
        }
    }


}
