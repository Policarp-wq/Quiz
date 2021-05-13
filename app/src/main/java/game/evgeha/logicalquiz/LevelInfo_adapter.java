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

public class LevelInfo_adapter extends ArrayAdapter<LevelInfo> {

    public LevelInfo_adapter(Context context, LevelInfo[] arr) {
        super(context, R.layout.list_item, arr);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final LevelInfo levelInfo = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);

        ((TextView) convertView.findViewById(R.id.theme_txt)).setText(levelInfo.getName());
        ((TextView) convertView.findViewById(R.id.cost_txt)).setText("Требуется монет: " + Integer.toString(levelInfo.getCost()));

        if(levelInfo.isLocked() == true)
            ((ImageView) convertView.findViewById(R.id.status_image)).setImageResource(R.drawable.locked);
        else {
            ((TextView) convertView.findViewById(R.id.cost_txt)).setTextColor(ContextCompat.getColor(getContext(), R.color.green));

            ((ImageView) convertView.findViewById(R.id.status_image)).setImageResource(R.drawable.unlocked);
        }

        return convertView;
    }
}
