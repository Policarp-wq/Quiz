package game.evgeha.logicalquiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import static game.evgeha.logicalquiz.Activity_Main.coin_count;
import static game.evgeha.logicalquiz.Activity_Main.right_sound;
import static game.evgeha.logicalquiz.Activity_Main.wrong_sound;

public class Activity_CommonLevel extends Level {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_level);
        intent = new Intent(Activity_CommonLevel.this, Activity_LevelSelection.class);
        // Предустановка базовых элементов уровня
        levelMainSetUp(Activity_CommonLevel.this);
        // Создаём отдельный поток для движения по вопросам
        new Thread() {
            @Override
            public void run() {
                for (stage = 0; stage < end; ++stage) {
                    // Создаём класс вопроса и берём варианты ответа из него
                    int id = stage * 5;
                    Question question = new Question(questions[id], questions[id + 1], questions[id + 2], questions[id + 3], questions[id + 4]);
                    String[] vars = question.getVars();
                    updateQuestionUi(question.getText(), vars);
                    updateProgressBar(stage);
                    String fact = facts[stage], png_code = png_codes[stage];
                    btn_id = -1;
                    if(hints[stage].equals("null") || coin_count - penalty < hint_cost) {
                        disableHint();
                    } else enableHint();
                    // Ставим таймер на TIME секунд
                    time_progress = 0;
                    for (; cur_time < TIME * 1000; ++cur_time) {
                        time_progress = cur_time / (TIME * 10);
                        updateCurTime();
                        try {
                            this.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Если какая-то кнопка нажата, то получаем её порядковый номер и сравнимаем ответ, принадлежащий данной кнопке с правильным
                        if (btn_id != -1) {
                            // Если ответ неправильный
                            if (!vars[btn_id].equals(question.getAns())) {
                                playSound(wrong_sound);
                                heartsCnt--;
                            } else{
                                ++right_ans_cnt;
                                playSound(right_sound);
                            }
                            break;
                        }
                    }
                    updateQuestionUi(question.getText(), vars);
                    if(cur_time == TIME * 1000) {
                        --heartsCnt;
                        playSound(wrong_sound);
                    }
                    showDialogFact(question.getAns(), fact, png_code);
                    if (heartsCnt == 0) {
                        stage = end - 1;
                    }
                    // Если все жизни потрачены, то преждевременно переходим в лобби
                }
            }
        }.start();
    }

}