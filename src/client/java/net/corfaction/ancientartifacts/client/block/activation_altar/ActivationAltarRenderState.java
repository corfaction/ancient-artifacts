package net.corfaction.ancientartifacts.client.block.activation_altar;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ActivationAltarRenderState extends BlockEntityRenderState {

    public final ItemStackRenderState item = new ItemStackRenderState();
    public float time;
}