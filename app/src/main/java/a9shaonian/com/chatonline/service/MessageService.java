package a9shaonian.com.chatonline.service;

import a9shaonian.com.chatonline.MainActivity;
import a9shaonian.com.chatonline.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MessageService extends Service  {


    private MessageCome messageCome;

    private SQLiteDatabase db;
    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }


    public void setMessageCome(MessageCome messageCome) {
        this.messageCome = messageCome;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        WSManager wsManager = WSManager.getInstance();
        try {
            String address = "ws://192.168.7.104:8080/chat?token=0";
            wsManager.init(address);
            //wsManager.addMsgListener(this);

            wsManager.addMsgListener(new StatusChangeListener() {

                @Override
                public void connect() {
                    Log.i("conn","链接成功");

                }

                @Override
                public void disconnect() {
                    Log.i("conn","链接失败");

                }

                @Override
                public void error() {
                    Log.i("conn","错误");
                }

                @Override
                public void message(String text) {
                    messageCome.MessageCome(text);
                    MessageService.this.notify(text);
                    db.execSQL("INSERT INTO message(msg,date,type)values(?,?,?)", new String[]{text,String.valueOf(System.currentTimeMillis()), "0"});
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }

   public class MsgBinder extends Binder{
        public MessageService getMessageService(){
            return MessageService.this;
        }
    }



    private void notify(String message){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("虫虫");
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this, MainActivity.class),PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent,false);
        builder.setAutoCancel(true);

        Notification notification = builder.getNotification();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }
}
