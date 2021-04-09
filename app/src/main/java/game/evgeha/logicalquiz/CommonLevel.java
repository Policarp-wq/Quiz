package game.evgeha.logicalquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static game.evgeha.logicalquiz.MainActivity.right_sound;
import static game.evgeha.logicalquiz.MainActivity.soundPool;
import static game.evgeha.logicalquiz.MainActivity.wrong_sound;

public class CommonLevel extends Level {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_level);
        setFullScreen();
        intent = new Intent(CommonLevel.this, LevelSelection.class);
        ans1 = (Button) findViewById(R.id.ans1);
        ans2 = (Button) findViewById(R.id.ans2);
        ans3 = (Button) findViewById(R.id.ans3);
        ans4 = (Button) findViewById(R.id.ans4);
        question_txt = (TextView) findViewById(R.id.question_txt);

        hearts[0] = (ImageView) findViewById(R.id.heart1);
        hearts[1] = (ImageView) findViewById(R.id.heart2);
        hearts[2] = (ImageView) findViewById(R.id.heart3);

        pos = getIntent().getIntExtra("ID", 0) + 1;
        code = getIntent().getStringExtra("CODE");

        countDown_timer = (ProgressBar) findViewById(R.id.timer);
        // Получаем массив вопросов, фактов и картинок для данного уровня
        getArrays();
        createDialogFact(CommonLevel.this);
        listener();

        end = questions.length / 5;
        // Создаём отдельный поток для движения по вопросам
        new Thread() {
            @Override
            public void run() {
                for (stage = 0; stage < end; ++stage) {
                    // Создаём класс вопроса и берём варианты ответа из него
                    Question question = new Question(questions, stage * 5); // !!! Подаётся целый массив, хотя нам нужна всего лишь часть - ОПТИМИЗИРОВАТЬ
                    String[] vars = question.getVars();
                    updateQuestionUi(question.getText(), vars);
                    btn_id = -1;
                    // Ставим таймер на 5 секунд
                    progress = 0;
                    for (; cur_time < TIME * 1000; ++cur_time) {
                        progress = cur_time / (TIME * 10);
                        updateProgressBar();
                        try {
                            this.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Если какая-то кнопка нажата, то получаем её порядковый номер и сравнимаем ответ, принадлежащий данной кнопке с правильным
                        if (btn_id != -1) {
                            // Если ответ неправильный
                            if (vars[btn_id] != question.getAns()) {
                                soundPool.play(wrong_sound, 1, 1, 0, 0, 1);
                                heartsCnt--;
                            } else soundPool.play(right_sound, 1, 1, 0, 0, 1);
                            break;
                        }
                    }
                    if(cur_time == TIME * 1000)
                        --heartsCnt;
                    cur_time = 0;
                    showDialogFact(question.getAns(), facts[stage], png_codes[stage]);
                    // Если все жизни потрачены, то преждевременно переходим в лобби
                    if (heartsCnt == 0)
                        stage = end;
                }
            }
        }.start();
    }
    private void setFullScreen(){
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}