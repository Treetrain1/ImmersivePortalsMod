package qouteall.imm_ptl.core.portal;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import qouteall.q_misc_util.Helper;
import qouteall.q_misc_util.my_util.BoxPredicate;
import qouteall.q_misc_util.my_util.DQuaternion;
import qouteall.q_misc_util.my_util.Plane;

import java.util.UUID;

/**
 * The PortalLike interface is introduced for the merge portal rendering optimization.
 * (A portal or a portal rendering group is a PortalLike)
 * You probably need to manipulate portal entities, not PortalLike
 */
public interface PortalLike {
    @Deprecated
    @Environment(EnvType.CLIENT)
    BoxPredicate getInnerFrustumCullingFunc(
        double cameraX, double cameraY, double cameraZ
    );
    
    boolean isConventionalPortal();
    
    // bounding box
    AABB getExactAreaBox();
    
    Vec3 transformPoint(Vec3 pos);
    
    Vec3 transformLocalVec(Vec3 localVec);
    
    Vec3 transformLocalVecNonScale(Vec3 localVec);
    
    Vec3 inverseTransformLocalVec(Vec3 localVec);
    
    Vec3 inverseTransformPoint(Vec3 point);
    
    double getDistanceToNearestPointInPortal(Vec3 point);
    
    double getDestAreaRadiusEstimation();
    
    Vec3 getOriginPos();
    
    Vec3 getDestPos();
    
    Level getOriginWorld();
    
    Level getDestWorld();
    
    ResourceKey<Level> getDestDim();
    
    boolean isRoughlyVisibleTo(Vec3 cameraPos);
    
    @Nullable
    Plane getInnerClipping();
    
    @Nullable
    DQuaternion getRotation();
    
    double getScale();
    
    boolean getIsGlobal();
    
    boolean isVisible();
    
    // used for super advanced frustum culling
    @Nullable
    Vec3[] getOuterFrustumCullingVertices();
    
    // Scaling does not interfere camera transformation
    @Nullable
    Matrix4f getAdditionalCameraTransformation();
    
    @Nullable
    UUID getDiscriminator();
    
    boolean cannotRenderInMe(Portal portal);
    
    boolean isFuseView();
    
    boolean getDoRenderPlayer();
    
    boolean getHasCrossPortalCollision();
    
    default boolean hasScaling() {
        return Math.abs(getScale() - 1.0) > 0.01;
    }
    
    default ResourceKey<Level> getOriginDim() {
        return getOriginWorld().dimension();
    }
    
    default boolean isOnDestinationSide(Vec3 entityPos, double valve) {
        Plane innerClipping = getInnerClipping();
        
        if (innerClipping == null) {
            return true;
        }
        
        double v = entityPos.subtract(innerClipping.pos()).dot(innerClipping.normal());
        return v > valve;
    }
    
    default double getSizeEstimation() {
        final Vec3 boxSize = Helper.getBoxSize(getExactAreaBox());
        final double maxDimension = Math.max(Math.max(boxSize.x, boxSize.y), boxSize.z);
        return maxDimension;
    }
    
}
