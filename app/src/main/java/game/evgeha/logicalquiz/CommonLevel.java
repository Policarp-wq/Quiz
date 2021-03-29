package game.evgeha.logicalquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static game.evgeha.logicalquiz.MainActivity.click_sound;
import static game.evgeha.logicalquiz.MainActivity.coin_count;
import static game.evgeha.logicalquiz.MainActivity.player;
import static game.evgeha.logicalquiz.MainActivity.right_sound;
import static game.evgeha.logicalquiz.MainActivity.soundPool;
import static game.evgeha.logicalquiz.MainActivity.wrong_sound;

public class CommonLevel extends AppCompatActivity {

    private Button ans1, ans2, ans3, ans4;
    private TextView question_txt;
    private ProgressBar countDown_timer;

    private String[] vars = new String[4]; // Варианты ответов

    private int numb = -1, heartsCnt = 3, progress;
    private final int TIME = 5;

    private ImageView[] hearts = new ImageView[3]; // Сердечки на экране

    private SharedPreferences spCnt;

    private Handler handlerAns, handlerQuest_txt, handlerHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_level);

        // Делаем полный экран
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ans1 = (Button) findViewById(R.id.ans1);
        ans2 = (Button) findViewById(R.id.ans2);
        ans3 = (Button) findViewById(R.id.ans3);
        ans4 = (Button) findViewById(R.id.ans4);
        question_txt = (TextView) findViewById(R.id.question_txt);

        hearts[0] = (ImageView)findViewById(R.id.heart1);
        hearts[1] = (ImageView)findViewById(R.id.heart2);
        hearts[2] = (ImageView)findViewById(R.id.heart3);

        countDown_timer = (ProgressBar)findViewById(R.id.timer);

        // Получаем массив вопросов для данного уровня
        String[] questions = getResources().getStringArray(R.array.animals_questions);

        Intent intent = new Intent(CommonLevel.this, LevelSelection.class);

        spCnt = getSharedPreferences("Coins", Context.MODE_PRIVATE);

        listener();

        // Ставим варианты ответов
        handlerAns = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                String[] vars = (String[]) msg.obj;
                ans1.setText(vars[0]);
                ans2.setText(vars[1]);
                ans3.setText(vars[2]);
                ans4.setText(vars[3]);
            }
        };

        // Ставим текст  вопроса
        handlerQuest_txt = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                String txt = (String)msg.obj;
                question_txt.setText(txt);
            }
        };

        // Изменяем сердечко
        handlerHeart = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                int id = (int)msg.obj;
                hearts[id].setImageResource(R.drawable.empty_heart);
            }
        };

        // Создаём отдельный поток для движения по этапам
        new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < questions.length / 5; ++i) {
                    // Создаём класс вопроса и берём варианты ответа из него
                    Question question = new Question(questions, i * 5); // !!! Подаётся целый массив, хотя намнужна всего лишь часть - ОПТИМИЗИРОВАТЬ
                    vars = question.getVars();

                    //Создаём сообщение хендлеру
                    Message msg = new Message();
                    //Передаём в него текст вопроса
                    msg.obj = question.getName();
                    handlerQuest_txt.sendMessage(msg);

                    //Передаём в него варианты ответов
                    Message msg1 = new Message();
                    msg1.obj = vars;
                    handlerAns.sendMessage(msg1);
                    numb = -1;

                    // Ставим таймер на 5 секунд
                    progress = 0;
                    for(int j = 0; j < TIME * 1000; ++j){
                        progress = j / (TIME * 10);
                        updateProgressBar();
                        try {
                            this.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Если какая-то кнопка нажата, то получаем её порядковый номер и сравнимаем ответ, принадлежащий данной кнопке с правильным
                        if(numb != -1) {
                            // Если ответ неправильный
                            if(vars[numb] != question.getAns()){
                                soundPool.play(wrong_sound, 1, 1, 0, 0, 1);
                                heartsCnt--;
                                Message msg2 = new Message();
                                msg2.obj = heartsCnt;
                                handlerHeart.sendMessage(msg2);
                            } else soundPool.play(right_sound, 1, 1, 0, 0, 1);
                            break;
                        }
                    }
                    // Если все жизни потрачены, то преждевременно переходим в лобби
                    if(heartsCnt == 0)
                        startActivity(intent);
                }
                // Добавляем монеты пользователю
                coin_count += questions.length / 5 - (3 - heartsCnt);

                // Обновляем количество монет пользователя
                SharedPreferences.Editor editorCnt = spCnt.edit();
                editorCnt.putInt("Coins", coin_count);
                editorCnt.commit();

                // Переходим в лобби
                startActivity(intent);
            }
        }.start();

    }

    // Слушатель кнопок
    private void listener() {
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 0;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 1;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
        ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 2;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
        ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 3;
                soundPool.play(click_sound,1,1,0,0,1);
            }
        });
    }

    // Обновление progressBar
    private void updateProgressBar(){
        countDown_timer.setProgress(progress);
    }
}