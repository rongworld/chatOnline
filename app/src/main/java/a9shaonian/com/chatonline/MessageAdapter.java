package a9shaonian.com.chatonline;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends BaseAdapter {
    public final static int SEND_LEFT = 0;
    public final static int SEND_RIGHT = 1;
    private LayoutInflater mInflater;

    public void setmData(List<MessageBean> mData) {
        this.mData = mData;
    }

    private List<MessageBean> mData;
    private Html.ImageGetter mImageGetter;


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        if (0 == mData.get(position).getType()) {
            return SEND_LEFT;//消息类型在左边
        } else if (1 == mData.get(position).getType()) {
            return SEND_RIGHT;//消息类型在右边
        } else {
            return 0;
        }
    }

    MessageAdapter(LayoutInflater mInflater, List<MessageBean> mData, Html.ImageGetter mImageGetter) {
        this.mInflater = mInflater;
        this.mData = mData;
        this.mImageGetter = mImageGetter;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            /*
            通过判断消息类型的不同，加载不同的布局。这里使用到了getItemViewType()方法。
             */
            switch (getItemViewType(position)) {
                case SEND_LEFT:
                    convertView = mInflater.inflate(R.layout.left_msg, null);
                    break;
                case SEND_RIGHT:
                    convertView = mInflater.inflate(R.layout.right_msg, null);
                    break;
                default:
                    break;
            }
            viewHolder.imageViewPerson = convertView.findViewById(R.id.imageview_person);
            viewHolder.textViewTime = convertView.findViewById(R.id.textview_time);
            viewHolder.textViewName = convertView.findViewById(R.id.textview_name);
            viewHolder.textViewInput = convertView.findViewById(R.id.textview_input);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MessageBean data = mData.get(position);
        //如果不是第一个Item且发送消息的两次时间在1min之内，则不再显示时间；否则显示时间
        Long time = data.getTextViewTime();
        if (position != 0) {
            MessageBean dataBefore = mData.get(position - 1);
            long dateDifference = data.getTextViewTime() - dataBefore.getTextViewTime();
            if (dateDifference < 60000) {
                viewHolder.textViewTime.setVisibility(View.GONE);
            } else {
                showTime(viewHolder.textViewTime, time);
            }
        } else {
            showTime(viewHolder.textViewTime, time);
        }

        viewHolder.imageViewPerson.setImageResource(data.getImageViewPerson());

        viewHolder.textViewName.setText(data.getTextViewName());
        //将受到的数据以富文本的形式显示。
        Spanned spanned = Html.fromHtml(data.getTextViewInput(), mImageGetter, null);
        viewHolder.textViewInput.setText(spanned);
        return convertView;
    }


    private void showTime(TextView textView, Long date) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        String time = format.format(new Date(date));
        textView.setVisibility(View.VISIBLE);
        textView.setText(time);
    }


    class ViewHolder {
        ImageView imageViewPerson;
        TextView textViewName;
        TextView textViewTime;
        TextView textViewInput;
    }
}
