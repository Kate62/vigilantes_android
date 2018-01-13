package casarder.vigilantes_androidapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import casarder.vigilantes_androidapp.utils.ParsingUtils;


public class HorarioFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;

    public HorarioFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HorarioFragment newInstance(int sectionNumber) {
        HorarioFragment fragment = new HorarioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.horario_fragment, container, false);


        TextView textDate = (TextView) rootView.findViewById(R.id.dateText);
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        textDate.setText(formattedDate);

        //---apagar
        try {
            createHoursButton(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public void createHoursButton(JSONObject actualRounds) throws JSONException {
        LinearLayout inc_layout = (LinearLayout) rootView.findViewById(R.id.buttonsLinearLayout);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //DEL - getActivity().getIntent().putExtra("round", actualRounds.toString());
        //JSONArray rounds = actualRounds.getJSONArray("rounds");
        /*for (int i = 0; i < rounds.length(); i++) {
            final JSONObject round = new JSONObject(rounds.get(i).toString());
            View buttons_layout = li.inflate(R.layout.button_horario_layout, null);
            buttons_layout.setId(i);


            Button hours = (Button) buttons_layout.findViewById(R.id.bt);
            String h = getTimeRangeFromPath(rounds, i);
            hours.setText(h);

            inc_layout.addView(buttons_layout);

            buttons_layout.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.time_line_dialog);
                    dialog.setTitle("Percurso");

                    try {
                        createLinearLayoutPercurso(dialog, round);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    dialog.show();
                }
            });


        }*/

        for (int i = 0; i < 1; i++) {
            View buttons_layout = li.inflate(R.layout.button_horario_layout, null);
            buttons_layout.setId(i);


            Button hours = (Button) buttons_layout.findViewById(R.id.bt);
            //String h = getTimeRangeFromPath(rounds, i);
            hours.setText("07h00 - 14h30");

            inc_layout.addView(buttons_layout);

            buttons_layout.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.time_line_dialog);
                    dialog.setTitle("Percurso");

                    try {
                        createLinearLayoutPercurso(dialog, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    dialog.show();
                }
            });


        }
    }

    private String getTimeRangeFromPath(JSONArray rounds, int i) throws JSONException {
        JSONObject round = new JSONObject(rounds.get(i).toString());
        JSONArray path = round.getJSONArray("path");
        JSONArray info = path.getJSONArray(0);
        JSONArray info1 = path.getJSONArray(path.length() - 1);
        String si = ParsingUtils.hourToString(info.getInt(0), ":");
        String sf = ParsingUtils.hourToString(info1.getInt(0), ":");
        return String.format("%s - %s", si, sf);
    }

    public void changeDirection(Dialog dialog, String nameComplex, String iniFacility, String fimFacility) {

        TextView submittedNameFacility = (TextView) dialog.findViewById(R.id.title2);
        submittedNameFacility.setText(nameComplex);

        TextView submittedPercurs = (TextView) dialog.findViewById(R.id.designation2);
        submittedPercurs.setText(iniFacility + " ➞ " + fimFacility);

    }

    public void createLinearLayoutPercurso(Dialog dialog, JSONObject round) throws JSONException {

        LinearLayout inc_layout = (LinearLayout) dialog.findViewById(R.id.timeLinesinearLayout);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       /* JSONArray path = round.getJSONArray("path");*/
        JSONArray info = null;
        String init = "";

       /* for (int i = 0; i < path.length(); i++) {*/
        for (int i = 0; i < 8; i++) {

            /*
            info = path.getJSONArray(i);
            if (i == 0) init = info.getString(2);
            View time_layout = li.inflate(R.layout.time_line, null);
            time_layout.setId(i);

            TextView submittedHours = (TextView) time_layout.findViewById(R.id.horas);
            submittedHours.setText(ParsingUtils.hourToString(info.getInt(0), "h"));

            TextView submittedFacility = (TextView) time_layout.findViewById(R.id.nomeFacility);
            submittedFacility.setText(info.getString(2));

            /*TextView submittedDescription = (TextView) time_layout.findViewById(R.id.descricao);
            submittedDescription.setText("description");*/

            /*inc_layout.addView(time_layout);*/


            View time_layout = li.inflate(R.layout.time_line, null);
            time_layout.setId(i);


            TextView submittedHours = (TextView) time_layout.findViewById(R.id.horas);
            submittedHours.setText("0"+(6+i+1)+"h00");

            TextView submittedFacility = (TextView) time_layout.findViewById(R.id.nomeFacility);
            submittedFacility.setText("facility"+(i+1));

            /*TextView submittedDescription = (TextView) time_layout.findViewById(R.id.descricao);
            submittedDescription.setText("description");*/

            inc_layout.addView(time_layout);

        }
        /*changeDirection(dialog, "Complex", init, info.getString(2));*/
        changeDirection(dialog, "Complex1", "Faciity1", "Facility8");
    }

}
