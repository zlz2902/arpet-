package com.example.arpet.ar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.sceneview.ar.ArSceneView;

/**
 * 封装 {@link ArSceneView} 的生命周期与平面点击入口，便于 Activity 保持精简。
 */
public final class ArSessionFacade {

    private final ArSceneView sceneView;

    public ArSessionFacade(@NonNull ArSceneView sceneView) {
        this.sceneView = sceneView;
    }

    public void resume() {
        try {
            sceneView.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        sceneView.pause();
    }

    public void setOnPlaneTapListener(@Nullable ArSceneView.OnTapArPlaneListener listener) {
        sceneView.setOnTapArPlaneListener(listener);
    }

    @NonNull
    public ArSceneView getSceneView() {
        return sceneView;
    }
}
