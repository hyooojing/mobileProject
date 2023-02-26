package ddwucom.mobile.finalproject.ma02_20200981;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyMuseumAdapter extends BaseAdapter {
    public static final String TAG = "MyMuseumAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<MuseumDTO> list;
    private NetworkManager networkManager = null;

    public MyMuseumAdapter(Context context, int resource, ArrayList<MuseumDTO> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        networkManager = new NetworkManager(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public MuseumDTO getItem(int position) {return list.get(position); }

    @Override
    public long getItemId(int position) { return list.get(position).get_id(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.museumTitle = view.findViewById(R.id.museumTitle);
            viewHolder.museumTel = view.findViewById(R.id.museumTel);
            viewHolder.museumType = view.findViewById(R.id.museumType);
            viewHolder.museumAddress = view.findViewById(R.id.museumAddress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        MuseumDTO dto = list.get(position);

        viewHolder.museumTitle.setText(dto.getName());
        viewHolder.museumType.setText(dto.getType());
        viewHolder.museumTel.setText(dto.getNum());
        viewHolder.museumAddress.setText(dto.getAddress());

        return view;
    }

    public void setList(ArrayList<MuseumDTO> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    //    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView museumTitle = null;
        public TextView museumTel = null;
        public TextView museumAddress = null;
        public TextView museumType = null;
    }
}
