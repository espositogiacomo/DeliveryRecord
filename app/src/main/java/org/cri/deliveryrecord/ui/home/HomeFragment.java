package org.cri.deliveryrecord.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.cri.deliveryrecord.ItemDatabaseHandler;
import org.cri.deliveryrecord.MapDatabaseHandler;
import org.cri.deliveryrecord.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        String[] deliveryPoint = {"8 Prua", "8 Poppa", "6 Prua", "6 Poppa", "Covid"};

        Spinner spin = (Spinner) root.findViewById(R.id.sp_DeliveryPointSelection);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, deliveryPoint);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MapDatabaseHandler dbMap = MapDatabaseHandler.getInstance();
                int totBySel = dbMap.getCountByDelPoint(position + 1);
                ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
                db.setDeliveryPointSelection(position + 1);
                int tot = db.getTot();
                TextView labelTOT = root.findViewById(R.id.label_TOT);
                labelTOT.setText("TOT: " + tot + "/" + totBySel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });

        EditText editText_guestId = root.findViewById(R.id.guestId);
        TextWatcher editText_guestIdTw = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //reset();
                TextView labelResult = root.findViewById(R.id.label_Result);
                labelResult.setVisibility(View.INVISIBLE);
            }
        };

        editText_guestId.addTextChangedListener(editText_guestIdTw);

        ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
        int tot = db.getContactsCount();

        TextView labelTOT = root.findViewById(R.id.label_TOT);
        labelTOT.setText("TOT: " + tot);

        return root;
    }

    public void checkGuest(View view) {
// do not delete.
    }

    public void reset(View view) {
// do not delete.
    }

    public void spinnerOnChange(View v){

    }




}