package com.example.arpet.pet;

import android.content.Context;

import androidx.annotation.NonNull;
import com.example.arpet.config.PetExperienceConfig;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;

import io.github.sceneview.ar.ArSceneView;
import io.github.sceneview.ar.node.ArModelNode;

/**
 * 负责 GLB 加载与「点击平面 → 锚定宠物」；不关心 Toast / TextView 文案，由 UI 层处理。
 */
public final class PetAnchorPresenter {

    public interface LoadCallbacks {
        void onModelReady();

        void onModelLoadFailed(@NonNull Throwable error);
    }

    private final ArSceneView sceneView;
    private final PetExperienceConfig config;
    private final ArModelNode modelNode;

    public PetAnchorPresenter(@NonNull ArSceneView sceneView, @NonNull PetExperienceConfig config) {
        this.sceneView = sceneView;
        this.config = config;
        this.modelNode = new ArModelNode(sceneView.getEngine(), config.getPlacementMode());
    }

    public void loadModel(@NonNull Context context, @NonNull LoadCallbacks callbacks) {
        modelNode.loadModelGlbAsync(
                context,
                config.getModelAssetPath(),
                config.isUseModelCache(),
                config.getInitialScale(),
                null,
                modelInstance -> {
                    callbacks.onModelReady();
                    return null;
                },
                exception -> {
                    callbacks.onModelLoadFailed(exception);
                    return null;
                }
        );
    }

    /**
     * 将宠物锚定到平面命中点；若尚未加入场景图则追加。
     */
    public void placeFromHit(@NonNull HitResult hitResult) {
        Anchor anchor = hitResult.createAnchor();
        modelNode.setAnchor(anchor);
        if (!sceneView.getEntities().contains(modelNode.getEntity())) {
            sceneView.addChild(modelNode);
        }
    }

    @NonNull
    public ArModelNode getModelNode() {
        return modelNode;
    }

    @NonNull
    public ArSceneView getSceneView() {
        return sceneView;
    }

    @NonNull
    public PetExperienceConfig getConfig() {
        return config;
    }
}
