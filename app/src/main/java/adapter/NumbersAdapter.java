package adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import bp.BP;
import info.atiar.prnappdialer.NumberDialActivity;
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


        CardView _cardView = convertView.findViewById(R.id.cardView);
        ImageView _item_call = convertView.findViewById(R.id.item_call);
        ImageView _item_sms = convertView.findViewById(R.id.item_sms);
        TextView _number = convertView.findViewById(R.id.numberTV);
        ImageView _deleteButton = convertView.findViewById(R.id.deleteButton);
        _number.setText(numberModel.getNumber());

        if (!numberModel.getCallDuration().equals("0")){
            _item_call.setBackgroundColor(Color.GREEN);
        }

        _deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NumberDialActivity) activity).removeItem(position);
            }
        });

        _number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(activity)
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                BP.callNumber(activity, numberModel.getNumber());
                                BP.isSingleNumber = true;
                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {}
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