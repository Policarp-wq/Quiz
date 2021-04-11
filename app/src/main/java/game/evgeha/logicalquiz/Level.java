package game.evgeha.logicalquiz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static game.evgeha.logicalquiz.MainActivity.click_sound;
import static game.evgeha.logicalquiz.MainActivity.coin_count;
import static game.evgeha.logicalquiz.MainActivity.soundPool;

public class Level extends AppCompatActivity {

    public Button ans1, ans2, ans3, ans4, btn_continue; // Кнопки ответов, продолжить
    public TextView question_txt, fact_txt, right_ans; // Текст вопроса, факта и правильного ответа на экране
    public ProgressBar countDown_timer; // Обратный отсчёт

    public String code = "animals_";
    public String[] questions, facts, png_codes;

    public int btn_id = -1, heartsCnt = 3, progress, pos, stage, end;
    public final int TIME = 20; // Время на ответ
    public int cur_time = 0;

    public ImageView fact_png;
    public ImageView[] hearts = new ImageView[3]; // Сердечки на экране

    public Dialog dialog;

    public Intent intent;

    private SharedPreferences spCnt; // Кол-во монет пользователя

    // Слушатель кнопок
    public void listener() {
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cur_time = 0;
                if(stage == end)
                    levelEnding();
            }
        });
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 0;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 1;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
        ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 2;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
        ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 3;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
    }
    // Получаем массив вопросов, фактов и картинок для данного уровня
    public void getArrays(){
        questions = getResources().getStringArray(getResources().getIdentifier(code + "questions", "array", getPackageName()));
        facts =     getResources().getStringArray(getResources().getIdentifier(code + "facts", "array", getPackageName()));
        png_codes = getResources().getStringArray(getResources().getIdentifier(code + "ans_codes", "array", getPackageName()));
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
        dialog = new Dialog(context);
        dialogSetUp(dialog, R.layout.dialog_window_facts);
        fact_txt = (TextView)dialog.findViewById(R.id.fact_description);
        right_ans = (TextView)dialog.findViewById(R.id.right_ans);
        fact_png = (ImageView)dialog.findViewById(R.id.fact_img);
        btn_continue = (Button)dialog.findViewById(R.id.question_continue);
    }
    // Показ окна
    public void showDialogFact(String ans, String txt, String code){
        int id = getResources().getIdentifier(code, "drawable", getPackageName());
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
                dialog.show();
            }
        };
        runOnUiThread(runnable);
    }
    // Добавить монеты
    public void addCoin(){
        int length = questions.length;
        spCnt = getSharedPreferences("Coins", Context.MODE_PRIVATE);
        coin_count += (length / 5 - (3 - heartsCnt)) * pos;
        SharedPreferences.Editor editorCnt = spCnt.edit();
        editorCnt.putInt("Coins", coin_count);
        editorCnt.commit();
    }
    // Завершение уровня
    public void levelEnding(){
        if(heartsCnt != 0)  // Если все жизни потрачены, то не добавляем монеты
            addCoin();
        startActivity(intent);
        finish();
    }
}
