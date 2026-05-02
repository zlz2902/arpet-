package com.example.arpet;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arpet.ar.ArSessionFacade;
import com.example.arpet.config.PetExperienceConfig;
import com.example.arpet.pet.PetAnchorPresenter;

import io.github.sceneview.ar.ArSceneView;

/**
 * 入口：组装 AR 会话、宠物锚点逻辑与界面提示。业务扩展优先改 {@code pet} / {@code ar} / {@code config} 包。
 */
public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private ArSessionFacade arSession;
    private PetAnchorPresenter petPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArSceneView sceneView = findViewById(R.id.sceneView);
        statusText = findViewById(R.id.statusText);

        PetExperienceConfig config = PetExperienceConfig.defaultConfig();
        petPresenter = new PetAnchorPresenter(sceneView, config);
        petPresenter.loadModel(this, new PetAnchorPresenter.LoadCallbacks() {
            @Override
            public void onModelReady() {
                statusText.setText(config.getReadyStatusMessage());
            }

            @Override
            public void onModelLoadFailed(Throwable error) {
                statusText.setText("模型加载失败: " + error.getMessage());
            }
        });

        arSession = new ArSessionFacade(sceneView);
        arSession.setOnPlaneTapListener((hitResult, plane, motionEvent) -> {
            petPresenter.placeFromHit(hitResult);
            Toast.makeText(this, "小精灵出现了！", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        arSession.resume();
    }

    @Override
    protected void onPause() {
        arSession.pause();
        super.onPause();
    }
}
