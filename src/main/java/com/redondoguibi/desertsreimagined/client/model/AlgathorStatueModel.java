package com.redondoguibi.desertsreimagined.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class AlgathorStatueModel {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "algathor_statue"), "main");

    private final ModelPart al_gathor;

    public AlgathorStatueModel(ModelPart root) {
        this.al_gathor = root.getChild("al_gathor");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition al_gathor = partdefinition.addOrReplaceChild("al_gathor", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pedestal = al_gathor.addOrReplaceChild("pedestal", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -4.0F, -16.0F, 32.0F, 4.0F, 32.0F, new CubeDeformation(0.0F))
                .texOffs(129, 0).addBox(-14.5F, -6.02F, -14.5F, 29.0F, 2.0F, 29.0F, new CubeDeformation(0.0F))
                .texOffs(246, 0).addBox(-13.0F, -11.02F, -13.0F, 26.0F, 5.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(351, 0).addBox(-14.5F, -13.02F, -14.5F, 29.0F, 2.0F, 29.0F, new CubeDeformation(0.0F))
                .texOffs(0, 37).addBox(-12.5F, -14.52F, -12.5F, 25.0F, 1.5F, 25.0F, new CubeDeformation(0.0F))
                .texOffs(101, 37).addBox(-8.0F, -10.6F, -14.2F, 16.0F, 4.0F, 1.2F, new CubeDeformation(0.0F))
                .texOffs(137, 37).addBox(-7.0F, -10.0F, -14.8F, 14.0F, 2.8F, 0.4F, new CubeDeformation(0.0F))
                .texOffs(167, 37).addBox(-12.6F, -10.6F, 10.6F, 2.0F, 4.2F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(176, 37).addBox(-12.6F, -10.6F, -12.6F, 2.0F, 4.2F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(185, 37).addBox(10.6F, -10.6F, 10.6F, 2.0F, 4.2F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(194, 37).addBox(10.6F, -10.6F, -12.6F, 2.0F, 4.2F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition foot_l = al_gathor.addOrReplaceChild("foot_l", CubeListBuilder.create().texOffs(203, 37).addBox(-3.4F, -3.22F, 1.25F, 7.2F, 3.2F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(235, 37).addBox(-3.2F, -2.24F, -0.35F, 6.8F, 2.2F, 1.8F, new CubeDeformation(0.0F))
                .texOffs(254, 37).addBox(-3.0F, -10.22F, 3.35F, 6.4F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -14.0F, -6.0F));

        PartDefinition foot_r = al_gathor.addOrReplaceChild("foot_r", CubeListBuilder.create().texOffs(282, 37).addBox(-3.8F, -3.22F, 1.25F, 7.2F, 3.2F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(314, 37).addBox(-3.6F, -2.24F, -0.35F, 6.8F, 2.2F, 1.8F, new CubeDeformation(0.0F))
                .texOffs(333, 37).addBox(-3.4F, -10.22F, 3.35F, 6.4F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -14.0F, -6.0F));

        PartDefinition body = al_gathor.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition kilt = body.addOrReplaceChild("kilt", CubeListBuilder.create().texOffs(361, 37).addBox(-10.5F, -9.0F, -8.5F, 21.0F, 9.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(0, 65).addBox(-11.2F, -2.38F, -9.2F, 22.4F, 2.4F, 18.4F, new CubeDeformation(0.0F))
                .texOffs(83, 65).addBox(-11.0F, -11.2F, -9.0F, 22.0F, 2.6F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(164, 65).addBox(-3.4F, -11.6F, -9.9F, 6.8F, 3.4F, 1.1F, new CubeDeformation(0.0F))
                .texOffs(181, 65).addBox(-2.2F, -8.6F, -9.5F, 4.4F, 8.2F, 1.1F, new CubeDeformation(0.0F))
                .texOffs(193, 65).addBox(-6.2F, -8.4F, -9.35F, 3.2F, 7.8F, 0.95F, new CubeDeformation(0.0F))
                .texOffs(203, 65).addBox(3.0F, -8.4F, -9.35F, 3.2F, 7.8F, 0.95F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(213, 65).addBox(-11.0F, -8.02F, -9.5F, 22.0F, 8.0F, 19.0F, new CubeDeformation(0.0F))
                .texOffs(296, 65).addBox(-11.6F, -9.2F, -10.1F, 23.2F, 6.0F, 20.2F, new CubeDeformation(0.0F))
                .texOffs(384, 65).addBox(-10.6F, -4.6F, -10.6F, 21.2F, 2.2F, 19.6F, new CubeDeformation(0.0F))
                .texOffs(467, 65).addBox(-1.6F, -6.6F, -10.9F, 3.2F, 2.4F, 0.9F, new CubeDeformation(0.0F))
                .texOffs(0, 93).addBox(-10.0F, -14.22F, -8.6F, 20.0F, 6.0F, 17.2F, new CubeDeformation(0.0F))
                .texOffs(76, 93).addBox(-9.4F, -19.0F, -8.0F, 18.8F, 5.6F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(147, 93).addBox(-8.6F, -18.0F, -8.9F, 17.2F, 3.4F, 1.1F, new CubeDeformation(0.0F))
                .texOffs(185, 93).addBox(-4.4F, -21.2F, -4.4F, 8.8F, 2.6F, 8.8F, new CubeDeformation(0.0F))
                .texOffs(222, 93).addBox(-10.2F, -18.3F, -9.4F, 20.4F, 2.4F, 18.8F, new CubeDeformation(0.0F))
                .texOffs(302, 93).addBox(-9.4F, -19.6F, -8.6F, 18.8F, 2.0F, 17.2F, new CubeDeformation(0.0F))
                .texOffs(375, 93).addBox(-8.2F, -20.4F, -7.4F, 16.4F, 1.4F, 14.8F, new CubeDeformation(0.0F))
                .texOffs(439, 93).addBox(-2.4F, -17.2F, -9.9F, 4.8F, 3.2F, 1.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 0.0F));

        PartDefinition arm_l = body.addOrReplaceChild("arm_l", CubeListBuilder.create().texOffs(452, 93).addBox(-6.6F, -2.1F, -5.2F, 6.6F, 7.4F, 10.4F, new CubeDeformation(0.0F))
                .texOffs(0, 118).addBox(-6.4F, -4.6F, -4.4F, 6.0F, 3.0F, 8.8F, new CubeDeformation(0.0F))
                .texOffs(191, 118).addBox(-5.7F, 5.35F, -4.5F, 5.9F, 1.8F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.6F, -33.5F, 0.0F, -0.829F, 0.0F, 0.0F));

        PartDefinition fist_r1 = arm_l.addOrReplaceChild("fist_r1", CubeListBuilder.create().texOffs(396, 118).addBox(-2.5F, -2.7615F, -2.1255F, 4.4F, 5.2F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, 15.9F, -1.25F, -1.5708F, 0.0F, -0.3054F));

        PartDefinition forearm_r1 = arm_l.addOrReplaceChild("forearm_r1", CubeListBuilder.create().texOffs(349, 118).addBox(-2.0F, -2.1615F, -2.1255F, 18.4F, 4.6F, 4.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.9F, -1.0F, -1.5708F, 0.0F, -0.3054F));

        PartDefinition bracer_r1 = arm_l.addOrReplaceChild("bracer_r1", CubeListBuilder.create().texOffs(160, 118).addBox(-5.9F, 10.9F, -4.5F, 5.9F, 2.2F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(62, 118).addBox(-5.4F, 9.6F, -4.2F, 5.8F, 3.0F, 8.4F, new CubeDeformation(0.0F))
                .texOffs(31, 118).addBox(-5.8F, 3.2F, -4.6F, 5.8F, 8.2F, 9.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, 0.0F, -0.1047F));

        PartDefinition arm_r = body.addOrReplaceChild("arm_r", CubeListBuilder.create().texOffs(222, 118).addBox(0.0F, -2.1F, -5.2F, 6.6F, 7.4F, 10.4F, new CubeDeformation(0.0F))
                .texOffs(257, 118).addBox(0.4F, -4.6F, -4.4F, 6.0F, 3.0F, 8.8F, new CubeDeformation(0.0F))
                .texOffs(448, 118).addBox(-0.2F, 5.35F, -4.5F, 5.9F, 1.8F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.6F, -33.5F, 0.0F, -0.829F, 0.0F, 0.0F));

        PartDefinition fist_r2 = arm_r.addOrReplaceChild("fist_r2", CubeListBuilder.create().texOffs(139, 118).addBox(-2.3601F, -3.4512F, -2.9125F, 4.4F, 5.2F, 5.2F, new CubeDeformation(0.0F))
                .texOffs(92, 118).addBox(0.5831F, -3.0115F, -2.6255F, 18.4F, 4.6F, 4.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 13.5F, -0.5F, -1.5708F, 0.0F, 3.0543F));

        PartDefinition bracer_r2 = arm_r.addOrReplaceChild("bracer_r2", CubeListBuilder.create().texOffs(417, 118).addBox(0.0F, 10.9F, -4.5F, 5.9F, 2.2F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(319, 118).addBox(-0.4F, 9.6F, -4.2F, 5.8F, 3.0F, 8.4F, new CubeDeformation(0.0F))
                .texOffs(288, 118).addBox(0.0F, 3.2F, -4.6F, 5.8F, 8.2F, 9.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, 0.0F, 0.1047F));

        PartDefinition head_root = body.addOrReplaceChild("head_root", CubeListBuilder.create(), PartPose.offset(0.0F, -38.0F, 0.0F));

        PartDefinition head = head_root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(118, 137).addBox(-7.4F, -9.0F, -7.0F, 14.8F, 9.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(177, 137).addBox(-6.6F, -11.6F, -6.2F, 13.2F, 3.0F, 12.4F, new CubeDeformation(0.0F))
                .texOffs(230, 137).addBox(-7.6F, -8.6F, -7.6F, 15.2F, 2.2F, 4.4F, new CubeDeformation(0.0F))
                .texOffs(271, 137).addBox(-8.0F, -6.0F, -6.0F, 1.4F, 4.4F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(291, 137).addBox(6.6F, -6.0F, -6.0F, 1.4F, 4.4F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(311, 137).addBox(-5.4F, -7.4F, -13.6F, 10.8F, 6.4F, 6.8F, new CubeDeformation(0.0F))
                .texOffs(348, 137).addBox(-4.4F, -6.6F, -18.4F, 8.8F, 5.2F, 4.9F, new CubeDeformation(0.0F))
                .texOffs(377, 137).addBox(-3.4F, -5.8F, -21.6F, 6.8F, 4.0F, 3.3F, new CubeDeformation(0.0F))
                .texOffs(399, 137).addBox(-2.4F, -6.7F, -21.9F, 4.8F, 1.2F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(416, 137).addBox(-8.0F, -9.2F, -9.4F, 3.6F, 3.2F, 3.6F, new CubeDeformation(0.0F))
                .texOffs(432, 137).addBox(4.4F, -9.2F, -9.4F, 3.6F, 3.2F, 3.6F, new CubeDeformation(0.0F))
                .texOffs(448, 137).addBox(-7.7F, -8.6F, -9.9F, 3.0F, 2.0F, 0.6F, new CubeDeformation(0.0F))
                .texOffs(457, 137).addBox(4.7F, -8.6F, -9.9F, 3.0F, 2.0F, 0.6F, new CubeDeformation(0.0F))
                .texOffs(466, 137).addBox(-1.4F, -13.22F, -4.0F, 2.8F, 1.6F, 2.4F, new CubeDeformation(0.0F))
                .texOffs(478, 137).addBox(-1.6F, -13.07F, -1.0F, 2.8F, 1.45F, 2.4F, new CubeDeformation(0.0F))
                .texOffs(490, 137).addBox(-1.4F, -12.92F, 2.0F, 2.8F, 1.3F, 2.4F, new CubeDeformation(0.0F))
                .texOffs(0, 161).addBox(-1.6F, -12.77F, 5.0F, 2.8F, 1.15F, 2.4F, new CubeDeformation(0.0F))
                .texOffs(12, 161).addBox(-8.2F, -11.4F, -1.2F, 1.8F, 3.4F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(25, 161).addBox(6.4F, -11.4F, -1.2F, 1.8F, 3.4F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 137).addBox(-4.9F, -1.0F, -7.2F, 9.8F, 2.6F, 7.2F, new CubeDeformation(0.0F))
                .texOffs(35, 137).addBox(-4.0F, -0.8F, -12.0F, 8.0F, 2.0F, 4.9F, new CubeDeformation(0.0F))
                .texOffs(62, 137).addBox(-3.0F, -0.9F, -15.2F, 6.0F, 1.7F, 3.3F, new CubeDeformation(0.0F))
                .texOffs(82, 137).addBox(3.25F, -1.15F, -6.2F, 1.05F, 1.6F, 1.05F, new CubeDeformation(0.0F))
                .texOffs(88, 137).addBox(3.05F, -1.15F, -9.6F, 1.05F, 1.6F, 1.05F, new CubeDeformation(0.0F))
                .texOffs(94, 137).addBox(2.35F, -1.15F, -13.1F, 1.05F, 1.6F, 1.05F, new CubeDeformation(0.0F))
                .texOffs(100, 137).addBox(-4.25F, -1.15F, -6.2F, 1.05F, 1.6F, 1.05F, new CubeDeformation(0.0F))
                .texOffs(106, 137).addBox(-4.05F, -1.15F, -9.6F, 1.05F, 1.6F, 1.05F, new CubeDeformation(0.0F))
                .texOffs(112, 137).addBox(-3.35F, -1.15F, -13.1F, 1.05F, 1.6F, 1.05F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, -6.0F));

        PartDefinition nemes = head_root.addOrReplaceChild("nemes", CubeListBuilder.create().texOffs(38, 161).addBox(-8.2F, -5.0F, -8.0F, 16.4F, 4.4F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(104, 161).addBox(-7.2F, -8.4F, -7.0F, 14.4F, 3.6F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(162, 161).addBox(-5.6F, -11.2F, -5.4F, 11.2F, 3.0F, 10.8F, new CubeDeformation(0.0F))
                .texOffs(207, 161).addBox(-3.8F, -13.4F, -3.6F, 7.6F, 2.4F, 7.2F, new CubeDeformation(0.0F))
                .texOffs(238, 161).addBox(-1.8F, -16.2F, -1.7F, 3.6F, 3.0F, 3.4F, new CubeDeformation(0.0F))
                .texOffs(253, 161).addBox(-8.5F, -1.0F, -8.3F, 17.0F, 1.8F, 16.6F, new CubeDeformation(0.0F))
                .texOffs(322, 161).addBox(-8.7F, -1.8F, -8.5F, 17.4F, 0.9F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(392, 161).addBox(-2.2F, -4.1F, -9.6F, 4.4F, 3.2F, 1.6F, new CubeDeformation(0.0F))
                .texOffs(405, 161).addBox(-3.0F, -7.0F, -9.9F, 6.0F, 3.4F, 1.4F, new CubeDeformation(0.0F))
                .texOffs(421, 161).addBox(-1.4F, -8.8F, -10.2F, 2.8F, 2.2F, 1.8F, new CubeDeformation(0.0F))
                .texOffs(432, 161).addBox(-10.0F, -0.8F, -7.0F, 2.6F, 12.8F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(457, 161).addBox(-9.8F, 11.6F, -6.2F, 2.2F, 4.0F, 7.8F, new CubeDeformation(0.0F))
                .texOffs(478, 161).addBox(-9.6F, 15.2F, -5.4F, 1.8F, 2.6F, 6.4F, new CubeDeformation(0.0F))
                .texOffs(0, 184).addBox(7.4F, -0.8F, -7.0F, 2.6F, 12.8F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(25, 184).addBox(7.6F, 11.6F, -6.2F, 2.2F, 4.0F, 7.8F, new CubeDeformation(0.0F))
                .texOffs(46, 184).addBox(7.8F, 15.2F, -5.4F, 1.8F, 2.6F, 6.4F, new CubeDeformation(0.0F))
                .texOffs(64, 184).addBox(-7.8F, -1.0F, 7.2F, 15.6F, 9.0F, 2.4F, new CubeDeformation(0.0F))
                .texOffs(101, 184).addBox(-7.0F, 8.05F, 7.6F, 14.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(134, 184).addBox(-6.0F, 11.1F, 8.0F, 12.0F, 2.0F, 1.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, 8.6F));

        PartDefinition tail_scute_4_r1 = tail.addOrReplaceChild("tail_scute_4_r1", CubeListBuilder.create().texOffs(319, 184).addBox(-1.3F, -2.2F, 0.6F, 2.6F, 0.9F, 2.2F, new CubeDeformation(0.0F))
                .texOffs(303, 184).addBox(-1.6F, -1.5F, 0.0F, 3.2F, 3.0F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.4F, 17.0F, 0.6632F, 0.0F, 0.0F));

        PartDefinition tail_scute_3_r1 = tail.addOrReplaceChild("tail_scute_3_r1", CubeListBuilder.create().texOffs(291, 184).addBox(-1.3F, -2.85F, 0.6F, 2.6F, 1.1F, 2.5F, new CubeDeformation(0.0F))
                .texOffs(273, 184).addBox(-2.15F, -1.95F, 0.0F, 4.3F, 3.9F, 4.15F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.85F, 12.9F, 0.5585F, 0.0F, 0.0F));

        PartDefinition tail_scute_2_r1 = tail.addOrReplaceChild("tail_scute_2_r1", CubeListBuilder.create().texOffs(261, 184).addBox(-1.3F, -3.5F, 0.6F, 2.6F, 1.3F, 2.8F, new CubeDeformation(0.0F))
                .texOffs(240, 184).addBox(-2.7F, -2.4F, 0.0F, 5.4F, 4.8F, 4.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.8F, 8.6F, 0.4538F, 0.0F, 0.0F));

        PartDefinition tail_scute_1_r1 = tail.addOrReplaceChild("tail_scute_1_r1", CubeListBuilder.create().texOffs(227, 184).addBox(-1.3F, -4.15F, 0.6F, 2.6F, 1.5F, 3.1F, new CubeDeformation(0.0F))
                .texOffs(203, 184).addBox(-3.25F, -2.85F, 0.0F, 6.5F, 5.7F, 4.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.25F, 4.3F, 0.3491F, 0.0F, 0.0F));

        PartDefinition tail_scute_0_r1 = tail.addOrReplaceChild("tail_scute_0_r1", CubeListBuilder.create().texOffs(190, 184).addBox(-1.3F, -4.8F, 0.6F, 2.6F, 1.7F, 3.4F, new CubeDeformation(0.0F))
                .texOffs(163, 184).addBox(-3.8F, -3.3F, 0.0F, 7.6F, 6.6F, 5.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.3F, 0.0F, 0.2443F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
        al_gathor.render(poseStack, buffer, packedLight, packedOverlay);
    }
}