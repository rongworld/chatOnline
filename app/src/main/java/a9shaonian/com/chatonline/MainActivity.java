package a9shaonian.com.chatonline;

import a9shaonian.com.chatonline.service.MessageCome;
import a9shaonian.com.chatonline.service.MessageService;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{
    private EditText mEditTextInput;
    private ListView mListView;
    private List<MessageBean> mData;
    private MessageAdapter mMessageAdapter;
    private SQLiteDatabase db;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 0:
                    String text = (String) msg.obj;
                    showMessage(text, MessageAdapter.SEND_LEFT);
                    break;
                case 1:
                    break;
            }

        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mEditTextInput = findViewById(R.id.edittext_input);
        mListView = findViewById(R.id.listview);
        mListView.setDivider(null);

        Button rightButton = findViewById(R.id.button_right);
        Button leftButton = findViewById(R.id.button_left);

        rightButton.setOnClickListener(this);
        leftButton.setOnClickListener(this);

        db = openOrCreateDatabase("user", MODE_PRIVATE, null);
        db.execSQL("create table if not exists message(id integer primary key autoincrement,msg varchar,date integer,type integer)");

        mData = getMessageFromDB();
        mMessageAdapter = new MessageAdapter(getLayoutInflater(), mData, new Html.ImageGetter() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Drawable getDrawable(String source) {
                return MainActivity.this.getDrawable(R.id.button_right);
            }
        });

        mListView.setAdapter(mMessageAdapter);
        mListView.setSelection(mData.size() - 1);
        Intent intent = new Intent(this, MessageService.class);
        // startService(intent);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {


            MessageService messageService = ((MessageService.MsgBinder) service).getMessageService();
            messageService.setMessageCome(new MessageCome() {
                @Override
                public void MessageCome(final String text) {
                    Message message = new Message();
                    message.arg1 = 0;
                    message.obj = text;
                    handler.sendMessage(message);
                }
            });
            messageService.setDb(db);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onClick(View v) {

        if (mEditTextInput.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "发送的消息不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        String msg = mEditTextInput.getText().toString();
        switch (v.getId()) {
            case R.id.button_left:
                showMessage(msg, MessageAdapter.SEND_LEFT);
                db.execSQL("INSERT INTO message(msg,date,type)values(?,?,?)", new String[]{msg,String.valueOf(System.currentTimeMillis()), "0"});
                break;
            case R.id.button_right:
                showMessage(msg, MessageAdapter.SEND_RIGHT);
                db.execSQL("INSERT INTO message(msg,date,type)values(?,?,?)", new String[]{msg,String.valueOf(System.currentTimeMillis()), "1"});

                break;
        }
        mEditTextInput.setText("");
    }


    private void showMessage(String msg, int type) {
        MessageBean msgData = new MessageBean();
        msgData.setTextViewTime(System.currentTimeMillis());
        msgData.setImageViewPerson(R.drawable.ic_launcher);
        msgData.setTextViewInput(msg);
        msgData.setTextViewName("虫虫");
        switch (type) {
            case MessageAdapter.SEND_LEFT:
                msgData.setType(MessageAdapter.SEND_LEFT);
                break;
            case MessageAdapter.SEND_RIGHT:
                msgData.setType(MessageAdapter.SEND_RIGHT);
                break;
        }

        mData.add(msgData);
        mMessageAdapter.setmData(mData);
        mMessageAdapter.notifyDataSetChanged();
        mListView.setAdapter(mMessageAdapter);
        mListView.setSelection(mData.size() - 1);
    }


    private List<MessageBean> getMessageFromDB() {

        List<MessageBean> messageBeanList = new ArrayList<>();


        Cursor cursor = db.rawQuery("select msg,date,type from message", null);

        while (cursor.moveToNext()) {
            String msg = cursor.getString(0);
            Long date = cursor.getLong(1);
            Integer type = cursor.getInt(2);
            MessageBean messageBean = new MessageBean();
            messageBean.setImageViewPerson(R.drawable.ic_launcher);
            messageBean.setTextViewInput(msg);
            messageBean.setTextViewName("虫虫");
            messageBean.setTextViewTime(date);

            switch (type) {
                case 0:
                    messageBean.setType(MessageAdapter.SEND_LEFT);
                    break;
                case 1:
                    messageBean.setType(MessageAdapter.SEND_RIGHT);
                    break;
                default:
                    messageBean.setType(MessageAdapter.SEND_LEFT);

            }

            messageBeanList.add(messageBean);
        }
        cursor.close();
        return messageBeanList;
    }

}
