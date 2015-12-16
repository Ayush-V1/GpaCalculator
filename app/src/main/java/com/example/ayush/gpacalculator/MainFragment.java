package com.example.ayush.gpacalculator;

/**
 * Created by Ayush on 11/25/2015.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;


public class MainFragment extends Fragment {
    private final String TAG = "MainFragment";

    private ArrayList<String> listItems;
    private ArrayAdapter<String> mGPAListAdapter;
    private LinkedList<Course> courses;


    public MainFragment() {
        courses = new LinkedList<Course>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        final Spinner gpaWeightSpinner = (Spinner) rootView.findViewById(R.id.weight);
        final Spinner gpaPercentageSpinner = (Spinner) rootView.findViewById(R.id.percentage);
        final TextView courseCodeTextView = (TextView) rootView.findViewById(R.id.editText);
        final TextView gpaTextView = (TextView) rootView.findViewById(R.id.GPA_text_view);
        final ListView myCoursesListView = (ListView) rootView.findViewById(R.id.inputted_grades);


        setSpinners(rootView);


        listItems = new ArrayList<String>();
        mGPAListAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_layout,
                R.id.list_item_layout_gpa,
                listItems);
        myCoursesListView.setAdapter(mGPAListAdapter);


        Button submitButton = (Button) rootView.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String courseCode = courseCodeTextView.getText().toString().toUpperCase();
                double gpaWeight = Double.parseDouble(gpaWeightSpinner.getSelectedItem().toString()) * 2;
                String gpaPercentage = gpaPercentageSpinner.getSelectedItem().toString();
                double gpaValue = findGpaValue(gpaPercentage);

                if (courseCode.equals("")) {
                    displayToast("Enter course code");
                    return;
                }

                if (gpaPercentage.equals("CR/NCR"))
                    courses.add(new Course(courseCode, gpaWeight, gpaValue, true));
                else
                    courses.add(new Course(courseCode, gpaWeight, gpaValue, false));

                gpaTextView.setText("GPA: " + String.valueOf(updatedGpa()));


                listItems.add(courseCode);
                mGPAListAdapter.notifyDataSetChanged();


                courseCodeTextView.setText("");

                for (String temp : listItems)
                    Log.i(TAG, "INDEX: " + listItems.indexOf(temp) + " ITEM: " + temp);

            }
        });

                SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        myCoursesListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    try {
                                        Log.i(TAG, "ListView Item Position: " + position);
                                        Log.i(TAG, "ListView Item: " + mGPAListAdapter.getItem(position));
                                        mGPAListAdapter.remove(mGPAListAdapter.getItem(position));
                                        courses.remove(position);
                                    } catch (Exception e) {
                                        Log.i(TAG, "HAHA, Avoided the crash!");
                                        Log.i(TAG, e.toString());
                                    }
                                }
                                mGPAListAdapter.notifyDataSetChanged();
                                gpaTextView.setText("GPA: " + String.valueOf(updatedGpa()));
                            }
                        });
        myCoursesListView.setOnTouchListener(touchListener);
        myCoursesListView.setOnScrollListener(touchListener.makeScrollListener());

        myCoursesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "ListView Position: " + position);
                Intent intent = new Intent(getActivity(), CourseDialogActivity.class);
                intent.putExtra("Course", courses.get(position));
                startActivity(intent);

            }
        });


        return rootView;
    }

    public void setSpinners(View rootView) {
        Spinner percentageSpinner = (Spinner) rootView.findViewById(R.id.percentage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.percentage_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        percentageSpinner.setAdapter(adapter);

        Spinner weightSpinner = (Spinner) rootView.findViewById(R.id.weight);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.weight_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(adapter);

    }

    public double findGpaValue(String givenPercentage) {
        double[] gpaArray = {4.0, 4.0, 3.7, 3.3, 3.0, 2.7, 2.3, 2.0, 1.7, 1.3, 1.0, 0.7, 0.0};
        String[] percentageArray = {"90–100", "85–89", "80–84", "77–79", "73–76", "70–72", "67–69", "63–66", "60–62", "57–59", "53–56", "50–52", "0–49"};

        if (givenPercentage.equals("CR/NCR"))
            return -1;

        for (int index = 0; index < gpaArray.length; index++)
            if (givenPercentage.equals(percentageArray[index]))
                return gpaArray[index];


        return -1;
    }


    public double updatedGpa() {
        for (Course course : courses) {
            Log.i("TAG", "course: " + course.getCourseCode() + " GPA: " + course.getGpaValue() + " weight: " + course.getGpaWeight());
        }
        double totalGpa = 0;
        int totalWeight = 0;
        for (Course course : courses) {
            if (course.getGpaWeight() == 2 && !course.isCreditNoCredit()) {
                totalGpa += course.getGpaValue() * 2;
                totalWeight += 2;
            } else if (course.getGpaWeight() == 1 && !course.isCreditNoCredit()) {
                totalGpa += course.getGpaValue();
                totalWeight++;
            }
        }
        Log.i("TAG", "Total GPA: " + totalGpa);
        Log.i("TAG", "Total Weight: " + totalWeight);
        Log.i("TAG", "---------");

        if (totalWeight > 0)
            return Math.round((totalGpa / totalWeight) * 100.0) / 100.0;
        return 0;
    }

    public void displayToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
