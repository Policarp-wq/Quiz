package game.evgeha.logicalquiz;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    private Dialog dialog;

    private SharedPreferences spCnt;

    private Handler handlerHeart, handlerFact_txt, handlerAns, handlerFact_png;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_level);
        setFullScreen();
        ans1 = (Button) findViewById(R.id.ans1);
        ans2 = (Button) findViewById(R.id.ans2);
        ans3 = (Button) findViewById(R.id.ans3);
        ans4 = (Button) findViewById(R.id.ans4);
        question_txt = (TextView) findViewById(R.id.question_txt);

        hearts[0] = (ImageView) findViewById(R.id.heart1);
        hearts[1] = (ImageView) findViewById(R.id.heart2);
        hearts[2] = (ImageView) findViewById(R.id.heart3);

        pos = getIntent().getIntExtra("ID", 0) + 1;

        countDown_timer = (ProgressBar) findViewById(R.id.timer);

        // Получаем массив вопросов, фактов и картинок для данного уровня
        String[] questions = getResources().getStringArray(R.array.animals_questions);
        String[] facts = getResources().getStringArray(R.array.animals_facts);
        String[] png_codes = getResources().getStringArray(R.array.animals_ans);

        Intent intent = new Intent(CommonLevel.this, LevelSelection.class);

        spCnt = getSharedPreferences("Coins", Context.MODE_PRIVATE);

        listener();

        handlerAns = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String txt = (String) msg.obj;
                fact_txt.setText(txt);
            }
        };

        handlerAns = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String txt = (String) msg.obj;
                right_ans.setText(txt);
            }
        };
        handlerFact_png = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                int id = (int) msg.obj;
                fact_png.setImageResource(id);
            }
        };
        // Создаём отдельный поток для движения по вопросам
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < questions.length / 5; ++i) {
                    // Создаём класс вопроса и берём варианты ответа из него
                    Question question = new Question(questions, i * 5); // !!! Подаётся целый массив, хотя нам нужна всего лишь часть - ОПТИМИЗИРОВАТЬ
                    String[] vars = question.getVars();
                    updateQuestionUi(question.getText(), vars);
                    btn_id = -1;
                    // Ставим таймер на 5 секунд
                    progress = 0;
                    for (int j = 0; j < TIME * 1000; ++j) {
                        progress = j / (TIME * 10);
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
                    // Если все жизни потрачены, то преждевременно переходим в лобби
                    if (heartsCnt == 0)
                        startActivity(intent);
                }
                addCoin(questions.length);
                // Переходим в лобби
                startActivity(intent);
            }
        }.start();
    }

    private void setFullScreen(){
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showDialogFact(String ans, String txt, String code){
        dialog = new Dialog(CommonLevel.this);
        dialogSetUp(dialog, R.layout.dialog_window_facts, true);

        //Button btn_continue = (Button)dialog.findViewById(R.id.question_continue);
        TextView fact_txt = (TextView)dialog.findViewById(R.id.fact_description);
        TextView right_ans = (TextView)dialog.findViewById(R.id.right_ans);
        ImageView fact_png = (ImageView)dialog.findViewById(R.id.fact_img);
        int id = getResources().getIdentifier(code, "drawable", getPackageName());

        dialogSetUi(ans, txt, id);

       /* btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/
        dialog.show();
    }

    private void dialogSetUp(Dialog dialog, int id, boolean cancelable){
        dialog.setContentView(id); // Что будет показывать диалоговое окно
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Сделаем задний фон прозрачным
        dialog.setCancelable(cancelable); // Окно можно закрыть только выбрав какой-либо вариант
    }

    private void dialogSetUi(String ans, String txt, int id){
        Message msg = new Message();
        msg.obj = ans;
        handlerAns.sendMessage(msg);
        msg.obj = txt;
        handlerFact_txt.sendMessage(msg);
        msg.obj = id;
        handlerFact_png.sendMessage(msg);
    }

}