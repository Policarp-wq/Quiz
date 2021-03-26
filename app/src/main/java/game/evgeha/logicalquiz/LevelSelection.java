package game.evgeha.logicalquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import static game.evgeha.logicalquiz.MainActivity.coin_count;
import static game.evgeha.logicalquiz.MainActivity.player;

public class LevelSelection extends AppCompatActivity {

    Dialog dialog;

    private ListView lvl_types; // Список уровней
    private TextView cnt; // Отображение кол-ва монет
    private SharedPreferences spStatuses, spCnt; // Кеш

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        // Делаем полный экран
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Получаем монеты пользователя
        spCnt = getSharedPreferences("Coins", Context.MODE_PRIVATE);
        coin_count = spCnt.getInt("Coins", 100);

        cnt = (TextView)findViewById(R.id.coin_cnt);
        lvl_types = (ListView)findViewById(R.id.level_types);

        cnt.setText(Integer.toString(coin_count));

        LevelInfo[] levelInf = makeLevel(); // Создаём массив классов LevelInfo

        LevelInfo_adapter adapter = new LevelInfo_adapter(this, levelInf); //Создаём listView классов LevelInfo с помощью адаптера
        lvl_types.setAdapter(adapter);

        lvl_types.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                player.play(R.raw.click);
                //Если у нас уровень закрыт, но денег хватает
                if(levelInf[position].isLocked() && levelInf[position].getCost() <= coin_count)
                    // Показываем диалоговое окно с подтверждением
                    showDialog(levelInf[position]);

                //Если у нас уровень закрыт, но денег не хватает
                else if(levelInf[position].isLocked() == true && levelInf[position].getCost() > coin_count) {
                    // Показываем окно с отказом
                }
                //Если у нас уровень открыт
                else {
                    Intent intent = new Intent(LevelSelection.this, CommonLevel.class);
                    intent.putExtra("Level type", position);
                    //Переходим в сам уровень
                    startActivity(intent);
                }
            }
        });
    }

    // Получаем массив статусов уровней
    private boolean[] getStatuses(String[] keys){
        spStatuses = getSharedPreferences("Locked_status", Context.MODE_PRIVATE);
        boolean[] status = new boolean[keys.length];

        for(int i = 0; i < keys.length; ++i){
            status[i] = spStatuses.getBoolean(keys[i], true);
        }
        return status;
    }

    // Наполнение listView с помощью адаптера
    private LevelInfo[] makeLevel(){

        String[] names = getResources().getStringArray(R.array.level_name);
        boolean[] locked = getStatuses(names);
        int[] cost = getResources().getIntArray(R.array.сosts); //Стоимости уровней

        LevelInfo[] arr = new LevelInfo[names.length];

        for(int i = 0; i < arr.length; i++){
            LevelInfo level = new LevelInfo(names[i], cost[i], locked[i]);
            arr[i] = level;
        }
        return arr;
    }

    // Диалоговое окно
    private void showDialog(LevelInfo levelInfo){
        dialog = new Dialog(LevelSelection.this);
        dialog.setContentView(R.layout.preview_dialog_window); // Что будет показывать диалоговое окно
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Сделаем задний фон прозрачным
        dialog.setCancelable(false); // Окно можно закрыть только выбрав какой-либо вариант

        Button btn_yes = (Button)dialog.findViewById(R.id.yes);
        Button btn_no = (Button)dialog.findViewById(R.id.no);
        TextView question = (TextView)dialog.findViewById(R.id.ask);

        question.setText(getString(R.string.Warn) + " " + levelInfo.getCost() + " монет?");

        btn_no.setOnClickListener(new View.OnClickListener() { // Нажатие на кнопку отказа
            @Override
            public void onClick(View v) {
                player.play(R.raw.click);
                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() { // Нажатие на кнопку соглашения
            @Override
            public void onClick(View v) {
                player.play(R.raw.click);
                //Открываем уровень
                levelInfo.setUnLocked();
                SharedPreferences.Editor editorStatuses = spStatuses.edit();
                editorStatuses.putBoolean(levelInfo.getName(), false);
                editorStatuses.commit();

                //Забираем монеты
                SharedPreferences.Editor editorCnt = spCnt.edit();
                coin_count -= levelInfo.getCost();
                editorCnt.putInt("Coins", coin_count);
                editorCnt.commit();

                dialog.dismiss();
                // Обновляем экран
                finish();
                startActivity(getIntent());
            }
        });
        dialog.show();
    }
}