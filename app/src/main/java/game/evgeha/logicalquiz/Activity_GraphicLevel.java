package game.evgeha.logicalquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import static game.evgeha.logicalquiz.Activity_Main.coin_count;
import static game.evgeha.logicalquiz.Activity_Main.right_sound;
import static game.evgeha.logicalquiz.Activity_Main.wrong_sound;

public class Activity_GraphicLevel extends Level {

    private ImageView question_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic_level);
        setFullScreen();
        intent = new Intent(Activity_GraphicLevel.this, Activity_LevelSelection.class);
        // Предустановка базовых элементов уровня
        question_img = (ImageView)findViewById(R.id.question_img);
        levelMainSetUp(Activity_GraphicLevel.this);
        // Создаём отдельный поток для движения по вопросам
        new Thread() {
            @Override
            public void run() {
                for (stage = 0; stage < end; ++stage) {
                    // Создаём класс вопроса и берём варианты ответа из него
                    int id = stage * 5;
                    Question question = new Question(questions[id], questions[id + 1], questions[id + 2], questions[id + 3], questions[id + 4]);
                    String[] vars = question.getVars();
                    String fact = facts[stage], png_code = png_codes[stage];
                    updateQuestionUi(question.getText(), vars, png_code);
                    btn_id = -1;
                    if(hints[stage].equals("null") || coin_count - penalty < hint_cost) {
                        disableHint();
                    } else enableHint();
                    // Ставим таймер на 15 секунд
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
                                playSound(wrong_sound);
                                heartsCnt--;
                            } else playSound(right_sound);
                            break;
                        }
                    }
                    updateQuestionUi(question.getText(), vars, png_code);
                    if(cur_time == TIME * 1000)
                        --heartsCnt;
                    showDialogFact(question.getAns(), fact, png_code);
                    if (heartsCnt == 0) {
                        stage = end - 1;
                    }
                    // Если все жизни потрачены, то преждевременно переходим в лобби
                }
            }
        }.start();
    }
    public void updateQuestionUi(String question, String[] vars, String png_code){
        int png_id = getResources().getIdentifier(png_code, "drawable", getPackageName());
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
                question_img.setImageResource(png_id);
            }
        };
        runOnUiThread(runnable);
    }
}