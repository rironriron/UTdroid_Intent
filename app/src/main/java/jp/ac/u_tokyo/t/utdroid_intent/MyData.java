package jp.ac.u_tokyo.t.utdroid_intent;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Intentを通してデータをやり取りできる（Parcelableな）クラス
 */
public class MyData implements Parcelable {
    /* 文字列 */
    public String text;
    /* 繰り返し回数 */
    public int repeat;

    public MyData(String text, int repeat) {
        this.text = text;
        this.repeat = repeat;
    }

    /**
     * MyData型に格納されたデータを文字列として返すメソッド
     * 例）text="めちゃ", repeat="2" => "めちゃめちゃ"
     * 例）text="ゴリラ", repeat="3" => "ゴリラゴリラゴリラ"
     */
    public String getMessage() {
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<repeat; i++){
            buffer.append(text);
        }
        return buffer.toString();
    }

    /**
     * ここからParcelable関連
     */
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        /* メンバ変数をParcel（小包）に書き込む */
        out.writeString(text);
        out.writeInt(repeat);
    }

    public static final Parcelable.Creator<MyData> CREATOR = new Parcelable.Creator<MyData>() {
        public MyData createFromParcel(Parcel in) {
            return new MyData(in);
        }

        public MyData[] newArray(int size) {
            return new MyData[size];
        }
    };

    private MyData(Parcel in) {
        /* Parcel（小包）からメンバ変数を読み出す時は書き込んだ時と同じ順序で */
        this.text = in.readString();
        this.repeat = in.readInt();
    }
}
