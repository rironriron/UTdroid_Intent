package jp.ac.u_tokyo.t.utdroid_intent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListActivity extends AppCompatActivity {
    /* Viewを格納するための変数 */
    private TextView textViewDirName;
    private TextView textViewFileName;
    private ListView listView;
    private Button buttonOK;
    private Button buttonCancel;

    /* 現在のディレクトリの絶対パスを保持する変数 */
    private String currentDirectory;

    /* SDカードにアクセスするPermission取得のための定数 */
    private static final int SD_ACCESS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);

        /* ディレクトリの初期値をSDカードの一番上に設定 */
        currentDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();

        textViewDirName = (TextView) findViewById(R.id.textViewDirName);
        textViewFileName = (TextView) findViewById(R.id.textViewFileName);
        listView = (ListView) findViewById(R.id.listView);
        buttonOK = (Button) findViewById(R.id.buttonOK);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* 選択されたセルの内容（ファイル名・ディレクトリ名）を取得 */
                String item = (String) parent.getItemAtPosition(position);
                if (item.equals("..")) {
                    /* 上のディレクトリへ移動 */
                    if (currentDirectory.lastIndexOf("/") == 0) {
                        /* ルートディレクトリにいる時 */
                        currentDirectory = "/";
                    } else {
                        /* 一つ前のディレクトリへ */
                        currentDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
                    }
                    /* そのディレクトリの中身を読み込む */
                    makeFileList();
                } else if (item.endsWith("/")) {
                    /* ディレクトリの場合 */
                    if (currentDirectory.equals("/")) {
                        /* ルートディレクトリにいる時 */
                        currentDirectory = currentDirectory + item.replace("/", "");
                    } else {
                        /* 一つ前のディレクトリへ */
                        currentDirectory = currentDirectory + "/" + item.replace("/", "");
                    }
                    /* そのディレクトリの中身を読み込む */
                    makeFileList();
                } else {
                    /* ファイルの場合 */
                    /* ファイル名を画面に表示 */
                    textViewFileName.setText(item);
                }
            }
        });

        /* OKボタン */
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* ファイルの絶対パスを取得 */
                String filePath = "";
                if (currentDirectory.equals("/")) {
                    filePath = "/" + textViewFileName.getText();
                } else {
                    filePath = currentDirectory + "/" + textViewFileName.getText();
                }

                /* 元のActivityに結果を返す */
                Intent intent = new Intent();
                intent.putExtra(MainActivity.INTENT_KEY_FILEPATH, filePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        /* Cancelボタン */
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 元のActivityにキャンセルされた旨を返す */
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        /* Android 6.0以上かどうかで条件分岐 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /* Permissionを取得済みかどうか確認 */
            String[] dangerousPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                /* 未取得ならPermissionを要求 */
                requestPermissions(dangerousPermissions, SD_ACCESS_REQUEST_CODE);
            }else{
                /* 現在のディレクトリの中身を読み込む */
                makeFileList();
            }
        }else{
            /* 現在のディレクトリの中身を読み込む */
            makeFileList();
        }
    }

    /*
     * Android 6.0以上のDANGEROUS_PERMISSION対策
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SD_ACCESS_REQUEST_CODE) {
            // Permissionが許可された
            if (grantResults.length == 0) {
                return;
            }else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /* 現在のディレクトリの中身を読み込む */
                makeFileList();
            } else {
                Toast.makeText(this, "SDカードへのアクセスを許可して下さい。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* 現在のディレクトリの中身を読み込むメソッド */
    private void makeFileList() {
        /* カレントディレクトリのファイル一覧を取得 */
        File[] files = new File(currentDirectory).listFiles();
        if (files == null) {
            /* アクセス不能の旨を表示 */
            Toast.makeText(FileListActivity.this, "アクセスできません。", Toast.LENGTH_SHORT).show();
            /* 上のディレクトリへ移動 */
            if (currentDirectory.lastIndexOf("/") == 0) {
                /* ルートディレクトリにいる時 */
                currentDirectory = "/";
            } else {
                currentDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));
            }
            return;
        }
        /* TextViewにカレントディレクトリ名を表示 */
        textViewDirName.setText(currentDirectory);

        /* Listを作る */
        List<String> fileList = new ArrayList<String>();
        if (!currentDirectory.equals("/")) {
            /* ルートディレクトリでない場合は、上のディレクトリへ行けるように".."を先頭に追加 */
            fileList.add("..");
        }
        /* ファイルをListに追加していく */
        for (File file : files) {
            if (file.isDirectory()) {
                /* ディレクトリなら末尾に"/"を表示 */
                fileList.add(file.getName() + "/");
            } else {
                fileList.add(file.getName());
            }
        }

        /* ListからArrayAdapterを作る */
        ArrayAdapter<String> fileListAdapter = new ArrayAdapter<String>(getApplicationContext()
                , R.layout.cell_filelist, fileList);
        /* ArrayAdapterをListViewにセットする */
        listView.setAdapter(fileListAdapter);
    }
}
