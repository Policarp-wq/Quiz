package game.evgeha.logicalquiz;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class Level_adapter extends ArrayAdapter<Level> {

    public Level_adapter(Context context, Level[] arr) {
        super(context, R.layout.list_item, arr);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Level level = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
        }

        ((TextView) convertView.findViewById(R.id.theme_txt)).setText(level.getName());
        ((TextView) convertView.findViewById(R.id.cost_txt)).setText("Требуется монет: " + Integer.toString(level.getCost()));
        if(level.isLocked() == false)
            ((TextView) convertView.findViewById(R.id.cost_txt)).setTextColor(ContextCompat.getColor(getContext(), R.color.green));

        if(level.isLocked() == true)
            ((ImageView) convertView.findViewById(R.id.status_image)).setImageResource(R.drawable.locked);
        else ((ImageView) convertView.findViewById(R.id.status_image)).setImageResource(R.drawable.unlocked);

        return convertView;
    }
}
