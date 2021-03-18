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

    private String[] vars = new String[4];

    private int numb = 0;
    private boolean rightAns = false;

    private Handler handlerAns, handlerQuest_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_level);
        //Убираем херню сверху
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ans1 = (Button) findViewById(R.id.ans1);
        ans2 = (Button) findViewById(R.id.ans2);
        ans3 = (Button) findViewById(R.id.ans3);
        ans4 = (Button) findViewById(R.id.ans4);
        question_txt = (TextView) findViewById(R.id.question_txt);

        String[] questions = getResources().getStringArray(R.array.animals_questions);

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

        handlerQuest_txt = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                String txt = (String)msg.obj;
                question_txt.setText(txt);
            }
        };
        // Моментально пролистывается!!

        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < questions.length / 5; ++i) {
                    Log.d("Iteration", String.valueOf(i));
                    Question question = new Question(questions, i * 5);

                    //Создаём сообщение хендлеру
                    Message msg = new Message();
                    //Передаём в него текст вопроса
                    msg.obj = question.getName();
                    handlerQuest_txt.sendMessage(msg);

                    //Передаём в него варианты ответов
                    Message msg1 = new Message();
                    msg1.obj = question.getVars();
                    handlerAns.sendMessage(msg1);

                    vars = question.getVars();

                    /*ans1.setText(vars[0]);
                    ans2.setText(vars[1]);
                    ans3.setText(vars[2]);
                    ans4.setText(vars[3]);*/

                   /* try {
                        this.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                    /*View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (v.getId()) {
                                case R.id.ans1:
                                    if (vars[0].equals(question.getAns()))
                                        // rightAns = true;
                                        break;
                                case R.id.ans2:
                                    if (vars[1].equals(question.getAns()))
                                        // rightAns = true;
                                        break;
                                case R.id.ans3:
                                    if (vars[2].equals(question.getAns()))
                                        //  rightAns = true;
                                        break;
                                case R.id.ans4:
                                    if (vars[3].equals(question.getAns()))
                                        // rightAns = true;
                                        break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + v.getId());
                            }
                        }
                    };
                    ans1.setOnClickListener(onClickListener);
                    ans2.setOnClickListener(onClickListener);
                    ans3.setOnClickListener(onClickListener);
                    ans4.setOnClickListener(onClickListener);*/
                }
            }
        }.start();
    }
}