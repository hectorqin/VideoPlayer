package com.htmake.videoplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.widget.SimpleAdapter;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnItemClickListener{

    private ListView mListView;
    private List sourceList;
    private final String sourceFile = "source.txt";
    private JZVideoPlayerStandard mJZVideoPlayerStandard;
    private Map<String, String> headerMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sourceList = readAssetSourceFile(sourceFile);
        mListView = (ListView) findViewById(R.id.sourceList);
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, sourceList,
                R.layout.source_item, new String[] { "name" },
                new int[] { R.id.name});
        // 设置适配器
        mListView.setAdapter(adapter);
        // 绑定item的点击事件
        mListView.setOnItemClickListener(this);
        // 避免出现返回
        mJZVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        mJZVideoPlayerStandard.setUp("", JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "");

        headerMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        mJZVideoPlayerStandard.headData = headerMap;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (JZVideoPlayer.backPress()) {
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public List<Map<String, String>> readAssetSourceFile(String strFilePath) {
        List<Map<String, String>> sourceList = new ArrayList<Map<String, String>>(); //文件内容解析数据
        try {
            InputStream instream = getAssets().open(strFilePath);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream, "utf-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while (( line = buffreader.readLine()) != null) {
                    if(!TextUtils.isEmpty(line.trim())){
                        sourceList.add(parseLine(line));
                    }
                }
                instream.close();
            }
        } catch (IOException e) {
            Log.d("File Exception", e.getMessage());
        }
        return sourceList;
    }

    public Map<String, String> parseLine(String line){
        String[] strArr = line.split(",");
        if(strArr.length < 2){
            strArr = line.split("，");
        }
        Map<String, String> data = new HashMap<String, String>();
        if(strArr.length >= 2){
            data.put("name", strArr[0]);
            data.put("url", strArr[1].replaceAll("\n|\r", ""));
        }

        return data;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        String name = (String)((HashMap)sourceList.get(position)).get("name");
        String url = (String)((HashMap)sourceList.get(position)).get("url");
        Toast.makeText(this, "点击了第" + position + "条数据, 即将播放" + url, Toast.LENGTH_SHORT).show();
        if(mJZVideoPlayerStandard == null){
            mJZVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        }
        mJZVideoPlayerStandard.releaseAllVideos();
        mJZVideoPlayerStandard.setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, name);
        mJZVideoPlayerStandard.headData = headerMap;
        mJZVideoPlayerStandard.startVideo();
    }
}
