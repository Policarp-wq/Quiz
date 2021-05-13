package game.evgeha.logicalquiz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static game.evgeha.logicalquiz.Activity_Main.click_sound;
import static game.evgeha.logicalquiz.Activity_Main.coin_count;
import static game.evgeha.logicalquiz.Activity_Main.soundPool;

public class Level extends AppCompatActivity {

    public Button ans1, ans2, ans3, ans4, btn_continue, btn_end_continue, btn_end_play_again; // Кнопки ответов, продолжить
    public TextView question_txt, fact_txt, right_ans, end_txt; // Текст вопроса, факта и правильного ответа на экране
    public ProgressBar countDown_timer; // Обратный отсчёт

    public String code = "animals_", name;
    public String[] questions, facts, png_codes;

    public int btn_id = -1, heartsCnt = 3, progress, pos, stage, end, right_ans_cnt = 0, record;
    public final int TIME = 20; // Время на ответ
    public int cur_time = 0;

    public ImageView fact_png, end_png;
    public ImageView[] hearts = new ImageView[3]; // Сердечки на экране

    public Dialog dialogFact, dialogEnd; // Диалоговые окна

    public Intent intent;

    private SharedPreferences spCnts, spRecords; // Кол-во монет пользователя

    public void setFullScreen(){
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    // Слушатель кнопок
    public void listener() {
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFact.dismiss();
                cur_time = 0;
                if(stage == end)
                    levelEnding();
            }
        });
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 0;
                playSound(click_sound);
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 1;
                playSound(click_sound);
            }
        });
        ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 2;
                playSound(click_sound);
            }
        });
        ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 3;
                playSound(click_sound);
            }
        });
        btn_end_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                playSound(click_sound);
                finish();
            }
        });
        btn_end_play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }
    public void playSound(int sound_id){
        soundPool.play(sound_id,1,1,0,0,1);
    }
    // Получаем массив вопросов, фактов и картинок для данного уровня
    public void getArrays(){
        questions = getResources().getStringArray(getResources().getIdentifier(code + "questions", "array", getPackageName()));
        facts =     getResources().getStringArray(getResources().getIdentifier(code + "facts", "array", getPackageName()));
        png_codes = getResources().getStringArray(getResources().getIdentifier(code + "ans_codes", "array", getPackageName()));
    }
    // Настройка уровня
    public void levelMainSetUp(Context context){
        ans1 = (Button) findViewById(R.id.ans1);
        ans2 = (Button) findViewById(R.id.ans2);
        ans3 = (Button) findViewById(R.id.ans3);
        ans4 = (Button) findViewById(R.id.ans4);
        question_txt = (TextView) findViewById(R.id.question_txt);

        hearts[0] = (ImageView) findViewById(R.id.heart1);
        hearts[1] = (ImageView) findViewById(R.id.heart2);
        hearts[2] = (ImageView) findViewById(R.id.heart3);

        pos = getIntent().getIntExtra("ID", 0) + 1;

        LevelInfo levelInfo = getIntent().getParcelableExtra("levelInfo");
        code = levelInfo.getCode();
        record = levelInfo.getRecord();
        name = levelInfo.getName();

        countDown_timer = (ProgressBar) findViewById(R.id.timer);

        //record = spRecords.getInt("")

        createDialogFact(context);
        createDialogEnd(context);
        listener();
        try {
            getArrays();
            end = questions.length / 5;
        }catch (Exception e){
            createDialogEnd(context);
            listener();
            btn_end_play_again.setVisibility(View.INVISIBLE);
            btn_end_continue.setText("Вернуться в лобби");
            showDialogEnd("INVALID LEVEL CODE", R.drawable.cross);
        }
    }
    // Обновить прогресс таймера на экране
    public void updateProgressBar(){
        countDown_timer.setProgress(progress);
    }
    // Обновить интерфейс всей активности
    public void updateQuestionUi(String question, String[] vars){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                question_txt.setText(question);
                ans1.setText(vars[0]);
                ans2.setText(vars[1]);
                ans3.setText(vars[2]);
                ans4.setText(vars[3]);
                if(heartsCnt < 3)
                    hearts[heartsCnt].setImageResource(R.drawable.empty_heart);
            }
        };
        runOnUiThread(runnable);
    }
    // Первоначальная настройка окна
    public void dialogSetUp(Dialog dialog, int id){
        dialog.setContentView(id); // Что будет показывать диалоговое окно
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Сделаем задний фон прозрачным
        dialog.setCancelable(false);
    }
    // Создание диалогового окна
    public void createDialogFact(Context context){
        dialogFact = new Dialog(context);
        dialogSetUp(dialogFact, R.layout.dialog_window_facts);
        fact_txt = (TextView) dialogFact.findViewById(R.id.fact_description);
        right_ans = (TextView) dialogFact.findViewById(R.id.right_ans);
        fact_png = (ImageView) dialogFact.findViewById(R.id.fact_img);
        btn_continue = (Button) dialogFact.findViewById(R.id.question_continue);
    }
    // Показ окна
    public void showDialogFact(String ans, String txt, String png_code){
        int id = getResources().getIdentifier(png_code, "drawable", getPackageName());
        dialogUpdateUi(ans, txt, id);
        cur_time = -1000000;
    }
    // Обновить интерфейс окна
    public void dialogUpdateUi(String ans, String txt, int id){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                right_ans.setText(ans);
                fact_txt.setText(txt);
                fact_png.setImageResource(id);
                dialogFact.show();
            }
        };
        runOnUiThread(runnable);
    }
    // Добавить монеты
    public void addCoin(int coins){
        spCnts = getSharedPreferences("Counts", Context.MODE_PRIVATE);
        coin_count += coins;
        SharedPreferences.Editor editorCnt = spCnts.edit();
        editorCnt.putInt("Coins", coin_count);
        editorCnt.commit();
    }
    public void createDialogEnd(Context context){
        dialogEnd = new Dialog(context);
        dialogSetUp(dialogEnd, R.layout.dialog_window_level_end);
        end_png = (ImageView)dialogEnd.findViewById(R.id.end_img);
        end_txt = (TextView)dialogEnd.findViewById(R.id.end_txt);
        btn_end_continue = (Button)dialogEnd.findViewById(R.id.end_continue) ;
        btn_end_play_again = (Button)dialogEnd.findViewById(R.id.play_again);
    }
    public void showDialogEnd(String txt, int img_id){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                end_png.setImageResource(img_id);
                end_txt.setText(txt);
            }
        };
        runOnUiThread(runnable);
        dialogEnd.show();
    }
    // Завершение уровня
    public void levelEnding(){
        int id, right_cnt, wrong_cnt;
        String txt;
        if(heartsCnt != 0) { // Если все жизни потрачены, то не добавляем монеты
            int coins = (questions.length / 5 - (3 - heartsCnt)) * pos;
            id = R.drawable.successful_level;
            if(record == 0) {
                txt = getResources().getString(R.string.Succ_end) + " " + Integer.toString(coins) + " " + getResources().getString(R.string.Money);
                record = coins;
            }
            else if(record >= coins) {
                coins = 0;
                txt = getResources().getString(R.string.Old_record) + " " + Integer.toString(record) + " " + getResources().getString(R.string.Money);
            }
            else{
                int copy = coins;
                coins -= record;
                record = copy;
                txt = getResources().getString(R.string.New_record) + " " + Integer.toString(coins) + " " + getResources().getString(R.string.Money);
            }
            addCoin(coins);
            spRecords = getSharedPreferences("Records", Context.MODE_PRIVATE);
            SharedPreferences.Editor edRecord =  spRecords.edit();
            edRecord.putInt(name, record);
            edRecord.commit();
        }
        else{
            id = R.drawable.cross;
            txt = getResources().getString(R.string.UnSucc_end);
        }
        spCnts = getSharedPreferences("Counts", Context.MODE_PRIVATE);
        right_cnt = spCnts.getInt("Right_cnt", 0) + right_ans_cnt;
        wrong_cnt = spCnts.getInt("Wrong_cnt", 0) + 3 - heartsCnt;
        SharedPreferences.Editor ed = spCnts.edit();
        ed.putInt("Right_cnt", right_cnt);
        ed.putInt("Wrong_cnt", wrong_cnt);
        ed.commit();
        showDialogEnd(txt, id);
    }
}
