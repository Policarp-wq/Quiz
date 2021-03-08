package game.evgeha.logicalquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
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

public class LevelSelection extends AppCompatActivity {

    Dialog dialog;

    private ListView lvl_types;
    private TextView cnt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);
        //Убираем херню сверху
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cnt = (TextView)findViewById(R.id.coin_cnt);
        cnt.setText(Integer.toString(coin_count)); //Отображаем кол-во монет
        lvl_types = (ListView)findViewById(R.id.level_types);

       LevelInfo[] levelInf = makeLevel(); // Создаём массив классов LevelInfo

        LevelInfo_adapter adapter = new LevelInfo_adapter(this, levelInf); //Создаём listView классов LevelInfo с помощью адаптера
        lvl_types.setAdapter(adapter);
        lvl_types.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                //Если у нас уровень закрыт, но денег хватает
                if(levelInf[position].isLocked() && levelInf[position].getCost() <= coin_count){
                    //Диалоговое окно
                    dialog = new Dialog(LevelSelection.this);
                    dialog.setContentView(R.layout.preview_dialog_window); // Что будет показывать диалоговое окно
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Сделаем задний фон прозрачным
                    dialog.setCancelable(false); // Окно можно закрыть только выбрав какой-лио вариант
                    Button btn_yes = (Button)dialog.findViewById(R.id.yes);
                    Button btn_no = (Button)dialog.findViewById(R.id.no);
                    TextView question = (TextView)dialog.findViewById(R.id.ask);
                    question.setText(getString(R.string.Warn) + " " + levelInf[position].getCost() + " монет?");
                    btn_no.setOnClickListener(new View.OnClickListener() { // Нажатие на кнопку отказа
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_yes.setOnClickListener(new View.OnClickListener() { // Нажатие на кнопку соглашения
                        @Override
                        public void onClick(View v) {
                            levelInf[position].setUnLocked();
                            // Нужно менять состояние в самом ресурсном массиве!!!
                            coin_count -= levelInf[position].getCost();
                            dialog.dismiss();
                            // Обновляем экран
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    dialog.show();
                }
                //Если у нас уровень закрыт, но денег не хватает
                else if(levelInf[position].isLocked() == true && levelInf[position].getCost() > coin_count) {
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


    //Наполнение listView с помощью адаптера
    LevelInfo[] makeLevel(){
        LevelInfo[] arr = new LevelInfo[5];

        String[] name = getResources().getStringArray(R.array.level_name);
        int[] cost = getResources().getIntArray(R.array.сosts); //Стоимости уровней
        int[] locked = getResources().getIntArray(R.array.locked_statuses);

        for(int i = 0; i < arr.length; i++){
            LevelInfo level = new LevelInfo(name[i], cost[i], locked[i] == 1);
            arr[i] = level;
        }
        return arr;
    }

   /* public void listener(){
        Button btn_yes = (Button)dialog.findViewById(R.id.yes);
        Button btn_no = (Button)dialog.findViewById(R.id.no);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept = true;
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept = false;
            }
        });
    } */
}