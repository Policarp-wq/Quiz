package game.evgeha.logicalquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CommonLevel extends AppCompatActivity {

    private Button ans1, ans2, ans3, ans4;
    private TextView question_txt;

    private String[] vars = new String[4]; // Варианты ответов

    private int numb = -1;

    private Handler handlerAns, handlerQuest_txt;

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

        // Получаем массив вопросов для данного уровня
        String[] questions = getResources().getStringArray(R.array.animals_questions);

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

        // Создаём отдельный поток для движения по этапам
        new Thread(){
            @Override
            public void run() {
                for(int i = 0; i < questions.length / 5; ++i) {
                    Question question = new Question(questions, i * 5);
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

                    // Ставим таймер на 15 секунд
                    for(int j = 0; j < 60; ++j){
                        try {
                            this.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Если какая-то кнопка нажата, то получаем её порядковый номер и сравнимаем ответ, принадлежащий данной кнопке с правильным
                        if(numb != -1) {
                            if(vars[numb] == question.getAns())
                                break;
                        }
                    }
                }
            }
        }.start();
    }

    // Слушатель кнопок
    private void listener() {
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 0;
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 1;
            }
        });
        ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 2;
            }
        });
        ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numb = 3;
            }
        });

    }
}