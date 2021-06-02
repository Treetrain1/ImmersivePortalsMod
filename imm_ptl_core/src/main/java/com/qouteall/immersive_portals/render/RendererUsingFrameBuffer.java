package com.qouteall.immersive_portals.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.qouteall.immersive_portals.CGlobal;
import com.qouteall.immersive_portals.ducks.IEMinecraftClient;
import com.qouteall.immersive_portals.portal.Portal;
import com.qouteall.immersive_portals.portal.PortalLike;
import com.qouteall.immersive_portals.render.context_management.PortalRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class RendererUsingFrameBuffer extends PortalRenderer {
    SecondaryFrameBuffer secondaryFrameBuffer = new SecondaryFrameBuffer();
    
    @Override
    public void onBeforeTranslucentRendering(MatrixStack matrixStack) {
        renderPortals(matrixStack);
    }
    
    @Override
    public void onAfterTranslucentRendering(MatrixStack matrixStack) {
    
    }
    
    @Override
    public void onRenderCenterEnded(MatrixStack matrixStack) {
    
    }
    
    @Override
    public void finishRendering() {
    
    }
    
    @Override
    public void prepareRendering() {
        secondaryFrameBuffer.prepare();
    
        GlStateManager._enableDepthTest();
    
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    
        if (CGlobal.shaderManager == null) {
            CGlobal.shaderManager = new ShaderManager();
        }
    }
    
    @Override
    protected void doRenderPortal(
        PortalLike portal,
        MatrixStack matrixStack
    ) {
        if (PortalRendering.isRendering()) {
            //only support one-layer portal
            return;
        }
    
        if (!testShouldRenderPortal(portal, matrixStack)) {
            return;
        }
    
        PortalRendering.pushPortalLayer(portal);
    
        Framebuffer oldFrameBuffer = client.getFramebuffer();
    
        ((IEMinecraftClient) client).setFrameBuffer(secondaryFrameBuffer.fb);
        secondaryFrameBuffer.fb.beginWrite(true);
        
        GlStateManager._clearColor(1, 0, 1, 1);
        GlStateManager._clearDepth(1);
        GlStateManager._clear(
            GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT,
            MinecraftClient.IS_SYSTEM_MAC
        );
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    
        renderPortalContent(portal);
        
        ((IEMinecraftClient) client).setFrameBuffer(oldFrameBuffer);
        oldFrameBuffer.beginWrite(true);
    
        PortalRendering.popPortalLayer();
    
        renderSecondBufferIntoMainBuffer(portal, matrixStack);
    }
    
    @Override
    public void renderPortalInEntityRenderer(Portal portal) {
        //nothing
    }
    
    @Override
    public boolean replaceFrameBufferClearing() {
        return false;
    }
    
    private boolean testShouldRenderPortal(
        PortalLike portal,
        MatrixStack matrixStack
    ) {
        return QueryManager.renderAndGetDoesAnySamplePass(() -> {
            GlStateManager._enableDepthTest();
            GlStateManager._depthMask(false);
            GL20.glUseProgram(0);
            ViewAreaRenderer.drawPortalViewTriangle(portal, matrixStack, true, true);
            GlStateManager._depthMask(true);
        });
    }
    
    private void renderSecondBufferIntoMainBuffer(PortalLike portal, MatrixStack matrixStack) {
        MyRenderHelper.drawFrameBufferUp(
            portal,
            secondaryFrameBuffer.fb,
            matrixStack
        );
    }
    
}
