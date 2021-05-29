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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static game.evgeha.logicalquiz.Activity_Main.click_sound;
import static game.evgeha.logicalquiz.Activity_Main.coin_count;
import static game.evgeha.logicalquiz.Activity_Main.soundPool;
import static game.evgeha.logicalquiz.Activity_Main.successful_sound;

public class Activity_LevelSelection extends AppCompatActivity {

    private Dialog dialog;

    private ListView lvl_list; // Список уровней
    private TextView cnt; // Отображение кол-ва монет
    private ImageView coin_img;
    private SharedPreferences spStatuses, spCnt, spRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);
        selectionMainSetUp();

        LevelInfo[] levelInf = makeLevel(); // Создаём массив классов LevelInfo
        LevelInfo_adapter adapter = new LevelInfo_adapter(this, levelInf); //Создаём listView классов LevelInfo с помощью адаптера
        lvl_list.setAdapter(adapter);

        lvl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                playSound(click_sound);
                //Если у нас уровень закрыт, но денег хватает
                if (levelInf[position].isLocked() && levelInf[position].getCost() <= coin_count)
                    // Показываем диалоговое окно с подтверждением
                    showDialogConfirm(levelInf[position]);
                    //Если у нас уровень закрыт, но денег не хватает
                else if (levelInf[position].isLocked() == true && levelInf[position].getCost() > coin_count) {
                    showDialogWarn();
                }
                //Если у нас уровень открыт
                else {
                    Intent intent = new Intent();
                    switch (levelInf[position].getType()){
                        case LevelInfo.TYPE_GRAPHIC:
                            intent = new Intent(Activity_LevelSelection.this, Activity_GraphicLevel.class);
                            break;
                        default: intent = new Intent(Activity_LevelSelection.this, Activity_CommonLevel.class);
                    }
                    String type = levelInf[position].getType();
                    intent.putExtra("levelInfo", levelInf[position]);
                    intent.putExtra("ID", position);
                    showDialogDescription(type, intent);
                }
            }
        });

        coin_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] codes = getResources().getStringArray(R.array.level_codes);
                clearCache(getNames(codes));
            }
        });
    }
    // Делаем полный экран
    public void setFullScreen(){
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    // Начальная настройка
    public void selectionMainSetUp(){
        setFullScreen();
        //Получаем монеты пользователя
        spCnt = getSharedPreferences("Counts", Context.MODE_PRIVATE);
        coin_count = spCnt.getInt("Coins", 0);

        cnt = (TextView) findViewById(R.id.coin_cnt);
        lvl_list = (ListView) findViewById(R.id.level_types);
        coin_img = (ImageView)findViewById(R.id.coin) ;

        cnt.setText(Integer.toString(coin_count));

    }
    // Получаем массив названий уровней
    public String[] getNames(String[] codes){
        String[] arr = new String[codes.length];
        for(int i = 0; i < codes.length; ++i) {
            String code = codes[i];
            arr[i] = getResources().getString(getResources().getIdentifier(code + "name", "string", getPackageName()));
        }
        return arr;
    }
    // Получаем массив статусов уровней
    public boolean[] getStatuses(String[] keys){
        spStatuses = getSharedPreferences("Locked_status", Context.MODE_PRIVATE);
        boolean[] status = new boolean[keys.length];
        for(int i = 0; i < keys.length; ++i) {
            status[i] = spStatuses.getBoolean(keys[i], true);
        }
        return status;
    }
    // Получаем массив рекордов пользователя
    public int[] getRecords(String[] keys){
        spRecords = getSharedPreferences("Records", Context.MODE_PRIVATE);
        int[] records = new int[keys.length];
        for(int i = 0; i < keys.length; ++i)
            records[i] = spRecords.getInt(keys[i], 0);
        return records;
    }
    // Наполнение listView с помощью адаптера
    public LevelInfo[] makeLevel(){
        String[] codes = getResources().getStringArray(R.array.level_codes);
        int sz = codes.length;
        String[] types = new String[sz];
        String[] names = getNames(codes);
        LevelInfo[] arr = new LevelInfo[sz];
        boolean[] locked = getStatuses(names);
        // clearCache(names);
        int[] records = getRecords(names);
        for(int i = 0; i < sz; ++i){
            String code = codes[i];
            types[i] = getResources().getString(getResources().getIdentifier(code + "type","string", getPackageName()));
        }
        for(int i = 0; i < arr.length; i++){
            LevelInfo level = new LevelInfo(names[i], i * 8, locked[i], types[i], codes[i], records[i]);
            if(i == 0) { // Первый уровень всегда открыт
                SharedPreferences.Editor editorStatuses = spStatuses.edit();
                level.setUnLocked(editorStatuses);
            }
            arr[i] = level;
        }
        return arr;
    }
    public void playSound(int id){
        soundPool.play(id,1,1,0,0,1);
    }
    // Диалоговое окно с подтвреждением
    public void showDialogConfirm(LevelInfo levelInfo){
        dialog = new Dialog(Activity_LevelSelection.this);
        dialogSetUp(dialog, R.layout.dialog_window_confirm, false);

        Button btn_yes = (Button)dialog.findViewById(R.id.yes);
        Button btn_no = (Button)dialog.findViewById(R.id.no);
        TextView question = (TextView)dialog.findViewById(R.id.ask);

        question.setText(getString(R.string.Confirm_txt) + " " + levelInfo.getCost() + " " + getResources().getString(R.string.Money) + "?");

        btn_no.setOnClickListener(new View.OnClickListener() { // Нажатие на кнопку отказа
            @Override
            public void onClick(View v) {
                playSound(click_sound);
                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() { // Нажатие на кнопку соглашения
            @Override
            public void onClick(View v) {
                playSound(click_sound);
                playSound(successful_sound);
                //Открываем уровень
                SharedPreferences.Editor editorStatuses = spStatuses.edit();
                levelInfo.setUnLocked(editorStatuses);
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
    // Диалоговое окно с описанием уровня
    public void showDialogDescription(String type, Intent intent){
        dialog = new Dialog(Activity_LevelSelection.this);
        dialogSetUp(dialog, R.layout.dialog_window_about_level, true);
        TextView description = (TextView)dialog.findViewById(R.id.level_description);
        switch(type){
            case LevelInfo.TYPE_GRAPHIC: description.setText(R.string.Graphic_level_description);
                break;
            default: description.setText(R.string.Common_level_description);
        }
        Button btn_start_level = (Button)dialog.findViewById(R.id.start_level);
        btn_start_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(click_sound);
                dialog.dismiss();
                startActivity(intent);
            }
        });
        dialog.show();
    }
    // Диалоговое окно с предупреждением
    public void showDialogWarn(){
        dialog = new Dialog(Activity_LevelSelection.this);
        dialogSetUp(dialog, R.layout.dialog_window_warn, true);
        Button btn_start_level = (Button)dialog.findViewById(R.id.back);
        btn_start_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(click_sound);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    // SetUp диалогового окна
    public void dialogSetUp(Dialog dialog, int id, boolean cancelable){
        dialog.setContentView(id); // Что будет показывать диалоговое окно
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Сделаем задний фон прозрачным
        dialog.setCancelable(cancelable); // Окно можно закрыть только выбрав какой-либо вариант
    }
    // Очищение кеша
    public void clearCache(String[] keys){
        spStatuses = getSharedPreferences("Locked_status", Context.MODE_PRIVATE);
        spRecords = getSharedPreferences("Records", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorStatuses = spStatuses.edit();
        SharedPreferences.Editor editorRecords = spRecords.edit();
        for(String key : keys) {
            editorStatuses.putBoolean(key, true);
            editorRecords.putInt(key, 0);
        }
        editorStatuses.commit();
        editorRecords.commit();
        SharedPreferences.Editor editorCnt = spCnt.edit();
        editorCnt.putInt("Coins", 0);
        editorCnt.putInt("Right_cnt", 0);
        editorCnt.putInt("Wrong_cnt", 0);
        editorCnt.commit();
        finish();
        startActivity(getIntent());
    }
}