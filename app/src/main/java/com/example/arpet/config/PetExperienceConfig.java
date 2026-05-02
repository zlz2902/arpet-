package com.example.arpet.config;

import io.github.sceneview.ar.node.PlacementMode;

/**
 * 单机 AR 宠物体验的静态配置。后续可改为读取 assets 里的 JSON 或 SharedPreferences。
 */
public final class PetExperienceConfig {

    private final String modelAssetPath;
    private final float initialScale;
    private final boolean useModelCache;
    private final PlacementMode placementMode;
    private final String readyStatusMessage;

    public PetExperienceConfig(
            String modelAssetPath,
            float initialScale,
            boolean useModelCache,
            PlacementMode placementMode,
            String readyStatusMessage
    ) {
        this.modelAssetPath = modelAssetPath;
        this.initialScale = initialScale;
        this.useModelCache = useModelCache;
        this.placementMode = placementMode;
        this.readyStatusMessage = readyStatusMessage;
    }

    /**
     * 默认使用仓库内已附带的 {@code sample_pet.glb}（Khronos glTF 官方示例「Duck」，
     * 仅作占位，许可见 https://github.com/KhronosGroup/glTF-Sample-Models ）。
     * 若你有自有/已授权的宠物模型，把 glb 放入 {@code assets/} 并改此文件名即可。
     */
    public static PetExperienceConfig defaultConfig() {
        return new PetExperienceConfig(
                "sample_pet.glb",
                0.5f,
                false,
                PlacementMode.INSTANT,
                "模型加载完成，点击地面放置！"
        );
    }

    public String getModelAssetPath() {
        return modelAssetPath;
    }

    public float getInitialScale() {
        return initialScale;
    }

    public boolean isUseModelCache() {
        return useModelCache;
    }

    public PlacementMode getPlacementMode() {
        return placementMode;
    }

    public String getReadyStatusMessage() {
        return readyStatusMessage;
    }
}
