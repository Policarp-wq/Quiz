package game.evgeha.logicalquiz;

import android.content.Context;
import android.content.SharedPreferences;
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

    public Button ans1, ans2, ans3, ans4; // Кнопки ответов
    public TextView question_txt, fact_txt, right_ans; // Текст вопроса, факта и правильного ответа на экране
    public ProgressBar countDown_timer; // Обратный отсчёт

    public int btn_id = -1, heartsCnt = 3, progress, pos;
    public final int TIME = 5; // Время на ответ

    public ImageView fact_png;
    public ImageView[] hearts = new ImageView[3]; // Сердечки на экране
    private SharedPreferences spCnt; // Кол-во монет пользователя

    public void listener() {
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
    } // Слушатель кнопок
    public void updateProgressBar(){
        countDown_timer.setProgress(progress);
    } // Обновить прогресс таймера на экране
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
    } // Обновить интерфейс
    public void addCoin(int length){
        spCnt = getSharedPreferences("Coins", Context.MODE_PRIVATE);
        coin_count += (length / 5 - (3 - heartsCnt)) * pos;
        SharedPreferences.Editor editorCnt = spCnt.edit();
        editorCnt.putInt("Coins", coin_count);
        editorCnt.commit();
    } // Добавить монеты
}
