package adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import bp.BP;
import info.atiar.prnappdialer.R;
import model.NumberModel;

public class NumbersAdapter extends BaseAdapter {
    private Context context;
    private Activity activity;
    private LayoutInflater inflater;
    private List<NumberModel> data;

    private final String TAG = getClass().getSimpleName() + " Atiar= ";

    public NumbersAdapter(Activity activity, List<NumberModel> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int location) {
        return data.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = activity.getApplicationContext();
        NumberModel numberModel = data.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.number_list, null);


        TextView _number = convertView.findViewById(R.id.numberTV);
        _number.setText(numberModel.getNumber());

        _number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(activity)
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                BP.callNumber(activity, numberModel.getNumber());

                                /* ... */}
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                        }).check();
            }
        });

        return convertView;

    }

    //To update the searchView items in TransportList Activity
    public void update(List<NumberModel> resuls){
        data = new ArrayList<>();
        data.addAll(resuls);
        notifyDataSetChanged();
    }
}