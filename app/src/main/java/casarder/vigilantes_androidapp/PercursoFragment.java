package casarder.vigilantes_androidapp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import casarder.vigilantes_androidapp.utils.ParsingUtils;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PercursoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PercursoFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private int contId = 0;
    private int count = 0;

    public PercursoFragment() {
        // Required empty public constructor
    }

    public int getContId(){
        return contId;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static PercursoFragment newInstance(int sectionNumber) {
        PercursoFragment fragment = new PercursoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.percurso_fragment, container, false);

         try {
                createLinearLayoutPercurso(null);
             } catch (JSONException e) {
                  e.printStackTrace();
         }


        return rootView;
    }


    public void changeDirection(View rootView, String nameComplex, String iniFacility, String fimFacility) {
        LinearLayout inc_layout = (LinearLayout) rootView.findViewById(R.id.blackBar);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView submittedNameFacility = (TextView) inc_layout.findViewById(R.id.title);
        submittedNameFacility.setText(nameComplex);

        TextView submittedPercurs = (TextView) inc_layout.findViewById(R.id.designation);
        submittedPercurs.setText(iniFacility + " ➞ " + fimFacility);

    }

    public void createLinearLayoutPercurso(JSONObject actualRounds) throws JSONException {
        contId++;
        LinearLayout inc_layout = (LinearLayout) rootView.findViewById(R.id.mainLinearLayout);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        JSONArray info = null;
        String init = "";
        boolean in = false;

        //JSONArray rounds = actualRounds.getJSONArray("rounds");
        /*for (int i = 0; i < rounds.length(); i++) {*/
        for (int i = 0; i < 1; i++) {

           /* if (getTimeRangeFromPath(rounds, i)) {
                in = true;
                JSONObject round = new JSONObject(rounds.get(i).toString());
                JSONArray path = round.getJSONArray("path");

                for (int j = 0; j < path.length(); j++) {
                    info = path.getJSONArray(j);
                    if (i == 0) init = info.getString(2);
                    View time_layout = li.inflate(R.layout.time_line, null);
                    time_layout.setId(contId);

                    TextView submittedHours = (TextView) time_layout.findViewById(R.id.horas);
                    submittedHours.setText(ParsingUtils.hourToString(info.getInt(0), "h"));

                    TextView submittedFacility = (TextView) time_layout.findViewById(R.id.nomeFacility);
                    submittedFacility.setText(info.getString(2));

            /*TextView submittedDescription = (TextView) time_layout.findViewById(R.id.descricao);
            submittedDescription.setText("description");*/

                    /*inc_layout.addView(time_layout);
                }
*/

            for (int j = 0; j < 8; j++) {
                in = true;
                View time_layout = li.inflate(R.layout.time_line, null);
                time_layout.setId(contId);

                //time_layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 180));

                TextView submittedHours = (TextView) time_layout.findViewById(R.id.horas);
                submittedHours.setText("0" +(6+j+1)+ "h00");

                TextView submittedFacility = (TextView) time_layout.findViewById(R.id.nomeFacility);
                submittedFacility.setText("facility" + (j+1));

            /*TextView submittedDescription = (TextView) time_layout.findViewById(R.id.descricao);
            submittedDescription.setText("description");*/

                inc_layout.addView(time_layout);
            }
            count = 0;
            if (in) {
            /*changeDirection(rootView, "Complex", init, info.getString(2));*/
                changeDirection(rootView, "Complex1", "Facility1", "Facility8");
            }

        }
    }

    private boolean getTimeRangeFromPath(JSONArray rounds, int i) throws JSONException {
        JSONObject round = new JSONObject(rounds.get(i).toString());
        JSONArray path = round.getJSONArray("path");
        JSONArray info = path.getJSONArray(0);
        JSONArray info1 = path.getJSONArray(path.length() - 1);
        int si = info.getInt(0);
        int sf = info1.getInt(0);
        int current = ParsingUtils.getCurrentTime();

        if (current >= si && current < sf) {
            count++;
            return true;
        } else if (current < si && count == 0) {
            count++;
            int diference = si - current;
            float hora = diference / 60f;
            float min = (hora - (int) hora) * 60;
            String diferenceString = String.format("%02d horas e %02d minutos", (int) hora, (int) min);

            Toast.makeText(getContext(), "O seu percurso começa daqui a" + diferenceString + "!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }


}
