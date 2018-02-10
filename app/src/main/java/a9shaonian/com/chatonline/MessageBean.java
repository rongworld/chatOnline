package a9shaonian.com.chatonline;


public class MessageBean {
    private int imageViewPerson;//人物头像
    private long textViewTime;//显示的时间

    private String textViewName;//人物昵称
    private String textViewInput;//说话内容
    private int type;//信息类型，是在左边显示还是右边显示。

    /*
    定义两个构造器，一个无参，一个传值。
     */

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImageViewPerson() {
        return imageViewPerson;
    }

    public void setImageViewPerson(int imageViewPerson) {
        this.imageViewPerson = imageViewPerson;
    }

    public long getTextViewTime() {
        return textViewTime;
    }

    public void setTextViewTime(long textViewTime) {
        this.textViewTime = textViewTime;
    }



    public String getTextViewName() {
        return textViewName;
    }

    public void setTextViewName(String textViewName) {
        this.textViewName = textViewName;
    }

    public String getTextViewInput() {
        return textViewInput;
    }

    public void setTextViewInput(String textViewInput) {
        this.textViewInput = textViewInput;
    }
}