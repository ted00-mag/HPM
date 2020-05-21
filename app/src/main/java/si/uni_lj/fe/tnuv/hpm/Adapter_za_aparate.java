package si.uni_lj.fe.tnuv.hpm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class Adapter_za_aparate extends PagerAdapter {

    private List<Model_aparati> models_aparati;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter_za_aparate(List<Model_aparati> models, Context context) {
        this.models_aparati = models;
        this.context = context;
    }
    @Override
    public int getCount() {
        return models_aparati.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.potratni_aparati, container, false);
        TextView title, value;

        title = view.findViewById(R.id.valueOfKlimatskaNaprava);
        value = view.findViewById(R.id.valueOfPralniStroj);
        title.setText(models_aparati.get(position).getTitle());
        value.setText(models_aparati.get(position).getValue());
        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
