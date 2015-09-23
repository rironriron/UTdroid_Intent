package jp.ac.u_tokyo.t.utdroid_intent;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /* Viewを格納するための変数 */
    private TextView textViewGallery;
    private TextView textViewIntentForResult;
    private TextView textViewFilePicker;
    private TextView textView;

    /* Intentにオブジェクトを添付する際もKey-Valueストアの考え方に従う */
    public final static String INTENT_KEY_RESULT = "intentKeyResult";
    public final static String INTENT_KEY_FILEPATH = "intentKeyFilePath";

    /* IntentForResultが戻った時に、どのリクエストの結果か分かるようマーキングするための定数 */
    private final int REQUEST_GALLERY = 0;
    private final int REQUEST_INTENT = 1;
    private final int REQUEST_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* データ表示用のTextViewだけ先に読み込んでおく */
        textViewGallery = (TextView) findViewById(R.id.textViewGallery);
        textViewIntentForResult = (TextView) findViewById(R.id.textViewIntentForResult);
        textViewFilePicker = (TextView) findViewById(R.id.textViewFilePicker);
        textView = (TextView) findViewById(R.id.textView);

        /* 受信したIntent（ファイル）の読み込み */
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_VIEW)) {
                /* 今回はファイルの絶対パスを表示するだけ */
                String path = intent.getData().getPath();
                textView.setText(path);
            }
        }

        findViewById(R.id.buttonBrowser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 他のアプリのパッケージ名とActivity名を名指しして、その画面を開く（明示的なIntent）
                   指定のアプリやActivityが存在しないと強制終了するので危険。 */
                Intent intent = new Intent();
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 設定アプリの位置情報の画面を開く */
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        /**
         * ここから6つは、宛先のアプリやActivityを明示的に指定しない「暗黙のIntent」
         */
        findViewById(R.id.buttonWeb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 指定のWebページ（https://bitly.com/UTdroid）をブラウザで開く */
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://bitly.com/UTdroid"));
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 宛先と件名、本文を指定してメーラーを開く */
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:example@example.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "件名をここに書く。");
                intent.putExtra(Intent.EXTRA_TEXT, "本文をここに書く。");
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 緯度経度やキーワードを指定して地図を開く */
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                /* intent.setData(Uri.parse("geo:35.6813818,139.7660838")); */
                intent.setData(Uri.parse("geo:0,0?q=Tokyo"));
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonTel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 電話の発信画面を開き、指定の番号（117）を入力する */
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:117"));
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonMarket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Google PLAYで指定したパッケージ名（jp.tokyometro）のアプリを表示する */
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=jp.tokyometro"));
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* ギャラリーを開いてonActivityResultで結果を受け取る */
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        findViewById(R.id.buttonIntent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* SubActivityを開く */
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
                /* アニメーションを付与 */
                overridePendingTransition(R.anim.open_slide_left, R.anim.close_slide_left_half);
            }
        });

        findViewById(R.id.buttonIntentForResult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* SubActivityを開いてonActivityResultで結果を受け取る */
                Intent intent = new Intent(MainActivity.this, SubActivity.class);

                /* Primitiveな型のデータ、Percelableを実装した型のデータをKey-Value形式で添付できる */
                MyData myData = new MyData("めちゃ", 2);
                intent.putExtra(INTENT_KEY_RESULT, myData);
                startActivityForResult(intent, REQUEST_INTENT);

                /* アニメーションを付与 */
                overridePendingTransition(R.anim.open_slide_left, R.anim.close_slide_left_half);
            }
        });

        findViewById(R.id.buttonFilePicker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* FileListActivityを開く */
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivityForResult(intent, REQUEST_FILE);
            }
        });
    }

    /**
     * startActivityForResultの結果が返って来た時に呼ばれるメソッド
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    /* 選択した画像の絶対パスを調べる */
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    /* 画面に表示する */
                    textViewGallery.setText(picturePath);
                } else if (resultCode == RESULT_CANCELED) {
                    /* キャンセルされた旨を画面に表示する */
                    textViewGallery.setText("キャンセルされました。");
                }
                break;
            case REQUEST_INTENT:
                if (resultCode == RESULT_OK) {
                    /* 結果画面に表示する */
                    String result = data.getStringExtra(INTENT_KEY_RESULT);
                    textViewIntentForResult.setText(result);
                } else if (resultCode == RESULT_CANCELED) {
                    /* キャンセルされた旨を画面に表示する */
                    textViewIntentForResult.setText("キャンセルされました。");
                }
                break;
            case REQUEST_FILE:
                if (resultCode == RESULT_OK) {
                    /* 選択したファイルの絶対パスを取得する */
                    String filePath = data.getStringExtra(INTENT_KEY_FILEPATH);

                    /* 画面に表示する */
                    textViewFilePicker.setText(filePath);
                } else if (resultCode == RESULT_CANCELED) {
                    /* キャンセルされた旨を画面に表示する */
                    textViewFilePicker.setText("キャンセルされました。");
                }
                break;
        }
    }
}
